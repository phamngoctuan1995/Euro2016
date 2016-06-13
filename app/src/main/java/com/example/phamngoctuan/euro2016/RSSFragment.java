package com.example.phamngoctuan.euro2016;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by phamngoctuan on 13/06/2016.
 */
public class RSSFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView _rcv;
    SwipeRefreshLayout _refreshLayout;
    RSSAdapter _adapter;

    void setAdapter()
    {
        _adapter = new RSSAdapter(getContext());
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
    public void onResume() {
        super.onResume();
        new AsyncDownloadRSS(_adapter).execute("http://www.24h.com.vn/upload/rss/euro2016.rss");
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
        _adapter.deleteAllNews();
        new AsyncDownloadRSS(_adapter).execute("http://www.24h.com.vn/upload/rss/euro2016.rss");
    }
}

class AsyncDownloadRSS extends AsyncTask<String, Void, Void>
{
    WeakReference<RSSAdapter> rssAdapterWeakReference;
    Handler handler;
    AsyncDownloadRSS(RSSAdapter adapter)
    {
        rssAdapterWeakReference = new WeakReference<RSSAdapter>(adapter);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected Void doInBackground(String... params) {
        String _link = params[0];
        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fac.newDocumentBuilder();
            Document doc = builder.parse(_link);
            Element root= doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");
             final RSSAdapter adapter = rssAdapterWeakReference.get();

            if (adapter == null)
                return null;

            for (int i = 0; i < items.getLength(); ++i)
            {
                Node item = items.item(i);
                NodeList childs = item.getChildNodes();
                String title = childs.item(1).getTextContent();
                String link = childs.item(7).getTextContent();
                Element description = (Element) childs.item(3);
                String temp = description.getTextContent().trim();
                org.jsoup.nodes.Document document = Jsoup.parse(temp);
                String img = document.getElementsByTag("img").get(0).attr("src");
                String content = document.text();
                final News news = new News(title, content, img, link);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addNews(news);
                    }
                });
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}