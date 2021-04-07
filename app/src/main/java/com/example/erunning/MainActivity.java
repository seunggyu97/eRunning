package com.example.erunning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}