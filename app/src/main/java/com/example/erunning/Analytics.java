package com.example.erunning;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Analytics extends Fragment {
   private View view;

   private ArrayList<AnalyticsItem> items = new ArrayList<>();

   private static final String TAG = Analytics.class.getSimpleName();

   SwipeRefreshLayout refreshLayout;
   private AnalyticsAdapter adapter;

   public static Analytics newinstance(){
      Analytics analytics = new Analytics();
      return analytics;
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.analytics, container, false);

      initDataset();

      Context context = view.getContext();
      RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.RankRecyclerView);
      recyclerView.setHasFixedSize(true);

      refreshLayout = view.findViewById(R.id.srl_rank);

      refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            onResume();

            refreshLayout.setRefreshing(false);
         }
      });


      LinearLayoutManager layoutManager = new LinearLayoutManager(context);
      layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
      recyclerView.setLayoutManager(layoutManager);

      adapter = new AnalyticsAdapter(context, items);
      recyclerView.setAdapter(adapter);

      return view;
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   private void initDataset() {

      /*db.collection("users").orderBy("user_step")
              .get()*/
      FirebaseFirestore db = FirebaseFirestore.getInstance(); //Cloud FireStore 인스턴스 초기화

      CollectionReference collectionReference = db.collection("users");

      collectionReference.orderBy("user_step", Query.Direction.DESCENDING).get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                 @Override
                 public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                       //초기화
                       items.clear();
                       int num = 0;
                       for (QueryDocumentSnapshot doc : task.getResult()) {
                          if (doc.get("user_step") != null) {
                             items.add(new AnalyticsItem(doc.getString("name"),doc.getString("photoUrl"), String.valueOf(++num), Integer.parseInt(String.valueOf(doc.get("user_step")))));
                          }
                       }
                       adapter.notifyDataSetChanged();
                    } else {
                       Log.d(TAG, "Error getting documents: ", task.getException());
                    }
               
                  }
      });

   }

   @Override
   public void onResume() {
      super.onResume();

      initDataset();
   }


}
