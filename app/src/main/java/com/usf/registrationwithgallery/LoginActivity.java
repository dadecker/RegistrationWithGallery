package com.usf.registrationwithgallery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button login;
    EditText email,password;
    TextView goBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();


        goBack= (TextView) findViewById(R.id.goBack);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        goBack.setOnClickListener(this);
        login.setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(this, "starting onStart()", Toast.LENGTH_SHORT).show();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            Toast.makeText(LoginActivity.this, "User already logged in", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(getApplicationContext(), DisplayActivity.class));
        }
    }

    private void loginUser() {
        String email_string = email.getText().toString().trim();
        String password_string = password.getText().toString().trim();

        if(TextUtils.isEmpty(email_string)){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password_string)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email_string, password_string)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), GalleryActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this,"Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == login){
            loginUser();
        }

        if(view == goBack){
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}