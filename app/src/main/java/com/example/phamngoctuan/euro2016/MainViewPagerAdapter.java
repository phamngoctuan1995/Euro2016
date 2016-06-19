package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    WeakReference<Context> contextWeakReference;

    public MainViewPagerAdapter(FragmentManager fm, Context context, int NumOfTabs)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public Fragment getItem(int position)
    {
        Context context = contextWeakReference.get();
        if (context == null)
            return null;

        switch (position) {
            case 0:
                RSSAdapter _adapter = new RSSAdapter(context);
                return new RecycleViewFragment(_adapter);
            case 1:
                ScoreboardAdapter _adapter1 = new ScoreboardAdapter(context);
                return new RecycleViewFragment(_adapter1);
            case 2:
                MatchAdapter _adapter2 = new MatchAdapter(context);
                return new RecycleViewFragment(_adapter2);
            case 3:
                TopPlayerAdapter _adapter3 = new TopPlayerAdapter(context);
                return new RecycleViewFragment(_adapter3);
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
