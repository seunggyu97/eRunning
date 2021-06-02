package com.example.erunning;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
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
import static com.example.erunning.Utillity.showToast;
import static com.example.erunning.Utillity.storageUrlToName;

//import static com.example.erunning.Util.INTENT_PATH;

class FlagCommentAdapter extends RecyclerView.Adapter<FlagCommentAdapter.FlagCommentViewHolder> {

    // 이 데이터들을 가지고 각 뷰 홀더에 들어갈 텍스트 뷰에 연결할 것
    ArrayList<CommentInfo> mDataset;
    Activity activity;


    private ImageButton btn_comment;
    private ImageView commentmenu;
    private View LL_CommentDelete;
    private ImageView iv_leader;
    private CommentInfo commentInfo;
    private OnPostListener onPostListener;

    // 리사이클러뷰에 들어갈 뷰 홀더, 그리고 그 뷰 홀더에 들어갈 아이템들을 지정
    public static class FlagCommentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public FlagCommentViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    // 생성자
    public FlagCommentAdapter(Activity activity, ArrayList<CommentInfo> myDataset) {
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
    public FlagCommentAdapter.FlagCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_flag, parent, false);

        final FlagCommentViewHolder flagcommentViewHolder = new FlagCommentViewHolder(cardView);

        /*commentmenu = cardView.findViewById(R.id.btn_commentmenu);
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
                                        R.layout.comment_bottom_sheet,
                                        (LinearLayout) cardView.findViewById(R.id.CommentbottomSheetContainer)
                                );
                        LL_CommentDelete = bottomSheetView.findViewById(R.id.LL_CommentDelete);

                        LL_CommentDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.LL_CommentDelete:
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
*/
        return flagcommentViewHolder;
    }


    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(final FlagCommentViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        holder.itemView.setBackgroundColor(Color.parseColor("#00000000"));
        btn_comment = holder.cardView.findViewById(R.id.btn_writecomment);
        iv_leader = holder.cardView.findViewById(R.id.leader);
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
                                    DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("flags").document(mDataset.get(position).getId());
                                    documentReference2.get().addOnCompleteListener((task -> {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document != null){
                                                if(document.exists()){
                                                    if(document.getData().get("publisher").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                        iv_leader.setVisibility(View.VISIBLE);
                                                    }else{
                                                        iv_leader.setVisibility(View.GONE);
                                                    }

                                                }
                                            }
                                        }
                                    }));
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


        SimpleDateFormat dateSet = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분"); // date형식 지정
        String CreatedDate = new SimpleDateFormat("HH시mm분", Locale.getDefault()).format(mDataset.get(position).getCreatedAt());
        createdAtTextView.setText(CreatedDate);

        Log.e("로그: ", "데이터: " + mDataset.get(position).getTitle());
        //Log.e("글이 올라온 시간 : ",Locale.getDefault().format(mDataset.get(position).getCreatedAt()));
    }

    // iOS의 numberOfRows, 리사이클러뷰안에 들어갈 뷰 홀더의 개수
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}