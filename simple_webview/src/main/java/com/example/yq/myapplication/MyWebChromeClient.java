package com.example.yq.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yq on 2017/2/23.
 * 控制进度条、图片上传、全屏、...
 * 播放网络视频配置
 */

public class MyWebChromeClient extends WebChromeClient {
    private MainActivity activity;
    private ProgressBar progressBar;
    private WebView webView;
    public MyWebChromeClient(Context context,
                             ProgressBar progressBar) {
        this.activity = (MainActivity) context;
        this.progressBar = progressBar;
        this.webView = activity.webView;
    }

    @Override//进度条改变
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress==100){
            progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }
    View fullView;
    CustomViewCallback customViewCallback;

    /**
     * 当webview被点击全屏的时候，调用此方法
     * @param view  全屏视图
     * @param callback
     */
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        //请求屏幕切换,LANDSCAPE横屏
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (fullView!=null){
            callback.onCustomViewHidden();
            return;
        }
        fullViewAddView(view);
        fullView = view;
        customViewCallback = callback;
        showVideoFullView();
    }

    @Override//退出全屏
    public void onHideCustomView() {
        if (fullView==null){return;}//不是全屏状态
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fullView.setVisibility(View.GONE);
        if (getVideoFullView()!=null){
            getVideoFullView().removeView(fullView);
        }
        fullView = null;
        hindVideoFullView();
        customViewCallback.onCustomViewHidden();
        showWebView();
    }
    //全屏处理
    FullscreenHolder videoFullView;
    public void fullViewAddView(View view) {
        FrameLayout decor = (FrameLayout) activity.getWindow().getDecorView();
        videoFullView = new FullscreenHolder(activity);
        videoFullView.addView(view);
        decor.addView(videoFullView);
    }
    public void showVideoFullView() {
        videoFullView.setVisibility(View.VISIBLE);
    }
    public void hindVideoFullView() {
        videoFullView.setVisibility(View.GONE);
    }
    public FrameLayout getVideoFullView() {
        return videoFullView;
    }
    //改变WebView的显示
    public void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }
    public void hindWebView() {
        webView.setVisibility(View.INVISIBLE);
    }

    /**
     * 判断是否是全屏
     */
    public boolean inCustomView() {
        return (fullView != null);
    }

    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        activity.setTitle(title);
        super.onReceivedTitle(view, title);
    }


    /***
     * 图片上传
     */
    //扩展浏览器上传文件
    //3.0++版本
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        openFileChooserImpl(uploadMsg);
    }

    //3.0--版本
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooserImpl(uploadMsg);
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooserImpl(uploadMsg);
    }

    // For Android > 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
        openFileChooserImplForAndroid5(uploadMsg);
        return true;
    }
    ValueCallback<Uri> mUploadMessage;
    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(i, "文件选择"), FILECHOOSER_RESULTCODE);
    }

    ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5;
    public static int FILECHOOSER_RESULTCODE = 1;
    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "图片选择");

        activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    /**
     * 5.0以下 上传图片成功后的回调
     */
    public void mUploadMessage(Intent intent, int resultCode) {
        if (null == mUploadMessage)
            return;
        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
    }

    /**
     * 5.0以上 上传图片成功后的回调
     */
    public void mUploadMessageForAndroid5(Intent intent, int resultCode) {
        if (null == mUploadMessageForAndroid5)
            return;
        Toast.makeText(activity, "上传图片成功", Toast.LENGTH_SHORT).show();
        Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
        if (result != null) {
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
        } else {
            mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
        }
        mUploadMessageForAndroid5 = null;
    }
}
