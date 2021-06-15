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
    private MapView mapView = null; //맵뷰
    private Button btnCancel; //닫기 버튼
    private Button btnShare; //공유 버튼
    private TextView textDate; //운동 날짜 텍스트뷰
    private TextView rstextTime; //운동 시간 텍스트뷰
    private TextView rstextDistance; //운동 거리 텍스트뷰
    private TextView rstextStep; //걸음수 텍스트뷰
    private TextView rstextCalories; //소모 칼로리 텍스트뷰

    private FragmentActivity mContext;

    private static final String TAG = RecordResult.class.getSimpleName();
    private GoogleMap mMap;

    private Location mCurrentLocation;
    private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    private List<Polyline> polylines = new ArrayList();

    public String rstime = "0"; //운동 시간
    public String rsdistance = "0"; //운동 거리
    public String rsstep = "0"; //걸음수
    public String rscalories = "0"; //소모 칼로리
    public int userstep = 0; // 사용자 누적 걸음수
    float[] results = new float[1];
    int Seconds, Minutes, Hours, MilliSeconds ;

    private String featuretext; // 특징 글

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //Cloud FireStore 인스턴스 초기화

    public ArrayList<MarkerOptions> ftmarkerOptions = new ArrayList<>(); //전체 특징 마커옵션 리스트
    public ArrayList<LatLng> latlngList = new ArrayList<>(); //전체 경로 위도, 경도값 리스트
    public ArrayList<PolylineOptions> polyOptions = new ArrayList<>(); // 폴리라인 옵션 리스트

    MainActivity mainActivity;
    private Record record; // 운동 경로 기록 객체
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
    public void onAttach(Activity activity) { // Fragment 가 Activity에 attach 될 때 호출된다.
        mContext =(FragmentActivity) activity;
        super.onAttach(activity);
        mainActivity = (MainActivity) getActivity();
    }

    // 메인 액티비티에서 내려온다.
    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 초기화해야 하는 리소스들을 여기서 초기화 해준다.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout 을 inflate 하는 곳이다.
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

        // Record.java에서 가져오기
        Bundle mArgs = getArguments();
        latlngList = mArgs.getParcelableArrayList("latlnglist"); //전체 경로 위도, 경도값 리스트
        ftmarkerOptions = mArgs.getParcelableArrayList("ftmarkeroptions"); //전체 특징 마커옵션 리스트
        polyOptions = mArgs.getParcelableArrayList("polyoptions"); // 폴리라인 옵션 리스트
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

                // 추가하고 싶은 데이터를 Map 형식으로 만들어줍니다.
                Map<String, Object> recorddata = new HashMap<>();
                recorddata.put("latlnglist", latlngList); // 위도 경도 값 리스트
                //record.put("polyoptionlist", polyoptionlist); // 폴리라인 옵션 리스트
                recorddata.put("markeroptionlist", ftmarkerOptions); //전체 특징 마커옵션 리스트
                recorddata.put("time", rstime);
                recorddata.put("distance", rsdistance);
                recorddata.put("step", rsstep);
                recorddata.put("calories", rscalories);
                recorddata.put("uid", uid); // 사용자 식별자
                recorddata.put("ftimgurl", ftImgUrl);
                //recorddata.put("ftimgmarkerid", ftImgMarkerId);
                recorddata.put("markerimgexist", markerImgExist);
                //recorddata.put("ftimgmap", ftimg);
                //recorddata.put("mapPicUrl", mapPicUrl);

                DocumentReference recordRef = db.collection("records").document();

                recordRef.set(recorddata);

                documentid = recordRef.getId();


                // collection() 안에 문자열은 본인이 원하는 대로 정해주시면 됩니다.
                /*db.collection("records")
                        .add(recorddata)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //데이터가 성공적으로 추가되었을 때
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getPath());
                                documentid[0] = documentReference.getId();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //에러가 발생했을 때
                                Log.w(TAG, "Error ", e);
                            }
                        });*/

                // 이미지 storage 저장
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
                                            Log.e("로그", ftImgUrl.get(finalI));
                                        }
                                        else{
                                            Log.e("로그", finalI+"존재 안함");
                                        }*/

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("records").document(documentid)
                                                .update(
                                                        "ftimgurl", ftImgUrl
                                                );

                                    } else {
                                        Log.e("로그", "실패");
                                    }
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러" + e.toString());
                        }
                    }
                }

                if(documentid == null) {
                    Toast.makeText(mContext.getApplicationContext(), "문서 아이디 없음", Toast.LENGTH_SHORT).show();
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
                                            Log.e("로그", ftImgUrl.get(finalI));
                                        }
                                        else{
                                            Log.e("로그", finalI+"존재 안함");
                                        }*/

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("records").document(documentid)
                                            .update(
                                                    "mapPicurl", mapPicUrl
                                            );

                                } else {
                                    Log.e("로그", "실패");
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

                // 게시물 작성 페이지로 이동
                Intent intent = new Intent(getActivity(), NewPost.class);
                startActivityForResult(intent, MainActivity.REQUEST_POST);

            }
        });



        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
        // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수
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

        // 맵뷰에 폴리라인 그리기
        for(int i = 0; i<latlngList.size()-2; i++){
            PolylineOptions options = new PolylineOptions().add(latlngList.get(i)).add(latlngList.get(i+1)).width(8).color(Color.RED).geodesic(true);
            mMap.addPolyline(options);
        }

        if((latlngList != null)&&(latlngList.size()!=0)) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngList.get(0), 15));
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15));
            Toast. makeText( mContext, "기록된 경로가 없습니다.", Toast.LENGTH_SHORT ).show();
        }

        int j = 0;
        for( int i = 0; i < ftmarkerOptions.size(); i++){
            Marker pinMarker = mMap.addMarker(ftmarkerOptions.get(i));
            if(markerImgExist.get(i)==Boolean.TRUE) {
                ftImgMarkerId.add(pinMarker.getId());
                ftimg.put(ftImgMarkerId.get(j), ftImgPath.get(j));
                j++;
                //Log.e("로그", ftImgMarkerId.get(i));
            }
        }


        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(infoWindowClickListener);

    }

    @Override
    public void onStart() { // 유저에게 Fragment가 보이도록 해준다.
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
    public void onResume() { // 유저에게 Fragment가 보여지고, 유저와 상호작용이 가능하게 되는 부분
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
    public void onDestroyView() { // 프래그먼트와 관련된 View 가 제거되는 단계
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        // Destroy 할 때는, 반대로 OnDestroyView에서 View를 제거하고, OnDestroy()를 호출한다.
        super.onDestroy();
        //mapView.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    //정보창 클릭 리스너
    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            // String markerId = marker.getId();
            Bundle args = new Bundle();
            args.putString("showfeaturetext", marker.getSnippet()); // 다이얼로그로 특징 글 데이터 전달
            args.putString("markerid", marker.getId()); // 해당 마커 아이디 전달
            args.putString(marker.getId(), (String) ftimg.get(marker.getId()));
            showFeature.setArguments(args);
            showFeature.show(getFragmentManager(),"showFeature"); // 다이얼로그 호출
        }
    };
}
