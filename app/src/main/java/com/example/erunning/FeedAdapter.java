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

class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {
    ArrayList<PostInfo> mDataset;
    Activity activity;

    private ImageView btn_like;
    private ImageView btn_bookmark;
    private Button btn_comment;
    private Button tv_like;
    private TextView tv_comment;
    private TextView tv_like_upside;
    private ImageView postmenu;
    private View LL_PostEdit;
    private View LL_PostDelete;
    private Drawable drawable;
    private OnPostListener onPostListener;

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public FeedViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public FeedAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
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
    public FeedAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        final FeedViewHolder feedViewHolder = new FeedViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Post.class);
                intent.putExtra("postInfo", mDataset.get(feedViewHolder.getAdapterPosition()));
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
                                        onPostListener.onEdit(feedViewHolder.getAdapterPosition());
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
                                        onPostListener.onDelete(feedViewHolder.getAdapterPosition());
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

        return feedViewHolder;
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        Log.e("ref ::::",FirebaseDatabase.getInstance().getReference().child("posts").toString());
        btn_comment = holder.cardView.findViewById(R.id.btn_comment);
        btn_like = holder.cardView.findViewById(R.id.btn_like);
        btn_bookmark = holder.cardView.findViewById(R.id.btn_bookmark);
        tv_like = holder.cardView.findViewById(R.id.tv_like);
        tv_like_upside = holder.cardView.findViewById(R.id.tv_like_upside);
        tv_comment = holder.cardView.findViewById(R.id.tv_comment_upside);
        CircleImageView iv_profileImage = holder.cardView.findViewById(R.id.iv_profileimage);
        TextView titleTextView = holder.cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());
        TextView tv_feedname = holder.cardView.findViewById(R.id.tv_feedname);
        TextView tv_like_upside = holder.cardView.findViewById(R.id.tv_like_upside);
        PostInfo postdata = mDataset.get(position);

        /*if(mDataset.get(position).getLike().equals("0")){
            tv_like_upside.setVisibility(View.GONE);
            tv_like.setVisibility(View.VISIBLE);

            tv_like.setText("좋아요");
            tv_like.setTextColor(Color.parseColor("#000000"));
        }else{
            String SetLike = "좋아요 "+ mDataset.get(position).getLike()+"개";

            tv_like_upside.setVisibility(View.VISIBLE);
            tv_like_upside.setText(SetLike);;
            tv_like.setTextColor(Color.parseColor("#ff3300"));
        }*/
        setImageChange(postdata);
        if(postdata.getComment().equals("0")){
            tv_comment.setVisibility(View.GONE);

        }else{
            String SetComment = "댓글 "+ postdata.getComment()+"개";

            tv_comment.setVisibility(View.VISIBLE);
            tv_comment.setText(SetComment);
        }
        tv_feedname.setText(postdata.getPublisherName());
        Log.e("1차 feedname","설정");
        if(postdata.getPhotoUrl() != null) {
            Glide.with(activity).load(postdata.getPhotoUrl()).circleCrop().into(iv_profileImage);
        }
        Log.e("1차 프사","설정");
        TextView createdAtTextView = cardView.findViewById(R.id.createdAtTextView);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(postdata.getPublisher());
        documentReference.get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document != null){
                    if(document.exists()){
                        tv_feedname.setText(document.getData().get("name").toString());
                        Log.e("2차 feedname","설정");
                        if(document.getData().get("photoUrl") != null){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            storageRef.child("users/" +postdata.getPublisher()+"/profile_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //이미지 로드 성공시

                                    Glide.with(activity).load(uri).circleCrop().into(iv_profileImage);
                                    Log.e("2차 프사","설정");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //이미지 로드 실패시
                                    Log.e("프로필 이미지 로드","실패");
                                }
                            });

                        }else{
                            iv_profileImage.setImageResource(R.drawable.ic_account);
                        }
                    }
                }
            }
        }));

        btn_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Like(position);
            }
        });
        tv_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Like(position);
            }
        });
        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Post.class);
                intent.putExtra("postInfo", mDataset.get(position));
                activity.startActivity(intent);
                Log.e("comment : ", "클릭");
            }
        });


        /*bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBookmarked) {
                    like.setImageResource(R.drawable.ic_bookmark);
                    Log.e("bookmark : ", "클릭");
                } else {
                    like.setImageResource(R.drawable.ic_nobookmark);
                    Log.e("bookmark : ", "클릭");
                }
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


        long now = System.currentTimeMillis(); // 현재 시간 변수 생성
        SimpleDateFormat dateSet = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분"); // date형식 지정
        String date = dateSet.format(new Date()); //현재 시각을 date형식에 맞게 저장
        String CreatedDate = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분", Locale.getDefault()).format(mDataset.get(position).getCreatedAt());
        // 게시글 만든 시간
        try {
            Date startDate = dateSet.parse(date);
            Date endDate = dateSet.parse(CreatedDate);
            long duration = startDate.getTime() - endDate.getTime();
            long min = duration / 60000;
            if (min <= 1) {
                createdAtTextView.setText("방금 전");
            } else if (min == 2) {
                createdAtTextView.setText("1분 전");
            } else if (min == 3) {
                createdAtTextView.setText("2분 전");
            } else if (min == 4) {
                createdAtTextView.setText("3분 전");
            } else if (min == 5) {
                createdAtTextView.setText("4분 전");
            } else if (min == 6) {
                createdAtTextView.setText("5분 전");
            } else if (min == 7) {
                createdAtTextView.setText("6분 전");
            } else if (min == 8) {
                createdAtTextView.setText("7분 전");
            } else if (min == 9) {
                createdAtTextView.setText("8분 전");
            } else if (min == 10) {
                createdAtTextView.setText("9분 전");
            } else if (min == 11) {
                createdAtTextView.setText("10분 전");
            } else if (min == 12) {
                createdAtTextView.setText("11분 전");
            } else if (min == 13) {
                createdAtTextView.setText("12분 전");
            } else if (min == 14) {
                createdAtTextView.setText("13분 전");
            } else if (min == 15) {
                createdAtTextView.setText("14분 전");
            } else if (min == 16) {
                createdAtTextView.setText("15분 전");
            } else if (min == 17) {
                createdAtTextView.setText("16분 전");
            } else if (min == 18) {
                createdAtTextView.setText("17분 전");
            } else if (min == 19) {
                createdAtTextView.setText("18분 전");
            } else if (min == 20) {
                createdAtTextView.setText("19분 전");
            } else if (min == 21) {
                createdAtTextView.setText("20분 전");
            } else if (min == 22) {
                createdAtTextView.setText("21분 전");
            } else if (min == 23) {
                createdAtTextView.setText("22분 전");
            } else if (min == 24) {
                createdAtTextView.setText("23분 전");
            } else if (min == 25) {
                createdAtTextView.setText("24분 전");
            } else if (min == 26) {
                createdAtTextView.setText("25분 전");
            } else if (min == 27) {
                createdAtTextView.setText("26분 전");
            } else if (min == 28) {
                createdAtTextView.setText("27분 전");
            } else if (min == 29) {
                createdAtTextView.setText("28분 전");
            } else if (min == 30) {
                createdAtTextView.setText("31분 전");
            } else if (min == 31) {
                createdAtTextView.setText("32분 전");
            } else if (min == 32) {
                createdAtTextView.setText("33분 전");
            } else if (min == 33) {
                createdAtTextView.setText("34분 전");
            } else if (min == 34) {
                createdAtTextView.setText("35분 전");
            } else if (min == 35) {
                createdAtTextView.setText("36분 전");
            } else if (min == 36) {
                createdAtTextView.setText("37분 전");
            } else if (min == 37) {
                createdAtTextView.setText("38분 전");
            } else if (min == 38) {
                createdAtTextView.setText("39분 전");
            } else if (min == 39) {
                createdAtTextView.setText("40분 전");
            } else if (min == 40) {
                createdAtTextView.setText("41분 전");
            } else if (min == 41) {
                createdAtTextView.setText("42분 전");
            } else if (min == 42) {
                createdAtTextView.setText("43분 전");
            } else if (min == 43) {
                createdAtTextView.setText("44분 전");
            } else if (min == 44) {
                createdAtTextView.setText("45분 전");
            } else if (min == 45) {
                createdAtTextView.setText("46분 전");
            } else if (min == 46) {
                createdAtTextView.setText("47분 전");
            } else if (min == 47) {
                createdAtTextView.setText("48분 전");
            } else if (min == 48) {
                createdAtTextView.setText("49분 전");
            } else if (min == 49) {
                createdAtTextView.setText("50분 전");
            } else if (min == 50) {
                createdAtTextView.setText("51분 전");
            } else if (min == 51) {
                createdAtTextView.setText("52분 전");
            } else if (min == 52) {
                createdAtTextView.setText("53분 전");
            } else if (min == 53) {
                createdAtTextView.setText("54분 전");
            } else if (min == 54) {
                createdAtTextView.setText("55분 전");
            } else if (min == 55) {
                createdAtTextView.setText("56분 전");
            } else if (min == 56) {
                createdAtTextView.setText("57분 전");
            } else if (min == 57) {
                createdAtTextView.setText("58분 전");
            } else if (min == 58) {
                createdAtTextView.setText("59분 전");
            } else if (min == 59) {
                createdAtTextView.setText("1시간 전");
            } else if (min >= 60 && min <= 119) {
                createdAtTextView.setText("+ 1시간");
            } else if (min >= 120 && min <= 179) {
                createdAtTextView.setText("+ 2시간");
            } else if (min >= 180 && min <= 239) {
                createdAtTextView.setText("+ 3시간");
            } else if (min >= 240 && min <= 299) {
                createdAtTextView.setText("+ 4시간");
            } else if (min >= 300 && min <= 359) {
                createdAtTextView.setText("+ 5시간");
            } else if (min >= 360 && min <= 419) {
                createdAtTextView.setText("+ 6시간");
            } else if (min >= 420 && min <= 479) {
                createdAtTextView.setText("+ 7시간");
            } else if (min >= 480 && min <= 539) {
                createdAtTextView.setText("+ 8시간");
            } else if (min >= 540 && min <= 599) {
                createdAtTextView.setText("+ 9시간");
            } else if (min >= 600 && min <= 659) {
                createdAtTextView.setText("+ 10시간");
            } else if (min >= 660 && min <= 719) {
                createdAtTextView.setText("+ 11시간");
            } else if (min >= 720 && min <= 779) {
                createdAtTextView.setText("+ 12시간");
            } else if (min >= 780 && min <= 839) {
                createdAtTextView.setText("+ 13시간");
            } else if (min >= 840 && min <= 899) {
                createdAtTextView.setText("+ 14시간");
            } else if (min >= 900 && min <= 959) {
                createdAtTextView.setText("+ 15시간");
            } else if (min >= 960 && min <= 719) {
                createdAtTextView.setText("+ 16시간");
            } else if (min >= 720 && min <= 779) {
                createdAtTextView.setText("+ 17시간");
            } else if (min >= 780 && min <= 839) {
                createdAtTextView.setText("+ 18시간");
            } else if (min >= 840 && min <= 899) {
                createdAtTextView.setText("+ 19시간");
            } else if (min >= 900 && min <= 959) {
                createdAtTextView.setText("+ 20시간");
            } else if (min >= 960 && min <= 1019) {
                createdAtTextView.setText("+ 21시간");
            } else if (min >= 1020 && min <= 1079) {
                createdAtTextView.setText("+ 22시간");
            } else if (min >= 1080 && min <= 1139) {
                createdAtTextView.setText("+ 23시간");
            } else if (min >= 1140 && min <= 1199) {
                createdAtTextView.setText("+ 1일 전");
            } else {
                createdAtTextView.setText(CreatedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentList = mDataset.get(position).getContents();

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentList)) {
            contentsLayout.setTag(contentList);
            contentsLayout.removeAllViews();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) {
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else {
                    TextView textView = new TextView(activity);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    contentsLayout.addView(textView);
                }
            }
        }
        Log.e("로그: ", "데이터: " + mDataset.get(position).getTitle());
        //Log.e("글이 올라온 시간 : ",Locale.getDefault().format(mDataset.get(position).getCreatedAt()));
    }
    private void setImageChange(PostInfo postdata){
        if(!postdata.getLike().equals("0")){
            tv_like_upside.setVisibility(View.VISIBLE);
            tv_like_upside.setText("좋아요 " + postdata.getLike() + "개");
        }
        else{
            tv_like_upside.setVisibility(View.GONE);
        }
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference2 = firebaseFirestore.collection("posts").document(postdata.getId());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(postdata.getLiker() != null){
            if(postdata.getLiker().contains(user.getUid())) {                   //postdata liker 리스트에 본인 uid 가 포함되어있으면
                String getLike = postdata.getLike();
                btn_like.setImageResource(R.drawable.ic_like_red);              // btn_like의 리소스를 빨간하트로 바꾼다.
                tv_like.setText(getLike);                                       // tv_like의 텍스트를 postdata의 Like로 변경한다.
                tv_like.setTextColor(Color.parseColor("#ff3300"));   // tv_like의 텍스트 색상을 빨강으로 바꾼다.
            }
            else {
                btn_like.setImageResource(R.drawable.ic_like_gray);
                tv_like.setText("좋아요");
                tv_like.setTextColor(Color.parseColor("#000000"));
            }
        }
        else{
            btn_like.setImageResource(R.drawable.ic_like_gray);
            tv_like.setText("좋아요");
            tv_like.setTextColor(Color.parseColor("#000000"));
        }

                        /*if(likerList != null) {
                            if (likerList.contains(userId)) {// 좋아요가 눌러진 상태일 경우
                                String getLike = document.getData().get("like").toString();
                                btn_like.setImageResource(R.drawable.ic_like_red);
                                tv_like.setText(getLike);
                                tv_like.setTextColor(Color.parseColor("#ff3300"));
                            } else {
                                btn_like.setImageResource(R.drawable.ic_like_gray);
                                tv_like.setText("좋아요");
                                tv_like.setTextColor(Color.parseColor("#000000"));
                            }
                        }
                        else{
                            btn_like.setImageResource(R.drawable.ic_like_gray);
                            tv_like.setText("좋아요");
                            tv_like.setTextColor(Color.parseColor("#000000"));
                        }*/
    }
    private void Like(int position){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("posts").document(mDataset.get(position).getId());
        documentReference.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String userId= user.getUid();
                        PostInfo postdata = mDataset.get(position);
                        ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                        if(likerList != null) {
                            if (likerList.contains(userId)) {// 좋아요가 눌러진 상태일 경우
                                int fLike = Integer.parseInt(document.getData().get("like").toString());
                                fLike--;
                                likerList.remove(userId);
                                postdata.setLiker(likerList);
                                postdata.setLike(Integer.toString(fLike));
                                setImageChange(postdata);
                                notifyDataSetChanged();
                                documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                                documentReference.update("liker", FieldValue.arrayRemove(userId));//좋아요 리스트에서 자신 삭제

                            } else {// 좋아요가 눌러지지 않은 상태일 경우
                                int fLike = Integer.parseInt(document.getData().get("like").toString());
                                fLike++;
                                postdata.setLike(Integer.toString(fLike));
                                likerList.add(userId);
                                postdata.setLiker(likerList);
                                setImageChange(postdata);
                                notifyDataSetChanged();
                                mDataset.get(position).setLike(Integer.toString(fLike));
                                documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                                documentReference.update("liker", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
                            }
                        }
                        else{
                            ArrayList<String> likerListNull = new ArrayList<String>();
                            int fLike = Integer.parseInt(document.getData().get("like").toString());
                            fLike++;
                            postdata.setLike(Integer.toString(fLike));
                            likerListNull.add(userId);
                            postdata.setLiker(likerListNull);
                            setImageChange(postdata);
                            notifyDataSetChanged();
                            mDataset.get(position).setLike(Integer.toString(fLike));
                            documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                            documentReference.update("liker", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
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