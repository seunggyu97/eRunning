package com.example.erunning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AccountRecyclerViewAdapterSave extends RecyclerView.Adapter<AccountRecyclerViewAdapterSave.MyViewHolder> {
    private ArrayList<AccountpostItem> mAccountpostItem;
    private LayoutInflater mInflate;
    private Context mContext;

    public AccountRecyclerViewAdapterSave(Context context, ArrayList<AccountpostItem> accountpostItems) {
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mAccountpostItem = accountpostItems;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.accountsaveitemview,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String url = mAccountpostItem.get(position).photo;
        Glide.with(mContext)
                .load(url)
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mAccountpostItem.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
