package com.example.phamngoctuan.euro2016;

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
    String _name, _info, _role, _dateofbirth, _club, _image, _matchP;
    int _number, _height, _weigth;

    PlayerInfo()
    {
        _name = _info = _role = _dateofbirth
                = _club = _image = _matchP = "";
        _number = _height = _weigth = 0;
    }
}
