package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import com.example.digitalgarden.R;
import com.example.digitalgarden.adapters.GalleryAdapter;
import com.example.digitalgarden.adapters.PlantsAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class GalleryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int plantPosition;
    private RecyclerView mRecyclerView;
    private final int NUMOFCOLUMNS = 3;
    private String currentImagePath,imageName;
    private GalleryAdapter myAdapter;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        plantPosition = getIntent().getExtras().getInt("position");
        mRecyclerView = findViewById(R.id.galleryRecyclerView);
        list = MainActivity.plants.get(plantPosition).getPlantsImages();

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,NUMOFCOLUMNS));
        myAdapter = new GalleryAdapter(MainActivity.plants.get(plantPosition).getPlantsImages(),this,plantPosition);
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        myAdapter.setClickListener(new GalleryAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(GalleryActivity.this,ShowGalleryImageActivity.class);
                intent.putExtra("picture",MainActivity.plants.get(plantPosition).getPlantsImages().get(position));
                intent.putExtra("picturePosition",position);
                intent.putExtra("plant_pos",plantPosition);
                startActivity(intent);
            }
        });

        //Setting up the toolbar
        toolbar = findViewById(R.id.galleryToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(MainActivity.plants.get(plantPosition).getName() + "'s" + " pictures");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //The function that sets the back button so it finishes the activity.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            GalleryActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //The functions that runs when you take a picture of the plant.
    public void captureImage(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = null;
        try {
            imageFile = getImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Uri imageUri = FileProvider.getUriForFile(this, "com.example.digitalgarden.fileprovider", imageFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1){
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            FileOutputStream out;
            try {
                out = new FileOutputStream(currentImagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG,50,out);

                Bitmap bitmap2 = BitmapFactory.decodeFile(currentImagePath);
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(currentImagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;

                rotatedBitmap = rotateImage(bitmap2, 90);


                try {
                    FileOutputStream out2 = new FileOutputStream(currentImagePath);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out2);
                    MainActivity.plants.get(plantPosition).addImageToGallery(currentImagePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                myAdapter.notifyDataSetChanged();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageName ="jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onResume() {
        super.onResume();
        myAdapter.notifyDataSetChanged();
    }
}
