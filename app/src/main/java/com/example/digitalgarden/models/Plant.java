package com.example.digitalgarden.models;

import android.graphics.Bitmap;

public class Plant {

    private String name;
    private String type;
    private int waterLevel;
    private int imageNumber;

    public Plant(String n,String t,int w,int pp){
        setName(n);
        setType(t);
        setWaterLevel(w);
        setPlantImage(pp);
    }

    public void water(){
        this.waterLevel = 100;
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
    public int getPlantImage(){return this.imageNumber;}
    public void setPlantImage(int image){this.imageNumber = image;}

}
