package com.example.erunning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Account account;
    private Analytics analytics;
    private Feed feed;
    private Flag flag;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_record:
                        setFrag(0);
                        break;
                    case R.id.action_feed:
                        setFrag(1);
                        break;
                    case R.id.action_flag:
                        setFrag(2);
                        break;
                    case R.id.action_analytics:
                        setFrag(3);
                        break;
                    case R.id.action_account:
                        setFrag(4);
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
        setFrag(0); // 첫 프래그먼트 화면을 선택 0:기록 1:공유 2:참여자모집 3:순위 4:프로필

    }

    // 프래그먼트 교체가 일어나는 실행문
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, record);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, feed);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, flag);
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
    }
}