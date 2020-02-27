package com.example.digitalgarden;

public class Plant {

    private String name;
    private String type;
    private int waterLevel;

    public Plant(String n,String t,int w){
        setName(n);
        setType(t);
        setWaterLevel(w);
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
}
