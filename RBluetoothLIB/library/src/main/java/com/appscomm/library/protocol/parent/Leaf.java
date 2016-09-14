package com.appscomm.library.protocol.parent;

import com.appscomm.library.service.BluetoothService;

/**
 * Created by zhaozx on 2016/9/13.
 */
public abstract class Leaf {

    //发送数据
    public abstract void send(BluetoothService bluetoothService);
    //解析数据
    public abstract String parse(byte abyte[]);

}
