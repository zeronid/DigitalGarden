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
import android.widget.TextView;
import android.widget.Toast;
import com.example.digitalgarden.R;
import com.example.digitalgarden.models.Plant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private TextView appName;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFireBaseUser;
    private FirebaseAuth mFirebaseAuth;
    private DocumentReference mDocRef;
    private ArrayList<Plant> plants = new ArrayList<>();
    private FirebaseStorage storage;
    private StorageReference imageRef;

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
                                    ArrayList images = (ArrayList<String>) Objects.requireNonNull(snap.getDocument().get("PlantsImages"));
                                    String profilePicture = Objects.requireNonNull(snap.getDocument().get("ProfilePicture")).toString();
                                    int dayUpdated = Integer.parseInt(Objects.requireNonNull(snap.getDocument().get("DayUpdated")).toString());
                                    Plant p = new Plant(name,type,totalWater,currentWater,profilePicture,images,note,0);
                                    p.dayUpdated = dayUpdated;
                                    plants.add(p);
                                }
                                downloadImages();
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(SplashActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        };
    }

    private void downloadImages(){
        storage = FirebaseStorage.getInstance();
        imageRef = storage.getReference(); //stoargeReference

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
            Thread.sleep(700); //Lets the images load up smoothly
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Gson g = new Gson();
        String json = g.toJson(plants);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("plants",json);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
