package demo.okhttp.com.myapplication.utils;

import java.io.*;
import java.text.DecimalFormat;

import demo.okhttp.com.mylibrary.utils.StringUtils;

/**
 * Created by zhouyunfang on 17/6/6.
 * @version 1.0.0
 */
public class FileUtils {
    private static final int BUFFER_SIZE = 4 * 1024;
    private static final String LOG_TAG = "FileUtils";
    private static String mSeparator = File.separator;
    private static char mSeparatorChar = File.separatorChar;

    /**
     * 设置文件路径中的分割符号，一般情况下，windows上为"\"，其他平台大多为"/"
     * @param separatorChar char
     */
    public static void setSeparatorChar(char separatorChar) {
        mSeparatorChar = separatorChar;
        mSeparator = String.valueOf(mSeparatorChar);
    }

    /**
     * 判断路径是否存在
     *
     * @param path 路径
     * @return 如果条件成立，返回true
     */
    public static boolean exists(String path) {
        return !StringUtils.isEmpty(path) && new File(path).exists();
    }

    /**
     * 判断路径是文件，且存在
     *
     * @param path 文件路径，如果传入null字符串，则认为文件不存在
     * @return 如果条件成立，返回true;
     */
    public static boolean fileExists(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * 设置最后修改时间
     * @param filePath 文件路径
     * @param lastModified 时间
     * @return true表示成功
     */
    public static boolean updateFileLastModified(String filePath, long lastModified) {
        return (fileExists(filePath)) && new File(filePath).setLastModified(lastModified);
    }
    /**
     * 获取文件数量
     * @param filePath
     * @return
     */
    public static int getFileNumber(String filePath){
    	File file = new File(filePath);
    	if(!file.exists()){
    		return 0;
    	}else{
    		return file.listFiles().length;
    	}
    }
    /**
     * 文件长度，文件存在返回为0
     * @param path 文件路径
     * @return  文件长度
     */
    public static long fileLength(String path) {
        if (fileExists(path)) {
            return new File(path).length();
        }

        return 0;
    }

    /**
     * 判断路径是文件夹，且存在
     *
     * @param path 文件夹路径
     * @return 如果条件成立，返回true;
     */
    public static boolean folderExists(String path) {
        if (StringUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }

    /**
     * 创建文件， 如果不存在则创建，否则返回原文件的File对象
     * @param path 文件路径
     * @return 创建好的文件对象,返回为空表示失败
     */
    synchronized public static File createFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }

        File file = new File(path);
        if (file.isFile()) {
            return file;
        }

        File parentFile = file.getParentFile();
        if (parentFile != null && (parentFile.isDirectory() || parentFile.mkdirs()))
        {
            try {
                if (file.createNewFile()) {
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 创建目录。
     * <b>如果目录不存在，创建目录, 如果目录已存在，不再重新创建</b>
     *
     * @param path 目录路径
     * @return 创建好的目录文件对象
     */
    synchronized public static File createFolder(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        return file.isDirectory() || file.mkdirs() ? file : null;
    }

    /**
     * 获取文件大小
     * <br>如果文件是目录，则返回该目录下所有文件大小的总和，否则返回该文件的大小
     * @param path 文件夹路径
     * @return  所有文件的大小
     */
    synchronized public static long getFileSize(String path) {
        if (StringUtils.isEmpty(path)) {
            return 0;
        }

        return getFileSize(new File(path));
    }

    /**
     * 获取文件大小
     * <br>如果文件是目录，则返回该目录下所有文件大小的总和，否则返回该文件的大小
     * @param file file
     * @return  所有文件的大小
     */
    synchronized public static long getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                size += f.isDirectory() ? getFileSize(f) : f.length();
            }
        } else {
            size = file.length();
        }
        return size;
    }

    /**
     * 删除指定目录中距离现在时间超过interval的文件
     *
     * @param path     目录路径
     * @param interval 时间(单位:毫秒)
     * @return 删除文件个数
     */
    synchronized public static int clearFolder(String path, long interval) {
        if (StringUtils.isEmpty(path)) {
            return 0;
        }
        return clearFolder(new File(path), interval);
    }

    /**
     * 删除指定目录中距离现在时间超过interval(单位:毫秒)的文件
     *
     * @param path     目录路径
     * @param interval 时间(单位:毫秒)
     * @return 删除文件个数
     */
    synchronized public static int clearFolder(File path, long interval) {
        long expiredTimeMillis = System.currentTimeMillis() - interval;
        int deletedItems = 0;
        File[] fileList = path.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    deletedItems += clearFolder(file, interval);
                }
                if (file.lastModified() < expiredTimeMillis) {
                    if (file.delete()) {
                        deletedItems++;
                    }
                }
            }
        }
        return deletedItems;
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录路径。
     * @return true 表示删除成功，否则为失败
     */
    synchronized public static boolean delete(String path) {
        return !StringUtils.isEmpty(path) && delete(new File(path));
    }

    /**
     * 删除文件或目录
     *
     * @param path 文件或目录。
     * @return true 表示删除成功，否则为失败
     */
    synchronized public static boolean delete(File path) {
        if (path.isDirectory()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (!delete(file)) {
                    return false;
                }
            }
        }
        return !path.exists() || path.delete();
    }

    /**
     * 读取文件内容,并以字符串形式返回
     *
     * @param path 文件路径
     * @return 文件内容
     */
    public static String load(String path) {
        if (path == null) {
            throw new NullPointerException("path should not be null.");
        }

        String string = null;
        try {
            string = StringUtils.stringFromInputStream(new FileInputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return string != null ? string : "";
    }

    /**
     *
     * 将字符串数据保存到文件.
     * <br/>注意：如果没有目录则会创建目录
     *
     * @param content 字符串内容
     * @param path    文件路径
     * @return 成功返回true, 否则返回false
     */
     public synchronized static boolean store(String content, String path) {
         if (path == null) {
             throw new NullPointerException("path should not be null.");
         }


         BufferedWriter bufferedWriter = null;
         try {
             File file = createFile(path);
             if (file == null) {
                 //可能无存储卡或者其他原因导致
                 return false;
             }
             bufferedWriter = new BufferedWriter(new FileWriter(file));
             bufferedWriter.write(content != null ? content : "");
             bufferedWriter.flush();
             return true;
         } catch (IOException e) {
             e.printStackTrace();
         } catch (ArrayIndexOutOfBoundsException e) {
             e.printStackTrace();
         } finally {
             if (bufferedWriter != null) {
                 try {
                     bufferedWriter.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
         return false;
     }

    /**
     * 将输入流保存到文件，并关闭流.
     *
     * @param inputStream 字符串内容
     * @param path        文件路径
     * @return boolean
     */
    synchronized public static boolean store(InputStream inputStream, String path) {
        if (path == null) {
            throw new NullPointerException("path should not be null.");
        }
        int length;

        FileOutputStream fileOutputStream = null;

        try {
            File file = createFile(path);
            if (file == null) {
                //可能无存储卡或者其他原因导致
                return false;
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            fileOutputStream = new FileOutputStream(file);
            while ((length = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param desPath 目标文件路径
     * @param srcPath 源文件路径
     * @return false if file copy failed, true if file copy succeeded..
     */
    public static boolean copy(String desPath, String srcPath) {
        if (desPath == null || srcPath == null) {
            throw new NullPointerException("path should not be null.");
        }
        FileInputStream input = null;
        boolean succeed;

        try {
            input = new FileInputStream(srcPath);
            succeed = FileUtils.store(input, desPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                input.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return false;
        }

        return succeed;
    }

    /**
     * 获取本地文件或URL的文件名. 包含后缀
     *
     * @param path 本地文件或URL路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }

        int query = path.lastIndexOf('?');
        if (query > 0) {
            path = path.substring(0, query);
        }

        int filenamePos = path.lastIndexOf(mSeparatorChar);
        return (filenamePos >= 0) ? path.substring(filenamePos + 1) : path;
    }

    /**
     * 获取本地文件或URL的文件名. 不包含后缀
     *
     * @param path 本地文件或URL路径
     * @return 文件名
     */
    public static String getFileShortName(String path) {
        String fileName = getFileName(path);
        int separatorIndex = fileName.lastIndexOf('.');
        return separatorIndex > 0 ? fileName.substring(0, separatorIndex) : fileName;
    }

    /**
     * 获取文件所在目录的路径. 不包含最后的separatorChar
     *
     * @param path 文件路径
     * @return 文件所在目录的路径
     */
    public static String getFilePath(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        int separatorIndex = -1;

        if (path != null && path.startsWith(mSeparator)) {
            separatorIndex = path.lastIndexOf(mSeparatorChar);
        }

        return (separatorIndex == -1) ? mSeparator : path.substring(0, separatorIndex);
    }

    /**
     * 获取本地文件或URL后缀名. 无后缀名时，返回空字符串
     *
     * @param path 本地文件或URL路径
     * @return 后缀名
     */
    public static String getFileExtension(String path) {
        if (!StringUtils.isEmpty(path)) {
            int query = path.lastIndexOf('?');
            if (query > 0) {
                path = path.substring(0, query);
            }

            int filenamePos = path.lastIndexOf('/');
            String filename = (filenamePos >= 0) ? path.substring(filenamePos + 1) : path;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            // 去掉了Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename) 的判断，中文会返回false
            if (filename.length() > 0) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }
        return "";
    }

    /**
     * 文件最近修改时间
     *
     * @param path 文件路径
     * @return 从1970年1月1日0点起，单位毫秒
     */
    public static long lastModified(String path) {
        if (StringUtils.isEmpty(path)) {
            return 0;
        }
        return new File(path).lastModified();
    }

    /**
     * 重命名文件
     * @param srcPath 原名
     * @param dstPath 重命名后的文件名
     * @return 成功为true
     */
    public static boolean rename(String srcPath, String dstPath) {
        File file = new File(srcPath);
        return file.isFile() && file.renameTo(new File(dstPath));
    }


    /**
     * 合法化文件名
     * 替换文件名不允许出现的字符，比如{}/\:*?"<>以及无效或者不可视Unicode字符
     *
     * @param fileName 被合法化的文件名
     * @return 合法化后的文件名
     */
    public static String validateFileName(String fileName) {
        // {} \ / : * ? " < > |
        return fileName == null ? null : fileName.replaceAll("([{/\\\\:*?\"<>|}\\u0000-\\u001f\\uD7B0-\\uFFFF]+)", "");
    }


    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param file 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSizeTwo(File file) {
        long blockSize = 0;
        try {
            blockSize = getFileSizeFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }


    /**
     * 获取文件大小
     * <br>如果文件是目录，则返回该目录下所有文件大小的总和，否则返回该文件的大小
     * @param file file
     * @return  所有文件的大小
     */
    synchronized public static long getFileSizeFile(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                size += f.isDirectory() ? getFileSizeFile(f) : f.length();
            }
        } else {
            size = file.length();
        }
        return size;
    }
    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param dir
     *            被删除目录的文件路径
     * @return 目录删除成功返回true,否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
//			LogUtils.e("删除目录失败" + dir + "目录不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFiles(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
//            LogUtils.e("删除目录失败");
            return false;
        }

        // 删除当前目录
        if (dirFile.delete()) {
//            LogUtils.e("删除目录" + dir + "成功！");
//            CheersLog.d("deleteFiles", "删除目录" + dir + "成功！");
            return true;
        } else {
//            LogUtils.e("删除目录" + dir + "失败！");
//            CheersLog.d("deleteFiles", "删除目录" + dir + "失败！");
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            被删除文件的文件名
     * @return 单个文件删除成功返回true,否则返回false
     */
    public static boolean deleteFiles(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
//            LogUtils.e("删除单个文件" + fileName + "成功！");
//            CheersLog.d("deleteFiles", "删除单个文件" + fileName + "成功！");
            return true;
        } else {
//            LogUtils.e("删除单个文件" + fileName + "失败！");
//            CheersLog.d("deleteFiles", "删除单个文件" + fileName + "失败！");
            return false;
        }
    }


}
