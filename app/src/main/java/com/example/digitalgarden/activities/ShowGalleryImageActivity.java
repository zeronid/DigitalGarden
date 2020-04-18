package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowGalleryImageActivity extends AppCompatActivity {
    private ImageView image;
    private int position;
    private TextView date;
    private String s;
    private Toolbar toolbar;
    private FirebaseStorage mFirebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery_image);

        image = findViewById(R.id.picture);
        position = getIntent().getExtras().getInt("plant_pos");
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Bitmap bit = BitmapFactory.decodeFile(path + "/" + getIntent().getExtras().getString("picture"));
        image.setImageBitmap(bit);

        date = findViewById(R.id.dateCreated);
        s = getPictureDate();
        date.setText("Picture Date: " + s);

        toolbar = findViewById(R.id.galleryPictureToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    public void delete(View view) {
        if(!(MainActivity.plants.get(position).getPlantsImages().get(getIntent().getExtras().getInt("picturePosition")).equals(MainActivity.plants.get(position).getPlantImage()))) {
            new AlertDialog.Builder(this).
                    setTitle("Delete picture").
                    setMessage("Do you want to delete the picture?").
                    setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File file = new File(MainActivity.plants.get(position).getPlantsImages().get(getIntent().getExtras().getInt("picturePosition")));
                            file.delete();
                            deleteFileFromFirebase( (String) getIntent().getExtras().get("picture"));
                            MainActivity.plants.get(position).getPlantsImages().remove(getIntent().getExtras().getInt("picturePosition"));
                            finish();
                        }
                    }).setNegativeButton(android.R.string.no, null).
                    setIcon(android.R.drawable.alert_light_frame)
                    .show();
        } else{
            Toast.makeText(this, "Cant delete plants profile picture", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeProfile(View view) {
        MainActivity.plants.get(position).setPlantImage(getIntent().getExtras().getString("picture"));
        Toast.makeText(this, "Profile Picture Has Changed", Toast.LENGTH_SHORT).show();
    }

    private String getPictureDate(){
        String imageName = getIntent().getExtras().getString("picture");
        Pattern p = Pattern.compile("\\_(.*?)\\_");
        Matcher m = p.matcher(imageName);
        if(m.find()){
            s = m.group(1);
        }
        s = s.substring(0,4) + "." + s.substring(4,6) + "." +s.substring(6,8);
        return s;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            ShowGalleryImageActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteFileFromFirebase(String image){
        mFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mFirebaseStorage.getReference();
        StorageReference desertRef = storageRef.child(image);
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO implement
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO implement
            }
        });
    }
}
