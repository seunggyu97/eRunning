package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearch extends BasicActivity implements FirestoreAdapter.OnListItemClick {

    private RecyclerView recyclerView;
    private FirestoreAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView nullProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        firebaseFirestore = FirebaseFirestore.getInstance();
        findViewById(R.id.btn_back).setOnClickListener(onClickListener);


        recyclerView = findViewById(R.id.recycle_view_users); // 아이디 연결


        //Query
        Query query = firebaseFirestore.collection("users").orderBy("name").startAt("s"); // 데이터 정렬 orderBy

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        //RecyclerOptions
        FirestorePagingOptions<UserInfo> options = new FirestorePagingOptions.Builder<UserInfo>()
                .setLifecycleOwner(this)
                .setQuery(query, config, UserInfo.class)
                .build();

        adapter = new FirestoreAdapter(options, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
        }
    };


    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        nullProfile = findViewById(R.id.iv_otherprofileimage);
        String name = snapshot.getData().get("name").toString();
//        String post = snapshot.getData().get("post").toString();
//        String follower = snapshot.getData().get("follower").toString();
//        String following = snapshot.getData().get("following").toString();


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


        startActivity(intent);
        Log.d("ITEM_CLICK", "Clicked an item" + position + " and the ID :" + snapshot.getId() + " and username : " + snapshot.getData().get("username"));
    }
}