package com.example.erunning;

import android.os.Bundle;
import android.view.View;

public class UserSearch extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        findViewById(R.id.btn_writingback).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.btn_writingback:
                finish();
                break;
        }
    };
}