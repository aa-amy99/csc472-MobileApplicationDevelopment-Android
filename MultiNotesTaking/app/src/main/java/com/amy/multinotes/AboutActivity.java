package com.amy.multinotes;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }
    private void hideSystemUI() {
        View myDecorator = getWindow().getDecorView();
        myDecorator.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onWindowFocusChanged(boolean Focus) {
        super.onWindowFocusChanged(Focus);
        if (Focus) { hideSystemUI(); }
    }

    @Override
    public void onBackPressed() { super.onBackPressed(); }


}
