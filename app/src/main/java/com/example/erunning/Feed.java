package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Feed extends Fragment {
 private View view;
 private FloatingActionButton btn_user_search;
 private FloatingActionButton btn_add;
   public static Feed newinstance(){
      Feed feed = new Feed();
      return feed;
   }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.feed, container, false);


       btn_user_search = view.findViewById(R.id.btn_user_search);
       btn_add = view.findViewById(R.id.btn_add);

       btn_user_search.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               switch (v.getId()) {
                   case R.id.btn_user_search:
                       Intent intent = new Intent(getActivity(), UserSearch.class);
                       startActivityForResult(intent, MainActivity.REQUEST_USER_SEARCH);
                       break;
               }
           }
       });

       btn_add.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               switch (v.getId()) {
                   case R.id.btn_add:
                       Intent intent = new Intent(getActivity(), NewPost.class);
                       startActivityForResult(intent, MainActivity.REQUEST_POST);
                       break;
               }
           }
       });
       return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
