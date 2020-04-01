package com.example.digitalgarden.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalgarden.R;
import com.example.digitalgarden.activities.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    public GalleryAdapter(ArrayList<String> plantsImages, Context context,int plantClicked) {
        this.plantsImages = plantsImages;
        this.context = context;
        this.plantClicked = plantClicked;
    }

    private ArrayList<String> plantsImages;
    private Context context;
    private int plantClicked;
    private ItemClickListener mClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String listItem = plantsImages.get(position);
        holder.image.setImageBitmap(getPlantImage(position));
    }

    @Override
    public int getItemCount() {
        return plantsImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.galleryImageObject);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) mClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    private Bitmap getPlantImage(int pos){
        if(MainActivity.plants.get(plantClicked).getPlantsImages().get(pos) == "1") {
            Bitmap bitMap = BitmapFactory.decodeResource(this.context.getResources(),R.drawable.plant);
            return bitMap;
        }
        Bitmap b = BitmapFactory.decodeResource(context.getApplicationContext().getResources(),R.drawable.plant);
        try {
            b = BitmapFactory.decodeFile(MainActivity.plants.get(plantClicked).getPlantsImages().get(pos));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

}
