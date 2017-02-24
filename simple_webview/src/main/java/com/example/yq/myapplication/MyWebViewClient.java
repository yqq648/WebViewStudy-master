package com.example.yq.myapplication;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by yq on 2017/2/23.
 * 管理浏览器地址跳转，
 */
public class MyWebViewClient extends WebViewClient {
    private MainActivity activity;
    public MyWebViewClient(Context context) {
        this.activity = (MainActivity) context;
    }
    @SuppressWarnings("deprecation")
    @Override//TODO 监听URL地址
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 电话、短信、邮箱
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            Toast.makeText(activity, ""+url, Toast.LENGTH_SHORT).show();
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException ignored) {}
        }
        view.loadUrl(url);
        return super.shouldOverrideUrlLoading(view, url);
    }

    //TODO  视频全屏播放按返回页面被放大的问题
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (newScale - oldScale > 7) {
            view.setInitialScale((int) (oldScale / newScale * 100)); //异常放大，缩回去。
        }
    }
}