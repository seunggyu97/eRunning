package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.LoginPageSignUp_btn).setOnClickListener(onClickListener);
        mAuth = FirebaseAuth.getInstance();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.login_btn:
                    LoginAccount();
                    break;
                case R.id.LoginPageSignUp_btn:
                    startActivity(new Intent(Login.this, SignUp.class));
                    break;
            }
        }
    };

    private void LoginAccount() {
        String Email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String Password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();

        // 이메일 계정 생성 시작
        if (Email.length() > 0 && Password.length() > 0) {

            mAuth.signInWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Login.this, MainActivity.class));
                                Log.e("클릭","클릭");
                                Toast.makeText(Login.this, "로그인이 정상적으로 되었습니다.",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "이메일과 비밀번호를 확인해주세요.",
                                        Toast.LENGTH_SHORT).show();
                                // ...
                            }

                            // ...
                        }
                    });

        } else {
            Toast.makeText(Login.this, "이메일과 비밀번호를 입력해주세요.",
                    Toast.LENGTH_SHORT).show();

        }
    }
}
