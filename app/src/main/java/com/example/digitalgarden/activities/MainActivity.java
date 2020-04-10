package com.example.digitalgarden.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.digitalgarden.jobs.PlantsWaterJob;
import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;
import com.example.digitalgarden.adapters.PlantsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //An array list for plant objects and one for the names only.
    public static ArrayList<String> plantNames = new ArrayList<>();
    public static ArrayList<Plant> plants = new ArrayList<>();

    private RecyclerView plantList;
    private final int NUMOFCOLUMNS = 2;
    private Toolbar toolbar;
    private PlantsAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loads up all the saved Plants.
        loadData();

        //Setting up the ListView
        plantList = findViewById(R.id.recyclerView);
        updateList();

        //Manage the water of the plants (Start the PlantsWaterJob)
        startWateringJob();


        //Setting up the toolbar
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Plants");
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        saveData();
        super.onDestroy();
    }

    //The function responsible for the search box
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //The add plant method that is called when create plant button is pressed.
    public void addPlant(View view){
        Intent add = new Intent(getApplicationContext(), CreatePlantActivity.class);
        startActivity(add);
    }

    //Updates the listView
    private void updateList(){
        plantList = findViewById(R.id.recyclerView);
        plantList.setLayoutManager(new GridLayoutManager(this, NUMOFCOLUMNS));
        adapter = new PlantsAdapter(this, plants);
        adapter.setClickListener(new PlantsAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent goToPlantInfo = new Intent(getApplicationContext(), PlantDisplayActivity.class);
                goToPlantInfo.putExtra("position",position);
                startActivity(goToPlantInfo);
            }
        });
        plantList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    private void startWateringJob(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.project_id),Context.MODE_PRIVATE);
        if(!(sharedPreferences.getBoolean("wateringJob",false))) {
            ComponentName componentName = new ComponentName(this, PlantsWaterJob.class);
            JobInfo info = new JobInfo.Builder(1, componentName)
                    .setPeriodic(30 * 60 * 1000).setPersisted(true).build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(info);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("wateringJob",true);
            edit.apply();
        }
    }
}
