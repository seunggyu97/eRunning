package com.example.erunning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class InputFeature extends BottomSheetDialogFragment implements View.OnClickListener{

    public static InputFeature getInstance() { return new InputFeature(); }

    OnMyDialogResult mDialogResult;

    private EditText editFeature; // 경로 특징 글 작성란
    private ImageView imageGallery; // 첨부 이미지 뷰
    private Button btnPicture; // 사진첨부 버튼
    private Button btnFeature; // 등록 버튼

    private Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_feature, container,false);
        editFeature = view.findViewById(R.id.editFeature);
        imageGallery = view.findViewById(R.id.imageGallery);
        btnPicture = view.findViewById(R.id.btnPicture);
        btnFeature = view.findViewById(R.id.btnFeature);

        editFeature.setOnClickListener(this);
        imageGallery.setOnClickListener(this);
        btnPicture.setOnClickListener(this);
        btnFeature.setOnClickListener(this);

        /*btnFeature.setOnClickListener(new View.OnClickListener() { // Record로 이동
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle(); // 번들을 통해 값 전달
                bundle.putString("featuretext",editFeature.getText().toString());//번들에 넘길 값 저장
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Record record = new Record();//Record 선언
                record.setArguments(bundle);//번들을 Record로 보낼 준비
                transaction.replace(R.id.frame, record);
                transaction.commit();
            }
        });*/

        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("inputFeature");

        return view;
    }

    @Override
    public void onClick(View view) {

        if((view.getId() != R.id.editFeature) && (view.getId() != R.id.imageGallery)
                && (view.getId() != R.id.btnPicture) && (view.getId() != R.id.btnFeature)) {
            dismiss();
        }

        switch (view.getId()){
            case R.id.editFeature:
                Toast.makeText(getContext(),"edit",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageGallery:
                Toast.makeText(getContext(),"image",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnPicture:
                Toast.makeText(getContext(),"picture",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnFeature:
                //Toast.makeText(getContext(),"feature",Toast.LENGTH_SHORT).show();

                if (fragment != null) {

                    if( mDialogResult != null ){
                        mDialogResult.finish(editFeature.getText().toString());
                    }

                    BottomSheetDialogFragment dialogFragment = (BottomSheetDialogFragment) fragment;
                    dialogFragment.dismiss();
                }

                break;
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }

}

