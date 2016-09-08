package com.appscomm.library.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by zhaozx on 2016/9/7.
 */
public class DialogUtil {
    public static void showProgressDialog(Context context, ProgressDialog progressDialog,String content){
        if(progressDialog != null && !progressDialog.isShowing()){
            progressDialog.setMessage(content);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public static void hideProgressDialog(ProgressDialog progressDialog){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
