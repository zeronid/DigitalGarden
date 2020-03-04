package com.example.digitalgarden.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalgarden.activities.MainActivity;
import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

//public class PlantsAdapter extends RecyclerView.Adapter {
//    Context context;
//    ArrayList<Plant> plants;
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
//        return new ViewHolder(view);
//    }
//
//    public PlantsAdapter(Context c, ArrayList<Plant> plants){
//        super(c, R.layout.view_plant,R.id.textView1,plants);
//        this.context = c;
//        this.plants = plants;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        LayoutInflater layoutInflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = layoutInflater.inflate(R.layout.view_plant,parent,false);
//        TextView textName = row.findViewById(R.id.textView1);
//        TextView textType = row.findViewById(R.id.textView2);
//        ImageView dropImage = row.findViewById(R.id.colorCircle);
//        ImageView plantImage = row.findViewById(R.id.plantImage);
//
//
//        plantImage.setImageBitmap(getPlantImage(position));
//        textName.setText(plants.get(position).getName());
//        textType.setText(plants.get(position).getType());
//        dropImage.setImageResource(R.drawable.waterdropblue);
//        if(MainActivity.plants.get(position).getWaterLevel() <= 50){
//            dropImage.setImageResource(R.drawable.waterdropyellow);
//        } if (MainActivity.plants.get(position).getWaterLevel() <= 25){
//            dropImage.setImageResource(R.drawable.waterdropred);
//        }
//
//
//
//        return row;
//    }
//
//    private Bitmap getPlantImage(int pos){
//        if(MainActivity.plants.get(pos).getPlantImage() == -1) {
//            Bitmap bitMap = BitmapFactory.decodeResource(this.context.getResources(),R.drawable.plant);
//            return bitMap;
//        }
//        File f = new File(context.getFilesDir(),MainActivity.plants.get(pos).getPlantImage() +".png");
//        Bitmap b = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.plant);
//        try {
//            b = BitmapFactory.decodeStream(new FileInputStream(f));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return b;
//    }
//}


public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> {

    private ArrayList<String> plantList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    // data is passed into the constructor
    public PlantsAdapter(Context context, ArrayList<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.plantList = data;
        this.context = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_plant, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(MainActivity.plantNames.get(position));
        holder.type.setText(MainActivity.plants.get(position).getType());
        holder.drop.setImageResource(R.drawable.waterdropblue);
        if(MainActivity.plants.get(position).getWaterLevel() <= 50){
            holder.drop.setImageResource(R.drawable.waterdropyellow);
        } if (MainActivity.plants.get(position).getWaterLevel() <= 25){
            holder.drop.setImageResource(R.drawable.waterdropred);
        }
        holder.picture.setImageBitmap(getPlantImage(position));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return plantList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView type;
        ImageView drop;
        ImageView picture;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTextView);
            type = itemView.findViewById(R.id.typeTextView);
            drop = itemView.findViewById(R.id.colorCircle);
            picture = itemView.findViewById(R.id.plantImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return plantList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private Bitmap getPlantImage(int pos){
        if(MainActivity.plants.get(pos).getPlantImage() == -1) {
            Bitmap bitMap = BitmapFactory.decodeResource(this.context.getResources(),R.drawable.plant);
            return bitMap;
        }
        File f = new File(context.getFilesDir(),MainActivity.plants.get(pos).getPlantImage() +".png");
        Bitmap b = BitmapFactory.decodeResource(context.getApplicationContext().getResources(),R.drawable.plant);
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}