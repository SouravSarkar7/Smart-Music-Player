package com.ecom.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    private String[] audioAll;

    private ListView songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songList=(ListView)findViewById(R.id.songList);

        storagePermission();
    }



    public void storagePermission()
    {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                    {
                        dispSongName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File>readAudio(File file)
    {
        ArrayList<File>arrayList=new ArrayList<>();

        File[] allFile=file.listFiles();

        for(File iFile:allFile)
        {
            if(iFile.isDirectory() && !iFile.isHidden())
            {
                arrayList.addAll(readAudio(iFile));
            }
            else{
                if(iFile.getName().endsWith(".mp3") || iFile.getName().endsWith(".aac") ||iFile.getName().endsWith(".wav") ||iFile.getName().endsWith(".wma"))
                {
                    arrayList.add(iFile);
                }
            }
        }
        return arrayList;
    }

    private void dispSongName()
    {
        final ArrayList<File>audioSong=readAudio(Environment.getExternalStorageDirectory());

        audioAll=new String[audioSong.size()];

        for(int i=0;i<audioSong.size();i++)
        {
            audioAll[i]=audioSong.get(i).getName();
        }

        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>(PlayerActivity.this,android.R.layout.simple_list_item_1,audioAll);
        songList.setAdapter(arrayAdapter);

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName=songList.getItemAtPosition(i).toString();

                Intent intent=new Intent(PlayerActivity.this,MainActivity.class);
                intent.putExtra("song",audioSong);
                intent.putExtra("name",songName);
                intent.putExtra("position",i);

                startActivity(intent);


            }
        });
    }


}