package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Loading extends BasicActivity {
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        handler = new Handler();
        runnable = new Runnable() {

            @Override
            /*----로딩화면 종료시 실행할 액티비티*/
            public void run() {
                startActivity(new Intent(Loading.this, Login.class));
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