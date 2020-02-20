package com.example.digitalgarden;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlantInfo extends AppCompatActivity {

    public static ProgressBar waterLevelProgress;
    public TextView plantName;
    public TextView plantType;

    //Water a plant with the water button
    public void water(View view){
        ProgressBar waterLevel = findViewById(R.id.waterLevelProgressBar);
        waterLevel.setProgress(100);
        MainActivity.plants.get(getIntent().getExtras().getInt("position")).waterPlant();
    }

    //Delete an entry with the delete button
    public void delete(View view){
        new AlertDialog.Builder(this).
                setTitle("Delete plant").
                setMessage("Are you sure you want to delete this plant?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0 ; i<MainActivity.plantNames.size() ; i++){
                            if (MainActivity.plantNames.get(i).equals(getIntent().getExtras().getString("name"))){
                                MainActivity.plantNames.remove(i);
                                MainActivity.plants.remove(i);
                            }
                        }
                        finish();
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

        waterLevelProgress.setProgress(getIntent().getExtras().getInt("waterLevel"));
        plantName.setText("Name: " + getIntent().getExtras().getString("name"));
        plantType.setText("Type: " + getIntent().getExtras().getString("type"));

    }

}
