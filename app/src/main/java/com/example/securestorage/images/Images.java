package com.example.securestorage.images;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.securestorage.R;
import com.example.securestorage.audio.AudioAdapter;
import com.example.securestorage.audio.AudioEncrypter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Images extends AppCompatActivity {

    ImageView imageView;
    private SharedPreferences sharedPreferences;
    private ListView listView;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        setTitle("Images");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        FloatingActionButton fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

        listView = (ListView) findViewById(R.id.list_view2);
        SharedPreferences sp=getSharedPreferences("Images", Context.MODE_PRIVATE);
        HashMap<String,String> hs= (HashMap<String, String>) sp.getAll();
        Set<String> s=hs.keySet();
        List<Image> allImages=new ArrayList<>();
        Iterator<String> itr=s.iterator();
        while(itr.hasNext())
        {
            allImages.add(new Image(itr.next()));
        }
        adapter=new ImageAdapter(this,allImages);
        listView.setAdapter(adapter);



    }

    protected void onResume() {
        super.onResume();
        SharedPreferences sp=getSharedPreferences("Images", Context.MODE_PRIVATE);
        HashMap<String,String> hs= (HashMap<String, String>) sp.getAll();
        Set<String> s=hs.keySet();
        List<Image> allImages=new ArrayList<>();
        Iterator<String> itr=s.iterator();
        while(itr.hasNext())
        {
            allImages.add(new Image(itr.next()));
        }
        adapter=new ImageAdapter(this,allImages);
        listView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100)
        {
            if(resultCode==RESULT_OK) {
                Uri selectedImage = data.getData();
                File f = new File(selectedImage.getPath());
                byte[] img=getImage(selectedImage);
                byte[] enc=new byte[0];
                Log.d("img",img.length+" ");
                AudioEncrypter encrypter = new AudioEncrypter();
                try {
                    enc=encrypter.encrypt(f.getName(),img);
                    saveImage(enc,f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("img",enc.length+" ");
                sharedPreferences = getSharedPreferences("Images", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(f.getName(),f.getName());
                editor.commit();

                Toast.makeText(this, "Encrypted!", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void saveImage(byte[] encrypt, String name) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, UnrecoverableEntryException, InvalidAlgorithmParameterException {
        File dir = getExternalFilesDir(null);
        File path=new File(dir+"/"+ File.separator+"Images");
        if(!path.exists())
            path.mkdir();
        File file = new File(path + "/" + File.separator + name+".jpg");
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

    }

    public byte[] getImage(Uri uri) {
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


}

class Image
{
    String name;
    Image(String name)
    {
        this.name=name;
    }
}