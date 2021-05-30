package com.example.erunning;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import static com.example.erunning.Utillity.showToast;


public class InputFeature extends BottomSheetDialogFragment implements View.OnClickListener{

    public static InputFeature getInstance() { return new InputFeature(); }

    OnMyDialogResult mDialogResult;

    private EditText editFeature; // 경로 특징 글 작성란
    private ImageView imageGallery; // 첨부 이미지 뷰
    private Button btnPicture; // 사진첨부 버튼
    private Button btnFeature; // 등록 버튼

    private Fragment fragment;

    private FragmentActivity mContext;

    private ArrayList<String> pathList = new ArrayList<>();
    private ImageView selectedImageView;
    String profilePath;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    //pathList.add(profilePath);
                    //Toast.makeText(getContext(), profilePath, Toast.LENGTH_SHORT).show();

                    ContentsItemView contentsItemView = new ContentsItemView(getContext());

                    contentsItemView.setImage(profilePath);
                    imageGallery.setImageURI(Uri.parse(profilePath));
                    //imageGallery.setImageResource(profilePath);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedImageView = (ImageView) v;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {

        if((view.getId() != R.id.editFeature) && (view.getId() != R.id.imageGallery)
                && (view.getId() != R.id.btnPicture) && (view.getId() != R.id.btnFeature)) {
            dismiss();
        }

        switch (view.getId()){

            case R.id.btnPicture:
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        showToast(getActivity(), "권한을 허용해 주세요.");
                    }
                } else {
                    myStartActivity(Gallery.class, "image", 0);
                }
                break;
            case R.id.btnFeature:
                //Toast.makeText(getContext(),"feature",Toast.LENGTH_SHORT).show();

                if (fragment != null) {

                    if( mDialogResult != null ){
                        mDialogResult.finish(editFeature.getText().toString(), profilePath);
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
        void finish(String result1, String result2);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {

            }
        }
    };

    private void myStartActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(getContext(), c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }

}

