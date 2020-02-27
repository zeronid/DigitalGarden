package com.example.digitalgarden;

import android.graphics.Bitmap;

public class Plant {

    private String name;
    private String type;
    private int waterLevel;
    private Bitmap plantPicture;

    public Plant(String n,String t,int w,Bitmap pp){
        setName(n);
        setType(t);
        setWaterLevel(w);
        setPlantPicture(pp);
    }

    public void waterPlant(){
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
    public Bitmap getPlantPicture(){return this.plantPicture;}
    public void setPlantPicture(Bitmap pic){this.plantPicture = pic;}

}
