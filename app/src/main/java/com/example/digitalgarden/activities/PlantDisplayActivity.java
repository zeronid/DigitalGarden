package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PlantDisplayActivity extends AppCompatActivity {

    public static ProgressBar waterLevelProgress;
    public TextView plantName, plantType, plantNote , waterLevelTextView;
    public ImageView plantPicture;


    //Water a plant with the water button
    public void water(View view){
        //Sets the progress bar to 100%
        waterLevelProgress = findViewById(R.id.waterLevelProgressBar);
        waterLevelProgress.setMax(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getWaterLevel());
        waterLevelProgress.setProgress(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getWaterLevel() * 10);
        MainActivity.plants.get(getIntent().getExtras().getInt("position")).water();
        //Displays a toast that notifies the user about the watering.
        Toast.makeText(this, MainActivity.plants.get(getIntent().getExtras().getInt("position")).getName() + "'s water is now at 100%!", Toast.LENGTH_SHORT).show();
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
                                if(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getPlantImage() != -1) {
                                    deleteImage(getIntent().getExtras().getInt("position"));
                                }
                                MainActivity.plantNames.remove(getIntent().getExtras().getInt("position"));
                                MainActivity.plants.remove(getIntent().getExtras().getInt("position"));
                                finish();//Very important,when you press "Yes" finish the activity.
                    }
                }).setNegativeButton(android.R.string.no,null).
                setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

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

        //Setting up the text in the water level text view
        double currentWater = MainActivity.plants.get(getIntent().getExtras().getInt("position")).getCurrentWater() * 10;
        double totalWater = MainActivity.plants.get(getIntent().getExtras().getInt("position")).getWaterLevel() * 10;
        double waterLevelDouble = currentWater / totalWater * 100;
        int waterLevellnt = (int) waterLevelDouble;
        waterLevelTextView.setText("Water level : " + waterLevellnt + "%");

        //Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setting up the Water Level Bar.
        waterLevelProgress.setProgress(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getCurrentWater() * 10  / MainActivity.plants.get(getIntent().getExtras().getInt("position")).getWaterLevel() * 10);
        waterLevelProgress.setScaleY(6f);//Bulks up the water progress bar.

        //Setting up the name.
        plantName.setText(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getName());

        //Setting up the type.
        plantType.setText(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getType());

        //Setting up the picture.
        plantPicture.setImageBitmap(getPlantImage(getIntent().getExtras().getInt("position")));

        //Setting up the note.
        if(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getNote() != null) {
            plantNote.setText(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getNote());
        }
    }

    private Bitmap getPlantImage(int pos){
        if(MainActivity.plants.get(pos).getPlantImage() == -1) {
            Bitmap bitMap = BitmapFactory.decodeResource(getResources(),R.drawable.plant);
            return bitMap;
        }
        File f = new File(getFilesDir(),MainActivity.plants.get(pos).getPlantImage() + ".png");
        Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.plant);
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private void deleteImage(int pos){
            File file = new File(getFilesDir() + "/" + MainActivity.plants.get(pos).getPlantImage() + ".png");
            file.delete();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            PlantDisplayActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void changePicture(View view){
        new AlertDialog.Builder(this).
                setTitle("Change picture").
                setMessage("Do you want to change the plant's picture?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePicture();
                    }
                }).setNegativeButton(android.R.string.no,null).
                setIcon(android.R.drawable.alert_light_frame)
                .show();
    }

    public void takePicture(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA
            },100);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            plantPicture = findViewById(R.id.plantInfoPlantImageView);
            plantPicture.setImageBitmap(captureImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void changePlantsImageInStorage(Bitmap image){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getPlantImage() + ".png", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        try {
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        plantPicture = findViewById(R.id.plantInfoPlantImageView);
        Bitmap plantBitmap = ((BitmapDrawable)plantPicture.getDrawable()).getBitmap();
        changePlantsImageInStorage(plantBitmap);
        MainActivity.plants.get(getIntent().getExtras().getInt("position")).setNote(plantNote.getText().toString());
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
