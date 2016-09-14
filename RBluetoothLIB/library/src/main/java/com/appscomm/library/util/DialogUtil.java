package com.appscomm.library.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by zhaozx on 2016/9/7.
 */
public class DialogUtil {

    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context, ProgressDialog progressDialog,String content){
        mProgressDialog = progressDialog;
        if(progressDialog != null && !progressDialog.isShowing()){
            progressDialog.setMessage(content);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
    }

    public static void hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }
}
