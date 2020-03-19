package com.example.digitalgarden.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.digitalgarden.R;

public class SplashActivity extends AppCompatActivity {
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.appName);
        Typeface typeface = ResourcesCompat.getFont(this,R.font.sweet_leaf);
        appName.setTypeface(typeface);
        Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.text_fade_in);
        appName.startAnimation(fadeIn);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();

            }
        }.start();
    }
}
