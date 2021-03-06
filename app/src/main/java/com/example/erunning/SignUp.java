package com.example.erunning;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.erunning.Utillity.showToast;

public class SignUp extends BasicActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private int birthyear = 0;
    private int birthmonth = 0;
    private int birthday = 0;
    private ArrayList followerlist = new ArrayList<>();
    private ArrayList followinglist = new ArrayList<>();
    private ArrayList contents = new ArrayList<>();
    private RelativeLayout loaderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        findViewById(R.id.signup_btn).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);
        TextInputLayout lo_password = findViewById(R.id.CreatePassWordLayout);
        TextInputLayout lo_checkpassword = findViewById(R.id.CreatePassWordCheckLayout);

        lo_password.setCounterEnabled(true);
        lo_password.setCounterMaxLength(20);

        lo_checkpassword.setCounterEnabled(true);
        lo_checkpassword.setCounterMaxLength(20);

        initDatePicker();
        dateButton = findViewById(R.id.btn_birthday);
        //dateButton.setText(getTodaysDate());
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(year, month, day);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                birthyear = year;
                birthmonth = month;
                birthday = day;
                String date = makeDateString(year, month, day);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int year, int month, int day) {
        return year + "??? " + getMonthFormat(month) + " " + day + "???";
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "1???";
        if (month == 2)
            return "2???";
        if (month == 3)
            return "3???";
        if (month == 4)
            return "4???";
        if (month == 5)
            return "5???";
        if (month == 6)
            return "6???";
        if (month == 7)
            return "7???";
        if (month == 8)
            return "8???";
        if (month == 9)
            return "9???";
        if (month == 10)
            return "10???";
        if (month == 11)
            return "11???";
        if (month == 12)
            return "12???";

        //default ???
        return "1???";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void createSuccess(String email, String password) {

        FirebaseUser user = mAuth.getCurrentUser();
        profileUpdate();

        Intent intent = new Intent(this, AuthCheck.class);
        intent.putExtra("Authemail", email);
        intent.putExtra("Authpassword", password);
        startActivity(intent);
        finish();
    }

    private void profileUpdate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();

        UserInfo userinfo = new UserInfo(name, birthyear, birthmonth, birthday,followerlist,followinglist,contents);

        db.collection("users").document(user.getUid()).set(userinfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("SignUp_profileUpdate", "???????????? ????????????");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SignUp_profileUpdate", "????????????", e);
                    }
                });
    }

    private void createAccount() {
        String Email = ((TextInputEditText) findViewById(R.id.CreateemailEditText)).getText().toString();
        String Password = ((TextInputEditText) findViewById(R.id.CreatePassWordEditText)).getText().toString();
        String CheckPassWord = ((TextInputEditText) findViewById(R.id.CreatePassWordCheckEditText)).getText().toString();
        String UserName = ((TextInputEditText) findViewById(R.id.nameEditText)).getText().toString();

        String ErrorEmailAlreadyUse = "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.";
        String ErrorEmailNot = "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.";

        // ????????? ?????? ?????? ??????
        if (Email.length() > 0 && Password.length() > 0 && CheckPassWord.length() > 0 && UserName.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            if (Password.length() > 5 && CheckPassWord.length() > 5) {
                if (Password.equals(CheckPassWord)) {
                    if (UserName.length() > 1) {
                        if (birthday != 0 && birthmonth != 0 && birthyear != 0) {
                            mAuth.createUserWithEmailAndPassword(Email, Password)
                                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        mAuth.getCurrentUser().sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            loaderLayout.setVisibility(View.GONE);
                                                                            createSuccess(Email, Password);// ?????? ?????? ??????
                                                                        }
                                                                    }
                                                                });
                                                    } else {
                                                        // ?????????
                                                        loaderLayout.setVisibility(View.GONE);
                                                        String Errormsg = task.getException().toString();
                                                        Log.w(TAG, "????????? ?????? ??????", task.getException());
                                                        if (Errormsg.equals(ErrorEmailAlreadyUse)) {

                                                            showToast(SignUp.this, "?????? ????????? ??????????????????.");
                                                        } else if (Errormsg.equals(ErrorEmailNot)) {
                                                            showToast(SignUp.this, "???????????? ?????? ????????? ???????????????.");
                                                        }
                                                    }
                                                }
                                            }
                                    );
                        } else {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(SignUp.this, "??????????????? ??????????????????.");
                        }
                    } else {
                        loaderLayout.setVisibility(View.GONE);
                        showToast(SignUp.this, "????????? 2????????? ??????????????????.");
                    }
                } else {
                    //???????????? ???????????? ?????? ??????
                    loaderLayout.setVisibility(View.GONE);
                    showToast(SignUp.this, "??????????????? ???????????? ????????????.");
                }
            } else {
                loaderLayout.setVisibility(View.GONE);
                showToast(SignUp.this, "??????????????? ?????? 6??? ?????? ??????????????????.");
            }
        } else {
            loaderLayout.setVisibility(View.GONE);
            showToast(SignUp.this, "????????? ?????? ??????????????????.");
        }

    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.signup_btn:
                    //clearErrorMSG();
                    createAccount();

                    break;
            }
        }


    };

}
