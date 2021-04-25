package com.example.erunning;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class Account_Activity extends AppCompatActivity  {

    private final String TAG = this.getClass().getSimpleName();
    Context mContext;
    private ViewPager2 mViewPager;
    private Account_ViewPageAdapter myPagerAdapter;
    private TabLayout tabLayout;

    private String[] titles = new String[]{"게시글", "북마크" };

    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.account);
        mContext = Account_Activity.this;

        code = "";
        Log.e( TAG, code );

        Fragment frag1 = new Fragpictures().newInstance(code);
        Fragment frag2 = new Fragbookmark().newInstance(code);


        mViewPager = findViewById(R.id.viewPager2_container);
        tabLayout = findViewById(R.id.tabLayout);

        myPagerAdapter = new Account_ViewPageAdapter(this);
        myPagerAdapter.addFrag(frag1);
        myPagerAdapter.addFrag(frag2);


        mViewPager.setAdapter(myPagerAdapter);

        //displaying tabs
        new TabLayoutMediator(tabLayout, mViewPager, (tab, position) -> tab.setText(titles[position])).attach();
    }

}