package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OtherAccount extends AppCompatActivity {

    private String othername;
    private String otherbio;
    private String otherprofile;

    private String follower;
    private String following;
    private String post;
    private String otherUID;
    private ArrayList<String> followerlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_account);
        Log.d("dd","dd");

        TextView username = findViewById(R.id.tv_otheruserName);
        TextView userbio = findViewById(R.id.tv_otheruserInfo);
        ImageView userprofile = findViewById(R.id.iv_otherprofileimage);

        TextView tv_post = findViewById(R.id.account_tv_post_count);
        TextView tv_follower = findViewById(R.id.account_tv_follower_count);
        TextView tv_following = findViewById(R.id.account_tv_following_count);

        Intent intent = getIntent();

        othername = intent.getStringExtra("username");
        otherbio = intent.getStringExtra("userbio");
        otherprofile = intent.getStringExtra("userimage");
        post = intent.getStringExtra("post");
        follower = intent.getStringExtra("follower");
        following = intent.getStringExtra("following");
        otherUID = intent.getStringExtra("UID");


        username.setText(othername);
        if (otherbio != null)
            userbio.setText(otherbio);
        if (otherprofile != null)
            Glide.with(this).load(otherprofile).into(userprofile);

        tv_post.setText(post);
        tv_follower.setText(follower);
        tv_following.setText(following);


        ImageButton btn_back = (ImageButton)findViewById(R.id.btn_back2);
        Button btn_follow = findViewById(R.id.account_btn_follow);
        Button btn_unfollow = findViewById(R.id.account_btn_unfollow);

        final ArrayList<String> followerlist = new ArrayList<>();


        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int follower_num = Integer.parseInt(follower);
                follower_num += 1;
                follower = String.valueOf(follower_num);
                Log.e("follower", "팔로우" + follower + follower_num);
                tv_follower.setText(follower);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(otherUID).update("follower", Integer.parseInt(follower));

                followerlist.add(FirebaseFirestore.getInstance().collection("users").document(user.getUid()).toString()); //여기서 팔로잉 한 유저의 UID를 팔로잉 당한사람의 followerlist담아야함...

                btn_follow.setVisibility(v.GONE);
                btn_unfollow.setVisibility(v.VISIBLE);

            }

        });
        btn_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int follower_num = Integer.parseInt(follower);
                follower_num -= 1;
                follower = String.valueOf(follower_num);
                Log.e("follower", "팔로우" + follower + follower_num);
                tv_follower.setText(follower);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(otherUID).update("follower", Integer.parseInt(follower));

                btn_unfollow.setVisibility(v.GONE);
                btn_follow.setVisibility(v.VISIBLE);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserSearch.class);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
                startActivity(intent);


            }
        });

    }

}