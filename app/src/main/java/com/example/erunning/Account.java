package com.example.erunning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class Account extends Fragment {
 private View view;
 private TextView tv_userName; // 닉네임 text
 private ImageView iv_userProfile; // 프로필 이미지뷰
 private String user_name;
 private String route_file;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       view = inflater.inflate(R.layout.account, container, false);
       Bundle extra = getArguments();
       tv_userName = view.findViewById(R.id.tv_userName);
       iv_userProfile = view.findViewById(R.id.iv_userProfile);

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


       return view;
    }
}
