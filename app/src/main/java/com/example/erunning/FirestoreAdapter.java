package com.example.erunning;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class FirestoreAdapter extends FirestorePagingAdapter<UserInfo, FirestoreAdapter.ProductsViewHolder> {

    private  OnListItemClick onListItemClick;


    public FirestoreAdapter(@NonNull FirestorePagingOptions<UserInfo> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductsViewHolder productsViewHolder, int i, @NonNull UserInfo userInfo) {
        productsViewHolder.username.setText(userInfo.getName());
        productsViewHolder.bio.setText(userInfo.getBio()+ "");
        if (userInfo.getPhotoUrl() != null)
            Glide.with(productsViewHolder.itemView).load(userInfo.getPhotoUrl()).into(productsViewHolder.image_profile);

    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent,false);
        return new ProductsViewHolder(view);
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

    public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView username;
        private TextView bio;
        private CircleImageView image_profile;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            bio = itemView.findViewById(R.id.bio);
            image_profile = itemView.findViewById(R.id.image_profile);

            username.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListItemClick.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }
    public interface OnListItemClick {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
}
