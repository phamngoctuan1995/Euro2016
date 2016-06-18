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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */

class Match {
    String _team1Name, _team2Name;
    String _result, _stadium, _date;
}

public class MatchAdapter extends RecyclerView.Adapter implements RecycleAdapterInterface, MatchCallback {
    WeakReference<Context> _contextWeakReference;
    ArrayList<Match> _match;

    MatchAdapter(Context context)
    {
        _contextWeakReference = new WeakReference<Context>(context);
        _match = new ArrayList<>();
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

        Match matchInfo = _match.get(position);
        MatchViewHolder matchViewHolder = (MatchViewHolder) holder;
        matchViewHolder._team1Name.setText(matchInfo._team1Name);
        matchViewHolder._team2Name.setText(matchInfo._team2Name);
        matchViewHolder._result.setText(matchInfo._result);
        matchViewHolder._date.setText(matchInfo._date);
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
        return _match.size();
    }

    void setMatch(ArrayList<Match> mat)
    {
        _match = mat;
        notifyDataSetChanged();
    }

    void deleteAll()
    {
        _match.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Context context = _contextWeakReference.get();
        if (context != null) {
//            Intent intent = new Intent(context, activity_team_info.class);
//            intent.putExtra("teamid", position);
//            context.startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        deleteAll();
        new MatchAsync(this).execute("http://www.uefa.com/uefaeuro/season=2016/matches/index.html");
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

class MatchAsync extends AsyncTask<String, Void, ArrayList<Match>> {
    MatchCallback _callBack;

    MatchAsync(MatchCallback cb) {
        _callBack = cb;
    }
    @Override
    protected ArrayList<Match> doInBackground(String... params) {
        try {
            Document doc = Jsoup.connect(params[0]).get();
            Elements ele = doc.getElementsByClass("match-row d3-plugin match-finalTrn match-groupStage match-notFake match-row--horizontal");
            int temp = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Match> matches) {
        super.onPostExecute(matches);
        if (matches == null)
            _callBack.onLoadMatchFail();
        else
            _callBack.onLoadMatchSuccess(matches);
    }
}

interface MatchCallback {
    void onLoadMatchSuccess(ArrayList<Match> arr);
    void onLoadMatchFail();
}