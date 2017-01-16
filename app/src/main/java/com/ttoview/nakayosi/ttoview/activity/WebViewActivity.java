package com.ttoview.nakayosi.ttoview.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.ttoview.nakayosi.ttoview.BaseActivity;
import com.ttoview.nakayosi.ttoview.R;
import com.ttoview.nakayosi.ttoview.dialog.SelectorDialog;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RunnableScheduledFuture;

public class WebViewActivity extends BaseActivity {
    Context mContext = this;
    WebView 凹;
    private WebSettings webViewSetting;
    private EditText url_text;
    private ImageView go_url, url_back, do_write, goHome;
    private HashMap<String, String> 凸;
    private int running;

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);
        凸 = new HashMap<>();
        凸.put("naver", "http://m.blog.naver.com/");
        凸.put("naver/FeedList.nhn", "http://m.blog.naver.com/FeedList.nhn");
        凸.put("/Recommendation.nhn", "http://m.blog.naver.com/Recommendation.nhn");
        凸.put("facebook", "https://www.facebook.com/");
        凸.put("facebook/?_rdr", "https://www.facebook.com/?_rdr");
        凸.put("kakao", "https://story.kakao.com/");
        凸.put("kakao/browse", "https://story.kakao.com/browse");

        凹 = (WebView) findViewById(R.id.webview);
        url_text = (EditText) findViewById(R.id.url_text);
        go_url = (ImageView) findViewById(R.id.url_go);
        url_back = (ImageView) findViewById(R.id.url_back);
        do_write = (ImageView) findViewById(R.id.do_write);
        goHome = (ImageView) findViewById(R.id.goHome);

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        });

        凹.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test2", "클릭함");
            }
        });
        凹.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final String currentURL = 凹.getUrl();
                url_text.setText(currentURL);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("test2", "딜레이1");
                        if (!currentURL.equals(凹.getUrl())) {
                            Log.d("test2", "딜레이1 교체");
                            url_text.setText(凹.getUrl());
                        }
                    }
                }, 500);
                return false;
            }
        });
        凹.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test2","클릭함1");
            }
        });


        凹.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                running = Math.max(running, 1); // First request move it to 1.
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                running++;
                凹.loadUrl(urlNewString);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (--running == 0) { // just "running--;" if you add a timer.
                    // TODO: finished... if you want to fire a method.
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("test2", "딜레이 최초");
                            url_text.setText(凹.getUrl());
                        }
                    }, 500);
                }
            }
        }); // 클릭시 새창 방지
        webViewSetting = 凹.getSettings();
        webViewSetting.setJavaScriptEnabled(true);

        final String platform = getIntent().getStringExtra("platform").toString();
        Log.d("test2", platform);
        String URL = null;

        switch (platform) {
            case "naver":
                URL = "http://m.blog.naver.com/";
                break;
            case "facebook":
                URL = "https://www.facebook.com/";
                break;
            case "kakao":
                URL = "https://story.kakao.com/";
                break;
        }
        凹.loadUrl(URL);


        go_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                凹.loadUrl(url_text.getText().toString());
            }
        });

        url_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                凹.goBack();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("test2", "딜레이 최초");
                        url_text.setText(凹.getUrl());
                    }
                }, 500);
            }
        });

        do_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_text.setText(凹.getUrl());
                if (凸.containsValue(凹.getUrl())) {
                    Toast.makeText(getApplicationContext(), "이 주소는 기록 할 수 없습니다.\n게시글을 클릭 한 후 기록을 다시 시도하세요.", Toast.LENGTH_LONG).show();


                } else {
                    Intent intent = new Intent(mContext, WriteActivity2.class);
                    intent.putExtra("url", 凹.getUrl());
                    startActivity(intent);
                }
            }
        });
    }


}
