package com.example.androidhtml5;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidhtml5.htmlUtils.WebViewUtils;
import com.example.androidhtml5.htmlUtils.HtmlServiceInterface;

public class MainActivity extends AppCompatActivity {

    //在主线程创建 handler绑定在主线程
    //使用Handler 执行需要更新UI的任务 Runnable 使用Handler.post(Runnable) 即可
    private Handler uiHandler = new Handler();



    private WebView webview;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*String threadName = Thread.currentThread().getName();
        Log.i("线程名称："+threadName,"  主UI信息");
        WebView webview = (WebView) findViewById(R.id.myWebView);
        WebViewUtils androidWebView = new WebViewUtils(webview);

        webview.addJavascriptInterface(new HtmlServiceInterface(uiHandler,androidWebView), "android");
        androidWebView.openHtml("index");
*/
        //实现图片上传
        // 实例化WebView对象
        webview = new WebView(this);
        webview.setWebViewClient(new MyWebViewClinet());
        webview.setWebChromeClient(new MyChromeViewClient());
        //设置 缓存模式
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webview.getSettings().setDomStorageEnabled(true);
        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(true);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);  //开启DOM
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码
        // // web页面处理
        webSettings.setAllowFileAccess(true);// 支持文件流
        webSettings.setUseWideViewPort(true);// 调整到适合webview大小
        webSettings.setLoadWithOverviewMode(true);//调整到适合webview大小
        webSettings.setAppCacheEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
//		webSettings.setBlockNetworkImage(true);

        WebViewUtils androidWebView = new WebViewUtils(webview,"hi");
        webview.addJavascriptInterface(new HtmlServiceInterface(uiHandler,androidWebView), "android");
        androidWebView.openHtml("index");
        setContentView(webview);


    }





    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return false;
    }

    // Web视图
    private class MyWebViewClinet extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class MyChromeViewClient extends WebChromeClient {
        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        // For Android 3.0+
        public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
        }
        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult( Intent.createChooser( i, "File Browser" ), MainActivity.FILECHOOSER_RESULTCODE );
        }
        // For Android 5.0+
        public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            mUploadCallbackAboveL = filePathCallback;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            MainActivity.this.startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE);
            return true;
        }

    }
    /**
     *新打开的activity 关闭后 传输数据到此activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
            else  if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {

            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }
}
