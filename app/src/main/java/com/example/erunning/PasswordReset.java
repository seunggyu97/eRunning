package com.example.erunning;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.erunning.Utillity.showToast;

public class PasswordReset extends BasicActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "PasswordResetActivity";
    private RelativeLayout loaderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.resetPW_btn).setOnClickListener(onClickListener);
    }

    private void resetPW() {
        String Email = ((EditText)findViewById(R.id.ResetEmailEditText)).getText().toString();

        if(Email.length() > 0 ) {
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(Email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                showToast(PasswordReset.this, "등록하신 이메일로 확인메일을 발송했습니다.");
                                finish();
                            }
                        }
                    });
        }

        else{
                showToast(PasswordReset.this, "이메일을 입력해주세요.");

        }


    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.resetPW_btn:
                    resetPW();

                    break;
            }
        }
    };

}
