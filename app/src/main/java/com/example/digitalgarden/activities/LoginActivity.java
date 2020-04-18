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
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DocumentReference mDocRef;
    private String name;
    private ArrayList<Plant> plants;
    FirebaseStorage storage;
    StorageReference imageRef;

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
        plants = new ArrayList<>();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser = mFirebaseAuth.getCurrentUser();
                if (mFireBaseUser != null) {
                    mDocRef = FirebaseFirestore.getInstance().collection("Users").document(mFireBaseUser.getUid());
                    Task<DocumentSnapshot> doc = mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            name = doc.getString("Name");
                            Toast.makeText(LoginActivity.this, "Welcome, " + name, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        }
                    });
                }
                else {
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
                                FirebaseUser mFireBaseUser = mFirebaseAuth.getCurrentUser();
                                if (mFireBaseUser != null){
                                    mDocRef = FirebaseFirestore.getInstance().collection("Users").document(mFireBaseUser.getUid());


                                    CollectionReference plantsCollection = mDocRef.collection("Plants");
                                    plantsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(queryDocumentSnapshots != null){
                                                for(DocumentChange snap : queryDocumentSnapshots.getDocumentChanges()){
                                                    double currentWater = Double.parseDouble(Objects.requireNonNull(snap.getDocument().get("CurrentWater")).toString());
                                                    double totalWater = Double.parseDouble(Objects.requireNonNull(snap.getDocument().get("TotalWater")).toString());
                                                    String name = Objects.requireNonNull(snap.getDocument().get("Name")).toString();
                                                    String type = Objects.requireNonNull(snap.getDocument().get("Type")).toString();
                                                    String note = Objects.requireNonNull(snap.getDocument().get("Note")).toString();
                                                    String profilePicture = Objects.requireNonNull(snap.getDocument().get("ProfilePicture")).toString();
                                                    int dayUpdated = Integer.parseInt(Objects.requireNonNull(snap.getDocument().get("DayUpdated")).toString());
                                                    ArrayList images = (ArrayList) Objects.requireNonNull(snap.getDocument().get("PlantsImages"));
                                                    Plant p = new Plant(name,type,totalWater,currentWater,profilePicture,images,note,0);
                                                    p.dayUpdated = dayUpdated;
                                                    plants.add(p);
                                                }
                                                downloadImages();
                                            }
                                        }
                                    });
                                }
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

    private void downloadImages(){
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference();

        for(Plant plant : plants){
            for(final String image : plant.getPlantsImages()) {
                if(!(image.equals("1"))){
                    final String storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                    File file = new File(storageDir + "/" + image);
                    if (!(file.exists())) {
                        StorageReference imageReference = imageRef.child(image);
                        imageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                File file = new File(storageDir,image);
                                FileOutputStream out;
                                try {

                                    out = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                                    out.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
        try {
            Thread.sleep(1000); //Lets the images load up smoothly
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gson g = new Gson();
        String json = g.toJson(plants);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("plants",json);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
