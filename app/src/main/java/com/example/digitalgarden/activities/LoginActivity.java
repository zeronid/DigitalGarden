package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.example.digitalgarden.models.Plant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView appName, signUp , emailEditText, passwordEditText;
    private Button signInButton;
    private FirebaseAuth mFirebaseAuth;

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

}
