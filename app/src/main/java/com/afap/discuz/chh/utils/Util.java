package com.afap.discuz.chh.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {
    /**
     * 复制文件、文件夹
     *
     * @param orgFile File 原文件
     * @param tarDir  File 目标文件夹
     */
    public static void copyFile(File orgFile, File tarDir, String name) {
        File tarFile = new File(tarDir, name);
        if (tarFile.exists()) {
            tarFile = getUniqueFile(tarFile, 1);
        }
        try {
            int byteread = 0;
            if (orgFile.exists()) { // 源文件存在时
                InputStream inStream = new FileInputStream(orgFile); // 读入原文件
                FileOutputStream fs = new FileOutputStream(tarFile);
                byte[] buffer = new byte[4096];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 获取唯一名称的文件
     */
    public static File getUniqueFile(File file, int index) {
        String fileName = file.getName();
        if (fileName.indexOf(".") != -1) {
            String fileName_pre = fileName.substring(0, fileName.lastIndexOf("."));
            String fileName_type = fileName.substring(fileName.lastIndexOf("."));
            fileName = fileName_pre + "(" + index + ")" + fileName_type;
        } else {
            fileName = fileName + "(" + index + ")";
        }
        File uniqueFile = new File(file.getParentFile(), fileName);
        if (uniqueFile.exists()) {
            return getUniqueFile(file, (index + 1));
        } else {
            return uniqueFile;
        }
    }
}
