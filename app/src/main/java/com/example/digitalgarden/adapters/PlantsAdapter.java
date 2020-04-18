package com.example.digitalgarden.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.digitalgarden.activities.MainActivity;
import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.ViewHolder> implements Filterable {

    private ArrayList<Plant> plants;
    private ArrayList<Plant> plantsAll;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    // data is passed into the constructor
    public PlantsAdapter(Context context, ArrayList<Plant> data) {
        this.mInflater = LayoutInflater.from(context);
        this.plants = data;
        this.context = context;
        this.plantsAll = new ArrayList<>(data);
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
        if(((MainActivity.plants.get(position).getCurrentWater() * 10) / (MainActivity.plants.get(position).getWaterLevel() * 10)) <= 0.5){
            holder.drop.setImageResource(R.drawable.waterdropyellow);
        } if (((MainActivity.plants.get(position).getCurrentWater() * 10) / (MainActivity.plants.get(position).getWaterLevel() * 10)) <= 0.25){
            holder.drop.setImageResource(R.drawable.waterdropred);
        }
        holder.picture.setImageBitmap(getPlantImage(position));
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return plants.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Plant> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(plantsAll);
            }else{
                for(Plant plant : plantsAll){
                    if(plant.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(plant);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            plants.clear();
            plants.addAll((Collection<? extends Plant>) results.values);
            notifyDataSetChanged();
        }
    };


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
    Plant getItem(int id) {
        return plants.get(id);
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
        if(MainActivity.plants.get(pos).getPlantImage().equals("1")) {
            Bitmap bitMap = BitmapFactory.decodeResource(this.context.getResources(),R.drawable.plant);
            return bitMap;
        }
        Bitmap b = BitmapFactory.decodeResource(context.getApplicationContext().getResources(),R.drawable.plant);
        try {
            String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
            b = BitmapFactory.decodeFile(path + "/" + MainActivity.plants.get(pos).getPlantImage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }


}