package com.example.polarproject;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.polarproject.Classes.RouteDataPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    RequestQueue mQueue;
    String routeId;
    ArrayList<RouteDataPoint> dataPoints = new ArrayList<>();
    TextView bpmLowText;
    TextView bpmMidText;
    TextView bpmHighText;
    TextView avgBpmValue;
    TextView maxBpmValue;
    TextView timeValue;
    TextView distanceValue;
    MarkerOptions options = new MarkerOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Intent intent = getIntent();
        routeId = intent.getStringExtra("routeId");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mQueue = Volley.newRequestQueue(this);
        bpmLowText = findViewById(R.id.lowBpmText);
        bpmMidText = findViewById(R.id.midBpmText);
        bpmHighText = findViewById(R.id.highBpmText);
        avgBpmValue = findViewById(R.id.avgBpmValue);
        maxBpmValue = findViewById(R.id.maxBpmValue);
        timeValue = findViewById(R.id.timeValue);
        distanceValue = findViewById(R.id.distanceValue);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getRoute();

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }

            }
        });*/

    }

    public void getRoute()
    {
        String url = "https://polarapp-oamk.herokuapp.com/routes/" + routeId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try
                        {
                            try{
                                double bpmValueLow = jsonObject.getInt("bpm_low");
                                double bpmValueHigh = jsonObject.getInt("bpm_high");
                                bpmLowText.setText((int) bpmValueLow+"");
                                bpmMidText.setText(Math.round((bpmValueLow + bpmValueHigh)/2) + "");
                                bpmHighText.setText((int) bpmValueHigh+"");
                                avgBpmValue.setText(Math.round(jsonObject.getDouble("bpm_average"))+"");
                                maxBpmValue.setText((int) jsonObject.getDouble("bpm_maks")+"");
                                long ms = jsonObject.getLong("time");
                                int s = (int) ms / 1000;
                                int m = s / 60;
                                s -= m * 60;
                                timeValue.setText(m +" min , " + s + " s");
                                distanceValue.setText((Math.round(jsonObject.getDouble("distance") * 10.0 ) / 10.0)+" km");
                                JSONArray array = jsonObject.getJSONArray("datapoints");

                                for(int i = 0; i< array.length() ;i++){
                                    JSONObject jObject = array.getJSONObject(i);
                                    RouteDataPoint dataPoint = new RouteDataPoint();
                                    dataPoint.setTime(0);
                                    dataPoint.setLat(jObject.getDouble("lat"));
                                    dataPoint.setLng(jObject.getDouble("lng"));
                                    dataPoint.setActivity(jObject.getDouble("activity"));
                                    dataPoint.setBpm(jObject.getInt("bpm"));
                                    dataPoints.add(dataPoint);
                                }
                                boolean maxBpmFound = false;
                                for (int i = 1; i < dataPoints.size() ; i++) {
                                    int gValue = 255;
                                    int rValue = 255;
                                    double bpm = dataPoints.get(i).getBpm();
                                    if (maxBpmFound == false && bpm == jsonObject.getDouble("bpm_maks")) {
                                        options.position(new LatLng(dataPoints.get(i).getLat(), dataPoints.get(i).getLng()));
                                        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_heartrate_max));
                                        mMap.addMarker(options);
                                        maxBpmFound = true;
                                    }
                                    Double scaleValue = (bpm - bpmValueLow) / (bpmValueHigh - bpmValueLow);
                                    scaleValue = Math.min(1.0, scaleValue);
                                    scaleValue = Math.max(0.0, scaleValue);
                                    if (scaleValue > 0.5) {
                                        gValue -= (255 * ((scaleValue - 0.5) * 2));
                                    } else if (scaleValue < 0.5) {
                                        rValue -= (255 * ((0.5 - scaleValue) * 2));
                                    }
                                    int lineColor = Color.rgb(rValue, gValue, 0);
                                    Polyline line = mMap.addPolyline(new PolylineOptions()
                                            .add(new LatLng(dataPoints.get(i-1).getLat(), dataPoints.get(i-1).getLng()), new LatLng(dataPoints.get(i).getLat(), dataPoints.get(i).getLng()))
                                            .width(16)
                                            .color(lineColor));
                                }
                                options.position(new LatLng(dataPoints.get(0).getLat(), dataPoints.get(0).getLng()));
                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.startmarker));
                                mMap.addMarker(options);
                                options.position(new LatLng(dataPoints.get(dataPoints.size()-1).getLat(), dataPoints.get(dataPoints.size()-1).getLng()));
                                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.stopmarker));
                                mMap.addMarker(options);
                                double avgLat = (dataPoints.get(0).getLat() + dataPoints.get(dataPoints.size() - 1).getLat()) / 2;
                                double avgLng = (dataPoints.get(0).getLng() + dataPoints.get(dataPoints.size() - 1).getLng()) / 2;
                                LatLng startLocation = new LatLng(avgLat, avgLng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 15));
                            }catch (Exception e){
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token",((Application) getApplication()).getToken());
                return headers;
            }
        };
        mQueue.add(jsonObjectRequest);
        Log.d("LOL", "FINISH");
    }

    /*private class DownloadTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("LOL3", "jObject: " + jObject.toString());
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = (List) parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap>> result) {
            Log.d("LOL2", "result: " + result.size());
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat").toString());
                    Log.d("LOL2", "lat: " + lat);
                    double lng = Double.parseDouble(point.get("lng").toString());
                    Log.d("LOL2", "Lng: " + lng);
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            try {
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
            }
            catch (Exception e)
            {
                Log.d("LOL2", e.getMessage());
            }

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }*/
}
