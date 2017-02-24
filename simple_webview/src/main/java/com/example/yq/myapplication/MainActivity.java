package com.example.yq.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public WebView webView;
    public ProgressBar progressBar;
    MyWebChromeClient mWebChromeClient;
//    void a(){
//        this.onCreate();//父类的方法
//        onCreate();
//    }
    //可以切换到全屏功能
    String movieFullUrl = "http://player.youku.com/embed/XMTMxOTk1ODI4OA";
    //视频播放功能
    String movieUrl = "http://www.tudou.com/albumplay/eu0K8vLTD48/aHeFLTBfzU0.html";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        initWebView();
        //TODO 添加网页的url 电脑浏览器查看任意文件
        webView.loadUrl("file:///android_asset/temp.html");
    }






    private void initWebView() {
//        mProgressBar.setVisibility(View.VISIBLE);
        WebSettings ws = webView.getSettings();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        // 缩放比例 1
        webView.setInitialScale(1);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开 h5
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        ws.setTextZoom(100);
        //TODO 添加网页的ChromeClient 浏览器上传文件，进度条，视频全屏等设置
        mWebChromeClient = new MyWebChromeClient(this,progressBar);
        webView.setWebChromeClient(mWebChromeClient);
        //TODO 添加网页的WebViewClient 与js交互
        webView.addJavascriptInterface(new JSMethod(this), "anzhuo");
        webView.setWebViewClient(new MyWebViewClient(this));
    }

    /**
     * TODO 上传图片之后的回调 onActivityResult
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * TODO 全屏时按返加键执行退出全屏方法
     */


    @Override//选择文件之后， 会传递到这里接受文件信息
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Toast.makeText(this, "选择图片成功", Toast.LENGTH_SHORT).show();

        if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE) {
            mWebChromeClient.mUploadMessage(intent, resultCode);
        } else if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient.mUploadMessageForAndroid5(intent, resultCode);
        }
    }

    //webview比较庞大，一般需要在生命周期的时候做控制-百度地图，
    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView.resumeTimers();
        // 设置为横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    /**
     * 最后可以销毁庞大的WebView
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        videoFullView.removeAllViews();
        if (webView != null) {
//            ViewGroup parent = (ViewGroup) webView.getParent();
//            if (parent != null) {
//                parent.removeView(webView);
//            }
            webView.removeAllViews();
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);//video
            webView.setWebViewClient(null);//接受浏览器地址
            webView.destroy();
            webView = null;
        }
    }
}
