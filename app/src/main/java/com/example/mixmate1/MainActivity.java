package com.example.mixmate1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView TitleTrack1;
    TextView TitleTrack2;
    Button PlayTrack1;
    Button PlayTrack2;
    SeekBar SeekBar1;
    SeekBar seekbar2;
    String duration1;
    String duration2;
    MediaPlayer mediaPlayer1;
    MediaPlayer mediaPlayer2;
    ScheduledExecutorService timer1;
    ScheduledExecutorService timer2;
    public static final int maxVolume = 100;
    public static final int Choose_File = 99;
    public static final int Choose_File2 = 98;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button FileTrack1 = findViewById(R.id.FileTrack1);
        Button FileTrack2 = findViewById(R.id.FileTrack2);
        PlayTrack1 = findViewById(R.id.PlayTrack1);
        PlayTrack2 = findViewById(R.id.PlayTrack2);
        TitleTrack1 = findViewById(R.id.TitleTrack1);
        TitleTrack2 = findViewById(R.id.TitleTrack2);
        TextView SongProgress1 = findViewById(R.id.SongProgress1);
        TextView SongProgress2 = findViewById(R.id.SongProgress2);
        SeekBar1 = findViewById(R.id.SeekBar1);
        seekbar2 = findViewById(R.id.SeekBar2);
        SeekBar volumebar1 = findViewById(R.id.VolumeBar1);
        SeekBar volumebar2 = findViewById(R.id.VolumeBar2);

        /**
         * Open File
         */
        FileTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent, Choose_File);
            }
        });

        FileTrack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("audio/*");
                startActivityForResult(intent2, Choose_File2);
            }
        });

        /**
         * Play and Pause Buttons
         */
        PlayTrack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer1 != null) {
                    if (mediaPlayer1.isPlaying()){
                        mediaPlayer1.pause();
                        PlayTrack1.setText("PLAY");
                        timer1.shutdown();
                    } else {
                        //mediaPlayer1.setPlaybackParams(mediaPlayer1.getPlaybackParams().setSpeed(0.5f));
                        mediaPlayer1.start();
                        PlayTrack1.setText("PAUSE");

                        timer1 = Executors.newScheduledThreadPool(1);
                        timer1.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer1 != null) {
                                    if (!SeekBar1.isPressed()) {
                                        SeekBar1.setProgress(mediaPlayer1.getCurrentPosition());
                                    }
                                }
                            }
                        }, 10, 10, TimeUnit.MILLISECONDS);
                    }
                }
            }
        });

        PlayTrack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer2 != null) {
                    if (mediaPlayer2.isPlaying()){
                        mediaPlayer2.pause();
                        PlayTrack2.setText("PLAY");
                        timer2.shutdown();
                    } else {
                        //mediaPlayer2.setPlaybackParams(mediaPlayer2.getPlaybackParams().setSpeed(0.5f));
                        mediaPlayer2.start();
                        PlayTrack2.setText("PAUSE");

                        timer2 = Executors.newScheduledThreadPool(1);
                        timer2.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer2 != null) {
                                    // reset the seek bar
                                    if (!seekbar2.isPressed()) {
                                        seekbar2.setProgress(mediaPlayer2.getCurrentPosition());
                                    }
                                }
                            }
                        }, 10, 10, TimeUnit.MILLISECONDS);
                    }
                }
            }
        });

        /**
         * Song Progress (Time) and Seek functionality
         */
        SeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer1 != null){
                    int currentMilliseconds = mediaPlayer1.getCurrentPosition();
                    long currentSeconds = currentMilliseconds / 1000;
                    long showMinutes = currentSeconds / 60;
                    long showSeconds = currentSeconds - (showMinutes * 60);
                    SongProgress1.setText(showMinutes + ":" + showSeconds + " / " + duration1);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer1 != null) {
                    mediaPlayer1.seekTo(seekBar.getProgress());
                }
            }
        });
        seekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer2 != null){
                    int currentMilliseconds = mediaPlayer2.getCurrentPosition();
                    long currentSeconds = currentMilliseconds / 1000;
                    long showMinutes = currentSeconds / 60;
                    long showSeconds = currentSeconds - (showMinutes * 60);
                    SongProgress2.setText(showMinutes + ":" + showSeconds + " / " + duration2);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer2 != null) {
                    mediaPlayer2.seekTo(seekBar.getProgress());
                }
            }
        });

        /**
         * Volume Bars of Tracks
         */
        volumebar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //nothing
                if (mediaPlayer1 != null) {
                    // an equation is needed as volume does not increase linearly
                    final float volume = (float) (1 -
                            (Math.log(maxVolume - seekBar.getProgress()) / Math.log(maxVolume)));
                    mediaPlayer1.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //nothing
            }
        });
        volumebar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //nothing
                if (mediaPlayer2 != null) {
                    // Equation is needed as volume does not increase linearly
                    final float volume = (float) (1 -
                            (Math.log(maxVolume - seekBar.getProgress()) / Math.log(maxVolume)));
                    mediaPlayer2.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //nothing
            }
        });

        PlayTrack1.setEnabled(false);
        PlayTrack2.setEnabled(false);
        volumebar1.setProgress(100);
        volumebar2.setProgress(100);
    }

    /**
     * Open File Continued. Read the uri from the file selected to put into media player.
     * @param requestCode to tell the activity is ours
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Choose_File && resultCode == RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                createMediaPlayer(uri, 1);
            }
        }
        else if (requestCode == Choose_File2 && resultCode == RESULT_OK){
            if (data != null){
                Uri uri = data.getData();
                createMediaPlayer(uri, 2);
            }
        }
    }

    public void createMediaPlayer(Uri uri, Integer trackNumber){
        if (trackNumber == 1) {
            mediaPlayer1 = new MediaPlayer();
            mediaPlayer1.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                mediaPlayer1.setDataSource(getApplicationContext(), uri);
                mediaPlayer1.prepare();

                TitleTrack1.setText(getNameFromUri(uri));
                PlayTrack1.setEnabled(true);

                int maxMilliseconds = mediaPlayer1.getDuration();
                long maxSeconds = maxMilliseconds / 1000;
                long maxMinutes = maxSeconds / 60;
                long showMaxSeconds = maxSeconds - (maxMinutes * 60);
                duration1 = maxMinutes + ":" + showMaxSeconds;
                SeekBar1.setMax(maxMilliseconds);
                SeekBar1.setProgress(0);

                mediaPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        releaseMediaPlayer(1);
                        PlayTrack1.setText("PLAY");
                        PlayTrack1.setEnabled(false);
                    }
                });
            } catch (IOException e) {
                TitleTrack1.setText(e.toString());
            }
        }
        else if (trackNumber == 2){
            mediaPlayer2 = new MediaPlayer();
            mediaPlayer2.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                mediaPlayer2.setDataSource(getApplicationContext(), uri);
                mediaPlayer2.prepare();

                TitleTrack2.setText(getNameFromUri(uri));
                PlayTrack2.setEnabled(true);

                int maxMilliseconds = mediaPlayer2.getDuration();
                long maxSeconds = maxMilliseconds / 1000;
                long maxMinutes = maxSeconds / 60;
                long showMaxSeconds = maxSeconds - (maxMinutes * 60);
                duration2 = maxMinutes + ":" + showMaxSeconds;
                seekbar2.setMax(maxMilliseconds);
                seekbar2.setProgress(0);

                mediaPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        releaseMediaPlayer(2);
                        PlayTrack2.setText("PLAY");
                        PlayTrack2.setEnabled(false);
                    }
                });
            } catch (IOException e) {
                //nothing
            }
        }
    }

    @SuppressLint("Range")
    public String getNameFromUri(Uri uri){
        String fileName = "";
        Cursor cursor = null;
        cursor = getContentResolver().query(uri, new String[]{
                MediaStore.Images.ImageColumns.DISPLAY_NAME
        }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        }
        if (cursor != null) {
            cursor.close();
        }
        if (fileName.length() > 21) {
            fileName = fileName.substring(0, 21) + "..";
        }
        return fileName;
    }

    /**
     * Called when a scene or game changes or ends.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(1);
        releaseMediaPlayer(2);
    }

    /**
     * Called when media player is finished.
     * @param trackNumber Track 1 or 2
     */
    public void releaseMediaPlayer(Integer trackNumber) {
        if (trackNumber == 1) {
            if (timer1 != null) {
                timer1.shutdown();
            }
            if (mediaPlayer1 != null) {
                mediaPlayer1.release();
                mediaPlayer1 = null;
            }
            PlayTrack1.setEnabled(false);
            TitleTrack1.setText("");
            SeekBar1.setMax(100);
            SeekBar1.setProgress(0);
        } else if (trackNumber == 2) {
            if (timer2 != null) {
                timer2.shutdown();
            }
            if (mediaPlayer2 != null) {
                mediaPlayer2.release();
                mediaPlayer2 = null;
            }
            PlayTrack2.setEnabled(false);
            TitleTrack2.setText("");
            seekbar2.setMax(100);
            seekbar2.setProgress(0);
        }
    }
}
