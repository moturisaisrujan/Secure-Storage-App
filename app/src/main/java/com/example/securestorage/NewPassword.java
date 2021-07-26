package com.example.securestorage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NewPassword extends AppCompatActivity {

    EditText oldpass;
    EditText newpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        oldpass=(EditText)findViewById(R.id.oldpass);
        newpass=(EditText)findViewById(R.id.newpass);
    }

    public void change(View v)
    {
        String oldHash = null;
        String newHash = null;
        oldHash = sha512(oldpass);
        SharedPreferences sharedpreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String password = sharedpreferences.getString("hashedpassword", "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043");
        Log.d("oldhash",password);
        if (!oldHash.equals(password))
        {
            Toast.makeText(getApplicationContext(), "Old Password Didn't Match", Toast.LENGTH_SHORT).show();
            return;
        }
        newHash = sha512(newpass);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("hashedpassword", newHash);
        Log.d("newhash",newHash);
        editor.commit();
        Toast.makeText(getApplicationContext(), "Password Changed Sucessfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    public static String sha512(EditText ed)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            String pass = ed.getText().toString().trim();
            byte[] digest = md.digest(pass.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }



}