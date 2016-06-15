package com.example.phamngoctuan.euro2016;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class activity_splash extends AppCompatActivity implements ScoreBoardCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new ScoreboardAsync(this, null).execute();
    }

    @Override
    public void onSuccess(ArrayList<GroupInfo> scb) {
        MyConstant._scoreBoard = scb;
        initFlag();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFail() {
        finish();
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
        flag.put("Ukraine", R.drawable.ukraine);
        flag.put("Spain", R.drawable.spain);
        flag.put("Croatia", R.drawable.croatia);
        flag.put("Czech Republic", R.drawable.czechrepublic);
        flag.put("Turkey", R.drawable.turkey);
        flag.put("Italy", R.drawable.italy);
        flag.put("Sweden", R.drawable.sweden);
        flag.put("Republic of Ireland", R.drawable.ireland);
        flag.put("Belgium", R.drawable.belgium);
        flag.put("Hungary", R.drawable.hungaria);
        flag.put("Portugal", R.drawable.portugal);
        flag.put("Iceland", R.drawable.iceland);
        flag.put("Austria", R.drawable.austria);

        MyConstant._flag = flag;
    }
}

interface ScoreBoardCallback {
    void onSuccess(ArrayList<GroupInfo> scb);
    void onFail();
}

class ScoreboardAsync extends AsyncTask<Void, Void, ArrayList<GroupInfo>>
{
    WeakReference<ScoreBoardCallback> _callbackRef;
    WeakReference<ScoreboardAdapter> _adapterRef;
    ScoreboardAsync(ScoreBoardCallback callback, ScoreboardAdapter adapter)
    {
        _callbackRef = new WeakReference<ScoreBoardCallback>(callback);
        _adapterRef = new WeakReference<ScoreboardAdapter>(adapter);
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
        ScoreBoardCallback _callback = _callbackRef.get();
        if (_callback != null) {
            if (groupInfos != null)
                _callback.onSuccess(groupInfos);
            else
                _callback.onFail();
        }
        else
        {
            if (groupInfos != null) {
                ScoreboardAdapter adapter = _adapterRef.get();
                if (adapter != null)
                    adapter.setScoreboard(groupInfos);
            }
        }
    }
}
