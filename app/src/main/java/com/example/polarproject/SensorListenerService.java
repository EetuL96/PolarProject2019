package com.example.polarproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

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

public class SensorListenerService extends Service implements SensorEventListener{

    private final double filter = 0.1;
    private final int listSize = 50;
    private int pointer = 0;
    private double acc_x = 0;
    private double acc_y = 0;
    private double acc_z = 0;
    private ArrayList<Double> activityList;
    private SensorManager sensorManager;
    private android.hardware.Sensor sensor;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private PolarBleApi api;
    private Disposable broadcastDisposable;
    private Map<String, PolarHrBroadcastData> broadcastData;

    public IBinder onBind(Intent intent){
        return null;
    }

    public SensorListenerService() {
        super();
        Log.d("kimmo", "SensorListenerService: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("kimmo", "onStartCommand: ");
        setupSensors();
        monitorHR();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        /*try{
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            Log.d("kimmo", "SensorListenerService: Locationupdates requested");
        }
        catch (SecurityException e){
            Log.d("kimmo", "SensorListenerService: SecurityExcception");
        }*/
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("kimmo", "onDestroy: ");
        sensorManager.unregisterListener(this, sensor);
        fusedLocationClient.removeLocationUpdates(locationCallback);
        api.shutDown();
        api = null;
        Intent intent = new Intent("sensor");
        intent.putExtra("action", "destroyed");
        sendBroadcast(intent);
    }

    public void setupSensors(){
        Log.d("kimmo", "setupSensors: ");
        //Activity
        activityList = new ArrayList<>(listSize);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);

        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = new LocationRequest()
                .setInterval(5000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.d("kimmo", "onLocationChanged: " + location.getAccuracy());
                    Intent intent = new Intent("sensor");
                    intent.putExtra("action", "location");
                    intent.putExtra("lng", location.getLongitude());
                    intent.putExtra("lat", location.getLatitude());
                    intent.putExtra("accuracy", location.getAccuracy());
                    sendBroadcast(intent);
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        //Heartbeat
        broadcastData = new LinkedHashMap<>();
        api = PolarBleApiDefaultImpl.defaultImplementation(this, 0);
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
            try{
                if(activityList.size()==listSize){
                    activityList.set(pointer, hpFilter);
                }
                else{
                    activityList.add(hpFilter);
                }
            }
            catch (Exception e){
                e.printStackTrace();
                activityList = new ArrayList<>(listSize);
            }

            //Filtering creates latency so we send updates only when arraylist has updated
            if(pointer==listSize-1&& activityList.size()==listSize){
                double sum = 0;
                for(int i = 0; i< activityList.size(); i++){
                    sum += activityList.get(i);
                }
                //Intent intent = new Intent(getBaseContext(), SensorListenerService.class);
                Intent intent = new Intent("sensor");
                intent.putExtra("action", "activity");
                intent.putExtra("activity", sum/ activityList.size());
                sendBroadcast(intent);
                Log.d("kimmo", "onSensorChanged: activity " + sum/ activityList.size());
                pointer = 0;
            }
            else{
                pointer++;
            }
        }
    }

    public void monitorHR(){
        Log.d("kimmo", "monitorHR: ");
        if(broadcastDisposable == null){
            broadcastDisposable = api.startListenForPolarHrBroadcasts(null).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    new Consumer<PolarHrBroadcastData>() {
                        @Override
                        public void accept(PolarHrBroadcastData polarHrBroadcastData){
                            String deviceID = polarHrBroadcastData.polarDeviceInfo.deviceId;
                            Intent intent = new Intent("sensor");
                            intent.putExtra("action", "hr");
                            intent.putExtra("id", deviceID);
                            intent.putExtra("hr", polarHrBroadcastData.hr);
                            sendBroadcast(intent);
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

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {}
}
