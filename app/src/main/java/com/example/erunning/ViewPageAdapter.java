package com.example.erunning;

import android.icu.text.DisplayContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter {
    public ViewPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return Record.newinstance();
            case 1:
                return Flag.newinstance();
            case 2:
                return Feed.newinstance();
            case 3:
                return Analytics.newinstance();
            case 4:
                return Account.newinstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }


    //하단의 탭 레이아웃 텍스트 선언
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "운동 기록";
            case 1:
                return "참여모집";
            case 2:
                return "피드";
            case 3:
                return "주간 순위";
            case 4:
                return "내 프로필";
            default:
                return null;
        }
    }
}
