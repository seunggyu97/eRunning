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

public class FlagInfoDialog extends BasicActivity implements OnMapReadyCallback{
    private GoogleMap mgoogleMap;
    private TextView tv_msg;
    private TextView tv_description;
    private TextView tv_address;
    private TextView tv_starttime;
    private TextView tv_selectedcode;
    private Button btn_infocancel;
    private TextView tv_notice;
    private ImageView iv_selectedcode;
    private LatLng latLng;
    private FlagInfo flagdata;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FlagInfo flagInfo = (FlagInfo) getIntent().getSerializableExtra("flagInfo");

        flagdata = flagInfo; // 전역으로 설정

        setContentView(R.layout.flag_info_dialog);
        tv_msg = findViewById(R.id.et_msg_flag);
        tv_description = findViewById(R.id.tv_description);
        tv_address = findViewById(R.id.tv_address);
        tv_starttime = findViewById(R.id.tv_starttime);
        btn_infocancel = findViewById(R.id.btn_infocancel);
        tv_selectedcode = findViewById(R.id.tv_selectedcode);
        tv_notice = findViewById(R.id.tv_notice_flag);
        iv_selectedcode = findViewById(R.id.iv_selectedcode);


        latLng = new LatLng(Double.parseDouble(flagInfo.getLatitude()),Double.parseDouble(flagInfo.getLongitude())); // 좌표데이터를 받아온다.

        tv_msg.setText(flagInfo.getTitle());
        tv_description.setText(flagInfo.getDescription());
        tv_address.setText(flagInfo.getAddress());
        tv_starttime.setText(flagInfo.getStarthour() + "시 " + flagInfo.getStartminute() + "분");


        btn_infocancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlagInfoDialog.this, FlagPost.class);
                intent.putExtra("flagInfo", flagdata);
                startActivity(intent);
                finish();
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
}