package com.example.erunning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteListViewHolder> {
    private ArrayList<RouteListItem> mPersons;
    private LayoutInflater mInflate;
    private Context mContext;

    public RouteListAdapter(Context context, ArrayList<RouteListItem> persons) {
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mPersons = persons;
    }

    @NonNull
    @Override
    public RouteListAdapter.RouteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.route_list_item, parent, false);
        RouteListAdapter.RouteListViewHolder viewHolder = new RouteListAdapter.RouteListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RouteListAdapter.RouteListViewHolder holder, int position) {
        //데이터오 뷰를 바인딩
        String url = mPersons.get(position).routePic;
        /*Glide.with(mContext)
                .load(url)
                .centerCrop()
                .transition(withCrossFade())
                .into(holder.profile);*/

    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    //ViewHolder
    public static class RouteListViewHolder extends RecyclerView.ViewHolder {
        public ImageView routePic;

        public RouteListViewHolder(View itemView) {
            super(itemView);

            routePic = (ImageView) itemView.findViewById(R.id.iv_route);
        }
    }
}
