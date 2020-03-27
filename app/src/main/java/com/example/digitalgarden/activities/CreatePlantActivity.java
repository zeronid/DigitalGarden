package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;

import java.io.FileOutputStream;
import java.util.UUID;

public class CreatePlantActivity extends AppCompatActivity {

    //Plant types --- need to add alot more.
    private static final String[] TYPES = new String[]{"Succulent","Cactus","Moss","Embryophyte"};

    private EditText newPlantName,plantsNote;
    private ImageView plantPicture;
    private Spinner lastWateredSpinner , waterFrequencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plant);

        //Sets the types of plants into the typeView.
        AutoCompleteTextView typeView = findViewById(R.id.newPlantTypeEditText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,TYPES);
        typeView.setAdapter(adapter);

        //Initiates the toolbar with the title and back button.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Plant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Sets the last watered spinner options.
        lastWateredSpinner = findViewById(R.id.lastWateredSpinner);
        String[] lastWateredSpinnerOptions = {"1 Day","2 Days","3 Days","4 Days","5 Days","6 Days","7 Days"};
        ArrayAdapter<String> wateredSpinnerArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lastWateredSpinnerOptions);
        wateredSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lastWateredSpinner.setAdapter(wateredSpinnerArrayAdapter);

        //Sets the watering frequency spinner options.
        waterFrequencySpinner = findViewById(R.id.waterFrequencySpinner);
        String[] waterFrequencySpinnerOptions = {"1 Day","2 Days","3 Days","4 Days","5 Days","6 Days","7 Days","8 Days","9 Days"};
        ArrayAdapter<String> waterFrequencyArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,waterFrequencySpinnerOptions);
        wateredSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waterFrequencySpinner.setAdapter(waterFrequencyArrayAdapter);
    }

    public void makePlant(View view){
        newPlantName = findViewById(R.id.newPlantNameEditText);
        plantPicture = findViewById(R.id.plantImageView);
        AutoCompleteTextView newPlantType = findViewById(R.id.newPlantTypeEditText);
        Bitmap plantBitmap = ((BitmapDrawable)plantPicture.getDrawable()).getBitmap();
        plantsNote = findViewById(R.id.plantsNoteEditText);

        //Get the last watered value
        lastWateredSpinner = findViewById(R.id.lastWateredSpinner);
        int lastWateredSpinnerIntegerValue = Integer.parseInt(String.valueOf(lastWateredSpinner.getSelectedItem().toString().charAt(0)));

        //Get the water frequency value
        waterFrequencySpinner = findViewById(R.id.waterFrequencySpinner);
        int waterFrequencySpinnerIntegerValue = Integer.parseInt(String.valueOf(waterFrequencySpinner.getSelectedItem().toString().charAt(0)));

        //Get the plant picture
        String plantPictureUUID;
        if(plantPicture.getDrawable() == (getResources().getDrawable(R.drawable.plant))){
            plantPictureUUID = "1";
        } else {
            plantPictureUUID = saveImageToInternalStorage(plantBitmap);
        }

        //Display errors if the name is invalid
        if(MainActivity.plantNames.contains(newPlantName.getText().toString())){
            Toast.makeText(this, "This name is already taken", Toast.LENGTH_SHORT).show();
        }else if(newPlantName.getText().toString().equals("")){
            Toast.makeText(this, "Please choose a name", Toast.LENGTH_SHORT).show();
        } //Create the plant and push it to the plants arrayList
        else {
            Plant plant = new Plant(newPlantName.getText().toString(), newPlantType.getText().toString(), waterFrequencySpinnerIntegerValue , lastWateredSpinnerIntegerValue,plantPictureUUID,plantsNote.getText().toString());
            MainActivity.plants.add(plant);
            MainActivity.plantNames.add(newPlantName.getText().toString());
            finish();
        }
    }


    //The function that takes a picture of the plant
    public void takePicture(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.CAMERA
            },100);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,100);
    }

    //The function that sets the picture taken to the ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            plantPicture = findViewById(R.id.plantImageView);
            plantPicture.setImageBitmap(captureImage);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //The function that saves the image to the internal storage and sets the number of the picture to the plant.
    public String saveImageToInternalStorage(Bitmap image) {
        try {
            String unique = UUID.randomUUID().toString();
            FileOutputStream fos = openFileOutput(unique + ".JPEG", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return unique;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return "1";
        }
    }

    //The function that sets the back button so it finishes the activity.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            CreatePlantActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
