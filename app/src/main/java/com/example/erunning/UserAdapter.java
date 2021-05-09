//package com.example.erunning;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.Uri;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentActivity;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.firebase.ui.auth.data.model.User;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
//
//    private Context mContext; //context
//    private List<UserInfo> mUsers; //arrayList
//    private boolean isActivity;
//
//    private FirebaseUser firebaseUser;
//
//
//    public UserAdapter(Context mContext, List<UserInfo> mUsers) {
//        this.mContext = mContext;
//        this.mUsers = mUsers;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
//        return new UserAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        UserInfo userInfo = mUsers.get(position);
//        User user = mUsers.get(position);
//        holder.btn_follow.setVisibility(View.VISIBLE);
//        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        documentReference.get().addOnCompleteListener((task -> {
//            if(task.isSuccessful()){
//                DocumentSnapshot document = task.getResult();
//                if(document != null){
//                    if(document.exists()){
//                        if(document.getData().get("name") != null){
//                            holder.username.setText(document.getData().get("name").toString());
//                        }
//                        if(document.getData().get("photoUrl") != null){
//                            if(mUsers.get(position).getPhotoUrl() != null){
//                                Glide.with(mContext).load(mUsers.get(position).getPhotoUrl()).centerCrop().override(500).into(holder.image_profile);
//                            }
//                            /*storageRef.child("users/" +user.getUid()+"/profile_image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    Glide.with(mContext).load(uri).circleCrop().into(holder.image_profile);
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception exception) {
//                                    Log.e("프로필 이미지 로드","실패");
//                                }
//                            });*/
//                        }
//                    }
//                }
//            }
//        }));
////        holder.username.setText(user.getName());
//
//
//        Glide.with(mContext).load(user.getPhotoUrl()).into(holder.image_profile);
//        isFollowing(user.getuid, holder.btn_follow);
//
//        if(user.getid().equals(firebaseUser.getUid())) {
//            holder.btn_follow.setVisibility(View.GONE);
//        }
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
//                editor.putString("profileid",user.getId());
//                editor.apply();
//
//                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.account_profile, new Account()).commit();
//            }
//        });
//        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(holder.btn_follow.getText().toString().equals("follow")){
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).setValue(true);
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
//                } else {
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(user.getId()).removeValue();
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return (mUsers != null ? mUsers.size() : 0);
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        public TextView username;
//        public CircleImageView image_profile;
//        public Button btn_follow;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            username = itemView.findViewById(R.id.username);
//            image_profile = itemView.findViewById(R.id.image_profile);
//            btn_follow = itemView.findViewById(R.id.btn_follow);
//        }
//    }
//    private void isFollowing(String userid, Button button){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(userid).exists()) {
//                    button.setText("following");
//                } else {
//                    button.setText("follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//}
