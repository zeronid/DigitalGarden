package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.example.digitalgarden.app.app;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private TextView appName;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFireBaseUser;
    private FirebaseAuth mFirebaseAuth;
    private DocumentReference mDocRef;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        appName = findViewById(R.id.appName);
        Typeface typeface = ResourcesCompat.getFont(this,R.font.sweet_leaf);
        appName.setTypeface(typeface);

        mFirebaseAuth = FirebaseAuth.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                    authenticateUser();
                    mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            }
        }).start();
    }

    private void authenticateUser(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFireBaseUser = mFirebaseAuth.getCurrentUser();
                if (mFireBaseUser != null) {
                    mDocRef = FirebaseFirestore.getInstance().collection("Users").document(mFireBaseUser.getUid());
                    Task<DocumentSnapshot> doc = mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            name = doc.getString("Name");
                            Toast.makeText(SplashActivity.this, "Welcome, " + name, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            SplashActivity.this.finish();
                        }
                    });
                }
                else {
                    Toast.makeText(SplashActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }
        };
    }
}
