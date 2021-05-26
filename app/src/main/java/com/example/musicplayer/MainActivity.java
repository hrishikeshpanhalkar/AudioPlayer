package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView imagPlayButton;
    private TextView textCurrentTime, textTotalDuration;
    private SeekBar playerSeekbar;
    private MediaPlayer mediaPlayer;
    Dialog dialog;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Please Wait...");
        dialog.show();
        imagPlayButton = findViewById(R.id.imagePlayButton);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        playerSeekbar = findViewById(R.id.playerSeekbar);
        mediaPlayer = new MediaPlayer();

        playerSeekbar.setMax(100);

        imagPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imagPlayButton.setImageResource(R.drawable.ic_play);
                }else {
                    mediaPlayer.start();
                    imagPlayButton.setImageResource(R.drawable.ic_pause);
                    updateSeekbar();
                }
            }
        });

        prepareMediaPlayer();

        playerSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar = (SeekBar) v;
                int playPosition = (mediaPlayer.getDuration() /100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPosition);
                textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerSeekbar.setSecondaryProgress(percent);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerSeekbar.setProgress(0);
                imagPlayButton.setImageResource(R.drawable.ic_play);
                textCurrentTime.setText("0:00");
                textTotalDuration.setText("0:00");
                mediaPlayer.reset();
                prepareMediaPlayer();
            }
        });
    }

    private void prepareMediaPlayer(){
        try{
            mediaPlayer.setDataSource("https://fitness-diet-info.000webhostapp.com/Music/ALONE%20FITNESS%20MOTIVATION%20-%202020.mp3");
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
            dialog.dismiss();
        }catch (Exception e){
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentPosition = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentPosition));
        }
    };

    private void updateSeekbar(){
        if(mediaPlayer.isPlaying()){
            playerSeekbar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }


    private String milliSecondsToTimer(long milliseconds){
        String timerString = "";
        String secondsString;

        int hours = (int) (milliseconds/(1000*60*60));
        int minutes = (int) (milliseconds%(1000*60*60)) /(1000*60);
        int seconds = (int) (milliseconds%(1000*60*60))%(1000*60)/1000;

        if(hours>0){
            timerString = hours + ":";
        }
        if(seconds<10){
            secondsString = "0" + seconds;
        }else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }

}