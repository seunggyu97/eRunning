package com.example.erunning;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordResult extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static RecordResult getInstance() { return new RecordResult(); }

    private View view; // rootview
    private MapView mapView = null; //??????
    private Button btnCancel; //?????? ??????
    private Button btnShare; //?????? ??????
    private TextView textDate; //?????? ?????? ????????????
    private TextView rstextTime; //?????? ?????? ????????????
    private TextView rstextDistance; //?????? ?????? ????????????
    private TextView rstextStep; //????????? ????????????
    private TextView rstextCalories; //?????? ????????? ????????????

    private FragmentActivity mContext;

    private static final String TAG = RecordResult.class.getSimpleName();
    private GoogleMap mMap;

    private Location mCurrentLocation;
    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private LatLng startLatLng = new LatLng(0, 0);        //polyline ?????????
    private LatLng endLatLng = new LatLng(0, 0);        //polyline ??????
    private List<Polyline> polylines = new ArrayList();

    public String rstime = "0"; //?????? ??????
    public String rsdistance = "0"; //?????? ??????
    public String rsstep = "0"; //?????????
    public String rscalories = "0"; //?????? ?????????
    public int userstep = 0; // ????????? ?????? ?????????
    float[] results = new float[1];
    int Seconds, Minutes, Hours, MilliSeconds ;

    private String featuretext; // ?????? ???

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //Cloud FireStore ???????????? ?????????

    public ArrayList<MarkerOptions> ftmarkerOptions = new ArrayList<>(); //?????? ?????? ???????????? ?????????
    public ArrayList<LatLng> latlngList = new ArrayList<>(); //?????? ?????? ??????, ????????? ?????????
    public ArrayList<PolylineOptions> polyOptions = new ArrayList<>(); // ???????????? ?????? ?????????

    MainActivity mainActivity;
    private Record record; // ?????? ?????? ?????? ??????
    ShowFeature showFeature = ShowFeature.getInstance();

    String uid = null;

    public Map<String, Object> ftimg = new HashMap<>();

    public ArrayList<String> ftImgMarkerId = new ArrayList<>();
    public ArrayList<String> ftImgUrl = new ArrayList<>();
    public ArrayList<String> ftImgPath = new ArrayList<>();
    public ArrayList<Boolean> markerImgExist = new ArrayList<>();

    public String mapPicUrl = new String();

    public RecordResult()
    {
        // required
    }

    @Override
    public void onAttach(Activity activity) { // Fragment ??? Activity??? attach ??? ??? ????????????.
        mContext =(FragmentActivity) activity;
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }

    // ?????? ?????????????????? ????????????.
    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ??????????????? ?????? ??????????????? ????????? ????????? ?????????.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout ??? inflate ?????? ?????????.
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        view = inflater.inflate(R.layout.record_result, container, false);
        mapView = (MapView)view.findViewById(R.id.map);
        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        else{
            Log.d(TAG, "Map is null");
        }

        mapView.getMapAsync(this);

        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnShare = (Button)view.findViewById(R.id.btnShare);
        textDate = (TextView)view.findViewById(R.id.textDate);

        rstextTime = (TextView)view.findViewById(R.id.rstextTime);
        rstextDistance = (TextView)view.findViewById(R.id.rstextDistance);
        rstextCalories = (TextView)view.findViewById(R.id.rstextCalories);
        rstextStep = (TextView)view.findViewById(R.id.rstextStep);

        // Record.java?????? ????????????
        Bundle mArgs = getArguments();
        latlngList = mArgs.getParcelableArrayList("latlnglist"); //?????? ?????? ??????, ????????? ?????????
        ftmarkerOptions = mArgs.getParcelableArrayList("ftmarkeroptions"); //?????? ?????? ???????????? ?????????
        polyOptions = mArgs.getParcelableArrayList("polyoptions"); // ???????????? ?????? ?????????
        //ftimg = mArgs.getParcelable("ftimg");
        //ftImgMarkerId = mArgs.getStringArrayList("ftimgmarkerid");
        ftImgPath = mArgs.getStringArrayList("ftimgpath");
        markerImgExist = (ArrayList<Boolean>) mArgs.getSerializable("markerimgexist");

        rstime = mArgs.getString("rstime");
        rsdistance = mArgs.getString("rsdistance");
        rscalories = mArgs.getString("rscalories");
        rsstep = mArgs.getString("rsstep");

        rstextTime.setText(String.valueOf(rstime));
        rstextDistance.setText(String.valueOf(rsdistance));
        rstextCalories.setText(String.valueOf(rscalories));
        rstextStep.setText(String.valueOf(rsstep));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Check if user's email is verified
            //boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            uid = user.getUid();
        }

        DocumentReference docRef = db.collection("users").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        /*Map<String, Object> temp = new HashMap<>();
                        temp.put("user_step", document.get("user_step"));*/
                        if(document.get("user_step")!=null) {
                            userstep = Integer.parseInt(String.valueOf(document.get("user_step")));
                        }
                        userstep = userstep + Integer.parseInt(rsstep);

                        Map<String, Object> stepdata = new HashMap<>();
                        stepdata.put("user_step", userstep);

                        db.collection("users").document(uid)
                                .set(stepdata, SetOptions.merge());
                        //Toast.makeText(mContext.getApplicationContext(), String.valueOf(userstep), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record = Record.newinstance();
                mainActivity.changeToRecord(record);
                //mainActivity.removeResult(getTargetFragment());
            }
        });

        //String finalUid = uid;
        btnShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String documentid;
                /* Map<String, Object> polyoptionlist = new HashMap<>();
                polyoptionlist.put("polyoptions", polyOptions); */

                // ???????????? ?????? ???????????? Map ???????????? ??????????????????.
                Map<String, Object> recorddata = new HashMap<>();
                recorddata.put("latlnglist", latlngList); // ?????? ?????? ??? ?????????
                //record.put("polyoptionlist", polyoptionlist); // ???????????? ?????? ?????????
                recorddata.put("markeroptionlist", ftmarkerOptions); //?????? ?????? ???????????? ?????????
                recorddata.put("time", rstime);
                recorddata.put("distance", rsdistance);
                recorddata.put("step", rsstep);
                recorddata.put("calories", rscalories);
                recorddata.put("uid", uid); // ????????? ?????????
                recorddata.put("ftimgurl", ftImgUrl);
                //recorddata.put("ftimgmarkerid", ftImgMarkerId);
                recorddata.put("markerimgexist", markerImgExist);
                //recorddata.put("ftimgmap", ftimg);
                //recorddata.put("mapPicUrl", mapPicUrl);

                DocumentReference recordRef = db.collection("records").document();

                recordRef.set(recorddata);

                documentid = recordRef.getId();


                // collection() ?????? ???????????? ????????? ????????? ?????? ??????????????? ?????????.
                /*db.collection("records")
                        .add(recorddata)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //???????????? ??????????????? ??????????????? ???
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getPath());
                                documentid[0] = documentReference.getId();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //????????? ???????????? ???
                                Log.w(TAG, "Error ", e);
                            }
                        });*/

                // ????????? storage ??????
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                for(int i = 0; i < ftImgMarkerId.size(); i++) {

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final StorageReference mountainImagesRef = storageRef.child("records/" + documentid + "/marker_img/" + ftImgMarkerId.get(i) + ".jpg");
                    if (ftImgPath.get(i) == null) {

                    } else {
                        try {
                            InputStream stream = new FileInputStream(new File(ftImgPath.get(i)));

                            UploadTask uploadTask = mountainImagesRef.putStream(stream);

                            int finalI = i;
                            uploadTask.continueWithTask((task) -> {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return mountainImagesRef.getDownloadUrl();
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();

                                        ftImgUrl.add(downloadUri.toString());
                                        //ftimg.put(ftImgMarkerId.get(finalI), ftImgPath.get(finalI));

                                        /*if(ftImgUrl!=null)
                                        {
                                            Log.e("??????", ftImgUrl.get(finalI));
                                        }
                                        else{
                                            Log.e("??????", finalI+"?????? ??????");
                                        }*/

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("records").document(documentid)
                                                .update(
                                                        "ftimgurl", ftImgUrl
                                                );

                                    } else {
                                        Log.e("??????", "??????");
                                    }
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("??????", "??????" + e.toString());
                        }
                    }
                }

                if(documentid == null) {
                    Toast.makeText(mContext.getApplicationContext(), "?????? ????????? ??????", Toast.LENGTH_SHORT).show();
                }

                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {

                        // Create a reference to "mountains.jpg"
                        StorageReference mountainsRef = storageRef.child("records/" + documentid + "/map_picture.jpg");

                        Matrix m = new Matrix();
                        Bitmap bitmap = Bitmap.createBitmap(snapshot, 0, 0, snapshot.getWidth(), snapshot.getHeight(), m, false);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = mountainsRef.putBytes(data);
                        uploadTask.continueWithTask((task) -> {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mountainsRef.getDownloadUrl();
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();

                                    mapPicUrl = downloadUri.toString();
                                    //ftimg.put(ftImgMarkerId.get(finalI), ftImgPath.get(finalI));

                                        /*if(ftImgUrl!=null)
                                        {
                                            Log.e("??????", ftImgUrl.get(finalI));
                                        }
                                        else{
                                            Log.e("??????", finalI+"?????? ??????");
                                        }*/

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("records").document(documentid)
                                            .update(
                                                    "mapPicurl", mapPicUrl
                                            );

                                } else {
                                    Log.e("??????", "??????");
                                }
                            }
                        });
                    }
                };
                mMap.snapshot(callback);

                /*FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("records").document(documentid)
                        .update(
                                "ftimgurl", ftImgUrl
                        );*/

                //DocumentReference docRef2 = db.collection("users").document(finalUid);

                //db.collection("users").document(finalUid).update(data);
                /*docRef2
                        .update("user_step", userstep)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });*/

                Toast.makeText(mContext.getApplicationContext(), "?????? ?????? ????????? ?????????\n?????? ???????????? ???????????? ??????????????????", Toast.LENGTH_LONG).show();
                // ????????? ?????? ???????????? ??????
                //Intent intent = new Intent(getActivity(), NewPost.class);
                //startActivityForResult(intent, MainActivity.REQUEST_POST);

            }
        });



        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Fragement????????? OnCreateView??? ?????????, Activity?????? onCreate()??? ???????????? ?????? ???????????? ???????????????.
        // Activity??? Fragment??? ?????? ?????? ????????? ?????????, View??? ???????????? ????????? ????????? ?????????.
        super.onActivityCreated(savedInstanceState);

        //??????????????? ?????? ????????? ??? ???????????? ??????
        MapsInitializer.initialize(mContext);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // ????????? ???????????? ?????????
        for(int i = 0; i<latlngList.size()-2; i++){
            PolylineOptions options = new PolylineOptions().add(latlngList.get(i)).add(latlngList.get(i+1)).width(8).color(Color.RED).geodesic(true);
            mMap.addPolyline(options);
        }

        if((latlngList != null)&&(latlngList.size()!=0)) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngList.get(0), 15));
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15));
            Toast. makeText( mContext, "????????? ????????? ????????????.", Toast.LENGTH_SHORT ).show();
        }

        int j = 0;
        for( int i = 0; i < ftmarkerOptions.size(); i++){
            Marker pinMarker = mMap.addMarker(ftmarkerOptions.get(i));
            if(markerImgExist.get(i)==Boolean.TRUE) {
                ftImgMarkerId.add(pinMarker.getId());
                ftimg.put(ftImgMarkerId.get(j), ftImgPath.get(j));
                j++;
                //Log.e("??????", ftImgMarkerId.get(i));
            }
        }


        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(infoWindowClickListener);

    }

    @Override
    public void onStart() { // ???????????? Fragment??? ???????????? ?????????.
        super.onStart();
        mapView.onStart();
        Log.d(TAG, "onStart ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() { // ???????????? Fragment??? ????????????, ????????? ??????????????? ???????????? ?????? ??????
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() { // ?????????????????? ????????? View ??? ???????????? ??????
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        // Destroy ??? ??????, ????????? OnDestroyView?????? View??? ????????????, OnDestroy()??? ????????????.
        super.onDestroy();
        //mapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    //????????? ?????? ?????????
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            // String markerId = marker.getId();
            Bundle args = new Bundle();
            args.putString("showfeaturetext", marker.getSnippet()); // ?????????????????? ?????? ??? ????????? ??????
            args.putString("markerid", marker.getId()); // ?????? ?????? ????????? ??????
            args.putString(marker.getId(), (String) ftimg.get(marker.getId()));
            showFeature.setArguments(args);
            showFeature.show(getFragmentManager(),"showFeature"); // ??????????????? ??????
        }
    };
}
