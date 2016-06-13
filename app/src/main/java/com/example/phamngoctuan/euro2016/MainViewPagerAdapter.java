package com.example.phamngoctuan.euro2016;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by phamngoctuan on 13/06/2016.
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainViewPagerAdapter(FragmentManager fm, int NumOfTabs)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 0:
                return new RSSFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "News";
            default:
                return "Tab";
        }
    }

    @Override
    public int getCount()
    {
        return mNumOfTabs;
    }
}
