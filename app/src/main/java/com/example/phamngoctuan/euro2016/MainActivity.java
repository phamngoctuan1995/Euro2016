package com.example.phamngoctuan.euro2016;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private ViewPager _viewPager;
    private TabLayout _tabLayout;
    MainViewPagerAdapter _pagerAdapter;

    private void initViewPager()
    {
        _tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        _tabLayout.addTab(_tabLayout.newTab().setText("News"));
//        _tabLayout.addTab(_tabLayout.newTab().setText("Tab1"));
//        _tabLayout.addTab(_tabLayout.newTab().setText("Tab2"));
        _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        _pagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), _tabLayout.getTabCount());

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById(R.id.container);
        _viewPager.setAdapter(_pagerAdapter);

        _viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(_tabLayout));

        _tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                _viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().subscribeToTopic("euro2016");

        Intent intent = null;
        intent = getIntent();
        if (intent != null)
        {
            String data = intent.getStringExtra("data");
            if (data != null)
                Log.d("debug", data);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        initViewPager();
    }
}
