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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */

public class TopPlayerAdapter extends RecyclerView.Adapter implements RecycleAdapterInterface, TopPlayersCallback {
    WeakReference<Context> _contextWeakReference;

    TopPlayerAdapter(Context context)
    {
        _contextWeakReference = new WeakReference<Context>(context);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position % 6 == 0)
            return MyConstant.GROUP;
        else
            return MyConstant.TEAM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == MyConstant.TEAM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_topplayer, parent, false);
            return new TopPlayerViewHolder(v);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_toptitle, parent, false);
            return new TopTitleViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        int groupIndex = position / 6;
        TopPlayers top = MyConstant._topPlayer.get(groupIndex);
        if (position % 6 == 0)
        {
            TopTitleViewHolder topTitle = (TopTitleViewHolder)holder;
            topTitle._title.setText(top._type);
        }
        else
        {
            TopPlayerViewHolder topPlayer = (TopPlayerViewHolder) holder;
            PlayerInfo player = top._players.get(position % 6 - 1);
            topPlayer._name.setText(player._name);
            topPlayer._point.setText("" + player._number);
            Picasso.with(context).load(player._image).into(topPlayer._image);
            topPlayer._cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (MyConstant._topPlayer != null)
            for (TopPlayers top : MyConstant._topPlayer)
            {
                count += 1;
                count += top._players.size();
            }
        return count;
    }

    void setTop(ArrayList<TopPlayers> arr)
    {
        MyConstant._topPlayer = arr;
        notifyDataSetChanged();
    }

    void deleteAll()
    {
        MyConstant._topPlayer.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Context context = _contextWeakReference.get();
        if (context != null) {

        }
    }

    @Override
    public void onRefresh() {
        deleteAll();
        new TopPlayersAsync(this).execute();
    }

    @Override
    public void onLoadTopPlayerSuccess(ArrayList<TopPlayers> arr) {
        setTop(arr);
    }

    @Override
    public void onLoadTopPlayerFail() {
        Context context = _contextWeakReference.get();
        if (context != null)
            Toast.makeText(context, "Fail to load Top player", Toast.LENGTH_SHORT).show();
    }

    public static class TopPlayerViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _name, _point;
        ImageView _image;

        TopPlayerViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _name = (TextView) itemView.findViewById(R.id.topplayer_name);
            _point = (TextView) itemView.findViewById(R.id.topplayer_point);
            _image = (ImageView) itemView.findViewById(R.id.topplayer_thumb);
        }
    }

    public static class TopTitleViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _title;

        TopTitleViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _title = (TextView) itemView.findViewById(R.id.top_title);
        }
    }
}
