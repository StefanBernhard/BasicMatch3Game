package com.example.match3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class PlayerAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ListView listHighscores;
    private String[] players;
    private String[] highscores;

    public PlayerAdapter(Context c, String[] p, String[] h){
        players = p;
        highscores = h;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // a service provided by android
    }

    @Override
    public int getCount() {
        return players.length;
    }

    @Override
    public Object getItem(int i) {
        return players[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.listview_highscores, null); //takes a layout and a viewgroup
        TextView txtViewPlayer = (TextView) v.findViewById(R.id.txtViewPlayer);
        TextView txtViewScore = (TextView) v.findViewById(R.id.txtViewScore);

        String player = players[i];
        String score = highscores[i];

        txtViewPlayer.setText(player);
        txtViewScore.setText(score);

        return v;
    }
}
