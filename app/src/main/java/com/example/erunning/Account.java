package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class Account extends Fragment {
 private View view;
 private TextView tv_userName; // 닉네임 text
 private ImageView iv_userProfile; // 프로필 이미지뷰
 private String user_name;
 private String route_file;
 private Button btn_logout;
 private Button btn_accountDelete;

    public static Account newinstance(){
        Account account = new Account();
        return account;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       view = inflater.inflate(R.layout.account, container, false);
       Bundle extra = getArguments();

       tv_userName = view.findViewById(R.id.tv_userEmail);
       iv_userProfile = view.findViewById(R.id.iv_userProfile);
       btn_logout= view.findViewById(R.id.logout_btn);
       btn_accountDelete = view.findViewById(R.id.delete_btn);

       if (getArguments() != null) {
          //String user_name = extra.getString("이름");
          //String route_profile = extra.getString("프로필사진");
          user_name = getArguments().getString("name");
          tv_userName.setText(user_name);//닉네임 text를 텍스트 뷰에 세팅
          route_file = getArguments().getString("profile");
          Glide.with(this).load(route_file).into(iv_userProfile); //프로필 url을 이미지 뷰에 세팅
       }
        else{
            Log.e("getArguments()","값이 없음 ㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜ왜??????????????");
       }

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
