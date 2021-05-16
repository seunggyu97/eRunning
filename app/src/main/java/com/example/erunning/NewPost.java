package com.example.erunning;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class NewPost extends BasicActivity {
    private FirebaseUser user;
    private static final String TAG = "NewPost";
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private int pathCount, successCount;
    private RelativeLayout loaderLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        parent = findViewById(R.id.LL_NewPostContents);
        loaderLayout = findViewById(R.id.loaderLayout);

        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        contentsEditText = findViewById(R.id.et_writing);
        findViewById(R.id.btn_addpost).setOnClickListener(onClickListener);
        findViewById(R.id.btn_writingback).setOnClickListener(onClickListener);
        findViewById(R.id.btn_addphoto).setOnClickListener(onClickListener);
        findViewById(R.id.btn_addvideo).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);
        //findViewById(R.id.btn_addroute).setOnClickListener(onClickListener);
        findViewById(R.id.et_writing).setOnFocusChangeListener(onFocusChangeListener);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectedEditText = null;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(NewPost.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    if (selectedEditText == null) {
                        parent.addView(linearLayout);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(linearLayout, i + 1);
                                break;
                            }
                        }
                    }

                    ImageView imageView = new ImageView(NewPost.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    Glide.with(this).load(profilePath).override(1000).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(NewPost.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("사진/동영상에 대한 설명을 추가하시려면 여기에 입력하세요.");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.btn_addpost:
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                PostUpload();
                break;
            case R.id.btn_writingback:
                finish();
                break;
            case R.id.btn_addphoto:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    myStartActivity(Gallery.class, "image", 0);
                }
                break;
            case R.id.btn_addvideo:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    myStartActivity(Gallery.class, "video", 0);
                }
                break;
            /*case R.id.btn_route:
                finish();
                break;*/ // 경로 추가 버튼
            case R.id.buttonsBackgroundLayout:
                if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.delete:
                parent.removeView((View) selectedImageView.getParent());
                buttonsBackgroundLayout.setVisibility(View.GONE);
                break;
        }
    };


    private void PostUpload() {
        final String title = ((EditText) findViewById(R.id.et_writing)).getText().toString();
        if (title.length() > 0) {

            loaderLayout.setVisibility(View.VISIBLE);

            final ArrayList<String> contentsList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = firebaseFirestore.collection("posts").document();

            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                //getChildAt 시작은 내용입력 LinearLayout 부터
                //getChildCount : 해당 LinearLayout에 포함되어있는 LinearLayout의 수
                Log.e("parent.getChildAt(", +i + ") = " + parent.getChildAt(i));
                Log.e("getChildCount()", " = " + linearLayout.getChildCount());
                for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                    View view = linearLayout.getChildAt(ii);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                        }
                    } else {
                        contentsList.add(pathList.get(pathCount));
                        String[] pathArray = pathList.get(pathCount).split("\\.");
                        final StorageReference mountainImageRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." +pathArray[pathArray.length-1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImageRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 실패
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentsList.set(index, uri.toString());
                                            successCount++;
                                            if (pathList.size() == successCount) {
                                                //완료
                                                PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                                                Log.e("title : ", title + "  //contentsList : " + contentsList + "  //user.getUid() : " + user.getUid() + "  //Date : " + new Date());
                                                StoreUpload(documentReference, postInfo);
                                                for (int a = 0; a < contentsList.size(); a++) {
                                                    Log.e("로그 : ", "콘텐츠 : " + contentsList.get(a));
                                                }
                                            }

                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            loaderLayout.setVisibility(View.GONE);
                            Log.e("로그", "에러" + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if (pathList.size() == 0) {
                PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
                StoreUpload(documentReference, postInfo);
            }
        } else {
            Toast.makeText(this, "내용을 작성해 주세요.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };

    private void StoreUpload(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DB 저장 성공!");
                        loaderLayout.setVisibility(View.GONE);
                        startToast("게시글을 작성했습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "XXXXXX DB 저장 실패 XXXXXX" + e + "postInfo : " + postInfo);
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}
