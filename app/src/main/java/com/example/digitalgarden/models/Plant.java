package com.example.digitalgarden.models;

public class Plant {

    private String name,type,note;
    private int waterLevel , currentWater , imageNumber;

    public Plant(String name,String type,int waterLevel,int currentWater, int imageNumber,String note){
        setName(name);
        setType(type);
        setWaterLevel(waterLevel);
        setPlantImage(imageNumber);
        setNote(note);
        setCurrentWater(currentWater);
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
    public int getPlantImage(){return this.imageNumber;}
    public void setPlantImage(int image){this.imageNumber = image;}
    public String getNote() { return note; }
    public void setNote(String note){this.note = note;}
    public int getCurrentWater(){return this.currentWater;}
    public void setCurrentWater(int currentWater){this.currentWater = currentWater;}
}
