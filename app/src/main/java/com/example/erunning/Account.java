package com.example.erunning;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.erunning.Utillity.showToast;

public class Account extends Fragment {
    private View view;
    private TextView tv_userName; // 닉네임 text
    private TextView tv_biomsg;
    private TextView account_tv_post_count;
    private TextView account_tv_follower_count;
    private TextView account_tv_following_count;
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
    //private static final int REQUEST_CAMERA = 1;

    FirebaseUser firebaseUser;

    public static Account newinstance(){
        Account account = new Account();
        return account;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.account, container, false);
        Bundle extra = getArguments();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document != null){
                    if(document.exists()){
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
