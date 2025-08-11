package com.github.malitsplus.shizurunotes.utils;

import android.os.Build;

import com.github.malitsplus.shizurunotes.common.Statics;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    public static String getDbDirectoryPath() {
        if (Build.VERSION.SDK_INT <= 23) {
            return Utils.getApp().getFilesDir().getParent() + "/databases";
        } else {
            return Utils.getApp().getDataDir().getAbsolutePath() + "/databases";
        }
    }

    public static String getDbFilePath() {
        return Utils.getApp().getDatabasePath(Statics.DB_FILE_NAME).getAbsolutePath();
    }

    public static String getCompressedDbFilePath() {
        return Utils.getApp().getDatabasePath(Statics.DB_FILE_NAME_COMPRESSED).getAbsolutePath();
    }

    public static String getPrefabDirectoryPath() {
        if (Build.VERSION.SDK_INT <= 23) {
            return Utils.getApp().getFilesDir().getParent() + "/databases";
        } else {
            return Utils.getApp().getDataDir().getAbsolutePath() + "/prefabs";
        }
    }

    public static String getPrefabFilePath() {
        return getPrefabDirectoryPath() + "/" + Statics.PREFAB_FILE_NAME;
    }

    public static String getFileFilePath(String fileName) {
        return Utils.getApp().getFilesDir().getAbsolutePath() + "/" + fileName;
    }

    public static List<String> getFileListsExtension(String directory, String extension) {
        List<String> list = new ArrayList<String>();

        File dir = new File(directory);
        if (dir.isDirectory()) {
            FileFilter filter = f -> f.getName().endsWith(extension);
            File[] files = dir.listFiles(filter);
            if (files != null) {
                for (File file : files) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }

    public static boolean copyDBFile(String from, String to) {
        String srcFilePath = getDbDirectoryPath() + "/" + from;
        String desFilePath = getDbDirectoryPath() + "/" + to;

        File exDB = new File(desFilePath);
        if (exDB.exists())
            exDB.delete();
        try {
            FileInputStream fileInputStream = new FileInputStream(srcFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(exDB);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fileInputStream.read(buffer)) > 0)
                fileOutputStream.write(buffer, 0, count);
            fileOutputStream.flush();
            fileOutputStream.close();
            fileInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyInputStreamToFile(InputStream stream, String to) {
        String desFilePath = getDbDirectoryPath() + "/" + to;

        File exDB = new File(desFilePath);
        if (exDB.exists())
            exDB.delete();
        try {
            //FileInputStream fileInputStream = new FileInputStream(srcFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(exDB);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = stream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            //fileInputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean checkValidDBFile(InputStream stream) {
        try {
            byte[] buffer = new byte[16];
            int count = stream.read(buffer, 0, 16);
            String str = new String(buffer);

            return str.equals("SQLite format 3\u0000");
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /***
     * 单纯的复制文件
     * @param srcPath 源文件路径
     * @param desPath 目标路径
     * @param delete  是否删除源
     */
    public static void copyFile(String fileName, String srcPath, String desPath, boolean delete) {
        String srcFilePath = srcPath + fileName;
        String desFilePath = desPath + fileName;
        File dataBaseDir = new File(desPath);
        //检查数据库文件夹是否存在
        if (!dataBaseDir.exists())
            dataBaseDir.mkdirs();

        File exDB = new File(desFilePath);
        if (exDB.exists())
            exDB.delete();
        try {
            FileInputStream fileInputStream = new FileInputStream(srcFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(exDB);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fileInputStream.read(buffer)) > 0)
                fileOutputStream.write(buffer, 0, count);
            fileOutputStream.flush();
            fileOutputStream.close();
            fileInputStream.close();
            if (delete) {
                File srcFile = new File(srcFilePath);
                srcFile.delete();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File directoryFile) {
        if (directoryFile.isDirectory()) {
            File[] files = directoryFile.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteDirectory(child);
                }
            }
        }
        return deleteFile(directoryFile);
    }

    public static boolean deleteFile(String filePath) {
        return deleteFile(new File(filePath));
    }

    public static boolean deleteFile(File file) {
        boolean flag = true;
        try {
            if (!file.delete()) {
                flag = false;
                throw new IOException("Failed to delete file: " + file.getAbsolutePath() + ". Size: " + file.length() / 1024 + "KB.");
            } //else {
            //LogUtils.file("FileDelete", "Delete file " + file.getAbsolutePath());
            //}
        } catch (Exception e) {
            LogUtils.file(LogUtils.E, "FileDelete", e.getMessage());
        }
        return flag;
    }

    public static boolean deleteFilesExtension(String directory, String extension) {
        boolean flag = true;
        File dir = new File(directory);
        if (dir.isDirectory()) {
            FileFilter filter = f -> f.getName().endsWith(extension);
            File[] files = dir.listFiles(filter);
            if (files != null) {
                for (File file : files) {
                    flag = flag && deleteFile(file);
                }
            }
        }
        return flag;
    }

    public static boolean checkFile(@NotNull File file) {
        if (!file.exists()) {
            LogUtils.file(LogUtils.I, "FileCheck", "FileNotExists: " + file.getAbsolutePath());
            return false;
        }
        return true;
    }

    public static boolean checkFile(String filePath) {
        File file = new File(filePath);
        return checkFile(file);
    }

    public static boolean checkFileAndSize(String filePath, long border) {
        File file = new File(filePath);
        if (!checkFile(file)) {
            return false;
        }
        if (file.length() < border * 1024) {
            LogUtils.file(LogUtils.W, "FileCheck", "AbnormalDbFileSize: " + file.length() / 1024 + "KB." + " At: " + file.getAbsolutePath());
            return false;
        }
        LogUtils.file(LogUtils.I, "FileCheck", file.getAbsolutePath() + ". Size: " + file.length() / 1024 + "KB.");
        return true;
    }

    public static void checkFileAndDeleteIfExists(File file) {
        if (file.exists()) deleteFile(file);
    }

    public static boolean checkFileSize(String filePath, int size) {
        File file = new File(filePath);
        if (!checkFile(file)) {
            return false;
        }
        if (file.length() != size) {
            LogUtils.file(LogUtils.W, "FileCheck", "AbnormalDbFileSize: " + file.length() + "bytes != " + size + "bytes." + " At: " + file.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    public static String getFileMD5ToString(final String filePath) {
        File file = new File(filePath);
        return getFileMD5ToString(file);
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    public static String getFileMD5ToString(final File file) {
        return Utils.bytes2HexString(getFileMD5(file), true);
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    public static byte[] getFileMD5(final String filePath) {
        return getFileMD5(new File(filePath));
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    public static byte[] getFileMD5(final File file) {
        if (file == null) return null;
        DigestInputStream dis = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            dis = new DigestInputStream(fis, md);
            byte[] buffer = new byte[1024 * 256];
            while (true) {
                if (!(dis.read(buffer) > 0)) break;
            }
            md = dis.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void unzip(String path, String zipName) throws IOException {
        InputStream is;
        ZipInputStream zis;

        String filename;
        is = new FileInputStream(path + zipName);
        zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;
        byte[] buffer = new byte[1024];
        int count;

        while ((ze = zis.getNextEntry()) != null) {
            filename = ze.getName();

            // Need to create directories if not exists, or
            // it will generate an Exception...
            if (ze.isDirectory()) {
                File fmd = new File(path + filename);
                fmd.mkdirs();
                continue;
            }

            FileOutputStream fOut = new FileOutputStream(path + filename);

            while ((count = zis.read(buffer)) != -1) {
                fOut.write(buffer, 0, count);
            }

            fOut.close();
            zis.closeEntry();
        }

        zis.close();

    }

}
