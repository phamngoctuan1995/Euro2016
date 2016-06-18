package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

interface RecycleAdapterInterface
{
    public void onItemClickListener(View v, int position);
    public void onRefresh();
}

public class RSSAdapter extends RecyclerView.Adapter implements RecycleAdapterInterface, RSSCallback {
    WeakReference<Context> _contextWeakReference;

    RSSAdapter(Context context)
    {
        _contextWeakReference = new WeakReference<Context>(context);
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

        News news = MyConstant._listNews.get(position);

        NewsViewHolder _holder = (NewsViewHolder)holder;
        _holder._title.setText(news._title);
        _holder._content.setText(news._content);

        Picasso.with(context).load(news._thumb).into(_holder._image);

        _holder._cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return MyConstant._listNews.size();
    }

    @Override
    public void onItemClickListener(View v, int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        News news = MyConstant._listNews.get(position);
        Intent intent = new Intent(context, activity_webview.class);
        intent.putExtra("link", news._link);
        context.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        deleteAll();
        new AsyncDownloadRSS(this).execute("http://www.24h.com.vn/upload/rss/euro2016.rss");
    }

    void deleteAll()
    {
        MyConstant._listNews.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onLoadRSSSuccess(ArrayList<News> listNews) {
        MyConstant._listNews = listNews;
        notifyDataSetChanged();
    }

    @Override
    public void onLoadRSSFail() {
        Context context = _contextWeakReference.get();
        if (context != null)
            Toast.makeText(context, "Fail to load RSS", Toast.LENGTH_SHORT).show();
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