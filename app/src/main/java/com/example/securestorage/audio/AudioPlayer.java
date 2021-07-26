package com.example.securestorage.audio;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.rtp.AudioStream;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.securestorage.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class AudioPlayer extends AppCompatActivity {

    TextView audioName;
    ImageView btRew,btPlay,btPause,btFF;
    String name;
    MediaPlayer mediaPlayer;
    Uri uri;
    File orgDir,orgPath,file;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        setTitle("Audio File");

        Intent intent=getIntent();
        name=intent.getStringExtra("Name");

        audioName=findViewById(R.id.audioName);
        btRew=findViewById(R.id.btn_rew);
        btPlay=findViewById(R.id.btn_play);
        btPause=findViewById(R.id.btn_pause);
        btFF=findViewById(R.id.btn_ff);

        audioName.setText(name);



        //Get File and Decrypt it
        orgDir = getExternalFilesDir(null);
        orgPath=new File(orgDir+"/"+ File.separator+"Audio File");
        File orgFile = new File(orgPath + "/" + File.separator + name+".mp3");
        byte[] enc = new byte[0];
        try {
            enc=Files.readAllBytes(orgFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        AudioDecrypter d= null;
        byte[] dec=new byte[0];
        try {
            d = new AudioDecrypter();
            dec=d.decrypt(name,enc);
        } catch (Exception e) {
            Log.d("Exception",e.getMessage());
        }

        //Intialize MediaPlayer
        uri=getAudioUri(dec,name);
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(AudioPlayer.this,uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPlay.setVisibility(View.GONE);
                btPause.setVisibility(View.VISIBLE);
                mediaPlayer.start();
            }
        });

        btPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPlay.setVisibility(View.VISIBLE);
                btPause.setVisibility(View.GONE);
                mediaPlayer.pause();
            }
        });

        btFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition=mediaPlayer.getCurrentPosition();
                int duration=mediaPlayer.getDuration();

                if(mediaPlayer.isPlaying() && duration!=currentPosition){
                    currentPosition+=5000;
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        btRew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition=mediaPlayer.getCurrentPosition();

                if(mediaPlayer.isPlaying()){
                    if(currentPosition<5000)
                        currentPosition=0;
                    else
                        currentPosition-=5000;
                    mediaPlayer.seekTo(currentPosition);
                }

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                btPause.setVisibility(View.GONE);
                btPlay.setVisibility(View.VISIBLE);
                mediaPlayer.seekTo(0);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaPlayer.release();
        file.delete();




    }

    public Uri getAudioUri(byte[] dec, String name)
    {
       file = new File(orgPath + "/" + File.separator + name+"_dec"+".mp3");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream fo = null;
        try {
            fo = new FileOutputStream(file);
            fo.write(dec);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(file);

    }


}