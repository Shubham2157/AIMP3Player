package com.example.aimp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MicrophoneInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class SmartPlayerActivity extends AppCompatActivity
{
    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";
    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private SeekBar seekBar;
    private Runnable runnable;
    private Handler handler;
    private TextView songNameTxt;
    private  ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode = "ON";
    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);

        checkVoiceCommandPermission();

        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        imageView = findViewById(R.id.logo);
        handler = new Handler();
        seekBar = findViewById(R.id.seekbar);
        lowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.songName);


        parentRelativeLayout = findViewById(R.id.parentRelativeLayout);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());




        validateReceiveValuesAndStartPlaying();
        seekBar.setMax((myMediaPlayer.getDuration()));
        changeSeekbar();
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
            public void onResults(Bundle bundle)
            {
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matchesFound != null)
                {
                    if (mode.equals("ON"))
                    {
                        keeper = matchesFound.get(0);

                        if (keeper.equals("pause the song") || keeper.equals("hey pause the song"))
                        {
                            playPauseSong();
                            seekBar.setMax((myMediaPlayer.getDuration()));
                            changeSeekbar();
                            Toast.makeText(SmartPlayerActivity.this, "Ooooooo.. Song Stops", Toast.LENGTH_LONG).show();
                        }
                        else if (keeper.equals("play the song") || keeper.equals("hey play the song"))
                        {
                            playPauseSong();
                            seekBar.setMax((myMediaPlayer.getDuration()));
                            changeSeekbar();
                            Toast.makeText(SmartPlayerActivity.this, "Yup, Song Start", Toast.LENGTH_LONG).show();
                        }
                        else if (keeper.equals("play next song"))
                        {
                            playNextSong();
                            Toast.makeText(SmartPlayerActivity.this, "Playing Next Song", Toast.LENGTH_LONG).show();
                        }
                        else if (keeper.equals("play previous song"))
                        {
                            playPreviousSong();
                            Toast.makeText(SmartPlayerActivity.this, "Playing Previous Song", Toast.LENGTH_LONG).show();
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




        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper = "";
                        break;

                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        break;
                }

                return false;

            }
        });




        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if (mode.equals("ON"))
                {
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                    mode = "OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                    mode = "ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }

            }
        });


        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                playPauseSong();
                seekBar.setMax((myMediaPlayer.getDuration()));
                changeSeekbar();
            }
        });


        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (myMediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (myMediaPlayer.getCurrentPosition()>0)
                {
                    playNextSong();
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                }
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if (b)
                {
                    myMediaPlayer.seekTo(i);
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




    private void validateReceiveValuesAndStartPlaying()
    {
        if (myMediaPlayer != null)
        {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle =intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);
        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());


        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        myMediaPlayer.start();
    }




    private void checkVoiceCommandPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED))
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }


    private  void playPauseSong()
    {
        imageView.setBackgroundResource(R.drawable.four);


        if (myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();
        }

        else
            {
                myMediaPlayer.start();
                pausePlayBtn.setImageResource(R.drawable.pause);
                imageView.setBackgroundResource(R.drawable.five);
            }
    }


    private void playNextSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position+1)%mySongs.size());

        Uri uri = Uri.parse((mySongs.get(position).toString()));
        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);


        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();


        imageView.setBackgroundResource(R.drawable.three);

        if (myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);

        }

        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }


    }



    private void playPreviousSong()
    {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();

        position = ((position-1)<0 ? (mySongs.size()-1) : (position-1));

        Uri uri = Uri.parse((mySongs.get(position).toString()));
        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);


        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();


        imageView.setBackgroundResource(R.drawable.two);

        if (myMediaPlayer.isPlaying())
        {
            pausePlayBtn.setImageResource(R.drawable.pause);

        }

        else
        {
            pausePlayBtn.setImageResource(R.drawable.play);

            imageView.setBackgroundResource(R.drawable.five);
        }
    }




    private void changeSeekbar()
    {
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());

        if (myMediaPlayer.isPlaying())
        {
            runnable = new Runnable() {
                @Override
                public void run()
                {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable,1000);

        }
    }


}
