package com.example.erunning;

import android.os.Bundle;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       view = inflater.inflate(R.layout.account, container, false);
       Bundle extra = this.getArguments();
       if (extra != null) {
          String user_name = extra.getString("sendUserName");
          String route_profile = extra.getString("route_profile");

          tv_userName = view.findViewById(R.id.tv_userName);
          tv_userName.setText(user_name);//닉네임 text를 텍스트 뷰에 세팅

          iv_userProfile = view.findViewById(R.id.iv_userProfile);
          Glide.with(this).load(route_profile).into(iv_userProfile); //프로필 url을 이미지 뷰에 세팅
       }



       return view;
    }
}
