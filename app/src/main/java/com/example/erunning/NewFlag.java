package com.example.erunning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.erunning.MainActivity.REQUEST_FLAG_NEW_MAP;
import static com.example.erunning.Utillity.showToast;

public class NewFlag extends BasicActivity implements TimePickerDialog.OnTimeSetListener{
    private FirebaseUser user;
    private static final String TAG = "NewFlag";
    private RelativeLayout buttonsBackgroundLayout;
    private EditText contentsEditText;
    private FlagInfo flagInfo;
    private Utillity util;
    private RelativeLayout loaderLayout;
    private TextView btn_setstarttime;
    private TextView btn_setmax;
    private TextView btn_setlocation;
    private TextView flag_selected;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String starthour;
    private String startminute;
    private String description;
    private String address;

    private ImageButton flag_run;
    private ImageButton flag_cycle;
    private ImageButton flag_fitness;
    private ImageButton flag_soccer;
    private ImageButton flag_baseball;
    private ImageButton flag_basketball;
    private ImageButton flag_tennis;
    private ImageButton flag_tabletennis;
    private Button flag_etc;

    private int selectedcode = 0;
    // 1: 런닝 2: 싸이클 3: 실내운동 4: 축구 5: 야구 6: 농구 7: 탁구 8: 테니스 9: 직접입력
    private int flag_max = 0;
    private LatLng latLng;


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int max, int dayOfMonth){
            flag_max = max;

            btn_setmax.setTypeface(btn_setmax.getTypeface(), Typeface.BOLD);
            btn_setmax.setText(flag_max + "명");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newflag);

        loaderLayout = findViewById(R.id.loaderLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        contentsEditText = findViewById(R.id.et_writing);
        btn_setstarttime = findViewById(R.id.btn_setstarttime);
        btn_setmax = findViewById(R.id.btn_setmax);
        btn_setlocation = findViewById(R.id.btn_setlocation);
        flag_selected = findViewById(R.id.flag_selected);

        btn_setstarttime.setOnClickListener(onClickListener);
        btn_setmax.setOnClickListener(onClickListener);
        btn_setlocation.setOnClickListener(onClickListener);
        buttonsBackgroundLayout.setOnClickListener(onClickListener);

        flag_run = findViewById(R.id.flag_run);
        flag_baseball = findViewById(R.id.flag_baseball);
        flag_cycle = findViewById(R.id.flag_cycle);
        flag_basketball = findViewById(R.id.flag_basketball);
        flag_soccer = findViewById(R.id.flag_soccer);
        flag_tennis = findViewById(R.id.flag_tennis);
        flag_tabletennis = findViewById(R.id.flag_tabletennis);
        flag_fitness = findViewById(R.id.flag_fitness);
        flag_etc = findViewById(R.id.flag_etc);

        flag_run.setOnClickListener(onClickListener);
        flag_baseball.setOnClickListener(onClickListener);
        flag_cycle.setOnClickListener(onClickListener);
        flag_basketball.setOnClickListener(onClickListener);
        flag_soccer.setOnClickListener(onClickListener);
        flag_tennis.setOnClickListener(onClickListener);
        flag_tabletennis.setOnClickListener(onClickListener);
        flag_fitness.setOnClickListener(onClickListener);
        flag_etc.setOnClickListener(onClickListener);

        flag_run.setBackgroundColor(R.drawable.collectbutton);
        flag_baseball.setBackgroundColor(R.drawable.collectbutton);
        flag_cycle.setBackgroundColor(R.drawable.collectbutton);
        flag_basketball.setBackgroundColor(R.drawable.collectbutton);
        flag_soccer.setBackgroundColor(R.drawable.collectbutton);
        flag_tennis.setBackgroundColor(R.drawable.collectbutton);
        flag_tabletennis.setBackgroundColor(R.drawable.collectbutton);
        flag_fitness.setBackgroundColor(R.drawable.collectbutton);

        findViewById(R.id.btn_addpost).setOnClickListener(onClickListener);
        findViewById(R.id.btn_writingback).setOnClickListener(onClickListener);

        flagInfo = (FlagInfo) getIntent().getSerializableExtra("flagInfo");
        postInit();
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.btn_addpost:
                FlagUpload();
                break;
            case R.id.btn_writingback:
                finish();
                break;
            case R.id.buttonsBackgroundLayout:
                if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_setstarttime:
                Log.e("btn_setstarttime","클릭");
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;
            case R.id.btn_setlocation:
                Intent intent = new Intent(this,FlagNewmap.class);
                startActivityForResult(intent,REQUEST_FLAG_NEW_MAP);
                break;
            case R.id.btn_setmax:
                SetMaxDialog pd = new SetMaxDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
                break;

            // 1: 런닝 2: 싸이클 3: 실내운동 4: 축구 5: 야구 6: 농구 7: 탁구 8: 테니스 9: 직접입력
            case R.id.flag_run:
                setSelectedButton(1);
                flag_run.setBackgroundColor(R.drawable.selectedbutton);
                flag_run.setImageResource(R.drawable.ic_baseline_directions_run_24_white);
                flag_selected.setText("런닝");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_cycle:
                setSelectedButton(2);
                flag_cycle.setBackgroundColor(R.drawable.selectedbutton);
                flag_cycle.setImageResource(R.drawable.ic_baseline_pedal_bike_24_white);
                flag_selected.setText("싸이클");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_fitness:
                setSelectedButton(3);
                flag_fitness.setBackgroundColor(R.drawable.selectedbutton);
                flag_fitness.setImageResource(R.drawable.ic_baseline_fitness_center_24_white);
                flag_selected.setText("실내운동");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_soccer:
                setSelectedButton(4);
                flag_soccer.setBackgroundColor(R.drawable.selectedbutton);
                flag_soccer.setImageResource(R.drawable.ic_baseline_sports_soccer_24_white);
                flag_selected.setText("축구");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_baseball:
                setSelectedButton(5);
                flag_baseball.setBackgroundColor(R.drawable.selectedbutton);
                flag_baseball.setImageResource(R.drawable.ic_baseline_sports_baseball_24_white);
                flag_selected.setText("야구");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_basketball:
                setSelectedButton(6);
                flag_basketball.setBackgroundColor(R.drawable.selectedbutton);
                flag_basketball.setImageResource(R.drawable.ic_baseline_sports_basketball_24_white);
                flag_selected.setText("농구");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_tabletennis:
                setSelectedButton(7);
                flag_tabletennis.setBackgroundColor(R.drawable.selectedbutton);
                flag_tabletennis.setImageResource(R.drawable.ic_baseline_sports_table);
                flag_selected.setText("탁구");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_tennis:
                setSelectedButton(8);
                flag_tennis.setBackgroundColor(R.drawable.selectedbutton);
                flag_tennis.setImageResource(R.drawable.ic_baseline_sports_tennis_24_white);
                flag_selected.setText("테니스");
                flag_etc.setText("직접 입력하기");
                flag_etc.setTextSize(14);
                break;
            case R.id.flag_etc:
                setSelectedButton(9);
                final EditText editText = new EditText(NewFlag.this);

                AlertDialog.Builder dlg = new AlertDialog.Builder(NewFlag.this);
                dlg.setTitle("원하는 운동을 입력하여 주세요.");
                dlg.setView(editText);
                dlg.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag_etc.setText(editText.getText());
                        flag_etc.setTextSize(24);
                        flag_selected.setText("");
                    }
                });
                dlg.show();
                break;

        }
    };

    private void setSelectedButton(int selectedcode){
        this.selectedcode = selectedcode;

        flag_run.setBackgroundColor(R.drawable.collectbutton);
        flag_run.setImageResource(R.drawable.ic_baseline_directions_run_24_black);
        flag_cycle.setBackgroundColor(R.drawable.collectbutton);
        flag_cycle.setImageResource(R.drawable.ic_baseline_pedal_bike_24_black);
        flag_fitness.setBackgroundColor(R.drawable.collectbutton);
        flag_fitness.setImageResource(R.drawable.ic_baseline_fitness_center_24);
        flag_soccer.setBackgroundColor(R.drawable.collectbutton);
        flag_soccer.setImageResource(R.drawable.ic_baseline_sports_soccer_24);
        flag_baseball.setBackgroundColor(R.drawable.collectbutton);
        flag_baseball.setImageResource(R.drawable.ic_baseline_sports_baseball_24);
        flag_basketball.setBackgroundColor(R.drawable.collectbutton);
        flag_basketball.setImageResource(R.drawable.ic_baseline_sports_basketball_24);
        flag_tabletennis.setBackgroundColor(R.drawable.collectbutton);
        flag_tabletennis.setImageResource(R.drawable.ic_baseline_sports_table);
        flag_tennis.setBackgroundColor(R.drawable.collectbutton);
        flag_tennis.setImageResource(R.drawable.ic_baseline_sports_tennis_24);
    }
    private void FlagUpload() {
        final String title = contentsEditText.getText().toString();
        String etc_text;
        if(selectedcode == 9){
            etc_text = flag_etc.getText().toString();
        }
        else{
            etc_text = "";
        }
        if (title.length() > 0) {
            if(starthour != null && startminute != null) {
                if(flag_max != 0) {
                    if (latLng != null) {
                        if(selectedcode != 0) {
                            loaderLayout.setVisibility(View.VISIBLE);
                            final ArrayList<String> flager = new ArrayList<>();
                            final String flag = "1";
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                            DocumentReference documentReference2 = firebaseFirestore.collection("users").document(firebaseUser.getUid());
                            documentReference2.get().addOnCompleteListener((task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        if (document.exists()) {
                                            String PublisherName = document.getData().get("name").toString();

                                            if (document.getData().get("photoUrl") != null) {
                                                String profilePhotoUrl = document.getData().get("photoUrl").toString();
                                                final DocumentReference documentReference = flagInfo == null ? firebaseFirestore.collection("flags").document() : firebaseFirestore.collection("flags").document(flagInfo.getId());
                                                final Date date = flagInfo == null ? new Date() : flagInfo.getCreatedAt();


                                                StoreUpload(documentReference, new FlagInfo(title, firebaseUser.getUid(), date, PublisherName, profilePhotoUrl, flag, flager, starthour, startminute,
                                                        description, address, Integer.toString(flag_max), "1", Integer.toString(selectedcode), etc_text, Double.toString(latLng.latitude), Double.toString(latLng.longitude), "0"));
                                            } else {
                                                String profilePhotoUrl = "0";
                                                final DocumentReference documentReference = flagInfo == null ? firebaseFirestore.collection("flags").document() : firebaseFirestore.collection("flags").document(flagInfo.getId());
                                                final Date date = flagInfo == null ? new Date() : flagInfo.getCreatedAt();

                                                StoreUpload(documentReference, new FlagInfo(title, firebaseUser.getUid(), date, PublisherName, profilePhotoUrl, flag, flager, starthour, startminute,
                                                        description, address, Integer.toString(flag_max), "1", Integer.toString(selectedcode),etc_text, Double.toString(latLng.latitude), Double.toString(latLng.longitude),"0"));
                                            }
                                        }
                                    }
                                }
                            }));
                        } else{
                            showToast(this, "운동 종류를 선택하여 주세요.");
                        }
                    } else {
                        showToast(this, "장소를 설정하여 주세요.");
                    }
                } else{
                    showToast(this, "참여 인원수를 설정하여 주세요.");
                }
            }
            else{
                showToast(this, "시간을 설정하여 주세요.");
            }
        } else {
            showToast(this, "내용을 작성하여 주세요.");
        }
    }

    private void StoreUpload(DocumentReference documentReference, final FlagInfo flagInfo) {
        documentReference.set(flagInfo.getFlagInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DB 저장 성공!");
                        loaderLayout.setVisibility(View.GONE);
                        showToast(NewFlag.this, "게시글을 작성했습니다.");

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "XXXXXX DB 저장 실패 XXXXXX" + e + "flagInfo : " + flagInfo);
                    }
                });
    }

    private void postInit() {
        if (flagInfo != null) {
            contentsEditText.setText(flagInfo.getTitle());
        }
    }

    private void myStartActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        btn_setstarttime.setTypeface(btn_setstarttime.getTypeface(), Typeface.BOLD);
        btn_setstarttime.setText(hourOfDay + "시 " + minute + "분");
        starthour = Integer.toString(hourOfDay);
        startminute = Integer.toString(minute);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FLAG_NEW_MAP) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            description = data.getExtras().getString("description");
            address = data.getExtras().getString("address");
            latLng = data.getExtras().getParcelable("Latlan");

            btn_setlocation.setTypeface(btn_setlocation.getTypeface(), Typeface.BOLD);
            btn_setlocation.setText(description);
        }
    }

}
