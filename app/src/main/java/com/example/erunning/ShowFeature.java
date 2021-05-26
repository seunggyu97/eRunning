package com.example.erunning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ShowFeature extends BottomSheetDialogFragment implements View.OnClickListener {

    public static ShowFeature getInstance() { return new ShowFeature(); }

    private TextView textFeature; // 경로 특징 글 텍스트뷰
    private ImageView imageFeature; // 경로 특징 사진 이미지뷰

    private Fragment fragment;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_feature, container, false);
        textFeature = view.findViewById(R.id.textFeature);
        imageFeature = view.findViewById(R.id.imageFeature);

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("showFeature");

        // Record.java에서 특징 글 가져오기
        Bundle mArgs = getArguments();
        String showfeaturetext = mArgs.getString("showfeaturetext");

        textFeature.setText(showfeaturetext);

        return view;
    }

    @Override
    public void onClick(View view) {

        if((view.getId() != R.id.textFeature) && (view.getId() != R.id.imageFeature)) {
            dismiss();
        }

    }
}
