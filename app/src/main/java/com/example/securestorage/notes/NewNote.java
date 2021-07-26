package com.example.securestorage.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.securestorage.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class NewNote extends AppCompatActivity {

    EditText title;
    EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        setTitle("New Note");

        title=findViewById(R.id.title);
        content=findViewById(R.id.content);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.save_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        Calendar c=Calendar.getInstance();
        String todaysDate=c.get(Calendar.DAY_OF_MONTH)+"/"+(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
        String curTime=pad(c.get(Calendar.HOUR))+":"+pad(c.get(Calendar.MINUTE));
        String notes_title=title.getText().toString().trim();
        String notes_content=content.getText().toString();

        if(notes_title.isEmpty() || notes_content.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Either Title or Content is Empty", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

        //Encrypting Text
        EncAndDec enc=new EncAndDec();
        byte[] encryptedText=enc.encrypt(notes_title,notes_content);
        Log.d("Encrypted Text:",encryptedText.toString());
        byte[] notes_content1=encryptedText;
        byte[] notes_iv=enc.enCryptor.getIv();



        //Database Part
        NotesDB helper=new NotesDB(this,NotesDB.DATABASE_NAME,null,NotesDB.DATABASE_VERSION);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NotesDB.column_title, notes_title);
        values.put(NotesDB.column_content,notes_content1);
        values.put(NotesDB.column_iv, notes_iv);
        values.put(NotesDB.column_date, todaysDate);
        values.put(NotesDB.column_time, curTime);
        

        db.insert(NotesDB.table_name,null,values);

        finish();
        Toast.makeText(getApplicationContext(), "Succesfully Encrypted And Saved", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private String pad(int i) {
        if(i<10)
            return "0"+i;
        else
            return String.valueOf(i);
    }

}


