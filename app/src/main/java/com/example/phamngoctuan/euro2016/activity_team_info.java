package com.example.phamngoctuan.euro2016;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class activity_team_info extends AppCompatActivity {
    private ViewPager _viewPager;
    private TabLayout _tabLayout;
    FragmentStatePagerAdapter _pagerAdapter;
    int _teamId;

    private void initViewPager()
    {
        _tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        _tabLayout.addTab(_tabLayout.newTab().setText("Players"));
        _tabLayout.addTab(_tabLayout.newTab().setText("Match"));
        _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        _pagerAdapter = new TeamViewPagerAdapter(getSupportFragmentManager(), this, _tabLayout.getTabCount(), _teamId);

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

        Intent intent = null;
        intent = getIntent();
        if (intent != null)
        {
            _teamId = intent.getIntExtra("teamid", -1);
            if (_teamId == -1)
                finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        initViewPager();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
