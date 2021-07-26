package com.example.securestorage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    EditText ed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ed=(EditText)findViewById(R.id.pass);
    }

     public void onDone(View v)
     {
         final ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
         progressDialog.setTitle("Log In");
         progressDialog.setMessage("Logging In...");

         String generatedHash = null;
         try {
             MessageDigest md = MessageDigest.getInstance("SHA-512");
             String pass=ed.getText().toString().trim();
             if (pass.isEmpty()) {
                 Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                 return;
             }
             byte[] digest = md.digest(pass.getBytes());
             StringBuilder sb = new StringBuilder();
             for (int i = 0; i < digest.length; i++) {
                 sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
             }
             generatedHash=sb.toString();
             SharedPreferences sharedpreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
             String password = sharedpreferences.getString("hashedpassword", "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043");
             Log.d("This",password);
             if (!generatedHash.equals(password))
             {
                 Toast.makeText(getApplicationContext(), "Wrong Password Try Again", Toast.LENGTH_SHORT).show();
                 return;
             }
             progressDialog.show();
             Intent intent=new Intent(LoginActivity.this,MainActivity.class);
             startActivity(intent);
             finish();
             Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
         }
         catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
         }
     }

     public void newpass(View v)
     {
         Intent intent=new Intent(LoginActivity.this,NewPassword.class);
         startActivity(intent);
     }
}