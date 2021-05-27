package com.example.erunning;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

import static com.example.erunning.MainActivity.REQUEST_FLAG_NEW_MAP;
import static com.example.erunning.Utillity.isStorageUrl;
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
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String starthour;
    private String startminute;
    private String description;
    private String address;
    private LatLng latLng;
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

        btn_setstarttime.setOnClickListener(onClickListener);
        btn_setmax.setOnClickListener(onClickListener);
        btn_setlocation.setOnClickListener(onClickListener);
        buttonsBackgroundLayout.setOnClickListener(onClickListener);

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
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;
            case R.id.btn_setlocation:
                Intent intent = new Intent(this,FlagNewmap.class);
                startActivityForResult(intent,REQUEST_FLAG_NEW_MAP);
        }
    };


    private void FlagUpload() {
        final String title = contentsEditText.getText().toString();

        if (title.length() > 0) {
            if(starthour != null && startminute != null) {
                if(latLng != null) {
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


                                        StoreUpload(documentReference, new FlagInfo(title, firebaseUser.getUid(), date, PublisherName, profilePhotoUrl, flag, flager, starthour, startminute, description, address));
                                    } else {
                                        String profilePhotoUrl = "0";
                                        final DocumentReference documentReference = flagInfo == null ? firebaseFirestore.collection("flags").document() : firebaseFirestore.collection("flags").document(flagInfo.getId());
                                        final Date date = flagInfo == null ? new Date() : flagInfo.getCreatedAt();

                                        StoreUpload(documentReference, new FlagInfo(title, firebaseUser.getUid(), date, PublisherName, profilePhotoUrl, flag, flager, starthour, startminute, description, address));
                                    }
                                }
                            }
                        }
                    }));
                }
                else{
                    showToast(this,"장소를 설정하여 주세요");
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

            btn_setlocation.setText(description);
        }
    }
}
