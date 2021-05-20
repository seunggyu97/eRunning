package com.example.erunning;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Gallery extends BasicActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final int numberOfColumns = 3;

        recyclerView = findViewById(R.id.rv_gallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager((new GridLayoutManager(this, numberOfColumns)));

        mAdapter = new GalleryAdapter(this,getImagesPath(this));
        recyclerView.setAdapter(mAdapter);
    }



    public ArrayList<String> getImagesPath(Activity activity){
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;
        String sortOrder = MediaStore.Images.ImageColumns._ID + " DESC";

        Intent intent = getIntent();
        if (intent.getStringExtra("media").equals("video")) {
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA,MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        }
        else{
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] { MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }

        cursor = activity.getContentResolver().query(uri, projection,null,null,sortOrder);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}