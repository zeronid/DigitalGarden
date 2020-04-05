package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlantDisplayActivity extends AppCompatActivity {

    public static ProgressBar waterLevelProgress;
    public TextView plantName, plantType, plantNote , waterLevelTextView;
    public ImageView plantPicture;
    private int plantPosition;

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
        Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.plants.get(plantPosition).getPlantImage());
        plantPicture = findViewById(R.id.plantInfoPlantImageView);
        plantPicture.setImageBitmap(bitmap);

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
                                if(MainActivity.plants.get(plantPosition).getPlantImage() != "1") {
                                    deleteImage(plantPosition);
                                }
                                for(String plant:MainActivity.plants.get(plantPosition).getPlantsImages()){
                                    File file = new File(plant);
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

    private void deleteImage(int pos){
            File file = new File(MainActivity.plants.get(pos).getPlantImage());
            file.delete();
    }

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

    public void changePicture(final View view){
        new AlertDialog.Builder(this).
                setTitle("Change picture").
                setMessage("Do you want to change the plant's picture?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        captureImage(view);
                    }
                }).setNegativeButton(android.R.string.no,null).
                setIcon(android.R.drawable.alert_light_frame)
                .show();
    }


    //The functions that runs when you take a picture of the plant.
    public void captureImage(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(MainActivity.plants.get(plantPosition).getPlantImage());


            Uri imageUri = FileProvider.getUriForFile(this, "com.example.digitalgarden.fileprovider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.plants.get(plantPosition).getPlantImage());
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(MainActivity.plants.get(plantPosition).getPlantImage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
                try {
                    FileOutputStream out = new FileOutputStream(MainActivity.plants.get(plantPosition).getPlantImage());
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                plantPicture = findViewById(R.id.plantInfoPlantImageView);
                plantPicture.setImageBitmap(rotatedBitmap);
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void goToGallery(View view) {
        Intent intent = new Intent(PlantDisplayActivity.this,GalleryActivity.class);
        intent.putExtra("position",plantPosition);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.plants.get(plantPosition).getPlantImage());
        plantPicture = findViewById(R.id.plantInfoPlantImageView);
        plantPicture.setImageBitmap(bitmap);
    }
}
