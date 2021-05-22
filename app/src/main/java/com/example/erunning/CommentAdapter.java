package com.example.erunning;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    // 이 데이터들을 가지고 각 뷰 홀더에 들어갈 텍스트 뷰에 연결할 것
    ArrayList<CommentInfo> mDataset;
    Activity activity;


    private ImageButton btn_comment;
    private ImageView commentmenu;
    private View LL_PostEdit;
    private View LL_PostDelete;

    private CommentInfo commentInfo;
    private OnPostListener onPostListener;

    // 리사이클러뷰에 들어갈 뷰 홀더, 그리고 그 뷰 홀더에 들어갈 아이템들을 지정
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public CommentViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    // 생성자
    public CommentAdapter(Activity activity, ArrayList<CommentInfo> myDataset) {
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


    // 어댑터 클래스 상속시 구현해야할 함수 3가지 : onCreateViewHolder, onBindViewHolder, getItemCount
    // 리사이클러뷰에 들어갈 뷰 홀더를 할당하는 함수, 뷰 홀더는 실제 레이아웃 파일과 매핑되어야하며, extends의 Adater<>에서 <>안에들어가는 타입을 따른다.
    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

        final CommentViewHolder commentViewHolder = new CommentViewHolder(cardView);

        commentmenu = cardView.findViewById(R.id.btn_commentmenu);
        commentmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("메뉴버튼 ", parent + v.toString());
                switch (v.getId()) {
                    case R.id.btn_commentmenu:
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
                                        onPostListener.onEdit(commentViewHolder.getAdapterPosition());
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
                                        onPostListener.onDelete(commentViewHolder.getAdapterPosition());
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

        return commentViewHolder;
    }


    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        btn_comment = holder.cardView.findViewById(R.id.btn_comment);

        CircleImageView iv_profileImage = holder.cardView.findViewById(R.id.iv_profileimage);
        TextView titleTextView = holder.cardView.findViewById(R.id.tv_comment);
        titleTextView.setText(mDataset.get(position).getTitle());
        TextView tv_commentname = holder.cardView.findViewById(R.id.tv_commentname);
        tv_commentname.setText(mDataset.get(position).getPublisherName());
        Log.e("1차 commentname","설정");
        if(mDataset.get(position).getPhotoUrl() != null) {
            Glide.with(activity).load(mDataset.get(position).getPhotoUrl()).circleCrop().into(iv_profileImage);
        }
        Log.e("1차 댓글 프사","설정");
        TextView createdAtTextView = cardView.findViewById(R.id.createdAtTextView);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(mDataset.get(position).getPublisher());
        documentReference.get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document != null){
                    if(document.exists()){
                        tv_commentname.setText(document.getData().get("name").toString());
                        Log.e("2차 commentname","설정");
                        if(document.getData().get("photoUrl") != null){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            storageRef.child("users/" +mDataset.get(position).getPublisher()+"/profile_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //이미지 로드 성공시

                                    Glide.with(activity).load(uri).circleCrop().into(iv_profileImage);
                                    Log.e("2차 댓글 프사","설정");
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
                createdAtTextView.setText("10분 전");
            } else if (min == 10) {
                createdAtTextView.setText("11분 전");
            } else if (min == 11) {
                createdAtTextView.setText("12분 전");
            } else if (min == 12) {
                createdAtTextView.setText("13분 전");
            } else if (min == 13) {
                createdAtTextView.setText("14분 전");
            } else if (min == 14) {
                createdAtTextView.setText("15분 전");
            } else if (min == 15) {
                createdAtTextView.setText("16분 전");
            } else if (min == 16) {
                createdAtTextView.setText("17분 전");
            } else if (min == 17) {
                createdAtTextView.setText("18분 전");
            } else if (min == 18) {
                createdAtTextView.setText("19분 전");
            } else if (min == 19) {
                createdAtTextView.setText("20분 전");
            } else if (min == 20) {
                createdAtTextView.setText("21분 전");
            } else if (min == 21) {
                createdAtTextView.setText("22분 전");
            } else if (min == 22) {
                createdAtTextView.setText("23분 전");
            } else if (min == 23) {
                createdAtTextView.setText("24분 전");
            } else if (min == 24) {
                createdAtTextView.setText("25분 전");
            } else if (min == 25) {
                createdAtTextView.setText("26분 전");
            } else if (min == 26) {
                createdAtTextView.setText("28분 전");
            } else if (min == 27) {
                createdAtTextView.setText("29분 전");
            } else if (min == 28) {
                createdAtTextView.setText("30분 전");
            }//
            else if (min >= 29 && min <= 59) {
                createdAtTextView.setText("+ 30분");
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

        Log.e("로그: ", "데이터: " + mDataset.get(position).getTitle());
        //Log.e("글이 올라온 시간 : ",Locale.getDefault().format(mDataset.get(position).getCreatedAt()));
    }

    // iOS의 numberOfRows, 리사이클러뷰안에 들어갈 뷰 홀더의 개수
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}