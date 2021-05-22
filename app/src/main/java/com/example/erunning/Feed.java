package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.erunning.Utillity.showToast;
import static com.example.erunning.Utillity.storageUrlToName;

public class Feed extends Fragment {
    private View view;
    private String post;
    private ImageButton btn_user_search;
    private FloatingActionButton btn_add;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private static final String TAG = "Feed";
    private FeedAdapter feedAdapter;
    private ArrayList<PostInfo> postList;
    private RelativeLayout loaderLayout;

    private Utillity util;
    private int successCount;

    SwipeRefreshLayout refreshLayout;

    public static Feed newinstance() {
        Feed feed = new Feed();
        return feed;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.feed, container, false);

        btn_user_search = view.findViewById(R.id.btn_user_search);
        btn_add = view.findViewById(R.id.btn_add);
        loaderLayout = view.findViewById(R.id.loaderLayout);
        refreshLayout = view.findViewById(R.id.srl_feed);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();

                refreshLayout.setRefreshing(false);
            }
        });


        btn_user_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_user_search:
                        Intent intent = new Intent(getActivity(), UserSearch.class);
                        startActivityForResult(intent, MainActivity.REQUEST_USER_SEARCH);
                        break;
                }
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_add:
                        Intent intent = new Intent(getActivity(), NewPost.class);
                        startActivityForResult(intent, MainActivity.REQUEST_POST);
                        break;
                }
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if (firebaseUser == null) {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else {
            loaderLayout.setVisibility(View.VISIBLE);
            firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener((task) -> {
                if (task.isSuccessful()) {
                    loaderLayout.setVisibility(View.GONE);
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data : " + document.getData());
                        } else {
                            Log.d(TAG, "No Such Document");
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                    }
                } else {
                    loaderLayout.setVisibility(View.GONE);
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }

        postList = new ArrayList<>();
        feedAdapter = new FeedAdapter(getActivity(), postList);
        feedAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(feedAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        postUpdate();

    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            final String id = postList.get(position).getId();
            Log.e("게시글삭제", "삭제삭제삭제삭제삭제삭제삭제삭제" + id);
            ArrayList<String> contentList = postList.get(position).getContents();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);

                if (Patterns.WEB_URL.matcher(contents).matches() && contents.contains("https://firebasestorage.googleapis.com/v0/b/e-running-735bb.appspot.com/o/post")) {

                    successCount++;
                    StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUrlToName(contents));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            successCount--;
                            storeUploader(id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                }
            }
            storeUploader(id);
        }

        @Override
        public void onEdit(int position) {
            myStartActivity(NewPost.class, postList.get(position));
        }
    };

    private void postUpdate() {
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");

            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if(document.getData().get("photoUrl") != null) {
                                        postList.add(new PostInfo(
                                                document.getData().get("title").toString(),
                                                (ArrayList<String>) document.getData().get("contents"),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                document.getId(),

                                                document.getData().get("publisherName").toString(),
                                                document.getData().get("photoUrl").toString()));
                                    }
                                    else {
                                        postList.add(new PostInfo(
                                                document.getData().get("title").toString(),
                                                (ArrayList<String>) document.getData().get("contents"),
                                                document.getId(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                document.getData().get("publisherName").toString()));
                                    }
                                }
                                feedAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void storeUploader(String id) {
        firebaseFirestore.collection("posts").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference documentReference = db.collection("users").document(user.getUid());
                        documentReference.get().addOnCompleteListener((task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        // post -1 코드
                                        post = document.getData().get("post").toString();
                                        int post_num = Integer.parseInt(post);
                                        if (post_num > 0) {
                                            post_num -= 1;
                                            post = String.valueOf(post_num);
                                            Log.e("게시글 post  ", "post : " + post_num + user.getUid());
                                            db.collection("users").document(user.getUid()).update("post", Integer.parseInt(post));
                                        }
                                        else
                                            showToast(getActivity(), "게시글 post -1 에러 !!!!! 계정을 삭제했다가 다시 만드세요 !!.");
                                    }
                                }
                            }
                        }));
                        showToast(getActivity(), "게시글을 삭제하였습니다.");
                        postUpdate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(getActivity(), "게시글 삭제를 실패하였습니다.");
                    }
                });
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }
}
