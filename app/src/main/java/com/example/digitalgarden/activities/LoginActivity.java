package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView appName, signUp , emailEditText, passwordEditText;
    Button signInButton;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Setting up the font
        appName = findViewById(R.id.digitalGardenTextView);
        Typeface typeface = ResourcesCompat.getFont(this,R.font.sweet_leaf);
        appName.setTypeface(typeface);

        //Setting up the Button onClickListener
        signInButton = findViewById(R.id.signInButton);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUp = findViewById(R.id.signUpTextView);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser = mFirebaseAuth.getCurrentUser();
                if(mFireBaseUser != null){
                    Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    LoginActivity.this.finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(email.isEmpty()){
                    emailEditText.setError("Please enter email ID");
                    emailEditText.requestFocus();
                }else if(password.isEmpty()){
                    passwordEditText.setError("Please enter a password");
                    passwordEditText.requestFocus();
                }
                else if(!(email.isEmpty() && password.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                LoginActivity.this.finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Login error, please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(LoginActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
