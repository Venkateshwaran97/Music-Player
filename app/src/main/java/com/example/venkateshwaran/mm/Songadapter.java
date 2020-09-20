package com.example.venkateshwaran.mm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Songadapter extends BaseAdapter {
    private ArrayList<song> songs;
    private LayoutInflater songInf;

    public Songadapter(Context c, ArrayList<song> theSongs) {
        songs = theSongs;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout) songInf.inflate
                (R.layout.songlist, parent, false);
        //get title and artist views
        TextView songView = (TextView) songLay.findViewById(R.id.textView);
        //TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //get song using position
        song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        //artistView.setText(currSong.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
