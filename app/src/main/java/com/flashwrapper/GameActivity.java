package com.flashwrapper;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

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

        // Enable fullscreen immersive
        setImmersiveMode();

        // WebView settings
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                    // Inject Ruffle after page loads
                    injectRuffle();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                injectRuffle();
            }
        });

        // Rotate button
        rotateBtn.setOnClickListener(v -> {
            isLandscape = !isLandscape;
            setRequestedOrientation(isLandscape
                    ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        });

        backBtn.setOnClickListener(v -> finish());

        // Load the URL
        String url = getIntent().getStringExtra("url");
        if (url != null) {
            webView.loadUrl(url);
        }
    }

    private void injectRuffle() {
        // Inject Ruffle from CDN and activate it on all Flash objects on the page
        String js = 
            "(function() {" +
            "  if (window.__ruffleInjected) return;" +
            "  window.__ruffleInjected = true;" +
            "  var script = document.createElement('script');" +
            "  script.src = 'https://unpkg.com/@ruffle-rs/ruffle';" +
            "  script.onload = function() {" +
            "    console.log('Ruffle loaded');" +
            "  };" +
            "  document.head.appendChild(script);" +
            "})();";
        webView.evaluateJavascript(js, null);
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
