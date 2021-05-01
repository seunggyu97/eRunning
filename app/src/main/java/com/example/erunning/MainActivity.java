package com.example.erunning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
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


        /*bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_record:
                        setFrag(0);
                        break;
                    case R.id.action_flag:
                        setFrag(1);
                        break;
                    case R.id.action_feed:
                        setFrag(2);
                        break;
                    case R.id.action_analytics:
                        setFrag(3);
                        break;
                    case R.id.action_account:
                        setFrag(4);
                        account.setArguments(bundle);
                        break;
                }

                return true;
            }
        });
        account = new Account();
        analytics = new Analytics();
        feed = new Feed();
        flag = new Flag();
        record = new Record();
        setFrag(0); // 첫 프래그먼트 화면을 선택 0:기록 1:참여자모집 2:피드 3:순위 4:프로필*/
        // 프래그먼트 교체가 일어나는 실행문
    /*private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, record);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, flag);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, feed);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, analytics);
                ft.commit();
                break;
            case 4:
                ft.replace(R.id.main_frame, account);
                ft.commit();
                break;
        }
    }*/

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
}