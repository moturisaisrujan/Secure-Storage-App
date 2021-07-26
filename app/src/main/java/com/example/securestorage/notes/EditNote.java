package com.example.securestorage.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.securestorage.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EditNote extends AppCompatActivity {

    TextView tv1;
    TextView tv2;
    TextView tv3;

    String alias;
    String dateandtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        setTitle("Edit Note");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        tv1=findViewById(R.id.display1);
        tv2=findViewById(R.id.display2);
        tv3=findViewById(R.id.display3);

        Intent intent=getIntent();
        alias=intent.getStringExtra("Alias");
        dateandtime=intent.getStringExtra("Date");

        //Database Part
        NotesDB helper=new NotesDB(this,NotesDB.DATABASE_NAME,null,NotesDB.DATABASE_VERSION);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+NotesDB.table_name+" Where title=?",new String[] {alias + ""});
        cursor.moveToFirst();

        //Decryption
        EncAndDec dec=new EncAndDec();
        String value="Nothing Present";
        try {
            value=dec.decrypt(alias,cursor.getBlob(1),cursor.getBlob(2));
        }
        catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        cursor.close();
        tv1.setText(alias);
        tv2.setText(dateandtime);
        tv3.setText(value);


        //Deletion Part
        FloatingActionButton fab = findViewById(R.id.del);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  helper.deleteNote(alias);
                 Toast.makeText(getApplicationContext(), "Succesfully Deleted", Toast.LENGTH_SHORT).show();
                  finish();
            }
        });


    }



}