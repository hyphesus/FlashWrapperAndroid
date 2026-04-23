package com.flashwrapper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText urlInput;
    private SharedPreferences prefs;
    private LinearLayout historyContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("flash_prefs", MODE_PRIVATE);
        urlInput = findViewById(R.id.urlInput);
        historyContainer = findViewById(R.id.historyContainer);
        Button launchBtn = findViewById(R.id.launchBtn);

        // Restore last URL
        String lastUrl = prefs.getString("last_url", "");
        if (!TextUtils.isEmpty(lastUrl)) {
            urlInput.setText(lastUrl);
        }

        launchBtn.setOnClickListener(v -> launchGame());
        loadHistory();
    }

    private void launchGame() {
        String url = urlInput.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        saveToHistory(url);
        prefs.edit().putString("last_url", url).apply();

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void saveToHistory(String url) {
        Set<String> history = new LinkedHashSet<>(prefs.getStringSet("history", new LinkedHashSet<>()));
        history.remove(url); // remove duplicate
        history.add(url);    // add to end (most recent)
        // Keep only last 5
        ArrayList<String> list = new ArrayList<>(history);
        if (list.size() > 5) list = new ArrayList<>(list.subList(list.size() - 5, list.size()));
        prefs.edit().putStringSet("history", new LinkedHashSet<>(list)).apply();
        loadHistory();
    }

    private void loadHistory() {
        historyContainer.removeAllViews();
        Set<String> history = prefs.getStringSet("history", new LinkedHashSet<>());
        ArrayList<String> list = new ArrayList<>(history);

        for (int i = list.size() - 1; i >= 0; i--) {
            String url = list.get(i);
            TextView tv = new TextView(this);
            tv.setText("▶  " + url);
            tv.setTextColor(0xFFCCCCCC);
            tv.setTextSize(13f);
            tv.setPadding(0, 18, 0, 18);
            tv.setMaxLines(1);
            tv.setEllipsize(TextUtils.TruncateAt.MIDDLE);

            // Divider line effect
            tv.setBackground(null);
            final String u = url;
            tv.setOnClickListener(v -> {
                urlInput.setText(u);
                launchGame();
            });
            historyContainer.addView(tv);

            // Simple divider
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(0xFF333333);
            historyContainer.addView(divider);
        }
    }
}
