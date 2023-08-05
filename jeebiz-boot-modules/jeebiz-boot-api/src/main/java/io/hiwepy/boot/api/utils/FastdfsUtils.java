/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.utils;

public class FastdfsUtils {

    public static String g_charset = "ISO8859-1";

    /**
     * get token for file URL
     *
     * @param file_id    the file id return by FastDFS server
     * @param ts         unix timestamp, unit: second
     * @param secret_key the secret key
     * @return token string
     */
    public static String getToken(String file_id, long ts, String secret_key) throws Exception {
        byte[] bsFileId = file_id.getBytes(g_charset);
        byte[] bsKey = secret_key.getBytes(g_charset);
        byte[] bsTimestamp = Long.valueOf(ts).toString().getBytes(g_charset);

        byte[] buff = new byte[bsFileId.length + bsKey.length + bsTimestamp.length];
        System.arraycopy(bsFileId, 0, buff, 0, bsFileId.length);
        System.arraycopy(bsKey, 0, buff, bsFileId.length, bsKey.length);
        System.arraycopy(bsTimestamp, 0, buff, bsFileId.length + bsKey.length, bsTimestamp.length);

        return md5(buff);
    }

    /**
     * md5 function
     *
     * @param source the input buffer
     * @return md5 string
     */
    public static String md5(byte[] source) throws java.security.NoSuchAlgorithmException {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        md.update(source);
        byte tmp[] = md.digest();
        char str[] = new char[32];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            str[k++] = hexDigits[tmp[i] >>> 4 & 0xf];
            str[k++] = hexDigits[tmp[i] & 0xf];
        }

        return new String(str);
    }

}
