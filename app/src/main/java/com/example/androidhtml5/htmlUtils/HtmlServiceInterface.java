package com.example.androidhtml5.htmlUtils;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class HtmlServiceInterface {
    WebViewUtils webViewUtils =null;
    Handler uiHandler=null;
    public HtmlServiceInterface( Handler uiHandler,WebViewUtils webViewUtils) {
        this.webViewUtils =webViewUtils;
        this.uiHandler=uiHandler;
    }

    @JavascriptInterface
    public String toast(String str) {
        String threadName = Thread.currentThread().getName();
        Log.i("线程名称："+threadName+"：toast HTML信息:",str);
        return "我是android信息";
    }
    //页面跳转
    @JavascriptInterface
    public void jumpHtml(String htmlName){
        final String htmlNamet=htmlName;
        String threadName = Thread.currentThread().getName();
        Log.i("线程名称："+threadName+"： jumpHtmlHTML信息:",htmlName);
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                webViewUtils.openHtml(htmlNamet);
            }
        };
        uiHandler.post(runnable);
    }
}
