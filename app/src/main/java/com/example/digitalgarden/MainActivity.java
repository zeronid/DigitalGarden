package com.example.digitalgarden;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.renderscript.Type;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    //An array list for plant objects and one for the names only.
    public static ArrayList<String> plantNames = new ArrayList<>();
    public static ArrayList<Plant> plants = new ArrayList<>();
    public ListView plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData(); //Loads up all the saved Plants.

        //Setting up the ListView
        plantList = findViewById(R.id.plantListView);
        updateListColors();
        checkDate();

        //Called when you click on a plant
        plantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToPlantInfo = new Intent(getApplicationContext(),PlantInfo.class);
                goToPlantInfo.putExtra("position",position);
                startActivity(goToPlantInfo);
            }
        });
    }

    //The add plant method that is called when create plant button is pressed.
    public void addPlant(View view){
        Intent add = new Intent(getApplicationContext(), NewPlant.class);
        startActivity(add);
    }

    @Override
    protected void onResume() {
        updateListColors();
        checkDate();
        super.onResume();
    }

    private void updateListColors(){
        plantList.post(new Runnable() {
            public void run() {
                plantList.setSelected(true);
//                for(int i=0;i<plantNames.size();i++){
//                    if(plants.get(i).getWaterLevel() <= 25){
//                        plantList.getChildAt(i).setBackgroundColor(Color.GREEN);
//                    } else if (plants.get(i).getWaterLevel() <= 50){
//                        plantList.getChildAt(i).setBackgroundColor(Color.YELLOW);
//                    } else {
//                        plantList.getChildAt(i).setBackgroundColor(Color.GREEN);
//                    }
//                }
            }
        });

        plantList = findViewById(R.id.plantListView);
        com.example.digitalgarden.myAdapter myAdapter = new myAdapter(this, plants);
        plantList.setAdapter(myAdapter);
    }


    //Saves the plants
    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(plants);
        editor.putString("plant list",json);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("plant list",null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        plants = gson.fromJson(json,type);
        if(plants == null){
            plants = new ArrayList<>();
        }
        //Gets all the names of the plants in "plantNames"
        for (int i = 0 ; i<plants.size();i++){
            plantNames.add(plants.get(i).getName());
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
