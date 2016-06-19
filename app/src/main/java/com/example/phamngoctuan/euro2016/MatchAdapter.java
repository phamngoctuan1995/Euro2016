package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */

class Match {
    String _team1Name, _team2Name;
    String _result, _date, _time, _link;

    Match()
    {
        _team1Name = _team2Name = _result = _date = _link = _time = "";
    }
}

public class MatchAdapter extends RecyclerView.Adapter implements RecycleAdapterInterface, MatchCallback {
    WeakReference<Context> _contextWeakReference;

    MatchAdapter(Context context)
    {
        _contextWeakReference = new WeakReference<Context>(context);
        if (MyConstant._listMatch.size() == 0)
            onRefresh();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_match, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        Match matchInfo = MyConstant._listMatch.get(position);
        MatchViewHolder matchViewHolder = (MatchViewHolder) holder;
        matchViewHolder._team1Name.setText(matchInfo._team1Name);
        matchViewHolder._team2Name.setText(matchInfo._team2Name);
        if (matchInfo._result.charAt(0) != '?') {
            matchViewHolder._result.setText(matchInfo._result);
            matchViewHolder._date.setText(matchInfo._time);
        }
        else {
            matchViewHolder._result.setText(matchInfo._time);
            matchViewHolder._date.setText(matchInfo._date);
        }
        Picasso.with(context).load(MyConstant._flag.get(matchInfo._team1Name)).into(matchViewHolder._team1Thumb);
        Picasso.with(context).load(MyConstant._flag.get(matchInfo._team2Name)).into(matchViewHolder._team2Thumb);
        matchViewHolder._cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener(v, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return MyConstant._listMatch.size();
    }

    void setMatch(ArrayList<Match> mat)
    {
        MyConstant._listMatch = mat;
        notifyDataSetChanged();
    }

    void deleteAll()
    {
        MyConstant._listMatch.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Context context = _contextWeakReference.get();
        Match match = MyConstant._listMatch.get(position);
        if (context != null) {
            Intent intent = new Intent(context, activity_webview.class);
            intent.putExtra("link", match._link);
            context.startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        deleteAll();
        new MatchAsync(this).execute();
    }

    @Override
    public void onLoadMatchSuccess(ArrayList<Match> mat) {
        setMatch(mat);
    }

    @Override
    public void onLoadMatchFail() {
        Context context = _contextWeakReference.get();
        if (context != null)
            Toast.makeText(context, "Fail to load Match", Toast.LENGTH_SHORT).show();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _team1Name, _team2Name, _result, _date;
        ImageView _team1Thumb, _team2Thumb;

        MatchViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _team1Name = (TextView) itemView.findViewById(R.id.team1_name);
            _team2Name = (TextView) itemView.findViewById(R.id.team2_name);
            _result = (TextView) itemView.findViewById(R.id.result);
            _date = (TextView) itemView.findViewById(R.id.date);
            _team1Thumb = (ImageView) itemView.findViewById(R.id.team1_thumb);
            _team2Thumb = (ImageView) itemView.findViewById(R.id.team2_thumb);
        }
    }
}