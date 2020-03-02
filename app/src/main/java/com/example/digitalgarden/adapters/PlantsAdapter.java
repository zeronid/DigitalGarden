package com.example.digitalgarden.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.digitalgarden.activities.MainActivity;
import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class PlantsAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Plant> plants;

    PlantsAdapter(Context c, ArrayList<Plant> plants){
        super(c, R.layout.view_plant,R.id.textView1,plants);
        this.context = c;
        this.plants = plants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.view_plant,parent,false);
        TextView textName = row.findViewById(R.id.textView1);
        TextView textType = row.findViewById(R.id.textView2);
        ImageView dropImage = row.findViewById(R.id.colorCircle);
        ImageView plantImage = row.findViewById(R.id.plantImage);


        plantImage.setImageBitmap(getPlantImage(position));
        textName.setText(plants.get(position).getName());
        textType.setText(plants.get(position).getType());
        dropImage.setImageResource(R.drawable.waterdropblue);
        if(MainActivity.plants.get(position).getWaterLevel() <= 50){
            dropImage.setImageResource(R.drawable.waterdropyellow);
        } if (MainActivity.plants.get(position).getWaterLevel() <= 25){
            dropImage.setImageResource(R.drawable.waterdropred);
        }



        return row;
    }

    private Bitmap getPlantImage(int pos){
        File f = new File(context.getFilesDir(),pos +".png");
        Bitmap b = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.plant);
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}
