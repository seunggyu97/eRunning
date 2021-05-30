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

public class Flag extends Fragment {
   private View view;
   private ImageButton btn_showmap;
   private FloatingActionButton btn_add;
   private FirebaseUser firebaseUser;
   private FirebaseFirestore firebaseFirestore;
   private StorageReference storageRef;
   private static final String TAG = "Flag";
   private FlagAdapter flagAdapter;
   private ArrayList<FlagInfo> flagList;
   private RelativeLayout loaderLayout;


   private Utillity util;
   private int successCount;

   SwipeRefreshLayout refreshLayout;

   public static Flag newinstance() {
      Flag flag = new Flag();
      return flag;
   }
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.flag, container, false);

      btn_showmap = view.findViewById(R.id.btn_user_search);
      btn_add = view.findViewById(R.id.btn_add);
      loaderLayout = view.findViewById(R.id.flagloader);
      refreshLayout = view.findViewById(R.id.srl_feed);

      refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            onResume();

            refreshLayout.setRefreshing(false);
         }
      });


      /*btn_showmap.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            switch (v.getId()) {
               case R.id.btn_showmap:
                  Intent intent = new Intent(getActivity(), UserSearch.class);
                  startActivityForResult(intent, MainActivity.REQUEST_USER_SEARCH);
                  break;
            }
         }
      });*/
      btn_add.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            switch (v.getId()) {
               case R.id.btn_add:
                  Intent intent = new Intent(getActivity(), NewFlag.class);
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

      flagList = new ArrayList<>();
      flagAdapter = new FlagAdapter(getActivity(), flagList);

      RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      recyclerView.setAdapter(flagAdapter);

      return view;
   }

   @Override
   public void onResume() {
      super.onResume();
      postUpdate();

   }

//   OnPostListener onPostListener = new OnPostListener() {
//      @Override
//      public void onDelete(int position) {
//      }
//
//      @Override
//      public void onEdit(int position) {
//      }
//
//      @Override
//      public void onExit(int position) {
//         firebaseFirestore = FirebaseFirestore.getInstance();
//         DocumentReference documentReference = firebaseFirestore.collection("flags").document(flag);
//         documentReference.get().addOnCompleteListener((task) -> {
//            if (task.isSuccessful()) {
//               loaderLayout.setVisibility(View.GONE);
//               DocumentSnapshot document = task.getResult();
//               if (document != null) {
//                  if (document.exists()) {
//                     Log.d(TAG, "DocumentSnapshot data : " + document.getData());
//                  } else {
//                     Log.d(TAG, "No Such Document");
//                     startActivity(new Intent(getActivity(), MainActivity.class));
//                     getActivity().finish();
//                  }
//               }
//            } else {
//               loaderLayout.setVisibility(View.GONE);
//               Log.d(TAG, "get failed with ", task.getException());
//            }
//         });
//      }
//   };

   private void postUpdate() {
      if (firebaseUser != null) {
         CollectionReference collectionReference = firebaseFirestore.collection("flags");

         collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if (task.isSuccessful()) {
                          flagList.clear();
                          for (QueryDocumentSnapshot document : task.getResult()) {
                             Log.d(TAG, document.getId() + " => " + document.getData());
                             if(document.getData().get("photoUrl") != null) {
                                flagList.add(new FlagInfo(
                                        document.getData().get("title").toString(),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        document.getId(),
                                        document.getData().get("publisherName").toString(),
                                        document.getData().get("photoUrl").toString(),
                                        document.getData().get("flag").toString(),
                                        (ArrayList<String>) document.getData().get("flager"),
                                        document.getData().get("starthour").toString(),
                                        document.getData().get("startminute").toString(),
                                        document.getData().get("description").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("maxpeople").toString(),
                                        document.getData().get("currentmember").toString(),
                                        document.getData().get("sportCode").toString(),
                                        document.getData().get("sportText").toString(),
                                        document.getData().get("latitude").toString(),
                                        document.getData().get("longitude").toString(),
                                        document.getData().get("comment").toString()));
                             }//String title, String publisher, Date createdAt, String id,String publisherName, String photoUrl, String flag, ArrayList<String> flager, int starthour, int startminute
                             else {
                                flagList.add(new FlagInfo(
                                        document.getData().get("title").toString(),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        document.getData().get("publisherName").toString(),
                                        document.getId(),
                                        "0",
                                        document.getData().get("flag").toString(),
                                        (ArrayList<String>) document.getData().get("flager"),
                                        document.getData().get("starthour").toString(),
                                        document.getData().get("startminute").toString(),
                                        document.getData().get("description").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("maxpeople").toString(),
                                        document.getData().get("currentmember").toString(),
                                        document.getData().get("sportCode").toString(),
                                        document.getData().get("sportText").toString(),
                                        document.getData().get("latitude").toString(),
                                        document.getData().get("longitude").toString(),
                                        document.getData().get("comment").toString()));
                             }//String title, String publisher, Date createdAt, String publisherName, String photoUrl, String flag, ArrayList<String> flager, int starthour, int startminute
                          }
                          flagAdapter.notifyDataSetChanged();
                       } else {
                          Log.d(TAG, "Error getting documents: ", task.getException());
                       }
                    }
                 });
      }
   }
   /*
      @Override
      public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);


      }
   */
   private void storeUploader(String id) {
      firebaseFirestore.collection("flags").document(id)
              .delete()
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void aVoid) {
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

   private void myStartActivity(Class c, FlagInfo flagInfo) {
      Intent intent = new Intent(getActivity(), c);
      intent.putExtra("flagInfo", flagInfo);
      startActivity(intent);
   }
}
