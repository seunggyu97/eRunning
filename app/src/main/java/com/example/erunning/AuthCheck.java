package com.example.erunning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthCheck extends SignUp {

    private FirebaseAuth mAuth;
    private static final String TAG = "AuthCheck";

    Intent intent;
    TextView verifyEmail;
    String intentEmail ;
    String intentPW ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authcheck);

        findViewById(R.id.btn_authcheck).setOnClickListener(onClickListener);
        verifyEmail = findViewById(R.id.verify_Email);
        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        intentEmail = intent.getStringExtra("Authemail");
        intentPW = intent.getStringExtra("Authpassword");
        verifyEmail.setText(intentEmail);

    }




    private void checkLogin(String email, String pw) {
        mAuth.signInWithEmailAndPassword(email,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    isEmailVerified();
                }
            }
        });

    }

    private void isEmailVerified(){
        if(mAuth.getCurrentUser()!=null){
            boolean isEmailverified = mAuth.getCurrentUser().isEmailVerified();
            if(isEmailverified){
                startActivity(new Intent(AuthCheck.this, Login.class));
                Toast.makeText(AuthCheck.this, "회원가입을 완료했습니다. 로그인 페이지로 이동합니다.",
                        Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();
                finish();

            }
            else{
                Animation error = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                TextView errormsg = findViewById(R.id.errorEditText);
                errormsg.setText("인증 실패 : 이메일에 첨부된 링크를 확인해 주세요.");
                errormsg.startAnimation(error);
                
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {10, 50, 10, 50}; // miliSecond
                //                 대기,진동,대기,진동,....
                // 짝수 인덱스 : 대기시간
                // 홀수 인덱스 : 진동시간
                vibrator.vibrate(pattern, -1);
                // 0 : 무한반복, -1: 반복없음,
                // 양의정수 : 진동패턴배열의 해당 인덱스부터 진동 무한반복

            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btn_authcheck:
                    checkLogin(intentEmail,intentPW);

                    break;
            }
        }
    };

}
