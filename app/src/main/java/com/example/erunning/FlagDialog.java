package com.example.erunning;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import static com.example.erunning.Utillity.showToast;

public class FlagDialog extends BasicActivity implements OnMapReadyCallback{
    private GoogleMap mgoogleMap;
    private TextView tv_msg;
    private TextView tv_description;
    private TextView tv_address;
    private TextView tv_starttime;
    private TextView tv_selectedcode;
    private TextView tv_cancle;
    private TextView tv_participate;
    private TextView tv_notice;
    private ImageView iv_selectedcode;
    private LatLng latLng;
    private FlagInfo flagdata;
    private boolean isusermember;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlagInfo flagInfo = (FlagInfo) getIntent().getSerializableExtra("flagInfo");

        flagdata = flagInfo; // 전역으로 설정

        membercheck(); // 멤버 여부 체크

        setContentView(R.layout.flag_participate_dialog);
        tv_msg = findViewById(R.id.et_msg_flag);
        tv_description = findViewById(R.id.tv_description);
        tv_address = findViewById(R.id.tv_address);
        tv_starttime = findViewById(R.id.tv_starttime);
        tv_cancle = findViewById(R.id.cancle);
        tv_participate = findViewById(R.id.flag_participate);
        tv_selectedcode = findViewById(R.id.tv_selectedcode);
        tv_notice = findViewById(R.id.tv_notice_flag);
        iv_selectedcode = findViewById(R.id.iv_selectedcode);

        
        latLng = new LatLng(Double.parseDouble(flagInfo.getLatitude()),Double.parseDouble(flagInfo.getLongitude())); // 좌표데이터를 받아온다.
        
        tv_msg.setText(flagInfo.getTitle());
        tv_description.setText(flagInfo.getDescription());
        tv_address.setText(flagInfo.getAddress());
        tv_starttime.setText(flagInfo.getStarthour() + "시 " + flagInfo.getStartminute() + "분");
        if(Integer.parseInt(flagdata.getCurrentmember()) < Integer.parseInt(flagdata.getMaxpeople())) {
            tv_participate.setText("참여하기 (" + flagdata.getCurrentmember() + "/" + flagdata.getMaxpeople() + ")");
            tv_participate.setTextColor(Color.WHITE);
        }else{
            tv_participate.setText("참여불가(" + flagdata.getCurrentmember() + "/" + flagdata.getMaxpeople() + ")");
            tv_participate.setTextColor(Color.RED);
        }
        tv_participate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isusermember){
                    participate();
                }
            }
        });

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isusermember){
                    finish();
                }
            }
        });
        switch (flagInfo.getSportCode()) {
            case "0":
                tv_selectedcode.setText("선택 안함");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_directions_run_24_black);
                break;
            case "1":
                tv_selectedcode.setText("런닝");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_directions_run_24_black);
                break;
            case "2":
                tv_selectedcode.setText("싸이클");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_pedal_bike_24_black);
                break;
            case "3":
                tv_selectedcode.setText("실내운동");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_fitness_center_24);
                break;
            case "4":
                tv_selectedcode.setText("축구");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_sports_soccer_24);
                break;
            case "5":
                tv_selectedcode.setText("야구");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_sports_baseball_24);
                break;
            case "6":
                tv_selectedcode.setText("농구");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_sports_basketball_24);
                break;
            case "7":
                tv_selectedcode.setText("탁구");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_sports_table);
                break;
            case "8":
                tv_selectedcode.setText("테니스");
                iv_selectedcode.setImageResource(R.drawable.ic_baseline_sports_tennis_24);
                break;
            case "9":
                tv_selectedcode.setText(flagInfo.getSportText());
                iv_selectedcode.setImageResource(android.R.color.transparent);
                break;
        }


        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.flagmap);
        supportMapFragment.getMapAsync(this);
        
        
    }
    
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;
        Log.e("onMapReady","실행@#######@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        googleMap.addMarker(markerOptions);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.e("moveCameraTO",latLng.toString());
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });
    }

    private void participate(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("flags").document(flagdata.getId());
        documentReference.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String userId= user.getUid();
                        ArrayList<String> flagerList = (ArrayList<String>)document.getData().get("flager");
                        if(flagerList != null) {
                            int currentmember = Integer.parseInt(document.getData().get("currentmember").toString());
                            if(currentmember >= Integer.parseInt(flagdata.getMaxpeople())){
                                    showToast(this,"모집인원이 가득차 입장할 수 없습니다.");
                            }
                            else {
                                currentmember++;
                                flagdata.setCurrentmember(Integer.toString(currentmember));
                                flagerList.add(userId);
                                flagdata.setFlager(flagerList);
                                documentReference.update("currentmember", Integer.toString(currentmember)); // 파베 저장
                                documentReference.update("flager", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
                                Intent intent = new Intent(this, FlagPost.class);
                                intent.putExtra("flagInfo", flagdata);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else{
                            int currentmember = Integer.parseInt(document.getData().get("currentmember").toString());
                            if(currentmember >= Integer.parseInt(flagdata.getMaxpeople())){
                                showToast(this,"현재 인원이 가득찼습니다.");
                            }
                            else {
                                currentmember++;
                                flagdata.setCurrentmember(Integer.toString(currentmember));
                                ArrayList<String> flagerListNull = new ArrayList<>();
                                flagerListNull.add(userId);
                                flagdata.setFlager(flagerListNull);
                                documentReference.update("currentmember", Integer.toString(currentmember)); // 파베 저장
                                documentReference.update("flager", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가

                                Intent intent = new Intent(this, FlagPost.class);
                                intent.putExtra("flagInfo", flagdata);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            }
        }));
    }

    private void membercheck(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("flags").document(flagdata.getId());
        documentReference.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        ArrayList<String> flagerList = (ArrayList<String>)document.getData().get("flager");
                        final String Publisher = document.getData().get("publisher").toString();
                        Log.e("ddddddddd",Publisher + "::::::" + user.getUid());
                        if(flagerList != null){
                            if(Publisher.equals(user.getUid())){
                                Intent intent = new Intent(FlagDialog.this, FlagPost.class);
                                intent.putExtra("flagInfo",flagdata);
                                startActivity(intent);
                                finish();
                            }
                            if(flagerList.contains(user.getUid())){
                                Intent intent = new Intent(FlagDialog.this, FlagPost.class);
                                intent.putExtra("flagInfo",flagdata);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                isusermember = false;
                            }
                        }else{
                            if(Publisher.equals(user.getUid())){
                                Intent intent = new Intent(FlagDialog.this, FlagPost.class);
                                intent.putExtra("flagInfo",flagdata);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
            }
        }));
    }
}