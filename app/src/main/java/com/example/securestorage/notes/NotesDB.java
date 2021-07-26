 package com.example.securestorage.notes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NotesDB extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
     static final String DATABASE_NAME = "notes.db";

    static final String table_name="notes";
    static final String column_title="title";
    static final String column_content="content";
    static final String column_iv="iv";
    static final String column_date="date";
    static final String column_time="time";

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    public NotesDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String create_table =  "CREATE TABLE " + NotesDB.table_name + " ("
                + NotesDB.column_title + " TEXT PRIMARY KEY , "
                + NotesDB.column_content+ " BLOB NOT NULL, "
                + NotesDB.column_iv + " BLOB, "
                + NotesDB.column_date + " TEXT, "
                + NotesDB.column_time + " TEXT);";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<EachNote> getAllNotes(){
        List<EachNote> allNotes = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery("Select * from "+NotesDB.table_name,null);
        while(cursor.moveToNext()) {
            EachNote cur=new EachNote(cursor.getString(0),cursor.getString(3)+" "+cursor.getString(4));
            allNotes.add(cur);
        }
        cursor.close();
        return allNotes;


    }

    public void deleteNote(String alias)  {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "title=?";
        String whereArgs[] = {alias};
        int x=db.delete(NotesDB.table_name, whereClause, whereArgs);
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            Enumeration<String> enumeration=keyStore.aliases();
            while(enumeration.hasMoreElements())
                enumeration.nextElement();
            keyStore.deleteEntry(alias);
            Log.d("status:", keyStore.size()+" "+x);
        } catch (KeyStoreException e) {
            Log.d("status:","Not Deleted");
        }

    }


}
