package com.example.venkateshwaran.mm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.security.Provider;
import java.util.ArrayList;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.app.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    //media player
   // private  static final String SD_PATH=new String("/storage/emulated/0/audios/");
    private MediaPlayer player;
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private ArrayList<song> songs;
    NotificationManager nm;
    private final IBinder musicBind = new MusicBinder();


    private int songPosn=8;
    @Override
    public void onCreate(){

        super.onCreate();
        songPosn=0;
        Log.e("","fffffffff");

        player = new MediaPlayer();
        initMusicPlayer();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    public void setList(ArrayList<song> theSongs){
        songs=  theSongs;
        Log.e("","eeeeeeeeeee");
    }
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    public void initMusicPlayer(){
        //player.setWakeMode(getApplicationContext(),
          //      PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }
    @Override
    public boolean onUnbind(Intent intent){
      //  player.stop();
        //player.release();
    //   nm.cancel(NOTIFY_ID);
        onDestroy();
        //stopSelf();
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public void playSong() throws IOException {

        player.reset();
        song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();
//get id
        long currSong = playSong.getID();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(this.getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
       /// songTitle = songs.get(songPosn);
        //player.setDataSource(SD_PATH +songs.get(songPosn));
      //  player.start();
        player.prepare();

    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();

                playNext();

        }
    }



    @Override
    public void onPrepared(MediaPlayer mp) {
mp.start();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);


                builder.setSmallIcon(R.drawable.play);
       builder.setTicker(songTitle);
        builder.setOngoing(true);
        builder.setContentTitle("Playing");
        builder.setContentText(songTitle);
        builder.setWhen(System.currentTimeMillis());
        Intent Intent = new Intent(this, MainActivity.class);
        Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

         nm=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      //  nm.notify(NOTIFY_ID,builder.build());
         // startForeground(NOTIFY_ID, builder.build());
    }
    public void setSong(int songIndex){
        songPosn=songIndex;

        try {
            playSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }
    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }
    public void rst(){
        player.reset();
    }



    public void seek(int pose){
        player.seekTo(pose);
    }

    public void go(){

            player.start();
        }
    public void playPrev(){
        songPosn--;
        if(songPosn<=0) songPosn=songs.size()-1;
        try {
            playSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void playNext(){
        songPosn++;
        if(songPosn>=songs.size()) songPosn=0;
        try {
            playSong();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




