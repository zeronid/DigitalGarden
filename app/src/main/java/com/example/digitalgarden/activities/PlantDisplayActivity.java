package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;

import java.io.File;
import java.io.FileInputStream;

public class PlantDisplayActivity extends AppCompatActivity {

    public static ProgressBar waterLevelProgress;
    public TextView plantName;
    public TextView plantType;
    public ImageView plantPicture;


    //Water a plant with the water button
    public void water(View view){
        waterLevelProgress = findViewById(R.id.waterLevelProgressBar);
        waterLevelProgress.setProgress(100);
        MainActivity.plants.get(getIntent().getExtras().getInt("position")).water();//Calls the Water Plant method of the Plant Class
        Toast.makeText(this, MainActivity.plants.get(getIntent().getExtras().getInt("position")).getName() + "'s water is now at 100%!", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_plant_info);

        waterLevelProgress = findViewById(R.id.waterLevelProgressBar);
        plantName = findViewById(R.id.plantNameTextView);
        plantType = findViewById(R.id.plantTypeTextView);
        plantPicture = findViewById(R.id.plantInfoPlantImageView);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        waterLevelProgress.setProgress(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getWaterLevel());
        plantName.setText(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getName());
        plantType.setText(MainActivity.plants.get(getIntent().getExtras().getInt("position")).getType());
        plantPicture.setImageBitmap(getPlantImage(getIntent().getExtras().getInt("position")));

        waterLevelProgress.setScaleY(6f);//Bulks up the water progress bar.

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
}
