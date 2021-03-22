package com.example.erunning;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.LoginPageSignUp_btn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:
                    Log.e("클릭","클릭");
                    break;
                case R.id.LoginPageSignUp_btn:
                    Log.e("회원가입 클릭","회원가입 클릭");
                    break;
            }
        }
    };
}
