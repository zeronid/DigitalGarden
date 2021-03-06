package com.example.digitalgarden.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class Plant {

    private String name,type,note;
    private double waterLevel , currentWater;
    private String imageStringUUID;
    private ArrayList<String> plantsImages;
    public int dayUpdated;

    public Plant(String name,String type,double waterLevel,double currentWater, String imageStringUUID,String note){
        setName(name);
        setType(type);
        setWaterLevel(waterLevel);
        setPlantImage(imageStringUUID);
        setNote(note);
        setCurrentWater(currentWater);
        setPlantsImages(imageStringUUID,new ArrayList<String>());
        this.dayUpdated = LocalDate.now().getDayOfYear();
    }

    public Plant(String name,String type,double waterLevel,double currentWater, String imageStringUUID,ArrayList<String> plantsImages,String note,int none){
        setName(name);
        setType(type);
        setWaterLevel(waterLevel);
        setPlantImage(imageStringUUID);
        setNote(note);
        setCurrentWater(currentWater);
        setPlantsImages("",plantsImages);
        this.dayUpdated = LocalDate.now().getDayOfYear();
    }

    public void water(){
        this.currentWater = getWaterLevel();
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public double getWaterLevel(){
        return waterLevel;
    }
    public void setWaterLevel(double waterLevel){
        this.waterLevel = waterLevel;
    }
    public String getPlantImage(){return this.imageStringUUID;}
    public void setPlantImage(String image){this.imageStringUUID = image;}
    public String getNote() { return note; }
    public void setNote(String note){this.note = note;}
    public double getCurrentWater(){return this.currentWater;}
    public void setCurrentWater(double currentWater){
        if(currentWater >= 0) {
            this.currentWater = currentWater;
        }else this.currentWater = 0;
    }
    public ArrayList<String> getPlantsImages() { return plantsImages; }
    public void setPlantsImages(String firstPlantsImage,ArrayList<String> plantsImages) {
        this.plantsImages = plantsImages;
//        if(!(firstPlantsImage.equals("")) && !(plantsImages.contains(firstPlantsImage))) {
//            plantsImages.add(firstPlantsImage);
//        }
    }
    public void addImageToGallery(String image){
        this.plantsImages.add(0,image);
    }
}
