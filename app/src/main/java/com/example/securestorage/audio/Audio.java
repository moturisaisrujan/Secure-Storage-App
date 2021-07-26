package com.example.securestorage.audio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.securestorage.R;
import com.example.securestorage.notes.Adapter;
import com.example.securestorage.notes.NewNote;
import com.example.securestorage.notes.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ScatteringByteChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Audio extends AppCompatActivity {

    Uri audioUri;

    private static final int PICK_AUDIO=1;


    byte[] enc;
    byte[] iv;
    byte[] dec;
    SharedPreferences sharedPreferences;
    private ListView listView;
    private AudioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        setTitle("Audio");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);



        FloatingActionButton fab1 = findViewById(R.id.fab1);
         fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), PICK_AUDIO);
            }
        });

        listView = (ListView) findViewById(R.id.list_view1);
        SharedPreferences sp=getSharedPreferences("AudioNames", Context.MODE_PRIVATE);
        HashMap<String,String> hs= (HashMap<String, String>) sp.getAll();
        Set<String> s=hs.keySet();
        List<AudioFile> allAudioFiles=new ArrayList<>();
        Iterator<String> itr=s.iterator();
        while(itr.hasNext())
        {
            allAudioFiles.add(new AudioFile(itr.next()));
        }
        adapter=new AudioAdapter(this,allAudioFiles);
        listView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp=getSharedPreferences("AudioNames", Context.MODE_PRIVATE);
        HashMap<String,String> hs= (HashMap<String, String>) sp.getAll();
        Set<String> s=hs.keySet();
        List<AudioFile> allAudioFiles=new ArrayList<>();
        Iterator<String> itr=s.iterator();
        while(itr.hasNext())
        {
            allAudioFiles.add(new AudioFile(itr.next()));
        }
        adapter=new AudioAdapter(this,allAudioFiles);
        listView.setAdapter(adapter);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK)
        {
            audioUri = data.getData();
            AudioEncrypter encrypter=null;
            try {
                encrypter = new AudioEncrypter();
                String nameE = getFileName(audioUri);
                Log.d("Enc name", nameE);
                byte[] arr=getAudio(audioUri);
                Log.d("Before enc", arr.length+"");
                enc = encrypter.encrypt(nameE, arr);
                iv = encrypter.getIv();
                Log.d("After enc", enc.length+"");
                saveAudio(enc,nameE);

               sharedPreferences = getSharedPreferences("AudioNames", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(nameE, nameE);
                editor.commit();


                Toast.makeText(this, "Encrypted!", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }



    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        result=result.substring(0,result.length()-4);
        return result;
    }

    public byte[] getAudio(Uri uri) {
        InputStream iStream = null;
        byte[] aud = new byte[0];
        try {
            iStream = getContentResolver().openInputStream(uri);
            aud = getBytes(iStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aud;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void saveAudio(byte[] encrypt, String name) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, UnrecoverableEntryException, InvalidAlgorithmParameterException {
            File dir = getExternalFilesDir(null);
            File path=new File(dir+"/"+ File.separator+"Audio File");
            if(!path.exists())
                path.mkdir();
            File file = new File(path + "/" + File.separator + name+".mp3");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream fo = null;
            try {
                fo = new FileOutputStream(file);
                fo.write(encrypt);
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("status:",file.getName().substring(0,file.getName().length()-4).equals(name)+"");
            byte[] a=getBytes(new FileInputStream(file));
            Log.d("bytes",a.toString());



    }


}

class AudioFile
{
    String name;
    AudioFile(String name)
    {
        this.name=name;
    }
}