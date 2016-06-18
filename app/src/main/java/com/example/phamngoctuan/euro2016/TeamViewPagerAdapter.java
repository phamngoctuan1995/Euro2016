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
public class TeamViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    WeakReference<Context> contextWeakReference;
    int _teamId;

    public TeamViewPagerAdapter(FragmentManager fm, Context context, int NumOfTabs, int id)
    {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        contextWeakReference = new WeakReference<Context>(context);
        _teamId = id;
    }

    @Override
    public Fragment getItem(int position)
    {
        Context context = contextWeakReference.get();
        if (context == null)
            return null;

        switch (position) {
            case 0:
                TeamPlayerAdapter _adapter = new TeamPlayerAdapter(context, _teamId);
                return new RecycleViewFragment(_adapter);
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
                return "Players";
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
