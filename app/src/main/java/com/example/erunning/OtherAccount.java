package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class OtherAccount extends AppCompatActivity {

    private String othername;
    private String otherbio;
    private String otherprofile;
    Feed feed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_account);
        Log.d("dd","dd");

        TextView username = findViewById(R.id.tv_otheruserName);
        TextView userbio = findViewById(R.id.tv_otheruserInfo);
        ImageView userprofile = findViewById(R.id.iv_otherprofileimage);

        Intent intent = getIntent();

        othername = intent.getStringExtra("username");
        otherbio = intent.getStringExtra("userbio");
        otherprofile = intent.getStringExtra("userimage");

        username.setText(othername);
        if (otherbio != null)
            userbio.setText(otherbio);
        if (otherprofile != null)
            Glide.with(this).load(otherprofile).into(userprofile);




        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back2);

//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);

            }
        });



    }
}