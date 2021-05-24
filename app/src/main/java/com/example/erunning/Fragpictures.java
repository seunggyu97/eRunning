package com.example.erunning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class Fragpictures extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private View rootView;

    private String mParam1;
    private String mParam2;

    public Fragpictures() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.frag_pictures, container, false);
        return rootView;
    }
}