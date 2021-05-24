package com.example.erunning;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends BasicActivity {


    private Button btn_post,btn_save;
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Account account;
    private Analytics analytics;
    private Feed feed;
    private Flag flag;
    private Record record;

    private RecordResult recordResult; // 운동 경로 결과 기능

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_GALLERY = 1000;
    static final int REQUEST_EDITPROFILE = 2000;
    static final int REQUEST_POST = 3000;
    static final int REQUEST_USER_SEARCH = 4000;

    //private TextView user_name; // 이름 Text
    //public ImageView route_profile; // 이미지 뷰
    Context context;

    public void replaceFragment(Fragment fragment){      ////// 화면전환 메소드 프레그먼트는 이메소드를 받아서 화면전환!!!!
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.const_vp,fragment).commit();
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            initView();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(context, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };
    /*public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("MainActivity","onActivityResult실행");
        if(requestCode == REQUEST_CAMERA){
            Log.e("REQUEST_CAMERA","if문 실행");
            if(resultCode == Activity.RESULT_OK){

                Log.e("REQUEST_CAMERA","resultCode == Activity.RESULT_OK 실행");
                String profilePath;
                profilePath = data.getStringExtra("profilePath");
                Log.e("로그: ","profilePath: "+ profilePath);
                Bundle bundle = new Bundle(1); // 파라미터의 숫자는 전달하려는 값의 갯수
                bundle.putString("profilePath", "profilePath");
                account.setArguments(bundle);
            }
        }
        switch(requestCode){
            case REQUEST_CAMERA :{
                Log.e("REQUEST_CAMERA","switch문 실행");
                if(resultCode == Activity.RESULT_OK){

                    Log.e("REQUEST_CAMERA","resultCode == Activity.RESULT_OK 실행");
                    String profilePath;
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("로그: ","profilePath: "+ profilePath);
                    Bundle bundle = new Bundle(1); // 파라미터의 숫자는 전달하려는 값의 갯수
                    bundle.putString("profilePath", "profilePath");
                    account.setArguments(bundle);
                }
                break;
            }
            default:{

                Log.e("REQUEST_CAMERA","switch문 실행 실패");
            }
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }

        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        String user_name = intent.getStringExtra("nickName");
        String route_profile = intent.getStringExtra("photoUrl");
        Bundle bundle = new Bundle();
        bundle.putString("name", user_name);
        bundle.putString("profile", route_profile);




        ViewPager viewPager = findViewById(R.id.viewPager);
        fragmentPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.bottomNavi);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_run));
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        recordResult = new RecordResult(); // 운동 경로 결과 생성

        context = this.getBaseContext();
        checkPermissions(); //권한 체크 메소드 호출

    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) { // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(context)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                            //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                    })
                    .check();

        } else {
            initView();
        }
    }



    // 기록 결과 화면으로 전환 메소드
    public void changeToResult() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.record_layout, recordResult).commit();
    }

    // 기록 결과 화면 제거 메소드
    public void removeResult() {
        getSupportFragmentManager().beginTransaction()
                .remove(recordResult).commit();
    }


    private void initView() {

        Intent intent = getIntent();
        String user_name = intent.getStringExtra("nickName");
        String route_profile = intent.getStringExtra("photoUrl");
        Bundle bundle = new Bundle();
        bundle.putString("name", user_name);
        bundle.putString("profile", route_profile);


        ViewPager viewPager = findViewById(R.id.viewPager);
        fragmentPagerAdapter = new ViewPageAdapter(getSupportFragmentManager());

        TabLayout tabLayout = findViewById(R.id.bottomNavi);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_run));
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }



    public class ActivityResultEvent {

        private int requestCode;
        private int resultCode;
        private Intent data;

        public ActivityResultEvent(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public Intent getData() {
            return data;
        }

        public void setData(Intent data) {
            this.data = data;
        }
    }

}