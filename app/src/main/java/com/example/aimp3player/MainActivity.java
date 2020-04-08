package com.example.aimp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private String[] itemsAll;
    private ListView mSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSongsList = findViewById(R.id.songsList);



        appExternalStorageStoragePermission();
    }





    public void appExternalStorageStoragePermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        displayAudioSongsName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response)
                    {
                        Toast.makeText(MainActivity.this, "Please Grant Permission to Enjoy with us", Toast.LENGTH_LONG).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }



    public ArrayList<File> readOnlyAudioSongs(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for (File indivisualFile : allFiles)
        {
            if (indivisualFile.isDirectory() && !indivisualFile.isHidden())
            {
                arrayList.addAll(readOnlyAudioSongs(indivisualFile));
            }
            else
            {
                if (indivisualFile.getName().endsWith(".mp3") || indivisualFile.getName().endsWith(".aac") || indivisualFile.getName().endsWith(".wav") || indivisualFile.getName().endsWith(".wma"))
                {
                    arrayList.add(indivisualFile);
                }
            }
        }

        return arrayList;
    }




    private void displayAudioSongsName()
    {
        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemsAll = new String[audioSongs.size()];

        for (int songCounter = 0; songCounter < audioSongs.size(); songCounter ++)
        {
            itemsAll[songCounter] = audioSongs.get(songCounter).getName();
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, itemsAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String songName = mSongsList.getItemAtPosition(i).toString();

                // now we will send user from main activity to smart player Activity with some some information

                Intent intent = new Intent(MainActivity.this, SmartPlayerActivity.class);
                intent.putExtra("song", audioSongs);
                intent.putExtra("name", songName);
                intent.putExtra("position", i);
                startActivity(intent);
            }
        });

    }


}
