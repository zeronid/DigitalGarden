package com.example.digitalgarden.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.digitalgarden.jobs.PlantsWaterJob;
import com.example.digitalgarden.jobs.UploadPlantsToServer;
import com.example.digitalgarden.models.Plant;
import com.example.digitalgarden.R;
import com.example.digitalgarden.adapters.PlantsAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //An array list for plant objects and one for the names only.
    public static ArrayList<String> plantNames = new ArrayList<>();
    public static ArrayList<Plant> plants = new ArrayList<>();

    private RecyclerView plantList;
    private Toolbar toolbar;
    private PlantsAdapter adapter;

    private final int NUMOFCOLUMNS = 2;

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
        waterPlants();



        //Setting up the toolbar
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Plants");
    }


    @Override
    protected void onResume() {
        updateList();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //Uploads the plants to the server
        uploadPlantsToServer();
        //Start the notification activity (Only for first time opening program)
        startWateringJob();
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

    private void loadData(){
        if (getIntent().hasExtra("plants")) {
            String json = getIntent().getExtras().getString("plants");
            Gson g = new Gson();
            java.lang.reflect.Type type = new TypeToken<ArrayList<Plant>>() {
            }.getType();
            plants = g.fromJson(json, type);
            for (Plant plant : plants) {
                plantNames.add(plant.getName());
            }
        }
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
                    .setPeriodic(24 * 60 * 60 * 1000).setPersisted(true).build();

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(info);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("wateringJob",true);
            edit.apply();
        }
    }

    public void waterPlants(){
        for (int i=0;i<plants.size();i++){
            if(plants.get(i).dayUpdated == LocalDate.now().getDayOfYear()){
                break;
            }else if(plants.get(i).dayUpdated < LocalDate.now().getDayOfYear()){
                plants.get(i).setCurrentWater(plants.get(i).getCurrentWater() - (LocalDate.now().getDayOfYear() - plants.get(i).dayUpdated));
                plants.get(i).dayUpdated = LocalDate.now().getDayOfYear();
            } else if(plants.get(i).dayUpdated > LocalDate.now().getDayOfYear()){
                plants.get(i).setCurrentWater(plants.get(i).getCurrentWater() - ((365-plants.get(i).dayUpdated) + LocalDate.now().getDayOfYear()));
                plants.get(i).dayUpdated = LocalDate.now().getDayOfYear();
            }
        }
    }

    public void uploadPlantsToServer(){

        //Put the plants array in bundle as JSON
        Gson g = new Gson();
        String json = g.toJson(plants);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("plants",json);

        ComponentName componentName = new ComponentName(this, UploadPlantsToServer.class);
        JobInfo info = new JobInfo.Builder(2, componentName).setExtras(bundle).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setPersisted(true).build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.this.finish();
    }
}
