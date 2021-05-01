package com.example.erunning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

public class MainActivity extends AppCompatActivity {

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

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_GALLERY = 1000;
    //private TextView user_name; // 이름 Text
    //public ImageView route_profile; // 이미지 뷰
    Context context;

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