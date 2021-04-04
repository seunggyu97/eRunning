package com.example.erunning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.erunning.R;

public class Feed extends Fragment {
 private View view;

   public static Feed newinstance(){
      Feed feed = new Feed();
      return feed;
   }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.feed, container, false);

       return view;
    }
}
