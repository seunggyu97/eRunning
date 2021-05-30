package com.example.erunning;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.erunning.Utillity.showToast;

public class Account extends Fragment {
    private View view;
    private TextView tv_userName; // 닉네임 text
    private TextView tv_biomsg;
    private TextView account_tv_post_count;
    private TextView account_tv_follower_count;
    private TextView account_tv_following_count;
    private String following;
    private String follower;
    private String post;
    private CircleImageView iv_userProfile; // 프로필 이미지뷰
    private String user_name;
    private String bio_msg;
    private String route_file;
    private Button btn_profile_edit;
    private Button btn_logout;
    private Button btn_accountDelete;
    private Button btn_picture;
    private View LL_picture;
    private View LL_gallery;
    private View LL_basic;
    private CircleImageView iv_profileImage;
    private CircleImageView profileImageView;
    private String profilePath;
    private ImageButton btn_opt;
    private View LL_accountEdit;
    private View LL_accountDelete;
    public String posturl;
    private ArrayList<String> contentList = new ArrayList<>();




    //private static final int REQUEST_CAMERA = 1;




    public static Account newinstance(){
        Account account = new Account();
        return account;
    }

    private ArrayList<AccountpostItem> AccountpostItem = new ArrayList<>();
    private ArrayList<AccountpostItem> AccountsaveItem = new ArrayList<>();



    private void saveinitDataset() {
        AccountpostItem.clear();
        AccountpostItem.add(new AccountpostItem("https://lh3.googleusercontent.com/proxy/Ay_nTlnN1sXok_tOfDVnJAP1BadkHXWwtv-s9KNZwHykDBAy8MfEKlD45-HCv6-Gvh7gVNpi9HcKQ6FofpvFcudlPRlmzDFYLO9MaXp_HgNzrE5cyomI2pj_uNzBcL9Z9jah56VkuEI4ad7V112UYQHVCcfualce3T6wAUXFOjbLjNEg_GuagiHXjQQSCArRM_CtTQ7cf74Rc5N9ST7l1MDjaP5nFpgMekuZcWWRRbkkNzqVNMrb0M3tv4NhMLgVYLuIMNdQej6McOm4x_sfBMXcuOP8rBSse7GsJJoB2vpMbQxqjxSOb_sLpx1BRUVHu7ydwkjH"));
        AccountpostItem.add(new AccountpostItem("https://upload3.inven.co.kr/upload/2021/04/12/bbs/i16288330962.jpg"));
        AccountpostItem.add(new AccountpostItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSu4U9PAJpw9y9EJuoiidnVFlBPH9Xa30JRHBW-PPL4Pz-c1Y0rhboy3S-Eq-BHw5eijwM&usqp=CAU"));
        AccountpostItem.add(new AccountpostItem("https://pbs.twimg.com/media/D9T4fzCVAAAgoXp.jpg"));
        AccountpostItem.add(new AccountpostItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTG6tmS9AYZZCyAxGlRIQiclKYQ1t71bslAow&usqp=CAU"));
        AccountpostItem.add(new AccountpostItem("https://s3.orbi.kr/data/file/united2/8d45fd407a3344b9b7457538ec64e0f8.jpg"));
        AccountpostItem.add(new AccountpostItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJg-FLB3iMZ8B-90xMF9nUVC9QZZtdFz-QfkNMx9AoUxomKJV-FPeq0-P7aOPqGN5N_6k&usqp=CAU"));
        AccountpostItem.add(new AccountpostItem("https://mblogthumb-phinf.pstatic.net/MjAxOTA4MzFfMTY1/MDAxNTY3MjM4MDQwMDcw.duDGbesVZgv0Un59A0VD9QH-Z5vdp8fcySmvk_O8DZog.E0jtYq4scfLDEWjvpk1hVLscbaFpJJq_AjqLe_bNQYYg.JPEG.dodojys/사각고양이.jpg?type=w2"));

        }

    private void postinitDataset() {
        AccountpostItem.clear();
//        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        documentReference.get().addOnCompleteListener((task -> {
//            if(task.isSuccessful()){
//                DocumentSnapshot document = task.getResult();
//                if(document != null){
//                    if(document.exists()){
//                        if(document.getData().get("contents") != null) {
//                            post = document.getData().get("contents").toString();
//
//                        }
//                    }
//                }
//            }
//        }));
//        AccountpostItem.add(new AccountpostItem(post));
        AccountpostItem.add(new AccountpostItem("data:image/jpeg;base64,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"));
        AccountpostItem.add(new AccountpostItem("https://post-phinf.pstatic.net/MjAxOTEwMTlfNzUg/MDAxNTcxNDUzMzUwNDYx.8G-pNltTtBbw7STJGg8u2IrRyyUmhgTMK8dY5OE--_Ig.vBeZC_OE4toNmS0FaOVmMKkYwNio8vhGzrQqEf04E8Ag.JPEG/d029e546dd5d449c99c15a586963ca96.jpg?type=w1200"));
        AccountpostItem.add(new AccountpostItem("https://img.huffingtonpost.com/asset/5b7fb0322000002d0034b13b.png?cache=Zb2usXxMiT&ops=1778_1000"));
        AccountpostItem.add(new AccountpostItem("https://t1.daumcdn.net/liveboard/holapet/7dd0ffdc19294528b5de0ffb31829366.JPG"));
        AccountpostItem.add(new AccountpostItem("https://t1.daumcdn.net/cfile/tistory/997028505BA2DE6911"));
        AccountpostItem.add(new AccountpostItem("https://dispatch.cdnser.be/wp-content/uploads/2018/09/9bf3b784bf915c15dcaedd21341b730f.png"));
        AccountpostItem.add(new AccountpostItem("https://i.pinimg.com/736x/6f/7d/a3/6f7da37d29f909db5dc90033bfb172c0.jpg"));
        AccountpostItem.add(new AccountpostItem("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTqiUHuQB7hFTPBfNNr6eStbO2vQfI_8ihDc9h3jwnnfNk-b03tGJzRkJf1-gTZ_0eAoUs&usqp=CAU"));
        AccountpostItem.add(new AccountpostItem("https://t1.daumcdn.net/liveboard/holapet/5a0bc34578a84d06a156809701d4bfd9.jpg"));
        AccountpostItem.add(new AccountpostItem("https://dispatch.cdnser.be/wp-content/uploads/2018/06/20180607225725_f.jpg"));
        AccountpostItem.add(new AccountpostItem("https://thumb.pann.com/tc_480/http://fimg4.pann.com/new/download.jsp?FileID=38261783"));

//        AccountpostItem.add(new AccountpostItem(posturl));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId){
            case 1:
                btn_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.logout_btn:
                                //user_logout();
                                AuthUI.getInstance()
                                        .signOut(getActivity())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                getActivity().finish();
                                                startActivity(new Intent(getActivity(), Login.class));
                                                showToast(getActivity(), "정상적으로 로그아웃 되었습니다.");

                                            }
                                        });
                                Log.e("로그아웃","버튼입력");
                                break;
                        }
                    }
                });
            case 2:
                btn_accountDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()){
                            case R.id.delete_btn:
                                //user_delete();
                                AuthUI.getInstance()
                                        .delete(getActivity())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getActivity(), "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
                                                getActivity().finish();
                                                startActivity(new Intent(getActivity(), Login.class));
                                            }
                                        });
                                Log.e("회원탈퇴","버튼입력");
                                break;
                        }
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case MainActivity.REQUEST_CAMERA:{
                if(resultCode == Activity.RESULT_OK){

                    String profilePath;
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그: ","profilePath: "+ profilePath);
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    Glide.with(this).load(bmp).circleCrop().into(iv_profileImage);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final StorageReference mountainImagesRef = storageRef.child("users/" +user.getUid()+"/profile_image.jpg");
                    if(profilePath == null){

                    } else{
                        try{
                            InputStream stream = new FileInputStream(new File(profilePath));

                            UploadTask uploadTask = mountainImagesRef.putStream(stream);

                            uploadTask.continueWithTask((task) ->  {
                                if(!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return mountainImagesRef.getDownloadUrl();
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri downloadUri = task.getResult();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(user.getUid())
                                                .update(
                                                        "photoUrl", downloadUri.toString()
                                                );
                                    }else{
                                        Log.e("로그","실패");
                                    }
                                }
                            });
                        }catch (FileNotFoundException e){
                            Log.e("로그","에러"+e.toString());
                        }
                    }
                }
                break;
            }
            case 0:{
                Log.e("REQUEST_GALLERY","switch문 실행");
                if(resultCode == Activity.RESULT_OK){

                    Log.e("REQUEST_CAMERA","resultCode == Activity.RESULT_OK 실행");
                    String profilePath;
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그: ","profilePath: "+ profilePath);
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    Glide.with(this).load(bmp).circleCrop().into(iv_profileImage);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final StorageReference mountainImagesRef = storageRef.child("users/" +user.getUid()+"/profile_image.jpg");
                    if(profilePath == null){

                    } else{
                        try{
                            InputStream stream = new FileInputStream(new File(profilePath));

                            UploadTask uploadTask = mountainImagesRef.putStream(stream);

                            uploadTask.continueWithTask((task) ->  {
                                if(!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return mountainImagesRef.getDownloadUrl();
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        Uri downloadUri = task.getResult();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(user.getUid())
                                                .update(
                                                        "photoUrl", downloadUri.toString()
                                                );
                                    }else{
                                        Log.e("로그","실패");
                                    }
                                }
                            });
                        }catch (FileNotFoundException e){
                            Log.e("로그","에러"+e.toString());
                        }
                    }
                }
                break;
            }
            case MainActivity.REQUEST_EDITPROFILE:{
                if(resultCode == Activity.RESULT_OK){
                    Log.e("REQUEST_EDITPROFILE","resultCode == Activity.RESULT_OK 실행");
                    //String edit_username;
                    //String edit_bio;
                    //edit_username = data.getStringExtra("edit_name");//인텐트
                    //edit_bio = data.getStringExtra("edit_bio");

                    /*업로드
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(user.getUid())
                            .update(
                                    "name", edit_username
                            );
                    */
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    documentReference.get().addOnCompleteListener((task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document != null){
                                if(document.exists()){
                                    if(document.getData().get("name") != null){
                                        tv_userName.setText(document.getData().get("name").toString());
                                    }
                                    if(document.getData().get("bio") != null){
                                        tv_biomsg.setText(document.getData().get("bio").toString());
                                    }
                                }
                            }
                        }
                    }));
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permission[], int[] grantResults){
        switch (requestCode){
            case 1: {
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(new Intent(getActivity(), Gallery.class));
                }
                else{
                    showToast(getActivity(), "권한을 허용해 주세요.");
                }
            }
        }
    }

    public void itemclick() {
        // 사진 눌렀을 때 사진 상세페이지러 이동
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.account, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            posturl = bundle.getString("posturl");
            Log.e("asd", "안녕하세요    " +bundle.getString("posturl"));
        }

        postinitDataset(); //post data

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //post grid set
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.post_recycle_view);
        recyclerView.setHasFixedSize(true);
        Context context = view.getContext();
        GridLayoutManager layoutManager1 = new GridLayoutManager(context,9); //post
        layoutManager1.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int gridPostition = 0;
                switch (gridPostition) {
                    case 0:
                    case 1:
                    case 2:
                        return 3;
                    case 3:
                    case 4:
                    case 5:
                        return 3;
                }
                return 0;
            }
        });

        //save grid set
        RecyclerView recyclerView1 = (RecyclerView) view.findViewById(R.id.save_recycle_view);
        recyclerView1.setHasFixedSize(true);
        Context context1 = view.getContext();
        GridLayoutManager layoutManager2 = new GridLayoutManager(context1,9); //save
        layoutManager2.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int gridPostition = 0;
                switch (gridPostition) {
                    case 0:
                    case 1:
                    case 2:
                        return 3;
                    case 3:
                    case 4:
                    case 5:
                        return 3;
                }
                return 0;
            }
        });


        // 내 프로필 켯을 때 포스터 리사이클러 뷰가 먼저 보여지게 set
        AccountRecyclerViewAdapterPost adapter = new AccountRecyclerViewAdapterPost(context, AccountpostItem);
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setAdapter(adapter);

        account_tv_post_count = view.findViewById(R.id.account_tv_post_count);
        account_tv_follower_count = view.findViewById(R.id.account_tv_follower_count);
        account_tv_following_count = view.findViewById(R.id.account_tv_following_count);
        tv_userName = view.findViewById(R.id.tv_userName);
        tv_biomsg = view.findViewById(R.id.tv_userInfo);
        //iv_userProfile = view.findViewById(R.id.cv_userProfile);
        btn_logout= view.findViewById(R.id.logout_btn);
        btn_accountDelete = view.findViewById(R.id.delete_btn);
        btn_profile_edit = view.findViewById(R.id.user_profile_edit_btn);
        iv_profileImage = view.findViewById(R.id.iv_profileimage);

        Button btn_post = (Button)view.findViewById(R.id.btn_post);
        Button btn_save = (Button)view.findViewById(R.id.btn_save);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);


        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postinitDataset();


                // post리사이클러뷰 셋
                AccountRecyclerViewAdapterPost adapter = new AccountRecyclerViewAdapterPost(context, AccountpostItem);
                recyclerView.setLayoutManager(layoutManager1);
                recyclerView.setAdapter(adapter);

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveinitDataset();

                //save 리사이클러뷰 셋
                recyclerView.setLayoutManager(layoutManager1);
                recyclerView.setAdapter(adapter);


            }
        });


        btn_opt = view.findViewById(R.id.btn_opt);

        btn_opt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   switch (v.getId()) {
                       case R.id.btn_opt:
                           BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                   getActivity(), R.style.BottomSheetDialogTheme
                           );
                           View bottomSheetView = LayoutInflater.from(getActivity().getApplicationContext())
                                   .inflate(
                                           R.layout.myaccount_bottom_sheet,
                                           (LinearLayout) view.findViewById(R.id.myaccountbottomSheetContainer)
                                   );
                           LL_accountEdit = bottomSheetView.findViewById(R.id.LL_accountEdit);
                           LL_accountDelete = bottomSheetView.findViewById(R.id.LL_accountDelete);

                           LL_accountEdit.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   switch (v.getId()) {
                                       case R.id.LL_accountEdit:
                                           bottomSheetDialog.dismiss();
                                           AuthUI.getInstance()
                                                   .signOut(getActivity())
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           getActivity().finish();
                                                           startActivity(new Intent(getActivity(), Login.class));
                                                           showToast(getActivity(), "정상적으로 로그아웃 되었습니다.");

                                                       }
                                                   });
                                           Log.e("로그아웃","버튼입력");
                                           break;
                                   }
                               }
                           });
                           LL_accountDelete.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   switch (v.getId()) {
                                       case R.id.LL_accountDelete:
                                           bottomSheetDialog.dismiss();
                                           AuthUI.getInstance()
                                                   .delete(getActivity())
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task) {
                                                           Toast.makeText(getActivity(), "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
                                                           getActivity().finish();
                                                           startActivity(new Intent(getActivity(), Login.class));
                                                       }
                                                   });
                                           Log.e("회원탈퇴","버튼입력");
                                           break;
                                   }
                               }
                           });
                           bottomSheetDialog.setContentView(bottomSheetView);
                           bottomSheetDialog.show();
                           break;
                   }
               }
           });

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document != null){
                    if(document.exists()){
                        following = document.getData().get("following").toString();
                        follower = document.getData().get("follower").toString();
                        post = document.getData().get("post").toString();
                        Log.e("folloing","folloing : " + following);
                        documentReference.collection("users").document(document.getId()).update("following",following);
                        documentReference.collection("users").document(document.getId()).update("follower",follower);
                        documentReference.collection("users").document(document.getId()).update("post",post);
                        account_tv_following_count.setText(document.getData().get("following").toString());
                        account_tv_follower_count.setText(document.getData().get("follower").toString());
                        account_tv_post_count.setText(document.getData().get("post").toString());
                        tv_userName.setText(document.getData().get("name").toString());
                        user_name = document.getData().get("name").toString();
                        if(document.getData().get("bio") != null) {
                            bio_msg = document.getData().get("bio").toString();
                            tv_biomsg.setText(bio_msg);
                        }
                        if(document.getData().get("photoUrl") != null){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final StorageReference mountainImagesRef = storageRef.child("users/" +user.getUid()+"/profile_image.jpg");

                            storageRef.child("users/" +user.getUid()+"/profile_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //이미지 로드 성공시

                                    Glide.with(view.getContext()).load(uri).circleCrop().into(iv_profileImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //이미지 로드 실패시
                                    Log.e("프로필 이미지 로드","실패");
                                }
                            });

                        }
                    }
                }
            }
        }));
        if (getArguments() != null) {
            user_name = getArguments().getString("name");
            tv_userName.setText(user_name);//닉네임 text를 텍스트 뷰에 세팅
            tv_biomsg.setText(bio_msg);
            route_file = getArguments().getString("profile");
            Glide.with(this).load(route_file).circleCrop().into(iv_profileImage); //프로필 url을 이미지 뷰에 세팅
        }

        profileImageView = view.findViewById(R.id.iv_profileimage);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.iv_profileimage:
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                getActivity(),R.style.BottomSheetDialogTheme
                        );
                        View bottomSheetView = LayoutInflater.from(getActivity().getApplicationContext())
                                .inflate(
                                        R.layout.account_bottom_sheet,
                                        (LinearLayout)view.findViewById(R.id.bottomSheetContainer)
                                );
                        LL_picture = bottomSheetView.findViewById(R.id.LL_picture);
                        LL_gallery = bottomSheetView.findViewById(R.id.LL_gallery);
                        LL_basic = bottomSheetView.findViewById(R.id.LL_basic);

                        LL_picture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()){
                                    case R.id.LL_picture:
                                        bottomSheetDialog.dismiss();
                                        Intent intent = new Intent(getActivity(), CameraActivity.class);
                                        startActivityForResult(intent, MainActivity.REQUEST_CAMERA);
                                        Log.e("MainActivity>Account","startActivityForResult실행"+MainActivity.REQUEST_CAMERA);


                                        //startActivity(new Intent(getActivity(), CameraActivity.class));

                                        break;
                                }
                            }
                        });


                        LL_gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()){
                                    case R.id.LL_gallery:

                                        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                                            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                                            } else {
                                                showToast(getActivity(), "권한을 허용해 주세요.");
                                            }
                                        }else{
                                            //Intent intent = new Intent(getActivity(), Gallery.class);
                                            //startActivityForResult(intent, MainActivity.REQUEST_GALLERY);
                                            myStartActivity(Gallery.class,"image", 0);
                                            bottomSheetDialog.dismiss();
                                        }

                                        break;
                                }
                            }
                        });
                        LL_basic.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()){
                                    case R.id.LL_basic:
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("users").document(user.getUid())
                                                .update(
                                                        "photoUrl", null
                                                );
                                        FirebaseStorage storage = FirebaseStorage.getInstance();

                                        // Create a storage reference from our app
                                        StorageReference storageRef = storage.getReference();

                                        // Create a reference to the file to delete
                                        StorageReference desertRef = storageRef.child("users/" +user.getUid()+"/profile_image.jpg");

                                        // Delete the file
                                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });
                                        /***********새로고침 코드**************/
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        ft.detach(Account.this).attach(Account.this).commit();
                                        bottomSheetDialog.dismiss();
                                        /************************************/
                                        break;
                                }
                            }
                        });

                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();
                        /*CardView cardView = view.findViewById(R.id.btn_cardview);
                        if(cardView.getVisibility() == View.VISIBLE){
                            cardView.setVisibility(View.GONE);
                        }else{
                            cardView.setVisibility(View.VISIBLE);
                        }*/
                        break;
                }
            }
        });


        btn_profile_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.user_profile_edit_btn:
                        //user_logout();
                        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                        startActivityForResult(intent, MainActivity.REQUEST_EDITPROFILE);
                        break;
                }
            }
        });







        /*profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.iv_profileimage:
                        startActivity(new Intent(getActivity(), CameraActivity.class));
                        Log.e("사진촬영","버튼입력");
                        profileUpdate();

                        break;
                }
            }
        });*/


        return view;
    }


    /*private void user_logout(){
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(), Login.class));
        Toast.makeText(getActivity(), "정상적으로 로그아웃 되었습니다.",
                Toast.LENGTH_SHORT).show();
    }



    private void user_delete(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), Login.class));
                    }
                });

    }*/
    private void myStartActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
}
