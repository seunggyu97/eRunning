package com.example.erunning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSearch extends BasicActivity {

    private RecyclerView recyclerView;
    private FirestorePagingAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<UserInfo> arrayList;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        firebaseFirestore = FirebaseFirestore.getInstance();
        findViewById(R.id.btn_writingback).setOnClickListener(onClickListener);


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
                .setQuery(query, config, new SnapshotParser<UserInfo>() {
                    @NonNull
                    @Override
                    public UserInfo parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        UserInfo userInfo = snapshot.toObject(UserInfo.class);
                        String itemId = snapshot.getId();
                        userInfo.setItem_id(itemId);
                        return userInfo;
                    }
                })
                .build();

        adapter = new FirestorePagingAdapter<UserInfo, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent,false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder productsViewHolder, int i, @NonNull UserInfo userInfo) {
                productsViewHolder.username.setText(userInfo.getName());
                productsViewHolder.bio.setText(userInfo.getItem_id()+ "");
                if (userInfo.getPhotoUrl() != null)
                    Glide.with(productsViewHolder.itemView).load(userInfo.getPhotoUrl()).into(productsViewHolder.image_profile);

            }

            @Override
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                super.onLoadingStateChanged(state);
                switch (state){
                    case LOADING_INITIAL:
                        Log.d("PAGING_LOG","Loading Initial Data");
                        break;
                    case LOADING_MORE:
                        Log.d("PAGING_LOG","Loading Next Page");
                        break;
                    case FINISHED:
                        Log.d("PAGING_LOG","All Data Loaded");
                        break;
                    case ERROR:
                        Log.d("PAGING_LOG","Error Loading Data");
                        break;
                    case LOADED:
                        Log.d("PAGING_LOG","Total Items Loaded : " + getItemCount());
                        break;
                }
            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.btn_writingback:
                finish();
                break;
        }
    };

    private class ProductsViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private TextView bio;
        private CircleImageView image_profile;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            bio = itemView.findViewById(R.id.bio);
            image_profile = itemView.findViewById(R.id.image_profile);
        }
    }

}