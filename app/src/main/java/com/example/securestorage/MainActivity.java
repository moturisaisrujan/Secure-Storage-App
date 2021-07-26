package com.example.securestorage;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.securestorage.audio.Audio;
import com.example.securestorage.images.Images;
import com.example.securestorage.notes.Notes;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView c1,c2,c3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c1=(CardView)findViewById(R.id.cd1);
        c2=(CardView)findViewById(R.id.cd2);
        c3=(CardView)findViewById(R.id.cd3);

        c1.setOnClickListener(this);
        c2.setOnClickListener(this);
        c3.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch(v.getId()){
            case R.id.cd1:
                i=new Intent(MainActivity.this, Notes.class);
                startActivity(i);
                break;
            case R.id.cd2:
                i=new Intent(MainActivity.this, Audio.class);
                startActivity(i);
                break;
            case R.id.cd3:
                i=new Intent(MainActivity.this, Images.class);
                startActivity(i);
                break;
        }
    }
}