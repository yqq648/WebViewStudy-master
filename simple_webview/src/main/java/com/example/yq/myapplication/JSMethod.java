package com.example.yq.myapplication;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by yq on 2017/2/24.
 */

public class JSMethod {
    Context context;

    public JSMethod(Context context) {
        this.context = context;
    }
    @android.webkit.JavascriptInterface
    public void toast(String msg){
        Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
    }

}
