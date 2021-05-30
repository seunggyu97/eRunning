package com.example.erunning;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.erunning.Utillity.showToast;


public class Record extends Fragment implements OnMapReadyCallback, SensorEventListener, GoogleMap.OnMarkerClickListener {
   private View view; // rootview
   private MapView mapView = null; //맵뷰
   private Button btnStart; //시작 버튼
   private Button btnFinish; //중지 버튼
   private Button btnPin; //핀 등록 버튼
   private TextView textTime; //운동 시간 텍스트뷰
   private TextView textDistance; //운동 거리 텍스트뷰
   private TextView textStep; //걸음수 텍스트뷰
   private TextView textCalories; //소모 칼로리 텍스트뷰
   private EditText editFeature; //특징 글 입력 뷰

   private FragmentActivity mContext;

   private static final String TAG = Record.class.getSimpleName();
   private GoogleMap mMap;
   private Marker currentMarker = null; // 현재 위치 마커
   private Marker pinMarker = null; // 핀 등록 마커
   private Marker tempMarker = null; // 임시 마커

   // The entry point to the Fused Location Provider.
   private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
   private LocationRequest locationRequest;
   private Location mCurrentLocation;

   private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
   private static final int DEFAULT_ZOOM = 15;
   private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
   private boolean mLocationPermissionGranted;

   private static final int GPS_ENABLE_REQUEST_CODE = 2001;
   private static final int UPDATE_INTERVAL_MS = 1000 * 10;  // 1분 단위 시간 갱신 1000 * 60 * 1
   private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 5; // 30초 단위로 화면 갱신 1000 * 30

   private static final String KEY_CAMERA_POSITION = "camera_position";
   private static final String KEY_LOCATION = "location";

   private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
   private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
   private boolean walkState = false;                    //걸음 상태
   private List<Polyline> polylines = new ArrayList();

   double time = 0; //운동 시간
   double distance = 0; //운동 거리
   int steps = 0; //걸음수
   int calories = 0; //소모 칼로리
   float[] results = new float[1];

   private SensorManager sm; //센서 매니저 (만보기용)
   private Sensor sensor_step_detector;

   long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
   Handler handler;
   int Seconds, Minutes, Hours, MilliSeconds ;

   private String featuretext; // 특징 글

   ArrayList<MarkerOptions> ftmarkerOptions = new ArrayList<>(); //전체 특징 마커옵션 리스트
   ArrayList<LatLng> latlngList = new ArrayList<>(); //전체 경로 위도, 경도값 리스트
   ArrayList<PolylineOptions> polyOptions = new ArrayList<>(); // 폴리라인 옵션 리스트

   MainActivity mainActivity;
   private RecordResult recordResult; // 운동 경로 결과 객체

   private long lastTime;
   private float speed;
   private float lastX;
   private float lastY;
   private float lastZ;
   private float x, y, z;

   private static final int SHAKE_THRESHOLD = 800;
   private static final int DATA_X = SensorManager.DATA_X;
   private static final int DATA_Y = SensorManager.DATA_Y;
   private static final int DATA_Z = SensorManager.DATA_Z;

   ShowFeature showFeature = ShowFeature.getInstance();
   Map<String, Object> ftimg = new HashMap<>();

   ArrayList<String> ftImgPath = new ArrayList<>();
   ArrayList<String> ftImgMarkerId = new ArrayList<>();
   ArrayList<Boolean> markerImgExist = new ArrayList<>();

   public static Record newinstance(){
      Record record = new Record();
      return record;
   }

   public Record()
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

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // 초기화해야 하는 리소스들을 여기서 초기화 해준다.
      // 활동 퍼미션 체크 (만보기)
      /*if(ContextCompat.checkSelfPermission(getActivity(),
              Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
         }
      }*/
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      // Layout 을 inflate 하는 곳이다.
      if (savedInstanceState != null) {
         mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
         CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
      }
      view = inflater.inflate(R.layout.record, container, false);
      mapView = (MapView)view.findViewById(R.id.map);
      if(mapView != null) {
         mapView.onCreate(savedInstanceState);
      }

      mapView.getMapAsync(this);

      btnStart = (Button)view.findViewById(R.id.btnStart);
      btnFinish = (Button)view.findViewById(R.id.btnFinish);
      btnPin = (Button)view.findViewById(R.id.btnPin);

      textTime = (TextView)view.findViewById(R.id.textTime);
      textDistance = (TextView)view.findViewById(R.id.textDistance);
      textCalories = (TextView)view.findViewById(R.id.textCalories);
      textStep = (TextView)view.findViewById(R.id.textStep);

      editFeature = (EditText)view.findViewById(R.id.editFeature);

      //textStep.setText("0"); // 걸음 수 초기화 및 출력
      sm = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);  // 센서 매니저 생성
      sensor_step_detector = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  // 스템 감지 센서 등록

      handler = new Handler(); //스톱워치 관련

      btnStart.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            if(mCurrentLocation != null){
               changeWalkState();        //걸음 상태 변경
            }
            else{
               showToast(getActivity(), "위치 정보를 가져오는 중입니다. 잠시만 기다려주세요...");
            }
         }
      });

      btnFinish.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle args = new Bundle();
            args.putParcelableArrayList("latlnglist", latlngList); // RecordResult.java로 데이터 전달
            args.putParcelableArrayList("ftmarkeroptions", ftmarkerOptions);
            args.putParcelableArrayList("polyoptions", polyOptions);
            args.putStringArrayList("ftimgmarkerid", ftImgMarkerId);
            args.putStringArrayList("ftimgpath", ftImgPath);
            args.putSerializable("markerimgexist", markerImgExist);

            args.putString("rstime", String.valueOf(textTime.getText()));
            args.putString("rsdistance", String.valueOf(textDistance.getText()));
            args.putString("rsstep", String.valueOf(textStep.getText()));
            args.putString("rscalories", String.valueOf(textCalories.getText()));

            recordResult = RecordResult.getInstance();
            recordResult.setArguments(args);

            //mapView.onDestroy();
            //mainActivity.removeResult();
            //mMap=null;

            mainActivity.changeToResult(recordResult);

         }
      });

      // 디바이스에 걸음 센서의 존재 여부 체크
      if (sensor_step_detector == null) {
         Toast.makeText(mContext.getApplicationContext(), "No Step Sensor", Toast.LENGTH_SHORT).show();
      }

      return view;
   }

   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState) {
      // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
      // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
      super.onActivityCreated(savedInstanceState);

      //액티비티가 처음 생성될 때 실행되는 함수
      MapsInitializer.initialize(mContext);

      locationRequest = new LocationRequest()
              .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 정확도를 최우선적으로 고려
              .setInterval(UPDATE_INTERVAL_MS) // 위치가 Update 되는 주기
              .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // 위치 획득후 업데이트되는 주기

      LocationSettingsRequest.Builder builder =
              new LocationSettingsRequest.Builder();

      builder.addLocationRequest(locationRequest);

      // FusedLocationProviderClient 객체 생성
      mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      mapView.onSaveInstanceState(outState);

   }

   @Override
   public void onMapReady(GoogleMap googleMap) {
      mMap = googleMap;

      setDefaultLocation(); // GPS를 찾지 못하는 장소에 있을 경우 지도의 초기 위치가 필요함.

      getLocationPermission();

      updateLocationUI();

      getDeviceLocation();

   }

   private void updateLocationUI() {
      if (mMap == null) {
         return;
      }
      try {
         if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
         } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mCurrentLocation = null;
            getLocationPermission();
         }
      } catch (SecurityException e)  {
         Log.e("Exception: %s", e.getMessage());
      }
   }

   private void setDefaultLocation() {
      if (currentMarker != null) currentMarker.remove();

      MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.position(mDefaultLocation);
      markerOptions.title("위치정보 가져올 수 없음");
      markerOptions.snippet("위치 퍼미션과 GPS 활성 여부 확인하세요");
      markerOptions.draggable(true);
      markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
      currentMarker = mMap.addMarker(markerOptions);

      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
      mMap.moveCamera(cameraUpdate);
   }

   String getCurrentAddress(LatLng latlng) {
      // 위치 정보와 지역으로부터 주소 문자열을 구한다.
      List<Address> addressList = null ;
      Geocoder geocoder = new Geocoder( mContext, Locale.getDefault());

      // 지오코더를 이용하여 주소 리스트를 구한다.
      try {
         addressList = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
      } catch (IOException e) {
         //Toast. makeText( mContext, "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
         e.printStackTrace();
         return "주소 인식 불가" ;
      }

      if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
         return "해당 위치에 주소 없음" ;
      }

      // 주소를 담는 문자열을 생성하고 리턴
      Address address = addressList.get(0);
      StringBuilder addressStringBuilder = new StringBuilder();
      for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
         addressStringBuilder.append(address.getAddressLine(i));
         if (i < address.getMaxAddressLineIndex())
            addressStringBuilder.append("\n");
      }

      return addressStringBuilder.toString();
   }

   LocationCallback locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
         super.onLocationResult(locationResult);

         List<Location> locationList = locationResult.getLocations();

         if (locationList.size() > 0) {
            Location location = locationList.get(locationList.size() - 1);

            LatLng currentPosition
                    = new LatLng(location.getLatitude(), location.getLongitude());

            String markerTitle = getCurrentAddress(currentPosition);
            /* String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                    + " 경도:" + String.valueOf(location.getLongitude());
            */
            String markerSnippet = "특징 글";

            Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

            //현재 위치에 마커 생성하고 이동
            setCurrentLocation(location, markerTitle, markerSnippet);
            mCurrentLocation = location;
         }
      }

   };

   private String CurrentTime(){
      Date today = new Date();
      SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
      SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
      return time.format(today);
   }

   public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) { //실시간 현재 위치 설정
      double latitude = location.getLatitude(), longtitude = location.getLongitude();

      if (currentMarker != null) currentMarker.remove();

      LatLng currentLatLng = new LatLng(latitude, longtitude);

      MarkerOptions markerOptions = new MarkerOptions();
      markerOptions.position(currentLatLng);
      markerOptions.title(markerTitle);
      markerOptions.snippet(markerSnippet);
      markerOptions.draggable(true);

      currentMarker = mMap.addMarker(markerOptions);

      mMap.setOnMarkerClickListener(this); // 마커 클릭에 대한 이벤트 처리 (리스너 지정)

      CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
      mMap.moveCamera(cameraUpdate);
      if(walkState){                        //걸음 시작 버튼이 눌렸을 때
         endLatLng = new LatLng(latitude, longtitude);        //현재 위치를 끝점으로 설정
         latlngList.add(endLatLng); //위도, 경도 값 저장 리스트에 추가
         drawPath();                                            //polyline 그리기
         startLatLng = new LatLng(latitude, longtitude);        //시작점을 끝점으로 다시 설정
      }

      btnPin.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) { //핀 버튼 클릭 시
            //tempMarker = mMap.addMarker(markerOptions);

            InputFeature inputFeature = InputFeature.getInstance();
            inputFeature.show(getFragmentManager(),"inputFeature"); // 다이얼로그 호출
            
            /*if (getArguments() != null)
            {
               featuretext = getArguments().getString("featuretext"); // BottomSheetDialog에서 받아온 값 넣기
               markerOptions.snippet(featuretext);
               pinMarker = mMap.addMarker(markerOptions); //핀 마커 추가
               Toast.makeText(getContext(),"전달됨",Toast.LENGTH_SHORT).show();
            }*/

            inputFeature.setDialogResult(new InputFeature.OnMyDialogResult() {
               @Override
               public void finish(String result1, String result2) {
                  // result에 dialog에서 보낸값이 저장되어 돌아옵니다. 값을 가지고 원하는 동작을 하면됩니다.
                  featuretext = result1;
                  String profilePath = result2;

                  markerOptions.snippet(featuretext);
                  pinMarker = mMap.addMarker(markerOptions); //핀 마커 추가

                  ftimg.put(pinMarker.getId(), profilePath);

                  if(profilePath == null) {
                     markerImgExist.add(Boolean.FALSE);
                  }
                  else
                  {
                     markerImgExist.add(Boolean.TRUE);
                     ftImgMarkerId.add(pinMarker.getId());
                     ftImgPath.add(profilePath);
                     ftimg.put(pinMarker.getId(), profilePath);
                  }

                  //Toast.makeText(getActivity(),"전달됨",Toast.LENGTH_SHORT).show();
                  ftmarkerOptions.add(markerOptions); //리스트에 특징 마커 추가

               }
            });

         }
      });

      //정보창 클릭 리스너
      mMap.setOnInfoWindowClickListener(infoWindowClickListener);

   }


   //private double GpsStatus = android.location.Location.getLatitude();
   private void changeWalkState(){
      Log.e(startLatLng.toString(),"startLatLng");
      if(!walkState) {
            Toast.makeText(mContext.getApplicationContext(), "기록 시작", Toast.LENGTH_SHORT).show();
            walkState = true;
            Log.e(startLatLng.toString(),"startLatLng");
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);

            startLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());        //현재 위치를 시작점으로 설정
            latlngList.add(startLatLng); //위도, 경도 값 저장 리스트에 추가
            btnStart.setText("정지");
      }else{
         Toast.makeText(mContext.getApplicationContext(), "기록 일시정지", Toast.LENGTH_SHORT).show();
         walkState = false;

         TimeBuff += MillisecondTime;
         handler.removeCallbacks(runnable);

         btnStart.setText("시작");
      }
   }

   private void drawPath(){        //polyline을 그려주는 메소드
      PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(8).color(Color.RED).geodesic(true);
      polyOptions.add(options); //폴리라인 옵션 값 리스트에 추가
      polylines.add(mMap.addPolyline(options));
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
      //Polyline polyline = polylines.get(polylines.size() - 1);
      //distance = distance + polyline.getLength();
      Location.distanceBetween(startLatLng.latitude, startLatLng.longitude, endLatLng.latitude, endLatLng.longitude, results); //거리 계산. 결과값은 results[0]에 있음.
      distance = distance + results[0]; //거리 합 누적
      //Toast.makeText(mContext.getApplicationContext(), String.valueOf(results[0]), Toast.LENGTH_SHORT).show();
      textDistance.setText(String.valueOf(String.format("%.2f", distance * 0.001))); //운동 거리 표시(km 단위로)
      calories = (int)(distance * 0.001 * 41);
      textCalories.setText(String.valueOf(calories)); //소모 칼로리 표시(1km당 41kcal, 천천히 걷는다고 가정)
   }

   private void getDeviceLocation() {
      try {
         if (mLocationPermissionGranted) {
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
         }
      } catch (SecurityException e)  {
         Log.e("Exception: %s", e.getMessage());
      }
   }

   private void getLocationPermission() {
      if (ContextCompat.checkSelfPermission(mContext,
              android.Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED) {
         mLocationPermissionGranted = true;
      } else {
         ActivityCompat.requestPermissions(mContext,
                 new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                 PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String permissions[],
                                          @NonNull int[] grantResults) {
      mLocationPermissionGranted = false;
      switch (requestCode) {
         case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               mLocationPermissionGranted = true;
            }
         }
      }
      updateLocationUI();
   }


   public boolean checkLocationServicesStatus() {
      LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

      return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
              locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
      if (mFusedLocationProviderClient != null) {
         Log.d(TAG, "onStop : removeLocationUpdates");
         mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
      }
   }

   @SuppressLint("MissingPermission")
   @Override
   public void onResume() { // 유저에게 Fragment가 보여지고, 유저와 상호작용이 가능하게 되는 부분
      sm.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_GAME);
      super.onResume();
      mapView.onResume();

      if (mLocationPermissionGranted) {
         Log.d(TAG, "onResume : requestLocationUpdates");
         mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
         if (mMap!=null)
            mMap.setMyLocationEnabled(true);
      }
   }

   @Override
   public void onPause() {
      sm.unregisterListener(this);
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
      if (mFusedLocationProviderClient != null) {
         Log.d(TAG, "onDestroyView : removeLocationUpdates");
         mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
      }
      //mapView.onDestroy();
   }

   @Override
   public void onDestroy() {
      // Destroy 할 때는, 반대로 OnDestroyView에서 View를 제거하고, OnDestroy()를 호출한다.
      super.onDestroy();
      mapView.onDestroy();
   }

   // 센서값이 변할때
   @Override
   public void onSensorChanged(SensorEvent event) {
      // 센서 유형이 스텝감지 센서인 경우 걸음수 +1

      /*float xValue = event.values[0];
      float yValue = event.values[1];
      float zValue = event.values[2];

      if((xValue>40.0f)|(yValue>40.0f)|(zValue>40.0f)) {
         Log.d(TAG, "x:" + xValue + ";y:" + yValue + ";z:" + zValue);

         Toast. makeText( mContext.getApplicationContext(), "걸음 감지", Toast.LENGTH_SHORT ).show();
         textStep.setText("" + (++steps));
      }*/

      // 걸음 센서 이벤트 발생시
      /*if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){

         if(event.values[0]==1.0f){
            // 센서 이벤트가 발생할때 마다 걸음수 증가
            textStep.setText("" + (++steps));
         }

      }*/

      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
         long currentTime = System.currentTimeMillis();
         long gabOfTime = (currentTime - lastTime);
         if (gabOfTime > 120) {
            lastTime = currentTime;
            x = event.values[SensorManager.DATA_X];
            y = event.values[SensorManager.DATA_Y];
            z = event.values[SensorManager.DATA_Z];

            speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
               textStep.setText("" + (++steps));
            }

            lastX = event.values[DATA_X];
            lastY = event.values[DATA_Y];
            lastZ = event.values[DATA_Z];
         }
      }
   }

   @Override
   public void onAccuracyChanged(Sensor sensor, int accuracy) {

   }

   public Runnable runnable = new Runnable() { //스톱워치

      public void run() {

         MillisecondTime = SystemClock.uptimeMillis() - StartTime;

         UpdateTime = TimeBuff + MillisecondTime;

         Seconds = (int) (UpdateTime / 1000);

         Minutes = Seconds / 60;

         Seconds = Seconds % 60;

         Hours = Minutes / 60;

         MilliSeconds = (int) (UpdateTime % 1000);

         textTime.setText(String.format("%02d", Hours) + ":"
                 + String.format("%02d", Minutes) + ":"
                 + String.format("%02d", Seconds));

         handler.postDelayed(this, 0);
      }

   };

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

