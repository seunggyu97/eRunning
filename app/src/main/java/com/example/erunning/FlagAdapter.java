package com.example.erunning;

import android.app.Activity;
import android.app.Dialog;
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
import com.google.android.gms.maps.GoogleMap;
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
    private FlagDialog flagDialog;

    private TextView tv_notice;
    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_location;
    private TextView tv_sport;
    private TextView tv_info;
    private ImageView postmenu;

    private View LL_PostEdit;
    private View LL_PostDelete;
    private OnPostListener onPostListener;

    private int isSelected;

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
                Intent intent = new Intent(activity, FlagDialog.class);
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
        tv_info = holder.cardView.findViewById(R.id.tv_info);
        FlagInfo flagdata = mDataset.get(position);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


        setImageChange(flagdata);

        Log.e("1차 feedname","설정");
        String timesethour = flagdata.getStarthour() + "시 ";
        String timesetminute = flagdata.getStartminute() + "분";

        tv_title.setText(flagdata.getTitle());
        tv_location.setText(flagdata.getDescription());
        tv_time.setText(timesethour + timesetminute);
        switch (flagdata.getSportCode()){
            case "0":
                tv_sport.setText("선택 안함");
                break;
            case "1":
                tv_sport.setText("런닝");
                break;
            case "2":
                tv_sport.setText("싸이클");
                break;
            case "3":
                tv_sport.setText("실내운동");
                break;
            case "4":
                tv_sport.setText("축구");
                break;
            case "5":
                tv_sport.setText("야구");
                break;
            case "6":
                tv_sport.setText("농구");
                break;
            case "7":
                tv_sport.setText("탁구");
                break;
            case "8":
                tv_sport.setText("테니스");
                break;
            case "9":
                tv_sport.setText(flagdata.getSportText());
                break;


        }
        Log.e("로그: ", "데이터: " + mDataset.get(position).getTitle());
    }
    private void setImageChange(FlagInfo flagdata){
        String fCurrentmember = flagdata.getCurrentmember();
        String fMaxpeople = flagdata.getMaxpeople();
        tv_notice.setText(fCurrentmember + "/" + fMaxpeople);
        if(fCurrentmember.equals(fMaxpeople)){
            tv_notice.setTextColor(Color.RED);
        }
        if(flagdata.getFlager() != null){
            if(flagdata.getFlager().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                tv_info.setVisibility(View.VISIBLE);
            }
            else{
                tv_info.setVisibility(View.GONE);
            }
        }

    }
    private void participate(int position){
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
                                Intent intent = new Intent(activity, Flag.class);
                                intent.putExtra("flagInfo", mDataset.get(isSelected));
                                activity.startActivity(intent);
                            } else {// 좋아요가 눌러지지 않은 상태일 경우
                                int currentmember = Integer.parseInt(document.getData().get("currentmember").toString());
                                currentmember++;
                                flagdata.setCurrentmember(Integer.toString(currentmember));
                                flagerList.add(userId);
                                flagdata.setFlager(flagerList);
                                setImageChange(flagdata);
                                notifyDataSetChanged();
                                mDataset.get(position).setCurrentmember(Integer.toString(currentmember));
                                documentReference.update("flag", Integer.toString(currentmember)); // 파베 저장
                                documentReference.update("flager", FieldValue.arrayUnion(userId));// 참여 리스트에 자신 추가
                            }
                        }
                        else{
                            int currentmember = Integer.parseInt(document.getData().get("currentmember").toString());
                            currentmember++;
                            flagdata.setCurrentmember(Integer.toString(currentmember));
                            flagerList.add(userId);
                            flagdata.setFlager(flagerList);
                            setImageChange(flagdata);
                            notifyDataSetChanged();
                            mDataset.get(position).setCurrentmember(Integer.toString(currentmember));
                            documentReference.update("flag", Integer.toString(currentmember)); // 파베 저장
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