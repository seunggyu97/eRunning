package com.example.erunning;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.example.erunning.BasicActivity;
import com.example.erunning.CommentInfo;
import com.example.erunning.FlagCommentAdapter;
import com.example.erunning.FlagInfo;
import com.example.erunning.R;
import com.example.erunning.Utillity;
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

public class FlagPost extends BasicActivity {
    private ImageButton btn_writecomment;
    private Button tv_showflag;
    private ImageView btn_back;
    private TextView tv_comment;
    private TextView tv_title;
    private EditText et_writecomment;
    private CommentInfo commentInfo;
    private Utillity util;
    private RelativeLayout loaderLayout;
    //private InputMethodManager imm; // 키보드
    private static final String TAG = "FlagPost";

    private ArrayList<CommentInfo> commentList;
    private FlagCommentAdapter commentAdapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FlagInfo flagdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flagpost);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_back = findViewById(R.id.btn_writingback);
        btn_writecomment = findViewById(R.id.btn_writecomment);
        //tv_showflag = findViewById(R.id.tv_showflag);
        loaderLayout = findViewById(R.id.loaderLayout);
        et_writecomment = findViewById(R.id.et_writecomment);
        tv_title = findViewById(R.id.tv_title_flag);
        //imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); // 키보드

        FlagInfo flagInfo = (FlagInfo) getIntent().getSerializableExtra("flagInfo");
        flagdata = flagInfo;

        Log.e("getTitle",flagInfo.getTitle());
        tv_title.setText(flagInfo.getTitle());
        tv_title.setPaintFlags(tv_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tv_title.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){ // 참여방 정보 눌렀을때
                Intent intent = new Intent(FlagPost.this, FlagInfoDialog.class);
                intent.putExtra("flagInfo", flagInfo);
                startActivity(intent);
                finish();
            }
        });
        TextView createdAtTextView = findViewById(R.id.createdAtTextView);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_writecomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentUpload();
                Log.e("comment : ", "클릭");

            }
        });

        commentList = new ArrayList<>();
        commentInfo = (CommentInfo) getIntent().getSerializableExtra("commentInfo");
        commentAdapter = new FlagCommentAdapter(this, commentList);

        RecyclerView recyclerView = findViewById(R.id.rv_comment);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);


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
                                final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("flags").document(flagdata.getId()).collection("comments").document() : firebaseFirestore.collection("comments").document(flagdata.getId()).collection("comments").document(commentInfo.getId());
                                final Date date = commentInfo == null ? new Date() : commentInfo.getCreatedAt();

                                StoreUpload(documentReference, new CommentInfo(title, firebaseUser.getUid(), date, PublisherName,profilePhotoUrl));

                            }
                            else{
                                String profilePhotoUrl = "0";
                                final DocumentReference documentReference = commentInfo == null ? firebaseFirestore.collection("flags").document(flagdata.getId()).collection("comments").document() : firebaseFirestore.collection("comments").document(flagdata.getId()).collection("comments").document(commentInfo.getId());
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
                        showToast(FlagPost.this, "댓글을 작성하였습니다.");

                        commentAdapter.notifyDataSetChanged();
                        et_writecomment.getText().clear();
                        //imm.hideSoftInputFromWindow(et_writecomment.getWindowToken(), 0); // 키보드를 내린다.
                        int fComment = Integer.parseInt(flagdata.getComment());
                        Log.e("fComment","========"+fComment);
                        fComment++;
                        Log.e("fComment","========"+fComment);

                        flagdata.setComment(Integer.toString(fComment));
                        firebaseFirestore.collection("flags").document(flagdata.getId()).update("comment", Integer.toString(fComment)); // 파베 저장
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

    private void commentUpdate() {
        if (firebaseUser != null) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            CollectionReference collectionReference = firebaseFirestore.collection("flags").document(flagdata.getId()).collection("comments");
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
    }

    private void storeUploader(String id) {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("flags").document(flagdata.getId()).collection("comments").document(id);
        documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(FlagPost.this, "댓글을 삭제하였습니다.");
                        int fComment = Integer.parseInt(flagdata.getComment());
                        fComment--;
                        flagdata.setComment(Integer.toString(fComment));
                        Log.e("id ::::::",":::::::::::::::::"+id);//o
                        Log.e("fComment ::::::",":::::::::::::::::"+fComment);
                        Log.e("flagdata.getComment::",":::::::::::::::::"+flagdata.getComment());
                        Log.e("flagdata.getId()::::",":::::::::::::::::"+flagdata.getId());//o
                        if(FirebaseFirestore.getInstance().collection("flags") != null){
                            FirebaseFirestore.getInstance().collection("flags").document(flagdata.getId()).update("comment", Integer.toString(fComment)); // 파베 저장
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
                        showToast(FlagPost.this, "댓글 삭제를 실패하였습니다.");
                    }
                });
    }
}