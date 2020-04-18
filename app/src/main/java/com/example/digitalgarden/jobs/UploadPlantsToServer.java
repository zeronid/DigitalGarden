package com.example.digitalgarden.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.renderscript.Type;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.digitalgarden.models.Plant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadPlantsToServer extends JobService {

    FirebaseUser mUser;
    CollectionReference mColRef;

    @Override
    public boolean onStartJob(JobParameters params) {

        //Get acces to firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mColRef = FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid()).collection("Plants");

        //Get the plants array from MainActivity
        String json = params.getExtras().getString("plants");
        Gson g = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<Plant>>() {}.getType();
        ArrayList<Plant> plants = g.fromJson(json,type);


        //Upload the plants to firebase
        for (int i = 0 ; i < plants.size() ; i++){
            Map<String,Object> plantsData = new HashMap<>();
            plantsData.put("Name",plants.get(i).getName());
            plantsData.put("Type",plants.get(i).getType());
            plantsData.put("Note",plants.get(i).getNote());
            plantsData.put("CurrentWater",plants.get(i).getCurrentWater());
            plantsData.put("TotalWater",plants.get(i).getWaterLevel());
            plantsData.put("ProfilePicture",plants.get(i).getPlantImage());
            plantsData.put("PlantsImages", FieldValue.delete());
            plantsData.put("PlantsImages",plants.get(i).getPlantsImages());
            plantsData.put("DayUpdated",plants.get(i).dayUpdated);

            mColRef.document(plants.get(i).getName()).set(plantsData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Success","Yayy");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Log.i("Failure","oh no");
                }
            });
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
