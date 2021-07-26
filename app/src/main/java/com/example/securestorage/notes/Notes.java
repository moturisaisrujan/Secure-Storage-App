package com.example.securestorage.notes;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.securestorage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

public class Notes extends AppCompatActivity {

    Adapter adapter;
    ListView listView;
    NotesDB helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        setTitle("Notes");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Notes.this, NewNote.class);
                startActivity(i);
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        helper=new NotesDB(this,NotesDB.DATABASE_NAME,null,NotesDB.DATABASE_VERSION);
        List<EachNote> allNotes=helper.getAllNotes();
        adapter=new Adapter(this,allNotes);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<EachNote> allNotes = helper.getAllNotes();
        adapter=new Adapter(this,allNotes);
        listView.setAdapter(adapter);
    }
}

class EachNote
{
    String Title;
    String DateAndTime;

    EachNote(String Title,String DateAndTime)
    {
        this.Title=Title;
        this.DateAndTime=DateAndTime;
    }
}