package com.flashwrapper;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class GameActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private boolean isLandscape = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        View rotateBtn = findViewById(R.id.rotateBtn);
        View backBtn = findViewById(R.id.backBtn);

        setImmersiveMode();
        setupWebView();

        rotateBtn.setOnClickListener(v -> {
            isLandscape = !isLandscape;
            setRequestedOrientation(isLandscape
                    ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });

        backBtn.setOnClickListener(v -> finish());

        String url = getIntent().getStringExtra("url");
        if (url != null) {
            loadGameWithRuffle(url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowFileAccessFromFileURLs(true);
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
    }

    private void loadGameWithRuffle(String gameUrl) {
        try {
            InputStream is = getAssets().open("ruffle_host.html");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String html = new String(buffer, StandardCharsets.UTF_8);
            html = html.replace("%%GAME_URL%%", gameUrl);

            webView.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "utf-8",
                null
            );
        } catch (IOException e) {
            webView.loadUrl(gameUrl);
        }
    }

    private void setImmersiveMode() {
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setImmersiveMode();
    }
}
