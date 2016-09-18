package com.appscomm.library.util;

/**
 * Created by zhaozx on 2016/9/8.
 */
public class NumberUtils {
    private static String hexStr = "0123456789ABCDEF";
    /**
     * @param bytes
     * @return 将二进制转换为十六进制字符输出
     * new byte[]{0b01111111}-->"7F" ;  new byte[]{0x2F}-->"2F"
     */
    public static String binaryToHexString(byte[] bytes) {
        String result = "";
        String hex = "";
        for (int i = 0; i < bytes.length; i++) {
            //字节高4位
            hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
            //字节低4位
            hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
            result += hex + "";
        }
        return result;
    }

    // 2进制的byte[]高低位置换数组转int类型
    public static int byteReverseToInt(byte[] b) {

        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = b.length - 1; i > -1; i--) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    /**
     * int转换到byte[]
     *
     * @param integer  需要转换的int
     * @param byteSize byte[]数组的大小
     * @return 转换后的byte[]
     */
    public static byte[] intToByteArray(final int integer, int byteSize) {
        byte[] bytes = new byte[byteSize];
        for (int i = 0; i < byteSize; i++) {
            bytes[i] = (byte) ((integer >> (8 * i)) & 0xFF);
        }
        return bytes;
    }

    /**
     * 解析日期时间 0~1:年 2:月 3:日 4:时 5:分 6:秒
     *
     * @param bytes
     * @return
     */
    public static String getDateTime(byte[] bytes, int start) {
        int year, month, day, hour, min, sec;
        year = (int) bytesToLong(bytes, start, start + 1);
        month = (int) (bytes[start + 2] & 0xff);
        day = (int) (bytes[start + 3] & 0xff);
        hour = (int) (bytes[start + 4] & 0xff);
        min = (int) (bytes[start + 5] & 0xff);
        sec = (int) (bytes[start + 6] & 0xff);
        return year + "-" + add0(month) + "-" + add0(day) + " " + add0(hour) + ":" + add0(min) + ":" + add0(sec);
    }

    /**
     * 当i<10的时候在前面加上0
     * @param i
     * @return
     */
    public static String add0(int i){
        return i<10?"0"+i:""+i;
    }

    /**
     * bytes转换为long，原理如下:
     * sum = (long) (bytes[0] & 0xff) + (long) ((bytes[1] & 0xff) << 8) +
     * (long) ((bytes[2] & 0xff) << 16) + (long) ((bytes[3] & 0xff) << 24) +
     * (long) ((bytes[4] & 0xff) << 32) + (long) ((bytes[5] & 0xff) << 40)……
     *
     * @param bytes 需要转换的bytes
     * @param start 开始索引
     * @param end   结束索引(包含)
     * @return 转换后的long
     */
    public static long bytesToLong(byte[] bytes, int start, int end) {
        if (start > end) {
            return -1;
        }
        long sum = 0;
        for (int i = start, bit = 0; i < end + 1; i++, bit += 8) {
            long temp = (long) (bytes[i] & 0xff) << bit;
            sum += temp;
        }
        return sum;
    }


}
