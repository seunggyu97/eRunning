
package com.example.erunning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    private TextView save;
    private MaterialEditText username;
    private MaterialEditText bio;

    private FirebaseUser fUser;
    private String user_name;
    private String bio_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close = findViewById(R.id.btn_close);
        save = findViewById(R.id.btn_save);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);

        fUser = FirebaseAuth.getInstance().getCurrentUser();


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()); //문서이름은 UID이기때
        documentReference.get().addOnCompleteListener((task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document != null){
                    if(document.exists()){ //문서가 존재하면
                        user_name = document.getData().get("name").toString();
                        if(document.getData().get("bio") != null){
                            bio_msg = document.getData().get("bio").toString();
                            bio.setText(bio_msg);
                        }

                        if(document.getData().get("name") != null){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            username.setText(user_name);//닉네임 text를 텍스트 뷰에 세팅

                        }
                    }
                }
            }
        }));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }
    private void updateProfile() {
        Log.e("updateProfile","실행");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid())
                .update(
                        "name", username.getText().toString(),
                        "bio", bio.getText().toString()
                );
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);

        finish();
    }

}