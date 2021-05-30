package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearch extends BasicActivity implements FirestoreAdapter.OnListItemClick {

    private RecyclerView recyclerView;
    private FirestoreAdapter adapter;
    private FirebaseFirestore firebaseFirestore;

    private FirestoreRecyclerAdapter mAdapter;
    private RecyclerView mFirestoreList;

    private String cha;

    private ArrayList<String> mItemlist= new ArrayList<String>();

    private CircleImageView nullProfile;

    EditText search_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        firebaseFirestore = FirebaseFirestore.getInstance();
        findViewById(R.id.btn_back).setOnClickListener(onClickListener);


        recyclerView = findViewById(R.id.recycle_view_users); // 아이디 연결
        mFirestoreList = findViewById(R.id.recycle_view_users);


        //Query
        Query query = firebaseFirestore.collection("users")
                .orderBy("name", Query.Direction.ASCENDING);// 데이터 정렬 orderBy


        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        //추가
        FirestoreRecyclerOptions<UserInfo> options1 = new FirestoreRecyclerOptions.Builder<UserInfo>()
                .setQuery(query,UserInfo.class)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<UserInfo, UserInfoViewHolder1>(options1) {
            @NonNull
            @Override
            public UserInfoViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull UserInfoViewHolder1 userInfoViewHolder1, int i, @NonNull UserInfo userInfo) {
                userInfoViewHolder1.username.setText(userInfo.getName());
                userInfoViewHolder1.bio.setText(userInfo.getBio()+ "");
            }
        };

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(mAdapter);


        //추가 끝

        //RecyclerOptions
        FirestorePagingOptions<UserInfo> options = new FirestorePagingOptions.Builder<UserInfo>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserInfo.class)
                .build();

        adapter = new FirestoreAdapter(options, this); //musers = this

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.e("dd","로그확인 : " + options + " "+ query+" "+adapter+" "+recyclerView);

        search_bar = findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("fasdf","sadsf"+ s.toString());
                Query query = firebaseFirestore.collection( "users")
                        .whereEqualTo("name",s.toString())
                        .orderBy("name", Query.Direction.ASCENDING);

                FirestoreRecyclerOptions<UserInfo> options = new FirestoreRecyclerOptions.Builder<UserInfo>()
                .setQuery(query,UserInfo.class)
                .build();



            }
        });

    }


    //추가

    private class UserInfoViewHolder1 extends RecyclerView.ViewHolder {

        private TextView username;
        private TextView bio;
        private CircleImageView image_profile;

        public UserInfoViewHolder1(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            bio = itemView.findViewById(R.id.bio);
            image_profile = itemView.findViewById(R.id.image_profile);

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    private void createnamelist() {
        mItemlist = new ArrayList<>();
        firebaseFirestore.collection("users");

    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.btn_back:
                /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);*/
                finish();
                break;
        }
    };


    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        nullProfile = findViewById(R.id.iv_otherprofileimage);
        String name = snapshot.getData().get("name").toString();
        String post = snapshot.getData().get("post").toString();
        String follower = snapshot.getData().get("follower").toString();
        String following = snapshot.getData().get("following").toString();
        String UID = snapshot.getId();
        ArrayList<String> followerlist = (ArrayList<String>) snapshot.getData().get("followerlist");
        ArrayList<String> followinglist = (ArrayList<String>) snapshot.getData().get("followinglist");


        Intent intent = new Intent(getApplicationContext(), OtherAccount.class);

        if(snapshot.getData().get("photoUrl") != null) {
            String image = snapshot.getData().get("photoUrl").toString();
            intent.putExtra("userimage", image);
        }
        if(snapshot.getData().get("bio") != null){
            String bio = snapshot.getData().get("bio").toString();
            intent.putExtra("userbio", bio);
        }

        intent.putExtra("username", name);
        intent.putExtra("post",post);
        intent.putExtra("follower",follower);
        intent.putExtra("following",following);
        intent.putExtra("UID",UID);
        intent.putStringArrayListExtra("followerlist",followerlist);
        intent.putStringArrayListExtra("followinglist",followinglist);

        startActivity(intent);
        Log.e("ITEM_CLICK", "Clicked an item" + position + " and the ID :" + snapshot.getId() + " and count : " + snapshot.getData().get("count"));
    }


}