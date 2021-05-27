package com.example.erunning;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.erunning.Utillity.isStorageUrl;

//import static com.example.erunning.Util.INTENT_PATH;

class FlagAdapter extends RecyclerView.Adapter<FlagAdapter.FlagViewHolder> {
    ArrayList<FlagInfo> mDataset;
    Activity activity;

    private TextView tv_notice;
    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_location;
    private TextView tv_sport;

    private ImageView postmenu;

    private View LL_PostEdit;
    private View LL_PostDelete;
    private OnPostListener onPostListener;
    public static class FlagViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public FlagViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public FlagAdapter(Activity activity, ArrayList<FlagInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public FlagAdapter.FlagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flag, parent, false);

        final FlagViewHolder flagViewHolder = new FlagViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Post.class);
                intent.putExtra("flagInfo", mDataset.get(flagViewHolder.getAdapterPosition()));

                activity.startActivity(intent);
            }
        });

        postmenu = cardView.findViewById(R.id.btn_postmenu);
        postmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("메뉴버튼 ", parent + v.toString());
                switch (v.getId()) {
                    case R.id.btn_postmenu:
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                activity, R.style.BottomSheetDialogTheme
                        );
                        View bottomSheetView = LayoutInflater.from(activity.getApplicationContext())
                                .inflate(
                                        R.layout.post_bottom_sheet,
                                        (LinearLayout) cardView.findViewById(R.id.PostbottomSheetContainer)
                                );
                        LL_PostEdit = bottomSheetView.findViewById(R.id.LL_PostEdit);
                        LL_PostDelete = bottomSheetView.findViewById(R.id.LL_PostDelete);

                        LL_PostEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.LL_PostEdit:
                                        bottomSheetDialog.dismiss();
                                        onPostListener.onEdit(flagViewHolder.getAdapterPosition());
                                        Log.e("게시물 수정 ", "클릭" + v);
                                        break;
                                }
                            }
                        });
                        LL_PostDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.LL_PostDelete:
                                        bottomSheetDialog.dismiss();
                                        onPostListener.onDelete(flagViewHolder.getAdapterPosition());
                                        Log.e("게시물 삭제", "클릭" + v);
                                        break;
                                }
                            }
                        });


                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();

                        break;
                }
            }
        });

        return flagViewHolder;
    }

    @Override
    public void onBindViewHolder(final FlagViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        Log.e("ref ::::",FirebaseDatabase.getInstance().getReference().child("flags").toString());

        tv_title = holder.cardView.findViewById(R.id.tv_title);
        tv_notice = holder.cardView.findViewById(R.id.tv_notice);
        tv_time = holder.cardView.findViewById(R.id.tv_time);
        tv_location = holder.cardView.findViewById(R.id.tv_location);
        tv_sport = holder.cardView.findViewById(R.id.tv_sport);

        FlagInfo flagdata = mDataset.get(position);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


        setImageChange(flagdata);

        Log.e("1차 feedname","설정");
        String timesethour = flagdata.getStarthour() + "시 ";
        String timesetminute = flagdata.getStartminute() + "분";

        tv_title.setText(flagdata.getTitle());
        tv_location.setText(flagdata.getDescription());
        tv_time.setText(timesethour + timesetminute);


/*        btn_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Like(position);
            }
        });*/



        /*DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("posts").document(mDataset.get(position).getId());
        documentReference1.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String getLike = document.getData().get("like").toString();
                        if(getLike.equals("0")){

                            btn_like.setImageResource(R.drawable.ic_like_gray);
                            tv_like_upside.setVisibility(View.GONE);
                            tv_like.setVisibility(View.VISIBLE);
                            tv_like.setText("좋아요");
                            tv_like.setTextColor(Color.parseColor("#000000"));
                            Log.e("tv_like,btn","setVisibility(View.GONE),setImageResource(R.drawable.ic_like_gray);");
                        }else{
                            String SetLike = "좋아요 "+ getLike+"개";
                            btn_like.setImageResource(R.drawable.ic_like_red);
                            tv_like_upside.setVisibility(View.VISIBLE);
                            tv_like_upside.setText(SetLike);;
                            tv_like.setText(getLike);
                            tv_like.setTextColor(Color.parseColor("#ff3300"));
                            Log.e("tv_like,btn","setVisibility(View.VISIBLE),setImageResource(R.drawable.ic_like_red);");
                        }

                    }
                }
            }
        }));*/


        Log.e("로그: ", "데이터: " + mDataset.get(position).getTitle());
    }
    private void setImageChange(FlagInfo flagdata){

        tv_notice.setText(flagdata.getFlag() + "/5");

    }
    private void Like(int position){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("flags").document(mDataset.get(position).getId());
        documentReference.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String userId= user.getUid();
                        FlagInfo flagdata = mDataset.get(position);
                        ArrayList<String> flagerList = (ArrayList<String>)document.getData().get("flager");
                        if(flagerList != null) {
                            if (flagerList.contains(userId)) {// 참여중인 경우
                                int fLike = Integer.parseInt(document.getData().get("flag").toString());
                                fLike--;
                                flagerList.remove(userId);
                                flagdata.setFlager(flagerList);
                                flagdata.setFlag(Integer.toString(fLike));
                                setImageChange(flagdata);
                                notifyDataSetChanged();
                                documentReference.update("flag", Integer.toString(fLike)); // 파베 저장
                                documentReference.update("flager", FieldValue.arrayRemove(userId));//참여 리스트에서 자신 삭제

                            } else {// 좋아요가 눌러지지 않은 상태일 경우
                                int fLike = Integer.parseInt(document.getData().get("flag").toString());
                                fLike++;
                                flagdata.setFlag(Integer.toString(fLike));
                                flagerList.add(userId);
                                flagdata.setFlager(flagerList);
                                setImageChange(flagdata);
                                notifyDataSetChanged();
                                mDataset.get(position).setFlag(Integer.toString(fLike));
                                documentReference.update("flag", Integer.toString(fLike)); // 파베 저장
                                documentReference.update("flager", FieldValue.arrayUnion(userId));// 참여 리스트에 자신 추가
                            }
                        }
                        else{
                            ArrayList<String> likerListNull = new ArrayList<String>();
                            int fLike = Integer.parseInt(document.getData().get("flag").toString());
                            fLike++;
                            flagdata.setFlag(Integer.toString(fLike));
                            likerListNull.add(userId);
                            flagdata.setFlager(likerListNull);
                            setImageChange(flagdata);
                            notifyDataSetChanged();
                            mDataset.get(position).setFlag(Integer.toString(fLike));
                            documentReference.update("flag", Integer.toString(fLike)); // 파베 저장
                            documentReference.update("flager", FieldValue.arrayUnion(userId));// 참여 리스트에 자신 추가
                        }
                    }
                }
            }
        }));
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}