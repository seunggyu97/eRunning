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
                Toast.makeText(AuthCheck.this, "??????????????? ??????????????????. ????????? ???????????? ???????????????.",
                        Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();
                finish();

            }
            else{
                Animation error = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                TextView errormsg = findViewById(R.id.errorEditText);
                errormsg.setText("?????? ?????? : ???????????? ????????? ????????? ????????? ?????????.");
                errormsg.startAnimation(error);
                
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {10, 50, 10, 50}; // miliSecond
                //                 ??????,??????,??????,??????,....
                // ?????? ????????? : ????????????
                // ?????? ????????? : ????????????
                vibrator.vibrate(pattern, -1);
                // 0 : ????????????, -1: ????????????,
                // ???????????? : ????????????????????? ?????? ??????????????? ?????? ????????????

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
