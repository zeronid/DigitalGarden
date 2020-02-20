package com.example.digitalgarden;

public class Plant {
    String name;
    String type;
    int waterLevel;

    public Plant(String n,String t,int w){
        name = n;
        type = t;
        waterLevel = w;
    }

    public void waterPlant(){
        this.waterLevel = 100;
    }
}
