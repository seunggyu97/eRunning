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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class Account extends Fragment {
    private View view;
    private TextView tv_userName; // 닉네임 text
    private ImageView iv_userProfile; // 프로필 이미지뷰
    private String user_name;
    private String route_file;
    private Button btn_logout;
    private Button btn_accountDelete;
    private Button btn_picture;
    private View LL_picture;
    private View LL_gallery;
    private View LL_basic;
    private ImageView iv_profileImage;
    private ImageView profileImageView;
    private String profilePath;

    public static Account newinstance(){
        Account account = new Account();
        return account;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 0 :{
                if(resultCode == Activity.RESULT_OK){
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그: ","profilePath: "+ profilePath);
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    profileImageView.setImageBitmap(bmp);
                }
                break;
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
                    Toast.makeText(getActivity(), "권한을 허용해 주세요.",
                            Toast.LENGTH_SHORT).show();
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


        tv_userName = view.findViewById(R.id.tv_userName);
        //iv_userProfile = view.findViewById(R.id.cv_userProfile);
        btn_logout= view.findViewById(R.id.logout_btn);
        btn_accountDelete = view.findViewById(R.id.delete_btn);
        iv_profileImage = view.findViewById(R.id.iv_profileimage);


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document != null){
                    if(document.exists()){
                        tv_userName.setText(document.getData().get("name").toString());
                    }
                }
            }
        }));
        if (getArguments() != null) {
            //String user_name = extra.getString("이름");
            //String route_profile = extra.getString("프로필사진");
            user_name = getArguments().getString("name");
            tv_userName.setText(user_name);//닉네임 text를 텍스트 뷰에 세팅
            route_file = getArguments().getString("profile");
            Glide.with(this).load(route_file).into(iv_userProfile); //프로필 url을 이미지 뷰에 세팅
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
                                        startActivity(new Intent(getActivity(), CameraActivity.class));
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
                                                            UserInfo userInfo = new UserInfo(downloadUri.toString());
                                                            db.collection("users").document(user.getUid()).set(userInfo)
                                                                    .addOnSuccessListener((OnSuccessListener) (aVoid) ->{
                                                                        Log.w("SignUp_profileUpdate", "회원정보 등록성공");
                                                                        //finish();
                                                                    })
                                                                    .addOnFailureListener((e) -> {
                                                                        Log.w("SignUp_profileUpdate", "회원정보 등록 실패");

                                                                    });
                                                        }else{
                                                            Log.e("로그","실패");
                                                        }
                                                    }
                                                });
                                            }catch (FileNotFoundException e){
                                                Log.e("로그","에러"+e.toString());
                                            }
                                        }
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
                                                Toast.makeText(getActivity(), "권한을 허용해 주세요.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            bottomSheetDialog.dismiss();
                                            startActivity(new Intent(getActivity(), Gallery.class));
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
                                        bottomSheetDialog.dismiss();
                                        Toast.makeText(getActivity(), "토스트테스트",
                                                Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(getActivity(), "정상적으로 로그아웃 되었습니다.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Log.e("로그아웃","버튼입력");
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



}