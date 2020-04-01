package com.example.digitalgarden.models;

import java.util.ArrayList;

public class Plant {

    private String name,type,note;
    private int waterLevel , currentWater;
    private String imageStringUUID;
    private ArrayList<String> plantsImages;

    public Plant(String name,String type,int waterLevel,int currentWater, String imageStringUUID,String note){
        setName(name);
        setType(type);
        setWaterLevel(waterLevel);
        setPlantImage(imageStringUUID);
        setNote(note);
        setCurrentWater(currentWater);
        setPlantsImages(imageStringUUID);
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
    public int getWaterLevel(){
        return waterLevel;
    }
    public void setWaterLevel(int waterLevel){
        this.waterLevel = waterLevel;
    }
    public String getPlantImage(){return this.imageStringUUID;}
    public void setPlantImage(String image){this.imageStringUUID = image;}
    public String getNote() { return note; }
    public void setNote(String note){this.note = note;}
    public int getCurrentWater(){return this.currentWater;}
    public void setCurrentWater(int currentWater){this.currentWater = currentWater;}
    public ArrayList<String> getPlantsImages() { return plantsImages; }
    public void setPlantsImages(String firstPlantsImage) {
        this.plantsImages = new ArrayList<String>();
        plantsImages.add(firstPlantsImage);
    }
    public void addImageToGallery(String image){
        this.plantsImages.add(image);
    }
}
