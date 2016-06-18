package com.example.phamngoctuan.euro2016;

import android.content.Context;
import android.os.AsyncTask;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by phamngoctuan on 13/06/2016.
 */
interface TeamPlayerCallback {
    void onLoadTeamPlayerSuccess(ArrayList<PlayerInfo> players);
    void onLoadTeamPlayerFail();
}
public class TeamPlayerAdapter extends RecyclerView.Adapter implements RecycleAdapterInterface, TeamPlayerCallback{
    WeakReference<Context> _contextWeakReference;
    int _position;
    TeamInfo _teamInfo;
    
    TeamPlayerAdapter(Context context, int pos)
    {
        _contextWeakReference = new WeakReference<Context>(context);
        _position = pos;
        _teamInfo = MyConstant._scoreBoard.get(pos / 5)._teams.get(pos % 5 - 1);
        if (_teamInfo._players.size() == 0)
            new TeamPlayerAsync(this).execute(_teamInfo._info);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
       
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_player, parent, false);
        return new PlayerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Context context = _contextWeakReference.get();
        if (context == null)
            return;

        PlayerInfo player = _teamInfo._players.get(position);
        
        PlayerViewHolder playerHolder = (PlayerViewHolder) holder;
        playerHolder._name.setText(player._name);
        playerHolder._role.setText(player._role);
        playerHolder._performence.setText(player._matchP);
        playerHolder._number.setText("" + player._number);
        Picasso.with(context).load(player._image).into(playerHolder._thumb);
        playerHolder._cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _teamInfo._players.size();
    }

    void setPlayers(ArrayList<PlayerInfo> players)
    {
        _teamInfo._players = players;
        notifyDataSetChanged();
    }

    void deleteAll()
    {
        _teamInfo._players.clear();
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
        new TeamPlayerAsync(this).execute(_teamInfo._info);
    }

    @Override
    public void onLoadTeamPlayerSuccess(ArrayList<PlayerInfo> players) {
        setPlayers(players);
    }

    @Override
    public void onLoadTeamPlayerFail() {
        Context context = _contextWeakReference.get();
        if (context != null)
            Toast.makeText(context, "Fail to load team info", Toast.LENGTH_SHORT).show();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder
    {
        CardView _cv;
        TextView _name, _role, _number, _performence;
        ImageView _thumb;

        PlayerViewHolder(View itemView) {
            super(itemView);
            _cv = (CardView) itemView.findViewById(R.id.cv);
            _name = (TextView) itemView.findViewById(R.id.player_name);
            _number = (TextView) itemView.findViewById(R.id.player_number);
            _role = (TextView) itemView.findViewById(R.id.player_role);
            _performence = (TextView) itemView.findViewById(R.id.player_performence);
            _thumb = (ImageView) itemView.findViewById(R.id.player_thumb);
        }
    }
}

class TeamPlayerAsync extends AsyncTask<String, Void, ArrayList<PlayerInfo>> {

    WeakReference<TeamPlayerCallback> _callbackWeakReference;
    TeamPlayerAsync(TeamPlayerCallback callback)
    {
        _callbackWeakReference = new WeakReference<TeamPlayerCallback>(callback);
    }

    @Override
    protected ArrayList<PlayerInfo> doInBackground(String... params) {
        ArrayList<PlayerInfo> _listPlayers = null;
        try {
            _listPlayers = new ArrayList<>();
            Document document = Jsoup.connect("http://www.uefa.com" + params[0]).get();

            Elements players = document.getElementsByClass("squad--team-player");
            for (Element player : players)
            {
                PlayerInfo playerInfo = new PlayerInfo();
                playerInfo._image = player.getElementsByClass("picture").get(0).attr("src");
                playerInfo._role = player.getElementsByClass("squad--player-role").get(0).text();
                playerInfo._number = Integer.parseInt(player.getElementsByClass("squad--player-num").get(0).text());
                Element name = player.getElementsByClass("squad--player-name").get(0).child(0);
                playerInfo._name = name.attr("title");
                playerInfo._info = name.attr("href");
                playerInfo._matchP = player.getElementsByClass("squad--player-performance-match").get(0).text();
                _listPlayers.add(playerInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return _listPlayers;
    }

    @Override
    protected void onPostExecute(ArrayList<PlayerInfo> playerInfos) {
        super.onPostExecute(playerInfos);
        TeamPlayerCallback callback = _callbackWeakReference.get();
        if (callback != null)
        {
            if (playerInfos == null)
                callback.onLoadTeamPlayerFail();
            else
                callback.onLoadTeamPlayerSuccess(playerInfos);
        }
    }
}
