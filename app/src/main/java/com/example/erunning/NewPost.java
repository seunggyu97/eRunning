package com.example.erunning;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewPost extends BasicActivity{
    private FirebaseUser user;
    private static final String TAG = "NewPost";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        findViewById(R.id.btn_addpost).setOnClickListener(onClickListener);
        findViewById(R.id.btn_writingback).setOnClickListener(onClickListener);
        findViewById(R.id.btn_addphoto).setOnClickListener(onClickListener);
        findViewById(R.id.btn_addvideo).setOnClickListener(onClickListener);
        //findViewById(R.id.btn_addroute).setOnClickListener(onClickListener);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0: {
                if(resultCode == Activity.RESULT_OK){
                    String profilePath = data.getStringExtra("profilePath");

                    LinearLayout parent = findViewById(R.id.LL_NewPostContents);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(NewPost.this);
                    imageView.setLayoutParams(layoutParams);
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    parent.addView(imageView);

                    EditText editText = new EditText(NewPost.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.btn_addpost:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                postUpdate();
                finish();
                break;
            case R.id.btn_writingback:
                finish();
                break;
            case R.id.btn_addphoto:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요.",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    myStartActivity(Gallery.class,"image");
                }
                break;
            case R.id.btn_addvideo:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요.",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    myStartActivity(Gallery.class,"video");
                }
                break;
            /*case R.id.btn_route:
                finish();
                break;*/ // 경로 추가 버튼
        }
    };

    private void postUpdate(){
        final String contents = ((EditText) findViewById(R.id.et_writing)).getText().toString();

        if (contents.length() > 0){
            user = FirebaseAuth.getInstance().getCurrentUser();
            PostInfo postInfo = new PostInfo(contents, user.getUid());
            uploader(postInfo);
        }
        else {
            Toast.makeText(this, "내용을 작성해 주세요.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void uploader(PostInfo postInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(postInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"DB문서 생성 ID : " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "DB문서 생성 에러 ", e);
                    }
                });
    }

    private void startToast(String msg){ Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    private void myStartActivity(Class c, String media){
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent,0);
    }
}
