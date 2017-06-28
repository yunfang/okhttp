package demo.okhttp.com.myapplication;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import demo.okhttp.com.myapplication.utils.SDKVersionUtils;

/**
 * Created by zhouyunfang on 17/6/28.
 */

public class Config {

    private static String sCacheFolderPath;
    private static final String OBJECT_CACHE_FOLDER_NAME = "object";


    /**
     * 对象缓存占用这个APP的内存比例
     */
    public static final float OBJECT_CACHE_MEM_PERCENT = 0.05f;


    /**
     * 初始化
     *
     * @param context context
     */
    public static void init(Context context) {
        sCacheFolderPath = Storage.getCachePath(context);
    }


    /**
     * 获取缓存文件夹路径
     *
     * @return 路径名
     */
    public static String getCacheFolderPath() {
        return sCacheFolderPath;
    }


    public static String getObjectCacheFolderPath() {
        return sCacheFolderPath + File.separator + OBJECT_CACHE_FOLDER_NAME;
    }


    /**
     * 存储信息
     */
    public static class Storage {
        /**
         * 外部存储是否可读写
         *
         * @return 可读写返回true, 否则返回false
         */
        public static boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(state);
        }

        /**
         * 获取外部目录缓存路径
         *
         * @param context context
         * @return 外部存储换成路径
         */
        public static File getExternalCacheDir(Context context) {
            File file = null;
            if (SDKVersionUtils.hasFroyo()) {
                file = context.getExternalCacheDir();
            }

            if (file == null) {
                final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
                file = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
            }

            file.mkdirs();

            if (file.isDirectory()) {
                return file;
            }

            return null;
        }

        /**
         * 获取缓存路径
         *
         * @param context context
         * @return 存储路径
         */
        public static String getCachePath(Context context) {
            File file = null;
            if (isExternalStorageWritable()) {
                file = getExternalCacheDir(context);
            }

            return (file != null) ? file.getAbsolutePath() : context.getCacheDir().getAbsolutePath();
        }
    }
}
