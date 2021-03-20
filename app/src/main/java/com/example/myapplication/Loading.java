package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Loading extends AppCompatActivity {
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(Loading.this, MainActivity.class));
                finish();
            }
        };
        //3sec
        handler.postDelayed(runnable, 3000);

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}