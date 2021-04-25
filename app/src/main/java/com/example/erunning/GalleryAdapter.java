package com.example.erunning;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.erunning.R;

import java.util.ArrayList;

//import static com.example.erunning.Util.INTENT_PATH;

public class GalleryAdapter  extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private String[] mDataset;

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public GalleryAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public GalleryAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        GalleryViewHolder galleryViewHolder = new GalleryViewHolder(v);
        /*v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                //resultIntent.putExtra(INTENT_PATH, mDataset.get(galleryViewHolder.getAdapterPosition()));
                activity.setResult(Activity.RESULT_OK, resultIntent);
                activity.finish();
            }
        });*/

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        TextView textView = holder.cardView.findViewById(R.id.tv_itemgallery);
        textView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}