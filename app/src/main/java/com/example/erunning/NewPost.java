package com.example.erunning;

import android.Manifest;
import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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

import static com.example.erunning.Utillity.isStorageUrl;
import static com.example.erunning.Utillity.showToast;

public class NewPost extends BasicActivity {
    private FirebaseUser user;
    private String post;
    public String posturl;
    private StorageReference storageRef;
    private static final String TAG = "NewPost";
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private PostInfo postInfo;
    private UserInfo userInfo;
    private Utillity util;
    private int pathCount, successCount;
    private RelativeLayout loaderLayout;
    Feed feed = new Feed();

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
        //findViewById(R.id.btn_addvideo).setOnClickListener(onClickListener);
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        postInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if (selectedEditText == null) {
                        parent.addView(contentsItemView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(contentsItemView, i + 1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(profilePath);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
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
                        showToast(NewPost.this, "????????? ????????? ?????????.");
                    }
                } else {
                    myStartActivity(Gallery.class, "image", 0);
                }
                break;
            /*case R.id.btn_addvideo:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        showToast(NewPost.this, "????????? ????????? ?????????.");
                    }
                } else {
                    myStartActivity(Gallery.class, "video", 0);
                }
                break;*/
            /*case R.id.btn_route:
                finish();
                break;*/ // ?????? ?????? ??????
            case R.id.buttonsBackgroundLayout:
                if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.delete:
                final View selectedView = (View) selectedImageView.getParent();
                String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                if(isStorageUrl(path)){

                    String[] list = pathList.get(parent.indexOfChild(selectedView) - 1 ).split("\\?");
                    String[] list2 = list[0].split("%2F");
                    String name = list2[list2.length -1];

                    StorageReference desertRef = storageRef.child("posts/" + postInfo.getId() + "/" + name);
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(NewPost.this, "????????? ?????????????????????.");
                            pathList.remove(parent.indexOfChild(selectedView) - 1);
                            parent.removeView(selectedView);
                            buttonsBackgroundLayout.setVisibility(View.GONE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showToast(NewPost.this, "????????? ??????????????? ?????????????????????.");
                        }
                    });
                }else{
                    pathList.remove(parent.indexOfChild(selectedView) - 1);
                    parent.removeView(selectedView);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                }
                break;
        }
    };


    private void PostUpload() {
        final String title = ((EditText) findViewById(R.id.et_writing)).getText().toString();

        if (title.length() > 0) {

            loaderLayout.setVisibility(View.VISIBLE);

            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> liker = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
            documentReference2.get().addOnCompleteListener((task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if(document.exists()){
                            String PublisherName = document.getData().get("name").toString();
                            if(document.getData().get("photoUrl") != null){
                                String profilePhotoUrl = document.getData().get("photoUrl").toString();
                                final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());

                                final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();


                                for (int i = 0; i < parent.getChildCount(); i++) {
                                    LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                                    //getChildAt ????????? ???????????? LinearLayout ??????
                                    //getChildCount : ?????? LinearLayout??? ?????????????????? LinearLayout??? ???
                                    Log.e("parent.getChildAt(", +i + ") = " + parent.getChildAt(i));
                                    Log.e("getChildCount()", " = " + linearLayout.getChildCount());
                                    for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                                        View view = linearLayout.getChildAt(ii);
                                        if (view instanceof EditText) {
                                            String text = ((EditText) view).getText().toString();
                                            if (text.length() > 0) {
                                                contentsList.add(text);
                                            }
                                        } else if (!isStorageUrl(pathList.get(pathCount))) {

                                            contentsList.add(pathList.get(pathCount));
                                            String[] pathArray = pathList.get(pathCount).split("\\.");
                                            final StorageReference mountainImageRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                                            try {
                                                InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                                                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                                                UploadTask uploadTask = mountainImageRef.putStream(stream, metadata);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // ??????
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
                                                                    //??????
                                                                    PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), date, PublisherName,profilePhotoUrl,"0","0", liker);
                                                                    Log.e("title : ", title + "  //contentsList : " + contentsList + "  //user.getUid() : " + user.getUid() + "  //Date : " + new Date() + "PublisherName : " + PublisherName + "profilePhotoUrl : " + profilePhotoUrl);
                                                                    StoreUpload(documentReference, postInfo);
                                                                }

                                                            }
                                                        });
                                                    }
                                                });
                                            } catch (FileNotFoundException e) {
                                                loaderLayout.setVisibility(View.GONE);
                                                Log.e("??????", "??????" + e.toString());
                                            }
                                            pathCount++;
                                        }
                                    }
                                }
                                if (pathList.size() == 0) {
                                    StoreUpload(documentReference, new PostInfo(title, contentsList, user.getUid(), date, PublisherName,profilePhotoUrl,"0","0",liker));
                                }

                            }else{
                                String profilePhotoUrl = "0";
                                final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
                                final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

                                for (int i = 0; i < parent.getChildCount(); i++) {
                                    LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                                    //getChildAt ????????? ???????????? LinearLayout ??????
                                    //getChildCount : ?????? LinearLayout??? ?????????????????? LinearLayout??? ???
                                    Log.e("parent.getChildAt(", +i + ") = " + parent.getChildAt(i));
                                    Log.e("getChildCount()", " = " + linearLayout.getChildCount());
                                    for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                                        View view = linearLayout.getChildAt(ii);
                                        if (view instanceof EditText) {
                                            String text = ((EditText) view).getText().toString();
                                            if (text.length() > 0) {
                                                contentsList.add(text);
                                            }
                                        } else if (!isStorageUrl(pathList.get(pathCount))) {

                                            contentsList.add(pathList.get(pathCount));
                                            String[] pathArray = pathList.get(pathCount).split("\\.");
                                            final StorageReference mountainImageRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                                            try {
                                                InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                                                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                                                UploadTask uploadTask = mountainImageRef.putStream(stream, metadata);
                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // ??????
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
                                                                    //??????
                                                                    PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), date, PublisherName,profilePhotoUrl,"0","0",liker);
                                                                    Log.e("title : ", title + "  //contentsList : " + contentsList + "  //user.getUid() : " + user.getUid() + "  //Date : " + new Date() + "PublisherName : " + PublisherName + "profilePhotoUrl : " + profilePhotoUrl);
                                                                    StoreUpload(documentReference, postInfo);
                                                                }

                                                            }
                                                        });
                                                    }
                                                });
                                            } catch (FileNotFoundException e) {
                                                loaderLayout.setVisibility(View.GONE);
                                                Log.e("??????", "??????" + e.toString());
                                            }
                                            pathCount++;
                                        }
                                    }
                                }
                                if (pathList.size() == 0) {
                                    StoreUpload(documentReference, new PostInfo(title, contentsList, user.getUid(), date, PublisherName,profilePhotoUrl,"0","0",liker));
                                }
                            }
                        }
                    }
                }
            }));

        } else {
            showToast(this, "????????? ????????? ?????????.");

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
                        Log.d(TAG, "DB ?????? ??????!");Bundle bundle = new Bundle();
                        bundle.putString("posturl", posturl);
                        Log.e("asdf","????????? "+posturl);

                        feed.setArguments(bundle);
                        Log.e("????????? ","?????????  "+posturl);

                        loaderLayout.setVisibility(View.GONE);
                        showToast(NewPost.this, "???????????? ??????????????????.");
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = db.collection("users").document(user.getUid());
                        documentReference.get().addOnCompleteListener((task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            if (document.exists()) {
                                                // post +1 ??????
                                                post = document.getData().get("post").toString();
                                                int post_num = Integer.parseInt(post);
                                                post_num += 1;
                                                post = String.valueOf(post_num);
                                                Log.e("????????? post  ", "post : " + post_num + user.getUid());
                                                db.collection("users").document(user.getUid()).update("post", Integer.parseInt(post));
                                            }
                                        }
                                    }
                                }));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "XXXXXX DB ?????? ?????? XXXXXX" + e + "postInfo : " + postInfo);
                    }
                });
    }


    private void postInit() {
        if (postInfo != null) {
            contentsEditText.setText(postInfo.getTitle());
            ArrayList<String> contentList = postInfo.getContents();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) {
                    pathList.add(contents);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if (i < contentList.size() - 1) {
                        String nextContents = contentList.get(i + 1);
                        if (!isStorageUrl(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }
                }
            }
        }
    }

    private void myStartActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }

    public interface Myposturl {
        void Myposturl(ArrayList<String> myposturl);
    }
}