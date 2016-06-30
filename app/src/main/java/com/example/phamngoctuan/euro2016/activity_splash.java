package com.example.phamngoctuan.euro2016;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.leakcanary.LeakCanary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class activity_splash extends AppCompatActivity implements ScoreboardCallback, RSSCallback, MatchCallback, TopPlayersCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LeakCanary.install(getApplication());
        FirebaseMessaging.getInstance().subscribeToTopic("euro2016");
        setContentView(R.layout.activity_splash);
        new ScoreboardAsync(this).execute();
    }

    @Override
    public void onLoadScoreboardSuccess(ArrayList<GroupInfo> scb) {
        MyConstant._scoreBoard = scb;
        new AsyncDownloadRSS(this).execute("http://www.24h.com.vn/upload/rss/euro2016.rss");
    }

    @Override
    public void onLoadScoreboardFail() {
        Toast.makeText(this, "Fail to load Scoreboard", Toast.LENGTH_SHORT).show();
        MyConstant._scoreBoard = new ArrayList<>();
        new AsyncDownloadRSS(this).execute("http://www.24h.com.vn/upload/rss/euro2016.rss");
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    void initFlag()
    {
        HashMap<String, Integer> flag = new HashMap<>();
        flag.put("France", R.drawable.france);
        flag.put("Switzerland", R.drawable.switzerland);
        flag.put("Albania", R.drawable.albania);
        flag.put("Romania", R.drawable.romainia);
        flag.put("Wales", R.drawable.wales);
        flag.put("England", R.drawable.england);
        flag.put("Russia", R.drawable.russia);
        flag.put("Slovakia", R.drawable.slovakia);
        flag.put("Germany", R.drawable.germany);
        flag.put("Poland", R.drawable.poland);
        flag.put("Northern Ireland", R.drawable.northireland);
        flag.put("N.Ireland", R.drawable.northireland);
        flag.put("Ukraine", R.drawable.ukraine);
        flag.put("Spain", R.drawable.spain);
        flag.put("Croatia", R.drawable.croatia);
        flag.put("Czech Republic", R.drawable.czechrepublic);
        flag.put("Turkey", R.drawable.turkey);
        flag.put("Italy", R.drawable.italy);
        flag.put("Sweden", R.drawable.sweden);
        flag.put("Republic of Ireland", R.drawable.ireland);
        flag.put("Ireland", R.drawable.ireland);
        flag.put("Belgium", R.drawable.belgium);
        flag.put("Hungary", R.drawable.hungaria);
        flag.put("Portugal", R.drawable.portugal);
        flag.put("Iceland", R.drawable.iceland);
        flag.put("Austria", R.drawable.austria);

        MyConstant._flag = flag;
    }

    @Override
    public void onLoadRSSSuccess(ArrayList<News> listNews) {
        MyConstant._listNews = listNews;
        new MatchAsync(this).execute();
    }

    @Override
    public void onLoadRSSFail() {
        MyConstant._listNews = new ArrayList<>();
        Toast.makeText(this, "Fail to load news", Toast.LENGTH_SHORT).show();
        new MatchAsync(this).execute();
    }

    @Override
    public void onLoadMatchSuccess(ArrayList<Match> arr) {
        MyConstant._listMatch = arr;
        new TopPlayersAsync(this).execute();
    }

    @Override
    public void onLoadMatchFail() {
        MyConstant._listMatch = new ArrayList<>();
        Toast.makeText(this, "Fail to load match", Toast.LENGTH_SHORT).show();
        new TopPlayersAsync(this).execute();
    }

    @Override
    public void onLoadTopPlayerSuccess(ArrayList<TopPlayers> arr) {
        MyConstant._topPlayer = arr;
        initFlag();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoadTopPlayerFail() {
        MyConstant._topPlayer = new ArrayList<>();
        Toast.makeText(this, "Fail to load top player", Toast.LENGTH_SHORT).show();
        initFlag();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

interface ScoreboardCallback {
    void onLoadScoreboardSuccess(ArrayList<GroupInfo> scb);
    void onLoadScoreboardFail();
}

class ScoreboardAsync extends AsyncTask<Void, Void, ArrayList<GroupInfo>>
{
    WeakReference<ScoreboardCallback> _callbackRef;
    ScoreboardAsync(ScoreboardCallback callback)
    {
        _callbackRef = new WeakReference<ScoreboardCallback>(callback);
    }

    @Override
    protected ArrayList<GroupInfo> doInBackground(Void... params) {
        ArrayList<GroupInfo> scoreBoard = new ArrayList<>();
        try {
            Document document = Jsoup.connect("http://www.uefa.com/uefaeuro/index.html").get();
            Elements groups = document.getElementsByClass("col-lg-4");

            for (org.jsoup.nodes.Element group : groups)
            {
                GroupInfo groupInfo = new GroupInfo();
                Element groupName = group.getElementsByClass("standings-groupname").get(0);
                groupInfo._name = groupName.text();
                Elements teams = group.getElementsByTag("tbody").get(0).getElementsByTag("tr");
                for (org.jsoup.nodes.Element team : teams)
                {
                    TeamInfo teamInfo = new TeamInfo();
                    Elements td = team.getElementsByTag("td");
                    org.jsoup.nodes.Element a = td.get(0).getElementsByClass("table_team-name_block").get(0);
                    teamInfo._info = a.attr("href");
                    teamInfo._name = a.attr("title");
                    teamInfo._played = Integer.parseInt(td.get(1).text());
                    teamInfo._won = Integer.parseInt(td.get(2).text());
                    teamInfo._drawn = Integer.parseInt(td.get(3).text());
                    teamInfo._lost = Integer.parseInt(td.get(4).text());
                    teamInfo._points = Integer.parseInt(td.get(5).text());
                    groupInfo._teams.add(teamInfo);
                }
                scoreBoard.add(groupInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return scoreBoard;
    }

    @Override
    protected void onPostExecute(ArrayList<GroupInfo> groupInfos) {
        super.onPostExecute(groupInfos);
        ScoreboardCallback _callback = _callbackRef.get();
        if (_callback != null) {
            if (groupInfos != null)
                _callback.onLoadScoreboardSuccess(groupInfos);
            else
                _callback.onLoadScoreboardFail();
        }
    }
}

interface RSSCallback {
    void onLoadRSSSuccess(ArrayList<News> listNews);
    void onLoadRSSFail();
}

class AsyncDownloadRSS extends AsyncTask<String, Void, ArrayList<News>>
{
    WeakReference<RSSCallback> _callbackWeakReference;
    AsyncDownloadRSS(RSSCallback callback)
    {
        _callbackWeakReference = new WeakReference<RSSCallback>(callback);
    }

    @Override
    protected ArrayList<News> doInBackground(String... params) {
        String _link = params[0];
        ArrayList<News> _listNews = null;

        try {
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = fac.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(_link);
            org.w3c.dom.Element root= doc.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");

            _listNews = new ArrayList<>();
            for (int i = 0; i < items.getLength(); ++i)
            {
                Node item = items.item(i);
                NodeList childs = item.getChildNodes();
                String title = childs.item(1).getTextContent();
                String link = childs.item(7).getTextContent();
                org.w3c.dom.Element description = (org.w3c.dom.Element) childs.item(3);
                String temp = description.getTextContent().trim();
                org.jsoup.nodes.Document document = Jsoup.parse(temp);
                String img = document.getElementsByTag("img").get(0).attr("src");
                String content = document.text();
                News news = new News(title, content, img, link);
                _listNews.add(news);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return _listNews;
    }

    @Override
    protected void onPostExecute(ArrayList<News> newses) {
        super.onPostExecute(newses);
        RSSCallback callback = _callbackWeakReference.get();
        if (callback != null) {
            if (newses == null)
                callback.onLoadRSSFail();
            else
                callback.onLoadRSSSuccess(newses);
        }
    }
}

class MatchAsync extends AsyncTask<String, Void, ArrayList<Match>> {
    WeakReference<MatchCallback> _callBackWeakReference;

    MatchAsync(MatchCallback cb) {
        _callBackWeakReference = new WeakReference<MatchCallback>(cb);
    }

    @Override
    protected ArrayList<Match> doInBackground(String... params) {
        ArrayList<Match> _match = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://www.livescore.com/euro/fixtures/").get();
            Elements ele = doc.getElementsByClass("content").first().children();
            for (int i = 1; i < ele.size() - 1; ++i)
            {
                Element node = ele.get(i);
                if (node.className().equals("row row-tall mt4"))
                    continue;
                Match match = new Match();
                match._date = node.getElementsByClass("col-2").get(1).text();
                Element clearfix = node.getElementsByClass("clearfix").first();
                match._time = clearfix.child(0).text();
                if (!match._time.equals("FT") && !match._time.equals("AET")) {
                    int hours = Integer.parseInt(match._time.split(":")[0]);
                    int minutes = Integer.parseInt(match._time.split(":")[1]);
                    hours += 7;
                    if (hours >= 24)
                    {
                        int day = Integer.parseInt(match._date.split(" ")[1]);
                        String month = match._date.split(" ")[0];
                        if (day == 30 && month.equals("June"))
                        {
                            day = 1;
                            month = "July";
                        }
                        else
                            if (day == 31 && month.equals("July"))
                            {
                                day = 1;
                                month = "August";
                            }
                        else
                                day += 1;
                        match._date = month + " " + day;
                    }
                    hours %= 24;
                    String h = "" + hours;
                    String m = "" + minutes;
                    if (h.length() == 1)
                        h = "0" + h;
                    if (m.length() == 1)
                        m = "0" + m;
                    match._time = h + ":" + m;
                }
                Element team = clearfix.child(1).child(0);
                String link = team.attr("href");
                match._link = "http://android.livescore.com/#/soccer/details/" + link.substring(link.indexOf('=') + 1);
                String t = team.text();
                String[] teamName = t.split(" vs ");
                match._team1Name = teamName[0];
                match._team2Name = teamName[1];
                match._result = node.getElementsByClass("col-1").first().text();
                _match.add(match);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // sort match
        Collections.sort(_match, new Comparator<Match>() {
            @Override
            public int compare(Match lhs, Match rhs) {
                if (lhs.isFinished() && !rhs.isFinished())
                    return 1;
                if (!lhs.isFinished() && rhs.isFinished())
                    return -1;
                String[] date1 = lhs._date.split(" ");
                String[] date2 = rhs._date.split(" ");

                // Compare month. This is for June and July only!!!
                // TODO: modify if to be used for other competitions
                int tmp = date1[0].compareTo(date2[0]);
                if (tmp != 0)
                    return -tmp;

                if (date1[1].compareTo(date2[1]) != 0) {
                    int d1 = Integer.parseInt(date1[1]);
                    int d2 = Integer.parseInt(date2[1]);
                    if (d1 < d2)
                        return -1;
                    if (d1 > d2)
                        return 1;
                }

                if (lhs.isStarted() && !rhs.isStarted())
                    return -1;
                if (!lhs.isStarted() && rhs.isStarted())
                    return 1;

                if (lhs.isFinished() && rhs.isFinished())
                    return 0;

                tmp = lhs._time.compareTo(rhs._time);
                if (tmp != 0)
                    return tmp;
                return 0;
            }
        });

        return _match;
    }

    @Override
    protected void onPostExecute(ArrayList<Match> matches) {
        super.onPostExecute(matches);
        MatchCallback _callBack = _callBackWeakReference.get();

        if (_callBack != null) {
            if (matches == null)
                _callBack.onLoadMatchFail();
            else
                _callBack.onLoadMatchSuccess(matches);
        }
    }
}

interface MatchCallback {
    void onLoadMatchSuccess(ArrayList<Match> arr);
    void onLoadMatchFail();
}

class TopPlayersAsync extends AsyncTask<Void, Void, ArrayList<TopPlayers>> {
    TopPlayersCallback _callBack;

    TopPlayersAsync(TopPlayersCallback cb) {
        _callBack = cb;
    }
    @Override
    protected ArrayList<TopPlayers> doInBackground(Void... params) {
        ArrayList<TopPlayers> _topPlayers = new ArrayList<>();

        try {
            Document doc = Jsoup.connect("http://www.uefa.com/uefaeuro/season=2016/statistics/").get();
            Elements tops = doc.getElementsByClass("card-content");
            for (int i = 4; i < tops.size(); ++i)
            {
                Element top = tops.get(i);
                TopPlayers topPlayer = new TopPlayers();
                topPlayer._type = top.child(0).text();

                Elements players = top.child(1).getElementsByTag("li");
                ArrayList<PlayerInfo> _playersInfo = new ArrayList<>();
                for (Element player : players)
                {
                    PlayerInfo playerInfo = new PlayerInfo();
                    playerInfo._image = player.child(0).attr("src");
                    Element a = player.child(1).getElementsByTag("a").first();
                    playerInfo._info = "http://www.uefa.com" + a.attr("href");
                    playerInfo._name = a.text();
                    playerInfo._number = (int)Double.parseDouble(player.child(2).text());
                    _playersInfo.add(playerInfo);
                }

                topPlayer._players = _playersInfo;
                _topPlayers.add(topPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return _topPlayers;
    }

    @Override
    protected void onPostExecute(ArrayList<TopPlayers> tops) {
        super.onPostExecute(tops);
        if (tops == null)
            _callBack.onLoadTopPlayerFail();
        else
            _callBack.onLoadTopPlayerSuccess(tops);
    }
}

interface TopPlayersCallback {
    void onLoadTopPlayerSuccess(ArrayList<TopPlayers> arr);
    void onLoadTopPlayerFail();
}
