package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.AlarmClock;
import android.support.v7.app.AlertDialog;
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
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */

class Match {
    String _stage;
    String _team1Name, _team2Name;
    String _result, _date, _time, _link;

    Match()
    {
        _stage = _team1Name = _team2Name = _result = _date = _link = _time = "";
    }

    public boolean isFinished() {
        return (_time.compareTo("AET") == 0 || _time.compareTo("FT") == 0);
    }

    public boolean isStarted() {
        return (_result.charAt(0) != '?');
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
        matchViewHolder._stage.setText(matchInfo._stage);
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
        String tmp = matchInfo._team1Name;
        if (tmp.charAt(tmp.length() - 1) == '*')
            tmp = tmp.substring(0, tmp.length() - 2);
        if (MyConstant._flag.containsKey(tmp))
            Picasso.with(context).load(MyConstant._flag.get(tmp)).into(matchViewHolder._team1Thumb);
        else
            Picasso.with(context).load(R.drawable.vietnam).into(matchViewHolder._team1Thumb);

        tmp = matchInfo._team2Name;
        if (tmp.charAt(tmp.length() - 1) == '*')
            tmp = tmp.substring(0, tmp.length() - 2);
        if (MyConstant._flag.containsKey(tmp))
            Picasso.with(context).load(MyConstant._flag.get(tmp)).into(matchViewHolder._team2Thumb);
        else
            Picasso.with(context).load(R.drawable.vietnam).into(matchViewHolder._team2Thumb);

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
            if (match.isStarted())
            {
                Intent intent = new Intent(context, activity_webview.class);
                intent.putExtra("link", match._link);
                context.startActivity(intent);
            }
            else {
                createAndShowAlertDialog(context, match);
            }
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
        TextView _team1Name, _team2Name, _result, _date, _stage;
        ImageView _team1Thumb, _team2Thumb;

        MatchViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _stage = (TextView) itemView.findViewById(R.id.stage);
            _team1Name = (TextView) itemView.findViewById(R.id.team1_name);
            _team2Name = (TextView) itemView.findViewById(R.id.team2_name);
            _result = (TextView) itemView.findViewById(R.id.result);
            _date = (TextView) itemView.findViewById(R.id.date);
            _team1Thumb = (ImageView) itemView.findViewById(R.id.team1_thumb);
            _team2Thumb = (ImageView) itemView.findViewById(R.id.team2_thumb);
        }
    }

    private void createAndShowAlertDialog(final Context context, final Match match) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Want to set alarm at " + match._time + "?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String[] times = match._time.split(":");
                    int hours = Integer.parseInt(times[0]);
                    int minutes = Integer.parseInt(times[1]);

                    Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
                    openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, hours);
                    openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
                    context.startActivity(openNewAlarm);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        catch (Exception e)
        {
            Log.d("debug", "Error dialog: " + e.getMessage());
        }
    }
}