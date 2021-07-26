package com.example.securestorage.audio;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securestorage.R;
import com.example.securestorage.notes.EditNote;

import java.util.List;


public class AudioAdapter extends BaseAdapter {

    Context context;
    List<AudioFile> allAudioFiles;

    public AudioAdapter(Context activity, List<AudioFile> allAudioFiles) {

        this.context = activity;
        this.allAudioFiles = allAudioFiles;
    }

    @Override
    public int getCount() {
        return allAudioFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.each_audiofile_row, null);

        }

        TextView audioTitle = (TextView)v.findViewById(R.id.AudioTitle);
        Button play= (Button)v.findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, AudioPlayer.class);
                intent.putExtra("Name",allAudioFiles.get(position).name);
                context.startActivity(intent);
                Toast.makeText(context, "Succesfully Decrypted", Toast.LENGTH_SHORT).show();
            }

        });

        audioTitle.setText(position+1+". "+allAudioFiles.get(position).name);
        return v;
    }
}
