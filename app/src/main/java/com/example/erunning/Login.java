package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends BasicActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private SignInButton btn_google; // 구글 로그인 버튼
    private GoogleApiClient googleApiClient; // 구글 API 클라이언트 객체
    private static final int REQ_SIGN_GOOGLE = 100; // 구글 로그인 결과 코드
    //private FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.LoginPageSignUp_btn).setOnClickListener(onClickListener);
        findViewById(R.id.LoginResetPW_btn).setOnClickListener(onClickListener);

        String Email = ((TextInputEditText)findViewById(R.id.emailEditText)).getText().toString();
        String Password = ((TextInputEditText)findViewById(R.id.passwordEditText)).getText().toString();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            boolean isEmailverified = mAuth.getCurrentUser().isEmailVerified();
            if(isEmailverified) {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
            else{
                Log.e("인증되지않은","이메일");
            }

        }
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        btn_google = findViewById(R.id.btn_googleLogin);
        btn_google.setOnClickListener(new View.OnClickListener() { // 구글 로그인 버튼을 클릭했을 때 이곳을 수행
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, REQ_SIGN_GOOGLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_SIGN_GOOGLE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) { // 인증결과가 성공적이면
                GoogleSignInAccount account = result.getSignInAccount();// account 라는 데이터는 구글로그인 정보를 담고있음(닉네임, 프로필url,이름, 이메일주소...)
                resultLogin(account);// 로그인 결과 값 출력 수행하라는 메소드
                Log.e("인증결과","성공");

            }
            else{
                Log.e("인증결과","실패");
            }
        }

    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //로그인이 성공했으면
                            Toast.makeText(Login.this, "로그인이 정상적으로 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("nickName", account.getDisplayName());
                            intent.putExtra("photoURL", String.valueOf(account.getPhotoUrl()));
                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else { // 로그인 실패시
                            Toast.makeText(Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                case R.id.LoginResetPW_btn:
                    startActivity(new Intent(Login.this, PasswordReset.class));
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
                                if(mAuth.getCurrentUser()!=null){
                                    boolean isEmailverified = mAuth.getCurrentUser().isEmailVerified();
                                    if(isEmailverified){
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        Toast.makeText(Login.this, "로그인이 정상적으로 되었습니다.",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(Login.this, "로그인 실패 : 인증되지 않은 이메일",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "이메일과 비밀번호를 확인해주세요.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });

        } else {
            Toast.makeText(Login.this, "이메일과 비밀번호를 입력해주세요.",
                    Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
