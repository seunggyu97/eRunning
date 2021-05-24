package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OtherAccount extends AppCompatActivity {

    private String othername;
    private String otherbio;
    private String otherprofile;

    private String otherfollower;
    private String otherfollowing; //팔로잉을 한사람
    private String following; //팔로앙을 당한사람
    private String otherpost;
    private String otherUID;
    private ArrayList<String> otherfollowerlist = new ArrayList<>();
    private ArrayList<String> followerlist = new ArrayList<>();
    private ArrayList<String> otherfollowinglist = new ArrayList<>();
    private ArrayList<String> followinglist = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_account);

        TextView username = findViewById(R.id.tv_other_userName);
        TextView userbio = findViewById(R.id.tv_other_userInfo);
        ImageView userprofile = findViewById(R.id.iv_otherprofileimage);
        TextView tv_otherpost = findViewById(R.id.account_tv_other_post_count);
        TextView tv_otherfollower = findViewById(R.id.account_tv_other_follower_count);
        TextView tv_otherfollowing = findViewById(R.id.account_tv_other_following_count);
        TextView tv_following = findViewById(R.id.account_tv_following_count);
        Button btn_follow = findViewById(R.id.account_btn_follow);
        Button btn_unfollow = findViewById(R.id.account_btn_unfollow);
        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back2);

        Intent intent = getIntent();

        othername = intent.getStringExtra("username");
        otherbio = intent.getStringExtra("userbio");
        otherprofile = intent.getStringExtra("userimage");
        otherpost = intent.getStringExtra("post");
        otherfollower = intent.getStringExtra("follower");
        otherfollowing = intent.getStringExtra("following");
        otherUID = intent.getStringExtra("UID");

        otherfollowerlist = intent.getStringArrayListExtra("followerlist");
        otherfollowinglist = intent.getStringArrayListExtra("followinglist");



        username.setText(othername);
        if (otherbio != null)
            userbio.setText(otherbio);
        if (otherprofile != null)
            Glide.with(this).load(otherprofile).into(userprofile);

        tv_otherpost.setText(otherpost);
        tv_otherfollower.setText(otherfollower);
        tv_otherfollowing.setText(otherfollowing);




        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore otherdb = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = otherdb.collection("users").document(otherUID);
        DocumentReference documentReference2 = db.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String FUL = document.getData().get("followerlist").toString();
                        String ID = user.getUid();
                        if (otherfollowerlist.contains(ID)) { //팔로잉을 당하는 유저 팔로워리스트에 팔로우하는사람의 ID가면 배열에 있으면
                           btn_follow.setVisibility(View.GONE);
                           btn_unfollow.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }));


        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int follower_num = Integer.parseInt(otherfollower);
                follower_num += 1;
                otherfollower = String.valueOf(follower_num);
                tv_otherfollower.setText(otherfollower);

                FirebaseFirestore otherdb = FirebaseFirestore.getInstance();
                otherdb.collection("users").document(otherUID).update("follower", Integer.parseInt(otherfollower));   // 팔로윙을 당한사람의 팔로워를 +1


                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection("users").document(user.getUid());
                documentReference.get().addOnCompleteListener((task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        following = document.getData().get("following").toString();
                                        int following_num = Integer.parseInt(following); following_num += 1;
                                        following = String.valueOf(following_num);

                                        String FUID = document.getId();
                                        db.collection("users").document(document.getId())
                                                .update("following", Integer.parseInt(following)); // 팔로윙을 한 사람 팔로잉을 +1
                                        db.collection("users").document(document.getId()).update(
                                                "followinglist", FieldValue.arrayUnion(otherUID)); //팔로우 당한사람의 ID를 팔로우 건사람의 followinglist에 넣

                                        otherdb.collection("users").document(otherUID).update(
                                                "followerlist", FieldValue.arrayUnion(FUID)); // 팔로우기 한사람의 ID를 팔로우 당한사람의 followerlist에 넣는다 !!
                                    }
                                }
                            }
                        }));


                btn_follow.setVisibility(v.GONE);
                btn_unfollow.setVisibility(v.VISIBLE);

            }

        });
        btn_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int follower_num = Integer.parseInt(otherfollower);
                follower_num -= 1;
                otherfollower = String.valueOf(follower_num);
                tv_otherfollower.setText(otherfollower);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(otherUID).update("follower", Integer.parseInt(otherfollower));

                FirebaseFirestore otherdb = FirebaseFirestore.getInstance(); //팔로우 당한사람 otherdb
                final FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db2.collection("users").document(user2.getUid());
                documentReference.get().addOnCompleteListener((task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                following = document.getData().get("following").toString();
                                int following_num = Integer.parseInt(following); following_num -= 1;
                                following = String.valueOf(following_num);

                                db.collection("users").document(document.getId())
                                        .update("following", Integer.parseInt(following)); // 팔로윙을 한 사람 팔로잉을 -1
                                String FUID = document.getId();
                                db.collection("users").document(document.getId()).update(
                                        "followinglist", FieldValue.arrayRemove(otherUID));
                                otherdb.collection("users").document(otherUID).update(
                                        "followerlist", FieldValue.arrayRemove(FUID)
                                );
                            }
                        }
                    }
                }));
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

