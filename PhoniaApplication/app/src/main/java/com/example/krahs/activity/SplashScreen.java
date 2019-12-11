package com.example.krahs.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.krahs.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private ImageView logo;
    private TextView app_name;
    private Animation animation;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logo = findViewById(R.id.logo);
        app_name = findViewById(R.id.app_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        logo.startAnimation(animation);
        app_name.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (firebaseUser != null){
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        },SPLASH_DISPLAY_LENGTH);
    }
}
