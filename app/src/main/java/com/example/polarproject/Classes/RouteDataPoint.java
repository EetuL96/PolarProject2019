package com.example.polarproject.Classes;

public class RouteDataPoint {
    String time;
    Double lat;
    Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getActivity() {
        return activity;
    }

    public void setActivity(Double activity) {
        this.activity = activity;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    Double activity;
    int bpm;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
