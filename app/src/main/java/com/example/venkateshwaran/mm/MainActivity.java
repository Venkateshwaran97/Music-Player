package com.example.venkateshwaran.mm;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



import android.app.ActionBar;
import android.app.Activity;
import android.app.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Handler;

import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
class Mp3Filter implements FilenameFilter{
    public  boolean accept(File dir,String name)
    {
        return(name.endsWith(".mp3"));
    }
}


public class MainActivity extends Activity  implements  AdapterView.OnItemClickListener{
    private Button b1,b2,b3,b4;
    private DrawerLayout drawerLayout;
    private MusicService mediaPlayer;
    private Intent playIntent;


    private String[] planets;
    int pos=0;

    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tx1,tx2,tx3;
    private boolean musicBound=false;
    int i=2;
    private boolean paused=false;
    private  static final String SD_PATH=new String("/storage/emulated/0/audios/");
    private ArrayList<song> songs=new ArrayList<song>();

    ListView l;



    public static int oneTimeOnly = 0;

    @Override
    protected void onPause() {
        super.onPause();
        paused=true;
        //mediaPlayer.pausePlayer();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(paused){

            paused=false;
        }
    }
    @Override
    protected void onStop() {

        super.onStop();
       // myHandler.removeCallbacks(UpdateSongTime);
       // unbindService(musicConnection);
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        myHandler.removeCallbacks(UpdateSongTime);
        unbindService(musicConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l=(ListView) findViewById(R.id.list);
        l.setOnItemClickListener(this);
        updateplaylist();
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        ListView  listView=(ListView) findViewById(R.id.drawerList);
        planets=getResources().getStringArray(R.array.planets);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,planets));
       // listView.setOnItemClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        b1 = (Button) findViewById(R.id.button);

        b3=(Button)findViewById(R.id.button3);
        b4=(Button)findViewById(R.id.button4);


        tx1=(TextView)findViewById(R.id.textView2);
        tx2=(TextView)findViewById(R.id.textView3);

        tx3=(TextView)findViewById(R.id.textView4);


        //   getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        //     ActionBar actionBar = getActionBar();
        //   actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#800000")));
        // actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#800000")));


        //mediaPlayer=mediaPlayer.create(this,R.raw.song);



        seekbar=(SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i % 2 == 0) {

                        mediaPlayer.go();

                    b3.setText("||");
                    seekbar.setClickable(true);

                    finalTime = mediaPlayer.getDur();
                    startTime = mediaPlayer.getPosn();

                    if (oneTimeOnly == 0) {
                        seekbar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }
                    tx2.setText(String.format("%d min,%d sec",
                                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                    );

                    tx1.setText(String.format("      %d min %d sec",
                                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                    );

                    seekbar.setProgress((int) startTime);
                    myHandler.postDelayed(UpdateSongTime, 100);
                    seekbar.setProgress((int) startTime);
                    i++;

                } else {
                    mediaPlayer.pausePlayer();
                    b3.setText(">");
                    i++;

                }
            }
        });



        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                try {
                    if (mediaPlayer.isPng() || mediaPlayer != null) {
                        if (fromUser)

                        { mediaPlayer.seek(progress);


                        }
                    } else if (mediaPlayer == null) {
                        Toast.makeText(getApplicationContext(), "Media is not running",
                                Toast.LENGTH_SHORT).show();
                        seekBar.setProgress(0);
                    }
                } catch (Exception e) {

                    seekBar.setEnabled(false);

                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.playNext();

                b3.setText("||");

                finalTime = mediaPlayer.getDur();
                startTime = mediaPlayer.getPosn();


                seekbar.setMax((int) finalTime);


                tx2.setText(String.format("%d min,%d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                tx1.setText(String.format("      %d min %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                seekbar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                seekbar.setProgress((int) startTime);


            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.playPrev();
                b3.setText("||");
                b3.setTextSize(55);


                finalTime = mediaPlayer.getDur();
                startTime = mediaPlayer.getPosn();


                seekbar.setMax((int) finalTime);


                tx2.setText(String.format("%d min,%d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                tx1.setText(String.format("      %d min %d sec",
                                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                seekbar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                seekbar.setProgress((int) startTime);

            }
        });
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getPosn();
            tx1.setText(String.format("%d min,%d sec",

                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))
            );

            myHandler.postDelayed(this, 100);
            seekbar.setProgress((int)startTime);
        }
    };


    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

            mediaPlayer = binder.getService();

            mediaPlayer.setList(songs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };



    public void updateplaylist()

    {
       /*File home=new File(SD_PATH);
        if(home.listFiles(new Mp3Filter()).length>0) {
            for (File file : home.listFiles(new Mp3Filter())) {
                songs.add(file.getName());

            }
            ArrayAdapter<String> songList = new ArrayAdapter<String>(this, R.layout.songlist, songs);
            Collections.sort(songs);
            l.setAdapter(songList);


        }*/
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songs.add(new song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        Collections.sort(songs, new Comparator<song>(){
            public int compare(song a, song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        Songadapter songAdt = new Songadapter(this, songs);
        l.setAdapter(songAdt);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        else {
            finalTime = mediaPlayer.getDur();
            startTime = mediaPlayer.getPosn();


            seekbar.setMax((int) finalTime);


            tx2.setText(String.format("%d min,%d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
            );

            tx1.setText(String.format("      %d min %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
            );

            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            seekbar.setProgress((int) startTime);

        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       // mediaPlayer.setSong(position);

        mediaPlayer.setSong(Integer.parseInt(view.getTag().toString()));
        b3.setText("||");

        finalTime = mediaPlayer.getDur();
        startTime = mediaPlayer.getPosn();


        seekbar.setMax((int) finalTime);


        tx2.setText(String.format("%d min,%d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
        );

        tx1.setText(String.format("      %d min %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );

        seekbar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
        seekbar.setProgress((int) startTime);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}



