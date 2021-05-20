package com.example.erunning;

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

public class UserSearch extends BasicActivity implements FirestoreAdapter.OnListItemClick {

    private RecyclerView recyclerView;
    private FirestoreAdapter adapter;
    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        firebaseFirestore = FirebaseFirestore.getInstance();
        findViewById(R.id.btn_back).setOnClickListener(onClickListener);


        recyclerView = findViewById(R.id.recycle_view_users); // 아이디 연결


        //Query
        Query query = firebaseFirestore.collection("users").orderBy("name"); // 데이터 정렬 orderBy

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
        Log.d("ITEM_CLICK", "Clicked an item" + position + " and the ID :" + snapshot.getId());

    }
}