package com.example.phamngoctuan.euro2016;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Created by phamngoctuan on 14/06/2016.
 */
public class GroupInfo {
    String _name, _info;
    ArrayList<TeamInfo> _teams;

    GroupInfo()
    {
        _name = _info = "";
        _teams = new ArrayList<>();
    }
}

class TeamInfo {
    String _name, _flag, _info;
    int _played, _won, _drawn, _lost, _points;
    ArrayList<PlayerInfo> _players;

    TeamInfo()
    {
        _name = _flag = _info = "";
        _played = _won = _drawn = _lost = _points = 0;
        _players = new ArrayList<>();
    }
}

class PlayerInfo {
    String _name, _info, _role, _image, _matchP;
    int _number;
    ArrayList<Pair<String, String>> _attr;

    PlayerInfo()
    {
        _name = _info = _role = _image = _matchP = "";
        _number = 0;
        _attr = new ArrayList<>();
    }
}

class TopPlayers {
    String _type;
    ArrayList<PlayerInfo> _players;

    TopPlayers()
    {
        _type = "";
        _players = new ArrayList<>();
    }
}
