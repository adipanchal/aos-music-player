package com.adjing.music;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {
    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekbar;
    ImageView playPause,nxt,prev,cd;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //CHANGE ACTIONBAR COLOR
        Window window = this.getWindow();
        ((Window) window).addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.statusbar_player));
        //

        titleTv=findViewById(R.id.song_title);
        currentTimeTv=findViewById(R.id.current_time);
        totalTimeTv=findViewById(R.id.total_time);
        seekbar=findViewById(R.id.bar);
        playPause=findViewById(R.id.play_pause);
        nxt=findViewById(R.id.nxt);
        prev=findViewById(R.id.prev);
        cd=findViewById(R.id.cd);

        titleTv.setSelected(true);

        songsList=(ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        playPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                        cd.setRotation(x++);
                    }else{
                        playPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
                        cd.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResourcesWithMusic() {
        currentSong=songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());

        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));
        playPause.setOnClickListener(v -> pause());
        nxt.setOnClickListener(v -> next());
        prev.setOnClickListener(v -> previous());

        play();
    }

    private void play(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekbar.setProgress(0);
            seekbar.setMax(mediaPlayer.getDuration());
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private void pause(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }
    private void next(){
        if(MyMediaPlayer.currentIndex==songsList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }
    private void previous(){
        if(MyMediaPlayer.currentIndex==songsList.size()-0){
            return;
        }
        MyMediaPlayer.currentIndex-=1;
        mediaPlayer.reset();
        setResourcesWithMusic();
    }
    public static String convertToMMSS(String duration){
        Long millis=Long.parseLong(duration);

        return String.format("%02d:%02d",
             TimeUnit.MILLISECONDS.toMinutes(millis)%TimeUnit.HOURS.toMinutes(1),
             TimeUnit.MILLISECONDS.toSeconds(millis)%TimeUnit.MINUTES.toSeconds(1));
    }
}