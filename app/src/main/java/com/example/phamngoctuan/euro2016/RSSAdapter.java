package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

class News
{
    String _title, _content, _thumb, _link;

    News()
    {
        _title = _content = _thumb = _link = "";
    }

    public News(String _title, String _content, String _thumb, String _link) {
        this._title = _title;
        this._content = _content;
        this._thumb = _thumb;
        this._link = _link;
    }
}

interface onItemClickInterface
{
    public void OnItemClickListener(View v, int position);
}

public class RSSAdapter extends RecyclerView.Adapter implements onItemClickInterface {
    ArrayList<News> _listNews;
    WeakReference<Context> _contextWeakReference;

    RSSAdapter(Context context)
    {
        _listNews = new ArrayList<>();
        _contextWeakReference = new WeakReference<Context>(context);
    }

    public RSSAdapter(ArrayList<News> _listNews, WeakReference<Context> contextWeakReference) {
        this._listNews = _listNews;
        this._contextWeakReference = contextWeakReference;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_news, parent, false);

        NewsViewHolder pvh = new NewsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        News news = _listNews.get(position);

        NewsViewHolder _holder = (NewsViewHolder)holder;
        _holder._title.setText(news._title);
        _holder._content.setText(news._content);

        Picasso.with(context).load(news._thumb).into(_holder._image);

        _holder._cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _listNews.size();
    }

    @Override
    public void OnItemClickListener(View v, int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        News news = _listNews.get(position);
        Intent intent = new Intent(context, activity_webview.class);
        intent.putExtra("link", news._link);
        context.startActivity(intent);
    }

    void addNews(News news)
    {
        _listNews.add(news);
        notifyItemInserted(getItemCount() - 1);
    }

    void deleteNews(int position)
    {
        if (position >= getItemCount() || position < 0)
            return;

        _listNews.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    void deleteAllNews()
    {
        _listNews.clear();
        notifyDataSetChanged();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _title;
        TextView _content;
        ImageView _image;

        NewsViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _title = (TextView) itemView.findViewById(R.id.news_title);
            _content = (TextView) itemView.findViewById(R.id.news_content);
            _image = (ImageView) itemView.findViewById(R.id.news_thumb);
        }
    }
}
