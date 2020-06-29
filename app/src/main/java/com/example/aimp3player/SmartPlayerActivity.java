package com.example.aimp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MicrophoneInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

public class SmartPlayerActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private RelativeLayout parentRelativeLayout1;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";
    private ImageView pausePlayBtn, nextBtn, previousBtn;
    private SeekBar seekBar;
    private Runnable runnable;
    private Handler handler;
    private TextView songNameTxt;
    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;
    private String mode = "ON";
    private MediaPlayer myMediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;
    private TextView totalTime;
    private TextView currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);

        requestAudioPermissions();



        pausePlayBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        previousBtn = findViewById(R.id.previous_btn);
        imageView = findViewById(R.id.logo);
        handler = new Handler();
        seekBar = findViewById(R.id.seekbar);
        lowerRelativeLayout = findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_enabled_btn);
        songNameTxt = findViewById(R.id.songName);
        currentTime = findViewById(R.id.elapsedTimeLabel);
        totalTime = findViewById(R.id.remainingTimeLabel);
        parentRelativeLayout1 = findViewById(R.id.parentRelativeLayout1);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        validateReceiveValuesAndStartPlaying();
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
                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matchesFound != null) {
                    if (mode.equals("ON")) {
                        keeper = matchesFound.get(0);

                        if (keeper.equals("pause") || keeper.equals("pause the song")) {
                            playPauseSong();
                            seekBar.setMax((myMediaPlayer.getDuration()));
                            changeSeekbar();
                            updateDuration();
                            Toast.makeText(SmartPlayerActivity.this, "Ooooooo.. Song Stops", Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("play") || keeper.equals("play the song")) {
                            playPauseSong();
                            seekBar.setMax((myMediaPlayer.getDuration()));
                            changeSeekbar();
                            updateDuration();
                            Toast.makeText(SmartPlayerActivity.this, "Yup, Song Start", Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("play next song") || keeper.equals("next song")) {
                            playNextSong();
                            seekBar.setMax((myMediaPlayer.getDuration()));
                            changeSeekbar();
                            updateDuration();
                            Toast.makeText(SmartPlayerActivity.this, "Playing Next Song", Toast.LENGTH_LONG).show();
                        } else if (keeper.equals("play previous song") || keeper.equals("previous song")) {
                            playPreviousSong();
                            seekBar.setMax((myMediaPlayer.getDuration()));
                            changeSeekbar();
                            updateDuration();
                            Toast.makeText(SmartPlayerActivity.this, "Playing Previous Song", Toast.LENGTH_LONG).show();
                        }
                        String totTime = createTimerLabel(myMediaPlayer.getDuration());
                        totalTime.setText(totTime);
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

        parentRelativeLayout1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
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
            public void onClick(View view) {

                if (mode.equals("ON")) {
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                    updateDuration();
                    mode = "OFF";
                    voiceEnabledBtn.setText("Voice Enabled Mode - OFF");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                    parentRelativeLayout1.setVisibility(View.GONE);
                } else {
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                    updateDuration();
                    mode = "ON";
                    voiceEnabledBtn.setText("Voice Enabled Mode - ON");
                    lowerRelativeLayout.setVisibility(View.GONE);
                    parentRelativeLayout1.setVisibility(View.VISIBLE);
                }

            }
        });
        pausePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong();
                seekBar.setMax((myMediaPlayer.getDuration()));
                changeSeekbar();
                updateDuration();

            }
        });
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMediaPlayer.getCurrentPosition() > 0) {
                    playPreviousSong();
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                    updateDuration();
                    String totTime = createTimerLabel(myMediaPlayer.getDuration());
                    totalTime.setText(totTime);
                }
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myMediaPlayer.getCurrentPosition() > 0) {
                    playNextSong();
                    seekBar.setMax((myMediaPlayer.getDuration()));
                    changeSeekbar();
                    updateDuration();
                    String totTime = createTimerLabel(myMediaPlayer.getDuration());
                    totalTime.setText(totTime);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
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

    @Override
    protected void onDestroy() {
        myMediaPlayer.stop();
        super.onDestroy();
    }

    private void validateReceiveValuesAndStartPlaying() {
        if (myMediaPlayer != null) {
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("song");
        mSongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);
        position = bundle.getInt("position", 0);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        myMediaPlayer.start();
        String totTime = createTimerLabel(myMediaPlayer.getDuration());
        totalTime.setText(totTime);
        seekBar.setMax((myMediaPlayer.getDuration()));
        changeSeekbar();
        updateDuration();
        myMediaPlayer.setLooping(true);

    }

    private void checkVoiceCommandPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }

        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Closing application")
                    .setMessage("Please Provide MicroPhone permission from app settings")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("No", null).show();
        }
    }


    private void playPauseSong() {
        imageView.setBackgroundResource(R.drawable.four);
        if (myMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.play);
            myMediaPlayer.pause();

        } else {
            myMediaPlayer.start();
            myMediaPlayer.setLooping(true);
            pausePlayBtn.setImageResource(R.drawable.pause);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }


    private void playNextSong() {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position = ((position + 1) % mySongs.size());
        Uri uri = Uri.parse((mySongs.get(position).toString()));
        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();
        myMediaPlayer.setLooping(true);
        imageView.setBackgroundResource(R.drawable.three);

        if (myMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.pause);

        } else {
            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }


    private void playPreviousSong() {
        myMediaPlayer.pause();
        myMediaPlayer.stop();
        myMediaPlayer.release();
        position = ((position - 1) < 0 ? (mySongs.size() - 1) : (position - 1));
        Uri uri = Uri.parse((mySongs.get(position).toString()));
        myMediaPlayer = MediaPlayer.create(SmartPlayerActivity.this, uri);
        mSongName = mySongs.get(position).toString();
        songNameTxt.setText(mSongName);
        myMediaPlayer.start();
        myMediaPlayer.setLooping(true);
        imageView.setBackgroundResource(R.drawable.two);

        if (myMediaPlayer.isPlaying()) {
            pausePlayBtn.setImageResource(R.drawable.pause);
        } else {
            pausePlayBtn.setImageResource(R.drawable.play);
            imageView.setBackgroundResource(R.drawable.five);
        }
    }

    private void changeSeekbar() {
        seekBar.setProgress(myMediaPlayer.getCurrentPosition());
        if (myMediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();

                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    private void updateDuration() {
        currentTime.setText(createTimerLabel(myMediaPlayer.getCurrentPosition()));
        if (myMediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateDuration();
                }
            };
            handler.postDelayed(runnable, 100);
        }
    }

    public String createTimerLabel(int duration) {
        String timerLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        timerLabel += min + ":";
        if (sec < 10) timerLabel += "0";
        timerLabel += sec;
        return timerLabel;

    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to use AI Feature", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }

        }
    }

}
