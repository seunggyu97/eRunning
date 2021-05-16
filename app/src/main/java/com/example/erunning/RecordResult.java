package com.example.erunning;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecordResult extends Fragment implements OnMapReadyCallback {
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

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    private List<Polyline> polylines = new ArrayList();

    double time = 0; //운동 시간
    double distance = 0; //운동 거리
    int steps = 0; //걸음수
    int calories = 0; //소모 칼로리
    float[] results = new float[1];
    int Seconds, Minutes, Hours, MilliSeconds ;

    private String featuretext; // 특징 글

    private FirebaseFirestore db = FirebaseFirestore.getInstance(); //Cloud FireStore 인스턴스 초기화

    ArrayList<MarkerOptions> ftmarkerOptions = new ArrayList<>(); //전체 특징 마커옵션 리스트
    ArrayList<LatLng> latlngList = new ArrayList<>(); //전체 경로 위도, 경도값 리스트

    MainActivity mainActivity;

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
        mapView = (MapView)view.findViewById(R.id.rsmap);
        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
        }

        mapView.getMapAsync(this);

        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnShare = (Button)view.findViewById(R.id.btnShare);
        textDate = (TextView)view.findViewById(R.id.textDate);

        rstextTime = (TextView)view.findViewById(R.id.rstextTime);
        rstextDistance = (TextView)view.findViewById(R.id.rstextDistance);
        rstextCalories = (TextView)view.findViewById(R.id.rstextCalories);
        rstextStep = (TextView)view.findViewById(R.id.rstextStep);

        rstextDistance.setText(String.format("%.2f", distance));
        rstextCalories.setText(String.valueOf(calories));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.removeResult();
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
    }

    private void drawPath(){        //polyline을 그려주는 메소드
        PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(8).color(Color.RED).geodesic(true);
        polylines.add(mMap.addPolyline(options));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 15));
        //Polyline polyline = polylines.get(polylines.size() - 1);
        //distance = distance + polyline.getLength();
    }

}
