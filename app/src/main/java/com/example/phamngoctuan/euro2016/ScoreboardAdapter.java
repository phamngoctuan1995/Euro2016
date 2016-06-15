package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */
public class ScoreboardAdapter extends RecyclerView.Adapter implements onItemClickInterface {
    WeakReference<Context> _contextWeakReference;

    ScoreboardAdapter(Context context)
    {
        _contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position % 5 == 0)
            return MyConstant.GROUP;
        else
            return MyConstant.TEAM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == MyConstant.TEAM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_team, parent, false);
            return new TeamViewHolder(v);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_group, parent, false);
            return new GroupViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        int groupIndex = position / 5;
        GroupInfo groupInfo = MyConstant._scoreBoard.get(groupIndex);
        if (position % 5 == 0)
        {
            GroupViewHolder groupViewHolder = (GroupViewHolder)holder;
            groupViewHolder._name.setText(groupInfo._name);
            groupViewHolder._cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClickListener(v, position);
                }
            });
        }
        else
        {
            TeamViewHolder teamViewHolder = (TeamViewHolder) holder;
            TeamInfo teamInfo = groupInfo._teams.get(position - 5 * groupIndex - 1);
            teamViewHolder._name.setText(teamInfo._name);
            teamViewHolder._played.setText("" + teamInfo._played);
            teamViewHolder._won.setText("" + teamInfo._won);
            teamViewHolder._drawn.setText("" + teamInfo._drawn);
            teamViewHolder._lost.setText("" + teamInfo._lost);
            teamViewHolder._points.setText("" + teamInfo._points);
            Picasso.with(context).load(MyConstant._flag.get(teamInfo._name)).into(teamViewHolder._flag);
            teamViewHolder._cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemClickListener(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (MyConstant._scoreBoard != null)
            for (GroupInfo groupInfo : MyConstant._scoreBoard)
            {
                count += 1;
                count += groupInfo._teams.size();
            }
        return count;
    }

    @Override
    public void OnItemClickListener(View v, int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;
    }

    void setScoreboard(ArrayList<GroupInfo> scb)
    {
        MyConstant._scoreBoard = scb;
        notifyDataSetChanged();
    }

    void deleteAll()
    {
        MyConstant._scoreBoard.clear();
        notifyDataSetChanged();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _name, _played, _won, _drawn, _lost,_points;
        ImageView _flag;

        TeamViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _name = (TextView) itemView.findViewById(R.id.team_name);
            _played = (TextView) itemView.findViewById(R.id.team_played);
            _won = (TextView) itemView.findViewById(R.id.team_won);
            _drawn = (TextView) itemView.findViewById(R.id.team_drawn);
            _lost = (TextView) itemView.findViewById(R.id.team_lost);
            _points = (TextView) itemView.findViewById(R.id.team_points);
            _flag = (ImageView) itemView.findViewById(R.id.team_flag);
        }
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _name;

        GroupViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _name = (TextView) itemView.findViewById(R.id.group_name);
        }
    }
}
