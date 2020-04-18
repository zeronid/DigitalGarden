package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class PlantDisplayActivity extends AppCompatActivity {

    private static ProgressBar waterLevelProgress;
    private TextView plantName, plantType, plantNote , waterLevelTextView;
    private ImageView plantPicture;
    private int plantPosition;
    private FirebaseUser mUser;
    private CollectionReference mColRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_display);

        //Setting up the Views.
        waterLevelProgress = findViewById(R.id.waterLevelProgressBar);
        plantName = findViewById(R.id.plantNameTextView);
        plantType = findViewById(R.id.plantTypeTextView);
        plantPicture = findViewById(R.id.plantInfoPlantImageView);
        plantNote = findViewById(R.id.plantsNoteTextView);
        waterLevelTextView = findViewById(R.id.waterLevelTextView);
        plantPosition = getIntent().getExtras().getInt("position");
        Context context = this.getApplicationContext();

        //Setting up the text in the water level text view
        double currentWater = MainActivity.plants.get(plantPosition).getCurrentWater() * 10;
        double totalWater = MainActivity.plants.get(plantPosition).getWaterLevel() * 10;
        double waterLevelDouble = currentWater / totalWater * 100;
        int waterLevellnt = (int) waterLevelDouble;
        waterLevelTextView.setText("Water level : " + waterLevellnt + "%");

        //Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(MainActivity.plants.get(plantPosition).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting up the Water Level Bar.
        double prog = MainActivity.plants.get(plantPosition).getCurrentWater()  / MainActivity.plants.get(plantPosition).getWaterLevel();
        prog = prog*100;
        waterLevelProgress.setProgress((int)prog);
        waterLevelProgress.setScaleY(6f);//Bulks up the water progress bar.

        //Setting up the name.
        plantName.setText(MainActivity.plants.get(plantPosition).getName());

        //Setting up the type.
        plantType.setText(MainActivity.plants.get(plantPosition).getType());

        //Setting up the picture.
        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + MainActivity.plants.get(plantPosition).getPlantImage());
        plantPicture = findViewById(R.id.plantInfoPlantImageView);
        if(bitmap != null) {
            plantPicture.setImageBitmap(bitmap);
        }

        //Setting up the note.
        if(MainActivity.plants.get(plantPosition).getNote() != null) {
            plantNote.setText(MainActivity.plants.get(plantPosition).getNote());
        }
    }


    //Water a plant with the water button
    public void water(View view){
        //Sets the progress bar to 100%
        waterLevelProgress = findViewById(R.id.waterLevelProgressBar);
        waterLevelProgress.setMax((int)MainActivity.plants.get(plantPosition).getWaterLevel());
        waterLevelProgress.setProgress((int)MainActivity.plants.get(plantPosition).getWaterLevel() * 10);
        MainActivity.plants.get(plantPosition).water();

        //Displays a toast that notifies the user about the watering.
        Toast.makeText(this, MainActivity.plants.get(plantPosition).getName() + "'s water is now at 100%!", Toast.LENGTH_SHORT).show();

        //Updates the text that display the percentage of the water
        waterLevelTextView = findViewById(R.id.waterLevelTextView);
        waterLevelTextView.setText("Water level : " + 100 + "%");
    }

    //Delete an entry with the delete button
    public void delete(View view){
        new AlertDialog.Builder(this).
                setTitle("Delete plant").
                setMessage("Are you sure you want to delete this plant?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mUser = FirebaseAuth.getInstance().getCurrentUser();
                        mColRef = FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid()).collection("Plants");
                        mColRef.document((MainActivity.plants.get(plantPosition).getName())).delete();

//                        if(MainActivity.plants.get(plantPosition).getPlantImage() != "1") {
//                            deleteImage(plantPosition);
//                        }
                        for(String image:MainActivity.plants.get(plantPosition).getPlantsImages()){
                            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
                            File file = new File(path + "/" + image);
                            file.delete();
                        }
                        MainActivity.plantNames.remove(plantPosition);
                        MainActivity.plants.remove(plantPosition);
                        finish();//Very important,when you press "Yes" finish the activity.
                    }
                }).setNegativeButton(android.R.string.no,null).
                setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

//    private void deleteImage(int pos){
//            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
//            File file = new File(path + "/" + MainActivity.plants.get(pos).getPlantImage());
//            file.delete();
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            PlantDisplayActivity.this.finish();
            MainActivity.plants.get(plantPosition).setNote(plantNote.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        MainActivity.plants.get(plantPosition).setNote(plantNote.getText().toString());
        super.onBackPressed();
    }

    public void goToGallery(View view) {
        Intent intent = new Intent(PlantDisplayActivity.this,GalleryActivity.class);
        intent.putExtra("position",plantPosition);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + MainActivity.plants.get(plantPosition).getPlantImage());
        plantPicture = findViewById(R.id.plantInfoPlantImageView);
        plantPicture.setImageBitmap(bitmap);
    }
}
