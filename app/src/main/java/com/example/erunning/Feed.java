package com.example.erunning;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Feed extends Fragment {
    private View view;
    private ImageButton btn_user_search;
    private FloatingActionButton btn_add;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView recyclerView;
    private static final String TAG = "Feed";

    private RelativeLayout loaderLayout;

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
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");

            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<PostInfo> postList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            (ArrayList<String>) document.getData().get("contents"),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime())));
                                }

                                RecyclerView.Adapter mAdapter = new FeedAdapter(getActivity(), postList);
                                recyclerView.setAdapter(mAdapter);
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
}
