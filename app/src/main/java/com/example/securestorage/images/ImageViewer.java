package com.example.securestorage.images;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.securestorage.R;
import com.example.securestorage.audio.AudioDecrypter;
import com.example.securestorage.audio.AudioPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class ImageViewer extends AppCompatActivity {

    String name;
    ImageView imageView;
    File orgDir,orgPath,file;
    Uri uri;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        setTitle("Images");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        Intent intent=getIntent();
        name=intent.getStringExtra("Name");
        imageView=findViewById(R.id.imageView);

        //Get image and decrypt it
        orgDir = getExternalFilesDir(null);
        orgPath=new File(orgDir+"/"+ File.separator+"Images");
        File orgFile = new File(orgPath + "/" + File.separator + name+".jpg");
        byte[] enc = new byte[0];
        try {
            enc= Files.readAllBytes(orgFile.toPath());
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

        uri=getImageUri(dec,name);
        Log.d("pewswe",uri.getPath());
        Glide.with(this).load(uri).into(imageView);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        file.delete();
    }

    public Uri getImageUri(byte[] dec, String name)
    {
        file = new File(orgPath + "/" + File.separator + name+"_dec"+".jpg");
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