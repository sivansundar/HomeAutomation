package com.homeautomation;

public class WeatherModel {

    public int humidity;
    public float temperature;

    public WeatherModel() {
    }

    public WeatherModel(int humidity, float temperature) {
        this.humidity = humidity;
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
