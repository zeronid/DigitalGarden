package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CreatePlantActivity extends AppCompatActivity {

    //Plant types --- need to add alot more.
    private static final String[] TYPES = new String[]{"Succulent","Cactus","Moss","Embryophyte"};

    private EditText newPlantName,plantsNote;
    private ImageView plantPicture;
    private Spinner lastWateredSpinner , waterFrequencySpinner;
    private String currentImagePath = null;
    private static final int IMAGE_REQUEST = 1;
    private File imageFile;

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

        //Display errors if the name is invalid
        if(MainActivity.plantNames.contains(newPlantName.getText().toString())){
            Toast.makeText(this, "This name is already taken", Toast.LENGTH_SHORT).show();
        }else if(newPlantName.getText().toString().equals("")){
            Toast.makeText(this, "Please choose a name", Toast.LENGTH_SHORT).show();
        } //Create the plant and push it to the plants arrayList
        else {
            if(currentImagePath == null){
                currentImagePath = "1";
            }
            Plant plant = new Plant(newPlantName.getText().toString(), newPlantType.getText().toString(), waterFrequencySpinnerIntegerValue ,(waterFrequencySpinnerIntegerValue - lastWateredSpinnerIntegerValue),currentImagePath,plantsNote.getText().toString());
            MainActivity.plants.add(plant);
            MainActivity.plantNames.add(newPlantName.getText().toString());
            finish();
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


    //Creates an empty image file for me to write to.
    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName ="jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        currentImagePath = imageFile.getAbsolutePath();

        return imageFile;
    }

    //The functions that runs when you take a picture of the plant.
    public void captureImage(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null && currentImagePath == null) {
                imageFile = null;
                try {
                    imageFile = getImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.digitalgarden.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST);
            }
    }

    public void changePhotoPicture(View view){
        captureImage(view);
        Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
        plantPicture = findViewById(R.id.plantImageView);
        plantPicture.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(currentImagePath);
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
                    FileOutputStream out = new FileOutputStream(currentImagePath);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }



                plantPicture = findViewById(R.id.plantImageView);
                plantPicture.setImageBitmap(rotatedBitmap);
            }
        }
    }


    //The method that rotates the image because IT KEEPS GETTING ROTATED!!
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
