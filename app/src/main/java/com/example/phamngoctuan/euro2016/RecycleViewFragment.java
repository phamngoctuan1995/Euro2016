package com.example.phamngoctuan.euro2016;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by phamngoctuan on 13/06/2016.
 */
public class RecycleViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView _rcv;
    SwipeRefreshLayout _refreshLayout;
    RecyclerView.Adapter _adapter = null;

    RecycleViewFragment(RecyclerView.Adapter adapter)
    {
        _adapter = adapter;
    }

    void setAdapter()
    {
        if (_adapter != null)
            _rcv.setAdapter(_adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_rss, container, false);

        _rcv = (RecyclerView) rootView.findViewById(R.id.rcv_listnews);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        _rcv.setLayoutManager(llm);

        setAdapter();

        _refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        _refreshLayout.setColorSchemeColors(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        _refreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onRefresh()
    {
        doOnRefresh();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                _refreshLayout.setRefreshing(false);
            }
        }, 1400);
    }

    public void doOnRefresh()
    {
        RecycleAdapterInterface adapterInterface = (RecycleAdapterInterface)_adapter;
        adapterInterface.onRefresh();
    }
}