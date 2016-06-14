package com.example.phamngoctuan.euro2016;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class activity_splash extends AppCompatActivity implements ScoreBoardCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new ScoreboardAsync(this).execute();
    }

    @Override
    public void onSuccess(ArrayList<GroupInfo> scb) {
        MyConstant._scoreBoard = scb;
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
}

interface ScoreBoardCallback {
    void onSuccess(ArrayList<GroupInfo> scb);
    void onFail();
}

class ScoreboardAsync extends AsyncTask<Void, Void, ArrayList<GroupInfo>>
{
    WeakReference<ScoreBoardCallback> _callbackRef;
    ScoreboardAsync(ScoreBoardCallback callback)
    {
        _callbackRef = new WeakReference<ScoreBoardCallback>(callback);
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
    }
}
