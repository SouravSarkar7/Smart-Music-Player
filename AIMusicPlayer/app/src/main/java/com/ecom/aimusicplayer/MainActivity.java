package com.ecom.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout parentRelLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecIntent;
    private String store="";

    private ImageView pausePlaybtn,nextbtn,previousbtn;
    private TextView songNameText;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnableBtn;
    private String mode="ON";

    private static MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String songName;
    private SeekBar seekBar;

    Thread updateSeekBar;

    private int temp=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVoiceCommandPermission();


        pausePlaybtn=(ImageView)findViewById(R.id.play_pause_btn);
        nextbtn=(ImageView)findViewById(R.id.next_btn);
        previousbtn=(ImageView)findViewById(R.id.previous_btn);
        imageView=(ImageView)findViewById(R.id.songImg);

        lowerRelativeLayout=(RelativeLayout) findViewById(R.id.lower);
        voiceEnableBtn=(Button) findViewById(R.id.voiceEnable_btn);
        songNameText=(TextView) findViewById(R.id.songName);
        seekBar=(SeekBar)findViewById(R.id.seekbar);


        updateSeekBar=new Thread(){
            @Override
            public void run() {
                int totalDuration=myMediaPlayer.getDuration();
                int currentPosition=0;
                while (currentPosition<totalDuration)
                {
                    try {
                        sleep(1000);
                        currentPosition=myMediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);

                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };


        parentRelLayout=(RelativeLayout)findViewById(R.id.parentRelLayout);

        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(MainActivity.this);

        speechRecIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        audioInformationReceived();
        imageView.setBackgroundResource(R.drawable.logo);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String>matchfound=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matchfound!=null)
                {
                    if(mode.equals("ON"))
                    {
                        store=matchfound.get(0);

                        if(store.equals("pause the song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, "Command="+store, Toast.LENGTH_SHORT).show();
                        }
                        else if(store.equals("play the song"))
                        {
                            playPauseSong();
                            Toast.makeText(MainActivity.this, "Command="+store, Toast.LENGTH_SHORT).show();
                        }
                        else if(store.equals("play next song"))
                        {
                            playNextSong();
                            Toast.makeText(MainActivity.this, "Command="+store, Toast.LENGTH_SHORT).show();
                        }
                        else if(store.equals("play previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(MainActivity.this, "Command="+store, Toast.LENGTH_SHORT).show();
                        }
                    }


                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        parentRelLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecIntent);
                        store="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;

                }

                return false;
            }
        });

        voiceEnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("ON"))
                {
                    mode="OFF";
                    voiceEnableBtn.setText("Voice Enabled Mode- OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode="ON";
                    voiceEnableBtn.setText("Voice Enabled Mode- ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

        pausePlaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong();
            }
        });

        previousbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myMediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                }
            }
        });
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myMediaPlayer.getCurrentPosition()>0)
                playNextSong();
            }
        });

    }

    private void audioInformationReceived()
    {
        if(myMediaPlayer!=null)
        {
            if (myMediaPlayer.isPlaying()) {
                myMediaPlayer.stop();
            }
            myMediaPlayer.release();
            myMediaPlayer = null;
        }


        Intent intent=getIntent();


        Bundle bundle=intent.getExtras();
        mySongs=(ArrayList)bundle.getParcelableArrayList("song");
        songName=mySongs.get(position).getName();
        String songName=intent.getStringExtra("name");
        songNameText.setText(songName);
        songNameText.setSelected(true);

        position=bundle.getInt("position",0);
        Uri uri=Uri.parse(mySongs.get(position).toString());

        myMediaPlayer=MediaPlayer.create(MainActivity.this,uri);
        myMediaPlayer.start();
        seekBar.setMax(myMediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        temp=1;
    }


    private void checkVoiceCommandPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(!(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED))
            {
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong()
    {
        imageView.setBackgroundResource(R.drawable.four);
        if(myMediaPlayer.isPlaying())
        {
            pausePlaybtn.setImageResource(R.drawable.ic_play);
            myMediaPlayer.pause();
        }
        else {
            pausePlaybtn.setImageResource(R.drawable.ic_pause);
            myMediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void playNextSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position=((position+1)%mySongs.size());

        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(MainActivity.this,uri);
        songName=mySongs.get(position).toString();
        songNameText.setText(songName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.three);
        /*if(myMediaPlayer.isPlaying())
        {
            pausePlaybtn.setImageResource(R.drawable.ic_pause);
            myMediaPlayer.pause();
        }
        else {
            pausePlaybtn.setImageResource(R.drawable.ic_play);
            imageView.setBackgroundResource(R.drawable.five);
        }*/
    }

    private void playPreviousSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position=((position-1)<0?(mySongs.size()-1) : (position-1));

        Uri uri=Uri.parse(mySongs.get(position).toString());
        myMediaPlayer=MediaPlayer.create(MainActivity.this,uri);

        songName=mySongs.get(position).toString();
        songNameText.setText(songName);
        myMediaPlayer.start();

        imageView.setBackgroundResource(R.drawable.two);
       /* if(myMediaPlayer.isPlaying())
        {
            pausePlaybtn.setImageResource(R.drawable.ic_play);
            myMediaPlayer.pause();
        }
        else {
            pausePlaybtn.setImageResource(R.drawable.ic_pause);
            imageView.setBackgroundResource(R.drawable.five);
        }*/

    }
}