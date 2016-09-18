package com.appscomm.library.globle;

/**
 * Created by zhaozx on 2016/9/18.
 * 给蓝牙设备的指令
 */
public class Commands {
    /*-----------------------------------------------开始结束标志-------------------------------------------------*/
    public static byte FLAG_START = (byte) 0x6F;                                    // 开始标志
    public static byte FLAG_END = (byte) 0x8F;                                      // 结束标志
    /*-----------------------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------动作-----------------------------------------------------*/
    public static byte ACTION_CHECK = (byte) 0x70;                                  // 查询
    public static byte ACTION_SET = (byte) 0x71;                                    // 设置
    public static byte ACTION_CHECK_RESPONSE = (byte) 0x80;                         // 查询响应
    public static byte ACTION_SET_RESPONSE = (byte) 0x81;                           // 设置响应
    /*-----------------------------------------------------------------------------------------------------------*/

    /*-----------------------------------------------具体的命令-------------------------------------------------*/
    public static byte COMMANDCODE_RESPONSE = (byte) 0x01;                          // 响应（设置指令的返回码）
    public static byte COMMANDCODE_WATCHID = (byte) 0x02;                           // watchID
    public static byte COMMANDCODE_DEVICE_VERSION = (byte) 0x03;                    // 设备版本
    public static byte COMMANDCODE_DATE_TIME = (byte) 0x04;                         // 日期时间
    /*-----------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------返回值---------------------------------------------------*/
    public static final int RESULTCODE_SUCCESS = 0;                                 // 成功
    public static final int RESULTCODE_FAILD = 1;                                   // 失败
    public static final int RESULTCODE_PROTOCOL_ERROR = 2;                          // 协议解析错误
    /*-----------------------------------------------------------------------------------------------------------*/
}
