package com.example.digitalgarden.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;
import com.example.digitalgarden.adapters.PlantsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //An array list for plant objects and one for the names only.
    public static ArrayList<String> plantNames = new ArrayList<>();
    public static ArrayList<Plant> plants = new ArrayList<>();
    public RecyclerView plantList;
    final int NUMOFCOLUMNS = 2;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData(); //Loads up all the saved Plants.

        //Setting up the ListView
        plantList = findViewById(R.id.recyclerView);
        updateList();
        checkDate();

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Plants");
    }

    @Override
    protected void onResume() {
        updateList();
        checkDate();
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    //The add plant method that is called when create plant button is pressed.
    public void addPlant(View view){
        Intent add = new Intent(getApplicationContext(), CreatePlantActivity.class);
        startActivity(add);
    }

    private void updateList(){
        plantList = findViewById(R.id.recyclerView);
        plantList.setLayoutManager(new GridLayoutManager(this, NUMOFCOLUMNS));
        PlantsAdapter adapter = new PlantsAdapter(this, plants);
        adapter.setClickListener(new PlantsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent goToPlantInfo = new Intent(getApplicationContext(), PlantDisplayActivity.class);
                goToPlantInfo.putExtra("position",position);
                startActivity(goToPlantInfo);
            }
        });
        plantList.setAdapter(adapter);
    }


    //Saves the plants
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(plants);
        String names = gson.toJson(plantNames);
        editor.putString("plant list",json);
        editor.putString("name list",names);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("plant list",null);
        String names = sharedPreferences.getString("name list",null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        java.lang.reflect.Type type2 = new TypeToken<ArrayList<String>>() {}.getType();
        plants = gson.fromJson(json,type);
        plantNames = gson.fromJson(names,type2);
        if(plants == null){
            plants = new ArrayList<>();
            plantNames = new ArrayList<>();
        }
    }


    //Checks if a day has passed and updates the water ammount of the plant.
    private void checkDate(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("lastTimeStarted", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        if(today != lastTimeStarted){
            for(int i=0;i<plants.size();i++){
                plants.get(i).setWaterLevel(plants.get(i).getWaterLevel()-10);
            }
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastTimeStarted", today);
            editor.apply();
        }
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }
}
