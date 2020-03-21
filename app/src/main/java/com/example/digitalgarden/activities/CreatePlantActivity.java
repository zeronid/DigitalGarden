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
import android.widget.Toast;

import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;

import java.io.FileOutputStream;

public class CreatePlantActivity extends AppCompatActivity {

    private static final String[] TYPES = new String[]{"Succulent","Cactus","Moss","Embryophyte"};
    private EditText newPlantName;
    private ImageView plantPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        AutoCompleteTextView typeView = findViewById(R.id.newPlantTypeEditText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,TYPES);
        typeView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Plant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void makePlant(View view){
        newPlantName = findViewById(R.id.newPlantNameEditText);
        plantPicture = findViewById(R.id.plantImageView);
        AutoCompleteTextView newPlantType = findViewById(R.id.newPlantTypeEditText);
        Bitmap plantBitmap = ((BitmapDrawable)plantPicture.getDrawable()).getBitmap();
        int plantPictureNumber;
        if(plantPicture.getDrawable() == (getResources().getDrawable(R.drawable.plant))){
            plantPictureNumber = -1;
        } else {
            plantPictureNumber = saveImageToInternalStorage(plantBitmap);
        }
        if(MainActivity.plantNames.contains(newPlantName.getText().toString())){
            Toast.makeText(this, "This name is already taken", Toast.LENGTH_SHORT).show();
        }else if(newPlantName.getText().toString().equals("")){
            Toast.makeText(this, "Please choose a name", Toast.LENGTH_SHORT).show();
        }
        else {
            Plant plant = new Plant(newPlantName.getText().toString(), newPlantType.getText().toString(), 100,plantPictureNumber);
            MainActivity.plants.add(plant);
            MainActivity.plantNames.add(newPlantName.getText().toString());
            finish();
        }
    }

    public void takePicture(View view){
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
        if(requestCode==100){
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            plantPicture = findViewById(R.id.plantImageView);
            plantPicture.setImageBitmap(captureImage);
        }
    }

    public int saveImageToInternalStorage(Bitmap image) {
        try {
            int imageNumber = (int)(Math.random()*100000);
            FileOutputStream fos = openFileOutput(imageNumber + ".png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return imageNumber;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return -1;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            CreatePlantActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
