package com.example.digitalgarden;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class myAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Plant> plants;

    myAdapter(Context c, ArrayList<Plant> plants){
        super(c,R.layout.row,R.id.textView1,plants);
        this.context = c;
        this.plants = plants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row,parent,false);
        TextView textName = row.findViewById(R.id.textView1);
        TextView textType = row.findViewById(R.id.textView2);

        textName.setText(plants.get(position).getName());
        textType.setText(plants.get(position).getType());



        return row;
    }
}
