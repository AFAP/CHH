package com.afap.discuz.chh.utils;

import java.text.DecimalFormat;


public class FileUtil {
    /**
     * 格式化文件大小
     */
    public static String FormatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String s;
        if (fileSize < 0) {
            s = "0 B";
        } else if (fileSize < 1024) {
            s = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            s = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            s = df.format((double) fileSize / 1048576) + "M";
        } else {
            s = df.format((double) fileSize / 1073741824) + "G";
        }
        return s;
    }
}
