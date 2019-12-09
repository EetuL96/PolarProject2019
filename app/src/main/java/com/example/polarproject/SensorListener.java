package com.example.polarproject;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrBroadcastData;
import polar.com.sdk.api.model.PolarHrData;

public class SensorListener implements SensorEventListener {

    private final double filter = 0.1;
    private final int listSize = 50;
    private int pointer = 0;
    private double acc_x = 0;
    private double acc_y = 0;
    private double acc_z = 0;
    private ArrayList<Double> activityList;
    private SensorManager sensorManager;
    private android.hardware.Sensor sensor;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private PolarBleApi api;
    private Disposable broadcastDisposable;
    private Map<String, PolarHrBroadcastData> broadcastData;

    private SensorInterface cb;

    public interface SensorInterface{
        void updateActivity(double acticity);
        void updateLocation(Location location);
        void updateHR(int hr);
    }

    public SensorListener(Context context, final SensorInterface cb) {

        //Callbackinterface
        this.cb = cb;

        //Activity
        activityList = new ArrayList<>(listSize);
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);

        //Location
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("kimmo", "onLocationChanged: " + location.getAccuracy());
                cb.updateLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        //Heartbeat
        broadcastData = new LinkedHashMap<>();
        api = PolarBleApiDefaultImpl.defaultImplementation(context, 0);
        api.setApiCallback(new PolarBleApiCallback() {
            @Override
            public void blePowerStateChanged(boolean b) {}

            @Override
            public void deviceConnected(PolarDeviceInfo s) {}

            @Override
            public void deviceConnecting(PolarDeviceInfo polarDeviceInfo) {}

            @Override
            public void deviceDisconnected(PolarDeviceInfo s) {}

            @Override
            public void ecgFeatureReady(String s) {}

            @Override
            public void accelerometerFeatureReady(String s) {}

            @Override
            public void ppgFeatureReady(String s) {}

            @Override
            public void ppiFeatureReady(String s) {}

            @Override
            public void biozFeatureReady(String s) {}

            @Override
            public void hrFeatureReady(String s) {}

            @Override
            public void disInformationReceived(String s, UUID u, String s1) {}

            @Override
            public void batteryLevelReceived(String s, int i) {}

            @Override
            public void hrNotificationReceived(String s, PolarHrData polarHrData) {}

            @Override
            public void polarFtpFeatureReady(String s) {}
        });
        broadcast();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER){

            //Low-pass filter
            acc_x = acc_x*(1-filter) + sensorEvent.values[0]*filter;
            acc_y = acc_y*(1-filter) + sensorEvent.values[1]*filter;
            acc_z = acc_z*(1-filter) + sensorEvent.values[2]*filter;

            //High-pass filter
            double hpx = sensorEvent.values[0]-acc_x;
            double hpy = sensorEvent.values[1]-acc_y;
            double hpz = sensorEvent.values[2]-acc_z;
            double hpFilter = Math.sqrt(Math.pow(hpx, 2) + Math.pow(hpy, 2) + Math.pow(hpz, 2));

            //Save to arraylist for later use
            if(activityList.size()==listSize){
                activityList.set(pointer, hpFilter);
            }
            else{
                activityList.add(hpFilter);
            }

            //Filtering creates latency so we send updates only when arraylist has updated
            if(pointer==listSize-1&& activityList.size()==listSize){
                double sum = 0;
                for(int i = 0; i< activityList.size(); i++){
                    sum += activityList.get(i);
                }
                cb.updateActivity(sum/ activityList.size());
                pointer = 0;
            }
            else{
                pointer++;
            }
        }
    }

    public void broadcast(){
        if(broadcastDisposable == null){
            broadcastDisposable = api.startListenForPolarHrBroadcasts(null).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Consumer<PolarHrBroadcastData>() {
                        @Override
                        public void accept(PolarHrBroadcastData polarHrBroadcastData){
                            String deviceID = polarHrBroadcastData.polarDeviceInfo.deviceId;
                            cb.updateHR(polarHrBroadcastData.hr);
                        }
                    },
                    new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable){}
                    },
                    new Action() {
                        @Override
                        public void run(){}
                    }
            );
        } else {
            broadcastDisposable.dispose();
            broadcastDisposable = null;
        }
    }

    public void start(){
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
            Log.d("kimmo", "SensorListener: Locationupdates requested");
        }
        catch (SecurityException e){
            Log.d("kimmo", "SensorListener: SecurityExcception");
        }
    }

    public void stop(){
        sensorManager.unregisterListener(this, sensor);
        locationManager.removeUpdates(locationListener);
        api.shutDown();
        api = null;
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {}
}
