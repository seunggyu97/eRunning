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

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.signup_btn).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    private void createSuccess(){
        Log.d(TAG, "이메일 생성 성공");
        FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(SignUp.this, "회원가입을 완료했습니다.",
                Toast.LENGTH_SHORT).show();
        finish();
    }
    private void createAccount() {
        String Email = ((EditText)findViewById(R.id.CreateemailEditText)).getText().toString();
        String Password = ((EditText)findViewById(R.id.CreatePassWordEditText)).getText().toString();
        String CheckPassWord = ((EditText)findViewById(R.id.CreatePassWordCheckEditText)).getText().toString();
        String ErrorEmailAlreadyUse = "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.";
        String ErrorEmailNot = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";

        // 이메일 계정 생성 시작
        if(Email.length() > 0 && Password.length() > 0 && CheckPassWord.length() > 0){
            if(Password.length() > 5 && CheckPassWord.length() > 5){
                if(Password.equals(CheckPassWord)) {
                    mAuth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                createSuccess();// 생성 성공 메소드 호출

                                            } else {
                                                // 실패시
                                                String Errormsg = task.getException().toString();
                                                Log.w(TAG, "이메일 생성 실패", task.getException());
                                                if(Errormsg.equals(ErrorEmailAlreadyUse)){
                                                    Toast.makeText(SignUp.this, "이미 가입된 이메일입니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                                else if(Errormsg.equals(ErrorEmailNot)){
                                                    Toast.makeText(SignUp.this, "올바르지 않은 이메일 형식입니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                            );
                }
                else{
                    //비밀번호 일치하지 않는 경우
                    Toast.makeText(SignUp.this, "비밀번호가 일치하지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(SignUp.this, "비밀번호는 최소 6자 이상 입력해주세요.",
                        Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(SignUp.this, "이메일과 비밀번호를 모두 입력해주세요.",
                    Toast.LENGTH_SHORT).show();
        }


    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.signup_btn:
                    createAccount();

                    break;
            }
        }
    };

}