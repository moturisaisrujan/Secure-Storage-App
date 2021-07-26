package com.example.securestorage.images;

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
import com.example.securestorage.audio.AudioPlayer;

import java.util.List;

public class ImageAdapter extends BaseAdapter
{
    Context context;
    List<Image> allImages;

    public ImageAdapter(Context activity, List<Image> allImages) {

        this.context = activity;
        this.allImages = allImages;
    }

    @Override
    public int getCount() {
        return allImages.size();
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
            v = inflater.inflate(R.layout.each_image_row, null);

        }

        TextView imageTitle = (TextView)v.findViewById(R.id.ImageTitle);
        Button show= (Button)v.findViewById(R.id.show);

        show.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, ImageViewer.class);
                intent.putExtra("Name",allImages.get(position).name);
                context.startActivity(intent);
                Toast.makeText(context, "Succesfully Decrypted", Toast.LENGTH_SHORT).show();
            }

        });

        imageTitle.setText(position+1+". "+allImages.get(position).name);
        return v;
    }

}
