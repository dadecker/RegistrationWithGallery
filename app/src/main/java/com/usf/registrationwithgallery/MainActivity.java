package com.usf.registrationwithgallery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.logging.Level;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button register;
    EditText email,password;
    TextView login;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();



        login = (TextView) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    private void registerUser() {
        String email_string = email.getText().toString().trim();
        String password_string = password.getText().toString().trim();
        Toast.makeText(this, "entering email", Toast.LENGTH_SHORT).show();

        if(TextUtils.isEmpty(email_string)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password_string)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email_string, password_string)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(MainActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            //message.hide();
                            Log.e("LoginActivity", "Failed Registration", e);
                            return;
                        }
                        else if(task.isSuccessful()){
                            //finish();
                            //startActivity(new Intent(getApplicationContext(), DisplayActivity.class));
                            Toast.makeText(MainActivity.this, "success!!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "error, registration failed", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(this, "starting onStart()", Toast.LENGTH_SHORT).show();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            Toast.makeText(MainActivity.this, "User already logged in", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        if(view == register){
            registerUser();
        }

        if(view == login){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}