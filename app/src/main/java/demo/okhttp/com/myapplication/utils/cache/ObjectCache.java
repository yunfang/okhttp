package demo.okhttp.com.myapplication.utils.cache;

import android.support.v4.util.LruCache;


import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

import demo.okhttp.com.myapplication.utils.ConstantUtils;
import demo.okhttp.com.myapplication.utils.FileUtils;
import demo.okhttp.com.mylibrary.utils.LogUtils;

/**
 * 对象缓存，可能会被缓存在文件
 *
 * Created by zhouyunfang on 17/6/6.
 * @version 1.0.0
 */
public final class ObjectCache {
    private static final float MIN_MEM_CACHE_PERCENT = 0.05f;
    private static final float MAX_MEM_CACHE_PERCENT = 0.5f;

    private static final long CLEAR_OBJECT_INTERVAL = 30 * 60 * 1000;

    private static final long MAX_EXPIRED_DURATION = 10 * 365 * ConstantUtils.MILLS_PER_DAY;

    private static ObjectCache sInstance;

    private static final ReentrantLock FILE_CRITICAL = new ReentrantLock();

    private File mDiskCacheDir;
    private boolean mClosed = false;
    private SaveObjectThread mSaveObjectThread = new SaveObjectThread();
    private LruCache<String, Entity> mSerializableLruCache;
    private HashMap<String, Entity> mMemCache;

    /**
     * 打开对象缓存，如果不存在创建
     *
     * @param memCacheSizePercent 内存占用比例
     * @param path                缓存路径
     * @return ObjectCache实例
     * @throws IOException 创建目录异常
     */
    public synchronized static ObjectCache open(float memCacheSizePercent, String path) throws IOException {
        if (sInstance == null) {
            sInstance = new ObjectCache(memCacheSizePercent, path);
        } else {
            throw new IllegalStateException("ObjectCache already existed!");
        }

        return sInstance;
    }

    private ObjectCache(float memCacheSizePercent, String path) throws IOException {
        mDiskCacheDir = FileUtils.createFolder(path);
        if (mDiskCacheDir == null) {
            throw new IOException("Create Folder:" + path + " failed!");
        }

        if (memCacheSizePercent < MIN_MEM_CACHE_PERCENT || memCacheSizePercent > MAX_MEM_CACHE_PERCENT) {
            throw new IllegalArgumentException("memCacheSizePercent - percent must be between"
                    + MIN_MEM_CACHE_PERCENT + "and" + MAX_MEM_CACHE_PERCENT + " (inclusive)");
        }

        mSerializableLruCache = new LruCache<String, Entity>(Math.round(memCacheSizePercent * Runtime.getRuntime().maxMemory()) / ConstantUtils.KILO) {
            @Override
            protected int sizeOf(String key, Entity value) {
                int objectSize = 0;
                if (value.getObject() instanceof ISizeOfObject) {
                    objectSize = ((ISizeOfObject) (value.getObject())).getSize() / ConstantUtils.KILO;
                }

                return objectSize == 0 ? 1 : objectSize;
            }
        };

        mMemCache = new HashMap<String, Entity>();

        mSaveObjectThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mSaveObjectThread.start();
    }

    /**
     * 添加�?个对�?
     *
     * @param key    KEY
     * @param object object
     */
    public synchronized void add(String key, Object object) {
        add(key, object, MAX_EXPIRED_DURATION);
    }

    /**
     * 添加�?个带实效的对�?
     *
     * @param key             KEY
     * @param object          object
     * @param expiredDuration 有效时长
     */
    public synchronized void add(String key, Object object, long expiredDuration) {
        if (mClosed) {
            throw new IllegalStateException("Cache has been closed!");
        }

        long invalidTimeStamp = System.currentTimeMillis() + expiredDuration;
        if (object instanceof Serializable) {
            Entity entity = new Entity(object, invalidTimeStamp);
            mSerializableLruCache.put(key, entity);
            synchronized (mSaveObjectThread) {
                mSaveObjectThread.addObject(key, entity);
                mSaveObjectThread.notify();
            }
        } else {
            mMemCache.put(key, new Entity(object, invalidTimeStamp));
        }
    }

    /**
     * 内存吃紧时调用，会将�?些内存缓存文件写入磁�?
     */
    public synchronized void notifyMemoryLow() {
        mSerializableLruCache.evictAll();
    }

    /**
     * 关闭缓存区，如果�?要序列化指定对象，需要调用save
     */
    public synchronized void close() {
        mClosed = true;
        mMemCache.clear();
        mSerializableLruCache.evictAll();
        mSaveObjectThread.close();
    }

    /**
     * 是否存在
     *
     * @param key key
     * @return true false
     */
    public synchronized boolean contain(String key) {
        return getObject(key) != null || getSerializableObject(key) != null;
    }

    /**
     * 获取某个可实例化的对�?
     *
     * @param key          key
     * @param defaultValue defaultValue
     * @param <T>          类型
     * @return 对象实例
     */
    public synchronized <T> T get(String key, T defaultValue) {
        Object object = getObject(key);
        if (object == null) {
            object = getSerializableObject(key);
        }

        if (object == null) {
            return defaultValue;
        }

        return (T) object;
    }

    /**
     * 获取某个可实例化的对象
     *
     * @param key key
     * @return 对象实例
     */
    public synchronized Object get(String key) {
        Object object = getObject(key);
        if (object == null) {
            object = getSerializableObject(key);
        }

        return object;
    }

    private synchronized Object getObject(String key) {
        if (mMemCache.containsKey(key)) {
            if (mMemCache.get(key).getInvalidTimeStamp() >= System.currentTimeMillis()) {
                return mMemCache.get(key).getObject();
            } else {
                mMemCache.remove(key);
            }
        }

        return null;
    }

    private synchronized Object getSerializableObject(String key) {
        if (mSerializableLruCache.get(key) != null) {
            if (mSerializableLruCache.get(key).getInvalidTimeStamp() >= System.currentTimeMillis()) {
                return mSerializableLruCache.get(key).getObject();
            }
        } else {
            Object object = null;
            FILE_CRITICAL.lock();
            File file = new File(getAbsolutePath(key));
            if (file.isFile()) {
                boolean isFileInvalid = false;
                ObjectInputStream ois = null;
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    ois = new ObjectInputStream(fileInputStream);
                    Entity entity = (Entity) ois.readObject();
                    if (entity.getInvalidTimeStamp() > System.currentTimeMillis()) {
                        mSerializableLruCache.put(key, entity);
                        object = entity.getObject();
                    } else {
                        isFileInvalid = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fileInputStream.close();
                        ois.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isFileInvalid) {
                        file.delete();
                    }

                    FILE_CRITICAL.unlock();
                }
            } else {
                FILE_CRITICAL.unlock();
            }
            return object;
        }

        return null;
    }

    /**
     * 删除某个对象
     *
     * @param key key
     */
    public synchronized void delete(String key) {
        if (mMemCache.remove(key) == null) {
            mSerializableLruCache.remove(key);
            deleteObject(key);
        }
    }

    /**
     * 清除keys对应的数�?
     *
     * @param keys keys
     */
    public synchronized void delete(String[] keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    /**
     * 序列化指定key的对�?
     *
     * @param key key
     */
    public synchronized void save(String key) {
        if (mMemCache.containsKey(key)) {
            throw new IllegalArgumentException("value of key must be instance of Serializable!");
        }

        if (mSerializableLruCache.get(key) != null) {
            saveObject(key, mSerializableLruCache.get(key));
        }
    }

    /**
     * 序列化指定keys的对�?
     *
     * @param keys keys
     */
    public synchronized void save(String[] keys) {
        for (String key : keys) {
            save(key);
        }
    }

    /**
     * 清除�?有缓存实例，包括磁盘缓存
     */
    public synchronized void clear() {
        FileUtils.clearFolder(mDiskCacheDir, 0);
        mMemCache.clear();
        mSerializableLruCache.evictAll();
    }

    private void deleteObject(String key) {
        FILE_CRITICAL.lock();
        FileUtils.delete(getAbsolutePath(key));
        FILE_CRITICAL.unlock();
    }

    private String getAbsolutePath(String key) {
        return mDiskCacheDir.getAbsolutePath() + File.separator + key;
    }

    private void saveObject(String key, Entity entity) {
        FILE_CRITICAL.lock();
        ObjectOutputStream oos = null;
        try {
            File file = new File(getAbsolutePath(key));
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(entity);
            file.setLastModified(entity.getInvalidTimeStamp());
        } catch (Exception e) {
            LogUtils.logE(e.getMessage());
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FILE_CRITICAL.unlock();
    }

    private static final class Entity implements Serializable {
        private Object mObject;
        private long mInvalidTimeStamp;

        private Entity(Object object, long timeStamp) {
            mObject = object;
            mInvalidTimeStamp = timeStamp;
        }

        public Object getObject() {
            return mObject;
        }

        public long getInvalidTimeStamp() {
            return mInvalidTimeStamp;
        }
    }

    private final class SaveObjectThread extends Thread {
        private LinkedHashMap<String, Entity> mLinkedHashMap = new LinkedHashMap<String, Entity>();
        private long mPreClearTimeStamp = 0;

        private ReentrantLock mMemCritical = new ReentrantLock();

        public void addObject(String key, Entity entity) {
            mMemCritical.lock();
            mLinkedHashMap.put(key, entity);
            mMemCritical.unlock();
        }

        private void saveObjects() {
            mMemCritical.lock();
            LinkedHashMap<String, Entity> linkedHashMap = new LinkedHashMap<String, Entity>(mLinkedHashMap);
            mMemCritical.unlock();

            for (String key : linkedHashMap.keySet()) {
                saveObject(key, linkedHashMap.get(key));

                mMemCritical.lock();
                mLinkedHashMap.remove(key);
                mMemCritical.unlock();
            }
        }

        private void clearExpiredObjects() {
            FILE_CRITICAL.lock();
            File[] files = mDiskCacheDir.listFiles();
            FILE_CRITICAL.unlock();
            for (File file : files) {
                if (file.lastModified() <= System.currentTimeMillis()) {
                    boolean isFileInvalid = false;
                    ObjectInputStream ois = null;
                    FileInputStream fileInputStream = null;
                    FILE_CRITICAL.lock();
                    try {
                        fileInputStream = new FileInputStream(file);
                        ois = new ObjectInputStream(fileInputStream);
                        Entity entity = (Entity) ois.readObject();
                        isFileInvalid = (entity.getInvalidTimeStamp() <= System.currentTimeMillis());
                    } catch (ClassCastException e) {
                        isFileInvalid = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fileInputStream.close();
                            ois.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (isFileInvalid) {
                        FileUtils.delete(file);
                    }
                    FILE_CRITICAL.unlock();
                }
            }
        }

        private void close() {
            interrupt();
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (System.currentTimeMillis() > mPreClearTimeStamp + CLEAR_OBJECT_INTERVAL) {
                    if (mPreClearTimeStamp != 0) {
                        clearExpiredObjects();
                    }
                    mPreClearTimeStamp = System.currentTimeMillis();
                }

                saveObjects();

                try {
                    synchronized (this) {
                        if (mLinkedHashMap.size() <= 0) {
                            wait();
                        }
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
