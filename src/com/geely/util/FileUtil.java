package com.geely.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *
 * <p>Title: 校讯通教师版</p>
 * <p>Description:文件操作工具类</p>
 * <p>创建日期:2011-12-28</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class FileUtil {
    /**
     * 文件是否存在
     * @param path
     * @return
     */
    public static boolean FileIsExist(String path) {
        File file = new File(path);

        return file.exists();
    }

    public static File getDownloadDir() throws Exception {
        File cacheDir = null;

        /**
         * 判断SD卡状态
         */
        if (android.os.Environment.getExternalStorageState()
                                      .equals(android.os.Environment.MEDIA_MOUNTED)) {
            //在SD卡根目录生成文件
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
                    "gmcs.apk");
        }

        if (cacheDir == null) {
            throw new Exception("SD卡未准备好...");
        }

        return cacheDir;
    }

    /**
     * 创建此抽象路径名指定的目录，包括所有必需但不存在的父目录
     * @param dirPath
     * @return
     */
    public static File createFileDirs(String dirPath) {
        File dir = new File(dirPath);
        dir.mkdirs();

        return dir;
    }

    /**
         * 在SD卡上面创建文件
         *
         * @param fileName
         * @return
         * @throws IOException
         */
    public static File createSDFile(String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();

        return file;
    }

    /**
     * 在SD卡上面创建目录
     *
     * @param dirName
     * @return
     */
    public static File createSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();

        return dir;
    }

    /**
     * 创建此抽象路径指定的目录和文件
     * @param filePath
     * @return
     * @throws IOException
     */
    public static File createFile(String filePath) throws IOException {
        String dirPath = filePath.substring(0, filePath.lastIndexOf("/"));

        if (!FileIsExist(dirPath)) {
            createFileDirs(dirPath);
        }

        File file = new File(filePath);
        file.createNewFile();

        return file;
    }

    /**
     * 创建文件目录和文件
     * @param filePath 文件目录
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public static File createFile(String filePath, String fileName)
        throws IOException {
        if (!FileIsExist(filePath)) {
            createFileDirs(filePath);
        }

        File file = new File(filePath + fileName);
        file.createNewFile();

        return file;
    }

    /**
     * 将指定的字符串内容写入到文件中
     * @param content 写入内容
     * @param path 文件全路径
     * @return
     */
    public static File writeFile(String content, String path) {
        if (content == null) {
            return null;
        }

        File file = null;
        OutputStream outputStream = null;

        try {
            file = createFile(path);
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 将指定内容写入到文件中
     * @param content 写入的内容
     * @param filePath 文件目录
     * @param fileName 文件名
     * @return
     */
    public static File writeFile(String content, String filePath,
        String fileName) {
        if (content == null) {
            return null;
        }

        File file = null;
        OutputStream outputStream = null;

        try {
            file = createFile(filePath, fileName);
            outputStream = new FileOutputStream(file);
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**将一个输入流写入文件
     * @param inputStream 输入流
     * @param filePath 文件目录
     * @param fileName 文件名
     * @return
     */
    public static File writeFile(InputStream inputStream, String filePath,
        String fileName) {
        File file = null;
        OutputStream outputStream = null;

        try {
            file = createFile(filePath, fileName);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 将一个输入流写入文件中
     * @param inputStream 输入流
     * @param path 文件路径
     * @return
     */
    public static File writeFile(InputStream inputStream, String path) {
        File file = null;
        OutputStream outputStream = null;

        try {
            file = createFile(path);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4 * 1024];
            int len = 0;

            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 删除该目录下的指定文件
     * @param filePath 文件目录
     * @param fileName 文件名
     * @return
     */
    public static boolean deleteFile(String filePath, String fileName) {
        if (FileIsExist(filePath + fileName)) {
            File file = new File(filePath + fileName);

            return file.delete();
        }

        return true;
    }

    /**
     * 删除该路径指定的文件
     * @param path 文件路径
     * @return
     */
    public static boolean deleteFile(String path) {
        if (FileIsExist(path)) {
            File file = new File(path);

            return file.delete();
        }

        return true;
    }

    /**
     * 读取文件类容
     * @param strPath
     * @return
     */
    public static byte[] readFile(String strPath) {
        try {
            File file = new File(strPath);
            FileInputStream fileInputStream = new FileInputStream(file);
            int intLength = fileInputStream.available();
            byte[] bytStream = new byte[intLength];

            fileInputStream.read(bytStream);
            fileInputStream.close();

            return bytStream;
        } catch (Exception e) {
            return null;
        } finally {
        }
    }

    /**
     * 读取文件返回字符串
     * @param strPath
     * @return
     */
    public static String readFileToString(String strPath) {
        byte[] bytes = readFile(strPath);

        return new String(bytes);
    }
    
    /**
     * 从文件流中读取内容
     * @param inputStream
     * @return
     */
    public static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];

        int len;

        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }

            outputStream.close();

            inputStream.close();
        } catch (IOException e) {
        }

        return outputStream.toString();
    }

    /**
     * 判断SD卡是否可用
     * @return
     */
    public static boolean ifSDCardIsAvailable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 获取路径可用空间大小
     * @param path
     * @return
     */
    public static long getPathAvailableBlocks(String path) {
        StatFs sf = new StatFs(path);

        return sf.getAvailableBlocks() * sf.getBlockSize();
    }
}
