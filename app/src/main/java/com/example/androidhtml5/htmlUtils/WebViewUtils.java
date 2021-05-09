package com.example.androidhtml5.htmlUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.androidhtml5.MainActivity;

//WebView 操作HTML对象
public class WebViewUtils {
    WebView webView=null;
    public WebViewUtils(WebView webView) {
        this.webView=webView;
        baseSet();
        setDefaultBroswer();
        openHtmlMethods();
    }

    public WebViewUtils(WebView webView,String name) {
        this.webView=webView;
    }


    //基本设置  支持中文 JavaScript
    public void  baseSet(){
        // 得到设置属性的对象
        WebSettings webSettings = webView.getSettings();
        // 使能JavaScript
        webSettings.setJavaScriptEnabled(true);
        // 支持中文，否则页面中中文显示乱码
        webSettings.setDefaultTextEncodingName("UTF-8");
        //设置此属性，可任意比例缩放
        webSettings.setUseWideViewPort(true);
    }
    //使用安卓默认浏览器打开网页
    public void  setDefaultBroswer(){
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        Log.i("访问A：",url);
                        view.loadUrl(url);
                    }
                    return true;
                } catch (Exception e){
                    return false;
                }
            }
        });
    }
    //开启HTML5 alert confirm 方法
    public void  openHtmlMethods(){
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("")
                        .setMessage(message)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {

                final EditText et = new EditText(view.getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("")
                        .setMessage(message)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm(et.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                return true;
            }
        });
    }
    //打开HTML文件
    public void openHtml(String htmlName){
        //assets 和res，java 在同一级，存放HTML文件 css js
        webView.loadUrl("file:///android_asset/html/"+htmlName+".html");
    }
}
