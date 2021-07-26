package com.example.securestorage.notes;

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

import java.util.ArrayList;
import java.util.List;

public class Adapter extends BaseAdapter {

    Context context;
    List<EachNote> allNotes;

    public Adapter(Context activity, List<EachNote> allNotes) {

        this.context = activity;
        this.allNotes = allNotes;
    }


    @Override
    public int getCount() {
        return allNotes.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = view;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.each_notes_row, null);

        }

        TextView title = (TextView)v.findViewById(R.id.heading);
        TextView dateandtime = (TextView)v.findViewById(R.id.dateandtime);
        Button decrypt= (Button)v.findViewById(R.id.decrypt);

        decrypt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context,EditNote.class);
                intent.putExtra("Alias",allNotes.get(i).Title);
                intent.putExtra("Date",allNotes.get(i).DateAndTime);
                context.startActivity(intent);
                Toast.makeText(context, "Succesfully Decrypted", Toast.LENGTH_SHORT).show();
            }

        });

        title.setText(allNotes.get(i).Title);
        dateandtime.setText(allNotes.get(i).DateAndTime);

        return v;
    }

}
