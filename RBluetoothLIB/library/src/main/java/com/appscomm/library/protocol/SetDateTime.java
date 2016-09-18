package com.appscomm.library.protocol;

import com.appscomm.library.globle.Commands;
import com.appscomm.library.protocol.parent.Leaf;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.NumberUtils;

/**
 * Created by zhaozx on 2016/9/18.
 */
public class SetDateTime extends Leaf {
    private int year;     //年
    private int month;    //月
    private int day;      //日
    private int hour;     //时
    private int minute;   //分
    private int second;   //秒
    /**
     *
     * @param year      年
     * @param month     月
     * @param day       日
     * @param hour      时
     * @param minute    分
     * @param second    秒
     */
    public SetDateTime(int year,int month,int day,int hour,int minute,int second){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    @Override
    public void send(BluetoothService bluetoothService) {
        byte []bytes = new byte[13];                                // 总字节长度 = 1(开始标识符) + 1(命令码) + 1(动作) + 2(内容长度) + N(命令内容) + 1(结束标识符)
        byte[] contentLen = NumberUtils.intToByteArray(7, 2);        // 处理内容长度
        bytes[0] = Commands.FLAG_START;                                   // 开始标识符
        bytes[1] = Commands.COMMANDCODE_DATE_TIME;                                           // 命令码
        bytes[2] = Commands.ACTION_SET;                                                // 动作
        bytes[12] = Commands.FLAG_END;                              // 结束标识符
        System.arraycopy(contentLen, 0, bytes, 3, 2);                     // 内容长度

        //内容
        byte[] bYear = NumberUtils.intToByteArray(year, 2);
        byte[] bMonth = NumberUtils.intToByteArray(month, 1);
        byte[] bDay = NumberUtils.intToByteArray(day, 1);
        byte[] bHour = NumberUtils.intToByteArray(hour, 1);
        byte[] bMin = NumberUtils.intToByteArray(minute, 1);
        byte[] bSec = NumberUtils.intToByteArray(second, 1);
        System.arraycopy(bYear, 0, bytes, 0+5, 2);
        System.arraycopy(bMonth, 0, bytes, 2+5, 1);
        System.arraycopy(bDay, 0, bytes, 3+5, 1);
        System.arraycopy(bHour, 0, bytes, 4+5, 1);
        System.arraycopy(bMin, 0, bytes, 5+5, 1);
        System.arraycopy(bSec, 0, bytes, 6+5, 1);
        bluetoothService.sendOrder2Device(bytes);
    }

    @Override
    public String parse(byte[] abyte) {
        if(abyte[5] == Commands.COMMANDCODE_DATE_TIME && abyte[6] == Commands.RESULTCODE_SUCCESS){

            return "success";
        }
        return "error";
    }
}
