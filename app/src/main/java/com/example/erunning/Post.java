package com.example.erunning;

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

public class Post extends BasicActivity{
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
    private static final String TAG = "Post";

    private ArrayList<CommentInfo> commentList;
    private CommentAdapter commentAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private PostInfo postdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_writecomment = findViewById(R.id.btn_writecomment);
        btn_like = findViewById(R.id.btn_like);
        bookmark = findViewById(R.id.btn_bookmark);
        tv_like = findViewById(R.id.tv_like);
        tv_like_upside = findViewById(R.id.tv_like_upside);
        loaderLayout = findViewById(R.id.loaderLayout);
        et_writecomment = findViewById(R.id.et_writecomment);
        postmenu = findViewById(R.id.btn_postmenu);
        postmenu.setVisibility(View.GONE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); // ?????????
        tv_comment = findViewById(R.id.tv_comment_upside);

        CircleImageView iv_profileImage = findViewById(R.id.iv_profileimage);
        PostInfo postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        postdata = postInfo;
        setImageChange(postdata);


        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(postInfo.getTitle());
        TextView tv_feedname = findViewById(R.id.tv_feedname);
        tv_feedname.setText(postInfo.getPublisherName());
        Log.e("1??? feedname","??????");
        if(postdata.getPhotoUrl() != null) {
            Glide.with(this).load(postdata.getPhotoUrl()).circleCrop().into(iv_profileImage);
        }
        Log.e("1??? ??????","??????");
        btn_like.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore.collection("posts").document(postdata.getId());
                documentReference.get().addOnCompleteListener((task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                String userId= user.getUid();
                                ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                                if(likerList != null) {
                                    if (likerList.contains(userId)) {// ???????????? ????????? ????????? ??????
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike -= 1;
                                        documentReference.update("like", Integer.toString(fLike)); // ?????? ??????
                                        //postdata.setLike(Integer.toString(fLike));
                                        documentReference.update("liker", FieldValue.arrayRemove(userId));//????????? ??????????????? ?????? ??????
                                        setImageChange(postdata);
                                        onResume();
                                    } else {// ???????????? ???????????? ?????? ????????? ??????
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike+= 1;

                                        documentReference.update("like", Integer.toString(fLike)); // ?????? ??????
                                       // postdata.setLike(Integer.toString(fLike));
                                        documentReference.update("liker", FieldValue.arrayUnion(userId));// ????????? ???????????? ?????? ??????
                                        setImageChange(postdata);
                                        onResume();
                                    }
                                }
                                else{
                                    int fLike = Integer.parseInt(document.getData().get("like").toString());
                                    fLike++;
                                    //postdata.setLike(Integer.toString(fLike));

                                    documentReference.update("like", Integer.toString(fLike)); // ?????? ??????

                                    documentReference.update("liker", FieldValue.arrayUnion(userId));// ????????? ???????????? ?????? ??????
                                    setImageChange(postdata);
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
                DocumentReference documentReference = firebaseFirestore.collection("posts").document(postdata.getId());
                documentReference.get().addOnCompleteListener((task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                String userId= user.getUid();
                                ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                                if(likerList != null) {
                                    if (likerList.contains(userId)) {// ???????????? ????????? ????????? ??????
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike--;
                                        postdata.setLike(Integer.toString(fLike));
                                        documentReference.update("like", Integer.toString(fLike)); // ?????? ??????
                                        documentReference.update("liker", FieldValue.arrayRemove(userId));//????????? ??????????????? ?????? ??????
                                        setImageChange(postdata);
                                        onResume();
                                    } else {// ???????????? ???????????? ?????? ????????? ??????
                                        int fLike = Integer.parseInt(document.getData().get("like").toString());
                                        fLike++;
                                        postdata.setLike(Integer.toString(fLike));
                                        documentReference.update("like", Integer.toString(fLike)); // ?????? ??????
                                        documentReference.update("liker", FieldValue.arrayUnion(userId));// ????????? ???????????? ?????? ??????
                                        setImageChange(postdata);
                                        onResume();
                                    }
                                }
                                else{
                                    int fLike = Integer.parseInt(document.getData().get("like").toString());
                                    fLike++;
                                    postdata.setLike(Integer.toString(fLike));
                                    documentReference.update("like", Integer.toString(fLike)); // ?????? ??????

                                    documentReference.update("liker", FieldValue.arrayUnion(userId));// ????????? ???????????? ?????? ??????
                                    setImageChange(postdata);
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
                        Log.e("postdata ::::",postdata.getComment());
                        if(getComment.equals("0")){
                            tv_comment.setVisibility(View.GONE);
                        }
                        else{
                            tv_comment.setVisibility(View.VISIBLE);
                            String SetCommentUpside = "?????? " + getComment+"???";
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
                        Log.e("2??? feedname","??????");
                        if(document.getData().get("photoUrl") != null){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            storageRef.child("users/" +postInfo.getPublisher()+"/profile_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //????????? ?????? ?????????

                                    Glide.with(getApplicationContext()).load(uri).circleCrop().into(iv_profileImage);
                                    Log.e("2??? ??????","??????");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    //????????? ?????? ?????????
                                    Log.e("????????? ????????? ??????","??????");
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
                Log.e("comment : ", "??????");

            }
        });

        long now = System.currentTimeMillis(); // ?????? ?????? ?????? ??????
        SimpleDateFormat dateSet = new SimpleDateFormat("yyyy???MM???dd??? HH???mm???"); // date?????? ??????
        String date = dateSet.format(new Date()); //?????? ????????? date????????? ?????? ??????
        String CreatedDate = new SimpleDateFormat("yyyy???MM???dd??? HH???mm???", Locale.getDefault()).format(postInfo.getCreatedAt());
        // ????????? ?????? ??????
        try {
            Date startDate = dateSet.parse(date);
            Date endDate = dateSet.parse(CreatedDate);
            long duration = startDate.getTime() - endDate.getTime();
            long min = duration / 60000;
            if (min <= 1) {
                createdAtTextView.setText("?????? ???");
            } else if (min == 2) {
                createdAtTextView.setText("1??? ???");
            } else if (min == 3) {
                createdAtTextView.setText("2??? ???");
            } else if (min == 4) {
                createdAtTextView.setText("3??? ???");
            } else if (min == 5) {
                createdAtTextView.setText("4??? ???");
            } else if (min == 6) {
                createdAtTextView.setText("5??? ???");
            } else if (min == 7) {
                createdAtTextView.setText("6??? ???");
            } else if (min == 8) {
                createdAtTextView.setText("7??? ???");
            } else if (min == 9) {
                createdAtTextView.setText("10??? ???");
            } else if (min == 10) {
                createdAtTextView.setText("11??? ???");
            } else if (min == 11) {
                createdAtTextView.setText("12??? ???");
            } else if (min == 12) {
                createdAtTextView.setText("13??? ???");
            } else if (min == 13) {
                createdAtTextView.setText("14??? ???");
            } else if (min == 14) {
                createdAtTextView.setText("15??? ???");
            } else if (min == 15) {
                createdAtTextView.setText("16??? ???");
            } else if (min == 16) {
                createdAtTextView.setText("17??? ???");
            } else if (min == 17) {
                createdAtTextView.setText("18??? ???");
            } else if (min == 18) {
                createdAtTextView.setText("19??? ???");
            } else if (min == 19) {
                createdAtTextView.setText("20??? ???");
            } else if (min == 20) {
                createdAtTextView.setText("21??? ???");
            } else if (min == 21) {
                createdAtTextView.setText("22??? ???");
            } else if (min == 22) {
                createdAtTextView.setText("23??? ???");
            } else if (min == 23) {
                createdAtTextView.setText("24??? ???");
            } else if (min == 24) {
                createdAtTextView.setText("25??? ???");
            } else if (min == 25) {
                createdAtTextView.setText("26??? ???");
            } else if (min == 26) {
                createdAtTextView.setText("28??? ???");
            } else if (min == 27) {
                createdAtTextView.setText("29??? ???");
            } else if (min == 28) {
                createdAtTextView.setText("30??? ???");
            }//
            else if (min >= 29 && min <= 59) {
                createdAtTextView.setText("+ 30???");
            } else if (min >= 60 && min <= 119) {
                createdAtTextView.setText("+ 1??????");
            } else if (min >= 120 && min <= 179) {
                createdAtTextView.setText("+ 2??????");
            } else if (min >= 180 && min <= 239) {
                createdAtTextView.setText("+ 3??????");
            } else if (min >= 240 && min <= 299) {
                createdAtTextView.setText("+ 4??????");
            } else if (min >= 300 && min <= 359) {
                createdAtTextView.setText("+ 5??????");
            } else if (min >= 360 && min <= 419) {
                createdAtTextView.setText("+ 6??????");
            } else if (min >= 420 && min <= 479) {
                createdAtTextView.setText("+ 7??????");
            } else if (min >= 480 && min <= 539) {
                createdAtTextView.setText("+ 8??????");
            } else if (min >= 540 && min <= 599) {
                createdAtTextView.setText("+ 9??????");
            } else if (min >= 600 && min <= 659) {
                createdAtTextView.setText("+ 10??????");
            } else if (min >= 660 && min <= 719) {
                createdAtTextView.setText("+ 11??????");
            } else if (min >= 720 && min <= 779) {
                createdAtTextView.setText("+ 12??????");
            } else if (min >= 780 && min <= 839) {
                createdAtTextView.setText("+ 13??????");
            } else if (min >= 840 && min <= 899) {
                createdAtTextView.setText("+ 14??????");
            } else if (min >= 900 && min <= 959) {
                createdAtTextView.setText("+ 15??????");
            } else if (min >= 960 && min <= 719) {
                createdAtTextView.setText("+ 16??????");
            } else if (min >= 720 && min <= 779) {
                createdAtTextView.setText("+ 17??????");
            } else if (min >= 780 && min <= 839) {
                createdAtTextView.setText("+ 18??????");
            } else if (min >= 840 && min <= 899) {
                createdAtTextView.setText("+ 19??????");
            } else if (min >= 900 && min <= 959) {
                createdAtTextView.setText("+ 20??????");
            } else if (min >= 960 && min <= 1019) {
                createdAtTextView.setText("+ 21??????");
            } else if (min >= 1020 && min <= 1079) {
                createdAtTextView.setText("+ 22??????");
            } else if (min >= 1080 && min <= 1139) {
                createdAtTextView.setText("+ 23??????");
            } else if (min >= 1140 && min <= 1199) {
                createdAtTextView.setText("+ 1??? ???");
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
    private void setImageChange(PostInfo postdata){
        if(!postdata.getLike().equals("0")){
            tv_like_upside.setVisibility(View.VISIBLE);
            tv_like_upside.setText("????????? " + postdata.getLike() + "???");
        }
        else{
            tv_like_upside.setVisibility(View.GONE);
        }
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference2 = firebaseFirestore.collection("posts").document(postdata.getId());
        documentReference2.get().addOnCompleteListener((task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String userId= firebaseUser.getUid();
                        ArrayList<String> likerList = (ArrayList<String>)document.getData().get("liker");
                        if(likerList != null) {
                            if (likerList.contains(userId)) {// ???????????? ????????? ????????? ??????
                                String getLike = document.getData().get("like").toString();
                                btn_like.setImageResource(R.drawable.ic_like_red);
                                tv_like.setText(getLike);
                                tv_like.setTextColor(Color.parseColor("#ff3300"));
                            } else {
                                btn_like.setImageResource(R.drawable.ic_like_gray);
                                tv_like.setText("?????????");
                                tv_like.setTextColor(Color.parseColor("#000000"));
                            }
                        }
                        else{
                            btn_like.setImageResource(R.drawable.ic_like_gray);
                            tv_like.setText("?????????");
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
                                final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("posts").document(postdata.getId()).collection("comments").document() : firebaseFirestore.collection("comments").document(postdata.getId()).collection("comments").document(commentInfo.getId());
                                final Date date = commentInfo == null ? new Date() : commentInfo.getCreatedAt();

                                StoreUpload(documentReference, new CommentInfo(title, firebaseUser.getUid(), date, PublisherName,profilePhotoUrl));

                            }
                            else{
                                String profilePhotoUrl = "0";
                                final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("posts").document(postdata.getId()).collection("comments").document() : firebaseFirestore.collection("comments").document(postdata.getId()).collection("comments").document(commentInfo.getId());
                                final Date date = commentInfo == null ? new Date() : commentInfo.getCreatedAt();

                                StoreUpload(documentReference, new CommentInfo(title, firebaseUser.getUid(), date, PublisherName,profilePhotoUrl));
                            }
                        }
                    }
                }
            }));
        } else {
            showToast(this, "????????? ????????? ?????????.");
        }
    }
    private void StoreUpload(DocumentReference documentReference, final CommentInfo commentInfo) {
        documentReference.set(commentInfo.getCommentInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("comment", "DB ?????? ??????!");
                        loaderLayout.setVisibility(View.GONE);
                        showToast(Post.this, "????????? ?????????????????????.");
                        commentAdapter.notifyDataSetChanged();
                        et_writecomment.getText().clear();
                        imm.hideSoftInputFromWindow(et_writecomment.getWindowToken(), 0); // ???????????? ?????????.
                        int fComment = Integer.parseInt(postdata.getComment());
                        Log.e("fComment","========"+fComment);
                        fComment++;
                        Log.e("fComment","========"+fComment);

                        postdata.setComment(Integer.toString(fComment));
                        firebaseFirestore.collection("posts").document(postdata.getId()).update("comment", Integer.toString(fComment)); // ?????? ??????
                        onResume();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("comment", "XXXXXX DB ?????? ?????? XXXXXX" + e + "commentInfo : " + commentInfo);
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
            Log.e("????????????", "????????????????????????????????????????????????" + id);
            Log.e("Publisher : ",Publisher);
            Log.e("UserId : ",UserId);
            Log.e("postdata.getId : ",postdata.getId());
            Log.e(".getPublisher : ",postdata.getPublisher());
            if(Publisher.equals(UserId)){
                storeUploader(id);
            }
            else{
                showToast(Post.this,"????????? ????????? ????????? ????????????.");
            }
        }
        public void onEdit(int position){
        }
        public void onExit(int position){
        }
    };
    private void commentUpdate() {
        if (firebaseUser != null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firebaseFirestore.collection("posts").document(postdata.getId()).collection("comments");
            collectionReference.orderBy("createdAt", Query.Direction.ASCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.e("onComplete","??????!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
        DocumentReference documentReferenceComment = FirebaseFirestore.getInstance().collection("posts").document(postdata.getId());
        documentReferenceComment.get().addOnCompleteListener((task ->{
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document !=null){
                    if(document.exists()){
                        String getComment = document.getData().get("comment").toString();
                        Log.e("getComment",getComment);
                        Log.e("postInfo ::::",postdata.getComment());
                        Log.e("postdata ::::",postdata.getComment());
                        if(getComment.equals("0")){
                            tv_comment.setVisibility(View.GONE);
                        }
                        else{
                            tv_comment.setVisibility(View.VISIBLE);
                            String SetCommentUpside = "?????? " + getComment+"???";
                            tv_comment.setText(SetCommentUpside);
                        }
                    }
                }
            }
        }));
    }

    private void storeUploader(String id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(postdata.getId()).collection("comments").document(id);
                documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(Post.this, "????????? ?????????????????????.");
                        int fComment = Integer.parseInt(postdata.getComment());
                        fComment--;
                        postdata.setComment(Integer.toString(fComment));
                        Log.e("id ::::::",":::::::::::::::::"+id);//o
                        Log.e("fComment ::::::",":::::::::::::::::"+fComment);
                        Log.e("postdata.getComment::",":::::::::::::::::"+postdata.getComment());
                        Log.e("postdata.getId()::::",":::::::::::::::::"+postdata.getId());//o
                        if(FirebaseFirestore.getInstance().collection("posts") != null){
                            FirebaseFirestore.getInstance().collection("posts").document(postdata.getId()).update("comment", Integer.toString(fComment)); // ?????? ??????
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
                        showToast(Post.this, "?????? ????????? ?????????????????????.");
                    }
                });
    }
    private void Like(int position){

    }
}