package com.example.phamngoctuan.euro2016;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class activity_webview extends AppCompatActivity {
    WebView _webview;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        _webview = (WebView) findViewById(R.id.webview);
        Intent intent = null;
        intent = getIntent();
        if (intent != null)
        {
            String _link = intent.getStringExtra("link");
            if (_link != null)
            {
                _webview.getSettings().setUserAgentString("Android");
                _webview.getSettings().setJavaScriptEnabled(true);
                _webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                _webview.setWebViewClient(new WebViewClient());
                _webview.loadUrl(_link);
            }
        }
    }
}
