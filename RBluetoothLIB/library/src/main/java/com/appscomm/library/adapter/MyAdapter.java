package com.appscomm.library.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

/**
 * Created by zhaozx on 2016/9/2.
 * ListView通用适配器
 */
public abstract class MyAdapter<T> extends BaseAdapter{
    private List<T> data;
    private Context context;
    public MyAdapter(Context context,List<T> data){
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public abstract View getView(int i, View view, ViewGroup viewGroup);

}
