package com.example.erunning;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.AnalyticsViewHolder> {

    private ArrayList<AnalyticsItem> mPersons;
    private LayoutInflater mInflate;
    private Context mContext;

    public AnalyticsAdapter(Context context, ArrayList<AnalyticsItem> persons) {
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mPersons = persons;
    }

    @NonNull
    @Override
    public AnalyticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.analytics_item, parent, false);
        AnalyticsViewHolder viewHolder = new AnalyticsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyticsAdapter.AnalyticsViewHolder holder, int position) {
        //데이터오 뷰를 바인딩
        String url = mPersons.get(position).profile;
        holder.name.setText(mPersons.get(position).name);
        /*Glide.with(mContext)
                .load(url)
                .centerCrop()
                .transition(withCrossFade())
                .into(holder.profile);*/
        holder.rank.setText(mPersons.get(position).rank);
        holder.at_step.setText(String.valueOf(mPersons.get(position).at_step));

    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    //ViewHolder
    public static class AnalyticsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView profile;
        public TextView rank;
        public TextView at_step;

        public AnalyticsViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.at_name);
            profile = (ImageView) itemView.findViewById(R.id.at_image_profile);
            rank = (TextView) itemView.findViewById(R.id.rank);
            at_step = (TextView) itemView.findViewById(R.id.at_step);
        }
    }

}
