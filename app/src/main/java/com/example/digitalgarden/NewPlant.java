package com.example.digitalgarden;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class NewPlant extends AppCompatActivity {

    private static final String[] TYPES = new String[]{"Succulent","Cactus","Moss","Embryophyte"};
    private EditText newPlantName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plant);

        AutoCompleteTextView typeView = findViewById(R.id.newPlantTypeEditText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,TYPES);
        typeView.setAdapter(adapter);
    }

    public void makePlant(View view){
        newPlantName = findViewById(R.id.newPlantNameEditText);
        AutoCompleteTextView newPlantType = findViewById(R.id.newPlantTypeEditText);
        if(MainActivity.plantNames.contains(newPlantName.getText().toString())){
            Toast.makeText(this, "This name is already taken", Toast.LENGTH_SHORT).show();
        } else {
            Plant plant = new Plant(newPlantName.getText().toString(), newPlantType.getText().toString(), 35);
            MainActivity.plants.add(plant);
            MainActivity.plantNames.add(newPlantName.getText().toString());
            finish();
        }
    }
}
