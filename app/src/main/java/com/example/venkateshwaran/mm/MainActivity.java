package com.example.venkateshwaran.mm;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

class Mp3Filter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3"));
    }
}


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String SD_PATH = new String("/storage/emulated/0/audios/");
    private DrawerLayout drawerLayout;
    private MusicService mediaPlayer;
    private Intent playIntent;
    private String[] planets;
    public int t = 0;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    int pos = 0;
    int i = 2;
    private Button b1, b2, b3, b4;
    private TextView tx1, tx2, tx3;
    private boolean musicBound = false;
    private boolean paused = false;
    private ArrayList<song> songs = new ArrayList<song>();
    ListView l;
    public static int oneTimeOnly = 0;
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
            seekbar.setProgress((int) startTime);
        }
    };
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mediaPlayer = binder.getService();
            mediaPlayer.setList(songs);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(UpdateSongTime);
        unbindService(musicConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            paused = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        } else {
            // Permission has already been granted
            initialSetup(savedInstanceState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initialSetup(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            l = findViewById(R.id.list);
            l.setOnItemClickListener(this);
            updateplaylist();
            drawerLayout = findViewById(R.id.drawer_layout);
            ListView listView = findViewById(R.id.drawerList);
            planets = getResources().getStringArray(R.array.planets);
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, planets));
            // listView.setOnItemClickListener(this);
            b1 = findViewById(R.id.button);
            b3 = findViewById(R.id.button3);
            b4 = findViewById(R.id.button4);
            tx1 = findViewById(R.id.textView2);
            tx2 = findViewById(R.id.textView3);
            tx3 = findViewById(R.id.textView4);
            //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            //ActionBar actionBar = getActionBar();
            //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#800000")));
            //actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#800000")));
            seekbar = findViewById(R.id.seekBar);
            seekbar.setClickable(false);
        }

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i % 2 == 0) {
                    mediaPlayer.go();
                    b3.setText("||");
                    seekbar.setClickable(true);
                    if (oneTimeOnly == 0) {
                        seekbar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }
                    setup();
                } else {
                    mediaPlayer.pausePlayer();
                    b3.setText(">");
                }
                i++;
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
                        if (fromUser) {
                            mediaPlayer.seek(progress);
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
                setup();
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.playPrev();
                b3.setText("||");
                b3.setTextSize(55);
                setup();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateplaylist() {
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
        if (musicCursor != null && musicCursor.moveToFirst()) {
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
        Collections.sort(songs, new Comparator<song>() {
            public int compare(song a, song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        Songadapter songAdt = new Songadapter(this, songs);
        l.setAdapter(songAdt);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        } else {
            setup();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mediaPlayer.setSong(Integer.parseInt(view.getTag().toString()));
        b3.setText("||");
        setup();
    }

    public void setup() {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}



