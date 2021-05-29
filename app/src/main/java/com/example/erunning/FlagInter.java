package com.example.erunning;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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

public class FlagInter extends BasicActivity{
    private ImageButton btn_writecomment;
    private ImageButton btn_like;
    private ImageButton bookmark;
    private ImageButton postmenu;
    private Button tv_like;
    private TextView tv_like_upside;
    private TextView tv_comment;
    private EditText et_writecomment;
    private CommentInfo commentInfo;
    private Utillity util;
    private RelativeLayout loaderLayout;
    private InputMethodManager imm;
    private static final String TAG = "Flag";

    private ArrayList<CommentInfo> commentList;
    private CommentAdapter commentAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FlagInfo flagdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flaginter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        boolean isLiked = false;
        boolean isBookmarked = false;

        btn_writecomment = findViewById(R.id.btn_writecomment);
        btn_like = findViewById(R.id.btn_like);
        bookmark = findViewById(R.id.btn_bookmark);
        tv_like = findViewById(R.id.tv_like);
        tv_like_upside = findViewById(R.id.tv_like_upside);
        loaderLayout = findViewById(R.id.loaderLayout);
        et_writecomment = findViewById(R.id.et_writecomment);
        postmenu = findViewById(R.id.btn_postmenu);
        postmenu.setVisibility(View.GONE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); // 키보드
        tv_comment = findViewById(R.id.tv_comment_upside);

        CircleImageView iv_profileImage = findViewById(R.id.iv_profileimage);
        FlagInfo postInfo = (FlagInfo) getIntent().getSerializableExtra("postInfo");
        flagdata = postInfo;
        setImageChange(flagdata);


        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(postInfo.getTitle());
        TextView tv_feedname = findViewById(R.id.tv_feedname);
        tv_feedname.setText(postInfo.getPublisherName());
        Log.e("1차 feedname","설정");
        if(flagdata.getPhotoUrl() != null) {
            Glide.with(this).load(flagdata.getPhotoUrl()).circleCrop().into(iv_profileImage);
        }
        Log.e("1차 프사","설정");
        btn_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection("posts").document(flagdata.getId());
                documentReference.get().addOnCompleteListener((task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                String userId= user.getUid();
                                ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                                if(likerList != null) {
                                    if (likerList.contains(userId)) {// 좋아요가 눌러진 상태일 경우
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike--;
                                        flagdata.setFlag(Integer.toString(fLike));
                                        documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                                        documentReference.update("liker", FieldValue.arrayRemove(userId));//좋아요 리스트에서 자신 삭제
                                        setImageChange(flagdata);
                                        onResume();
                                    } else {// 좋아요가 눌러지지 않은 상태일 경우
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike++;
                                        flagdata.setFlag(Integer.toString(fLike));
                                        documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                                        documentReference.update("liker", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
                                        setImageChange(flagdata);
                                        onResume();
                                    }
                                }
                                else{
                                    int fLike = Integer.parseInt(document.getData().get("like").toString());
                                    fLike++;
                                    flagdata.setFlag(Integer.toString(fLike));
                                    documentReference.update("like", Integer.toString(fLike)); // 파베 저장

                                    documentReference.update("liker", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
                                    setImageChange(flagdata);
                                    onResume();
                                }
                            }
                        }
                    }
                }));
            }
        });
        tv_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection("posts").document(flagdata.getId());
                documentReference.get().addOnCompleteListener((task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                String userId= user.getUid();
                                ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                                if(likerList != null) {
                                    if (likerList.contains(userId)) {// 좋아요가 눌러진 상태일 경우
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike--;
                                        flagdata.setFlag(Integer.toString(fLike));
                                        documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                                        documentReference.update("liker", FieldValue.arrayRemove(userId));//좋아요 리스트에서 자신 삭제
                                        setImageChange(flagdata);
                                        onResume();
                                    } else {// 좋아요가 눌러지지 않은 상태일 경우
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike++;
                                        flagdata.setFlag(Integer.toString(fLike));
                                        documentReference.update("like", Integer.toString(fLike)); // 파베 저장
                                        documentReference.update("liker", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
                                        setImageChange(flagdata);
                                        onResume();
                                    }
                                }
                                else{
                                    int fLike = Integer.parseInt(document.getData().get("like").toString());
                                    fLike++;
                                    flagdata.setFlag(Integer.toString(fLike));
                                    documentReference.update("like", Integer.toString(fLike)); // 파베 저장

                                    documentReference.update("liker", FieldValue.arrayUnion(userId));// 좋아요 리스트에 자신 추가
                                    setImageChange(flagdata);
                                    onResume();
                                }
                            }
                        }
                    }
                }));
            }
        });
        TextView createdAtTextView = findViewById(R.id.createdAtTextView);
        DocumentReference documentReferenceComment = FirebaseFirestore.getInstance().collection("posts").document(postInfo.getId());
        documentReferenceComment.get().addOnCompleteListener((task ->{
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document !=null){
                    if(document.exists()){
                        String getComment = document.getData().get("comment").toString();
                        Log.e("getComment",getComment);
                        Log.e("postInfo ::::",postInfo.getComment());
                        Log.e("flagdata ::::",flagdata.getComment());
                        if(getComment.equals("0")){
                            tv_comment.setVisibility(View.GONE);
                        }
                        else{
                            tv_comment.setVisibility(View.VISIBLE);
                            String SetCommentUpside = "댓글 " + getComment+"개";
                            tv_comment.setText(SetCommentUpside);
                        }
                    }
                }
            }
        }));

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(postInfo.getPublisher());
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

                            storageRef.child("users/" +postInfo.getPublisher()+"/profile_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //이미지 로드 성공시

                                    Glide.with(getApplicationContext()).load(uri).circleCrop().into(iv_profileImage);
                                    Log.e("2차 프사","설정");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //이미지 로드 실패시
                                    Log.e("프로필 이미지 로드","실패");
                                }
                            });

                        }
                    }
                }
            }
        }));

        btn_writecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentUpload();
                Log.e("comment : ", "클릭");

            }
        });

        long now = System.currentTimeMillis(); // 현재 시간 변수 생성
        SimpleDateFormat dateSet = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분"); // date형식 지정
        String date = dateSet.format(new Date()); //현재 시각을 date형식에 맞게 저장
        String CreatedDate = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분", Locale.getDefault()).format(postInfo.getCreatedAt());
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

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentList = postInfo.getContents();

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentList)) {
            contentsLayout.setTag(contentList);
            contentsLayout.removeAllViews();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) {
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    contentsLayout.addView(textView);
                }
            }
        }

        commentList = new ArrayList<>();
        commentInfo = (CommentInfo) getIntent().getSerializableExtra("commentInfo");
        commentAdapter = new CommentAdapter(this, commentList);
        commentAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = findViewById(R.id.rv_comment);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);


    }
    private void setImageChange(FlagInfo flagdata){
        if(!flagdata.getFlag().equals("0")){
            tv_like_upside.setVisibility(View.VISIBLE);
            tv_like_upside.setText("좋아요 " + flagdata.getFlag() + "개");
        }
        else{
            tv_like_upside.setVisibility(View.GONE);
        }
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference2 = firebaseFirestore.collection("posts").document(flagdata.getId());
        documentReference2.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String userId= firebaseUser.getUid();
                        ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                        if(likerList != null) {
                            if (likerList.contains(userId)) {// 좋아요가 눌러진 상태일 경우
                                String getFlag = document.getData().get("like").toString();
                                btn_like.setImageResource(R.drawable.ic_like_red);
                                tv_like.setText(getFlag);
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
                        }
                    }
                }
            }
        }));
    }
    private void CommentUpload() {
        final String title = et_writecomment.getText().toString();

        if (title.length() > 0) {

            loaderLayout.setVisibility(View.VISIBLE);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            firebaseFirestore = FirebaseFirestore.getInstance();

            DocumentReference documentReference2= firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference2.get().addOnCompleteListener((task -> {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if(document.exists()){
                            String PublisherName = document.getData().get("name").toString();

                            if(document.getData().get("photoUrl") != null){
                                String profilePhotoUrl = document.getData().get("photoUrl").toString();
                                final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("posts").document(flagdata.getId()).collection("comments").document() : firebaseFirestore.collection("comments").document(flagdata.getId()).collection("comments").document(commentInfo.getId());
                                final Date date = commentInfo == null ? new Date() : commentInfo.getCreatedAt();

                                StoreUpload(documentReference, new CommentInfo(title, firebaseUser.getUid(), date, PublisherName,profilePhotoUrl));

                            }
                            else{
                                String profilePhotoUrl = "0";
                                final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("posts").document(flagdata.getId()).collection("comments").document() : firebaseFirestore.collection("comments").document(flagdata.getId()).collection("comments").document(commentInfo.getId());
                                final Date date = commentInfo == null ? new Date() : commentInfo.getCreatedAt();

                                StoreUpload(documentReference, new CommentInfo(title, firebaseUser.getUid(), date, PublisherName,profilePhotoUrl));
                            }
                        }
                    }
                }
            }));
        } else {
            showToast(this, "내용을 작성해 주세요.");
        }
    }
    private void StoreUpload(DocumentReference documentReference, final CommentInfo commentInfo) {
        documentReference.set(commentInfo.getCommentInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("comment", "DB 저장 성공!");
                        loaderLayout.setVisibility(View.GONE);
                        showToast(FlagInter.this, "댓글을 작성하였습니다.");
                        commentAdapter.notifyDataSetChanged();
                        et_writecomment.getText().clear();
                        imm.hideSoftInputFromWindow(et_writecomment.getWindowToken(), 0); // 키보드를 내린다.
                        int fComment = Integer.parseInt(flagdata.getComment());
                        Log.e("fComment","========"+fComment);
                        fComment++;
                        Log.e("fComment","========"+fComment);

                        flagdata.setComment(Integer.toString(fComment));
                        firebaseFirestore.collection("posts").document(flagdata.getId()).update("comment", Integer.toString(fComment)); // 파베 저장
                        onResume();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("comment", "XXXXXX DB 저장 실패 XXXXXX" + e + "commentInfo : " + commentInfo);
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        commentUpdate();

    }
    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            final String id = commentList.get(position).getId();
            final String Publisher = commentList.get(position).getPublisher();
            final String UserId = firebaseUser.getUid();
            Log.e("댓글삭제", "삭제삭제삭제삭제삭제삭제삭제삭제" + id);
            Log.e("Publisher : ",Publisher);
            Log.e("UserId : ",UserId);
            Log.e("flagdata.getId : ",flagdata.getId());
            Log.e(".getPublisher : ",flagdata.getPublisher());
            if(Publisher.equals(UserId)){
                storeUploader(id);
            }
            else{
                showToast(FlagInter.this,"댓글을 삭제할 권한이 없습니다.");
            }
        }
        public void onEdit(int position){
            //
        }
    };
    private void commentUpdate() {
        if (firebaseUser != null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firebaseFirestore.collection("posts").document(flagdata.getId()).collection("comments");
            collectionReference.orderBy("createdAt", Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.e("onComplete","실행!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                commentList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if (document.getData().get("photoUrl") != null) {
                                        commentList.add(new CommentInfo(
                                                document.getData().get("title").toString(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                document.getId(),

                                                document.getData().get("publisherName").toString(),
                                                document.getData().get("photoUrl").toString()));

                                    }
                                    else{
                                        commentList.add(new CommentInfo(
                                                document.getData().get("title").toString(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                document.getId(),
                                                document.getData().get("publisherName").toString()));
                                    }
                                }
                                commentAdapter.notifyDataSetChanged();

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        DocumentReference documentReferenceComment = FirebaseFirestore.getInstance().collection("posts").document(flagdata.getId());
        documentReferenceComment.get().addOnCompleteListener((task ->{
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document !=null){
                    if(document.exists()){
                        String getComment = document.getData().get("comment").toString();
                        Log.e("getComment",getComment);
                        Log.e("postInfo ::::",flagdata.getComment());
                        Log.e("flagdata ::::",flagdata.getComment());
                        if(getComment.equals("0")){
                            tv_comment.setVisibility(View.GONE);
                        }
                        else{
                            tv_comment.setVisibility(View.VISIBLE);
                            String SetCommentUpside = "댓글 " + getComment+"개";
                            tv_comment.setText(SetCommentUpside);
                        }
                    }
                }
            }
        }));
    }

    private void storeUploader(String id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(flagdata.getId()).collection("comments").document(id);
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(FlagInter.this, "댓글을 삭제하였습니다.");
                        int fComment = Integer.parseInt(flagdata.getComment());
                        fComment--;
                        flagdata.setComment(Integer.toString(fComment));
                        Log.e("id ::::::",":::::::::::::::::"+id);//o
                        Log.e("fComment ::::::",":::::::::::::::::"+fComment);
                        Log.e("flagdata.getComment::",":::::::::::::::::"+flagdata.getComment());
                        Log.e("flagdata.getId()::::",":::::::::::::::::"+flagdata.getId());//o
                        if(FirebaseFirestore.getInstance().collection("posts") != null){
                            FirebaseFirestore.getInstance().collection("posts").document(flagdata.getId()).update("comment", Integer.toString(fComment)); // 파베 저장
                            commentUpdate();
                        }
                        else{
                            Log.e("NULL","NULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULL");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(FlagInter.this, "댓글 삭제를 실패하였습니다.");
                    }
                });
    }
    private void Like(int position){

    }
}
