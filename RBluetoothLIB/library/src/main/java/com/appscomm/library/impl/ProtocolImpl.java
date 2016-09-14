package com.appscomm.library.impl;

/**
 * Created by zhaozx on 2016/9/12.
 */
public abstract interface ProtocolImpl {
    public abstract void sendOrder();           //发送指令
    public abstract String paraseData();        //解析返回的数据
}
