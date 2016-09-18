package com.appscomm.library.protocol.parent;

import com.appscomm.library.globle.Commands;
import com.appscomm.library.service.BluetoothService;
import com.appscomm.library.util.NumberUtils;

/**
 * Created by zhaozx on 2016/9/13.
 */
public abstract class Leaf {

    //发送数据
    public abstract void send(BluetoothService bluetoothService);
    //解析数据
    public abstract String parse(byte abyte[]);

    /**
     * 发送的协议
     * @param length        内容的长度
     * @param content       内容
     * @param commandCode   命令码
     * @param action        动作
     * @return
     */
    public byte[] command(int length, int content,byte commandCode,byte action){
        byte []bytes = new byte[length+6];                                // 总字节长度 = 1(开始标识符) + 1(命令码) + 1(动作) + 2(内容长度) + N(命令内容) + 1(结束标识符)
        byte[] contentLen = NumberUtils.intToByteArray(length, 2);        // 处理内容长度
        byte[] contents = NumberUtils.intToByteArray(content, length);    // 处理内容
        bytes[0] = Commands.FLAG_START;                                   // 开始标识符
        bytes[1] = commandCode;                                           // 命令码
        bytes[2] = action;                                                // 动作
        bytes[length+5] = Commands.FLAG_END;                              // 结束标识符
        System.arraycopy(contentLen, 0, bytes, 3, 2);                     // 内容长度
        System.arraycopy(contents, 0, bytes, 5, length);                  // 内容
        return bytes;
    }

}
