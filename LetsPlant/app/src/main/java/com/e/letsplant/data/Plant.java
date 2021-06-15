package com.e.letsplant.data;

import com.google.firebase.database.Exclude;

public class Plant {

    private String plantId;
    private String title;
    private String image;
    private int moisture;
    private float temperature;
    private int light;
    private int humidity;
    private String owner;
    private String code;

    public Plant() {
    }

    public Plant(String plantId, String title, String image, int moisture,float temperature, int light, int humidity, String owner, String code) {
        this.plantId = plantId;
        this.title = title;
        this.image = image;
        this.moisture = moisture;
        this.temperature = temperature;
        this.light = light;
        this.humidity = humidity;
        this.owner = owner;
        this.code = code;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = moisture;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    @Exclude
//    public String getKey() {
//        return key;
//    }
//
//    @Exclude
//    public void setKey(String key) {
//        this.key = key;
//    }

}
