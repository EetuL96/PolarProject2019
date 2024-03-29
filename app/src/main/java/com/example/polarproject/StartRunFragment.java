package com.example.polarproject;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.example.polarproject.Classes.RouteDataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartRunFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartRunFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button button = null;

    private SensorReceiver receiver = new SensorReceiver();
    private Spinner IDSpinner = null;
    private TextView textViewBPM = null;
    private TextView textViewActivity = null;
    private TextView textViewASpeed = null;
    private TextView textViewDistance = null;
    private ArrayAdapter IDAdapter = null;
    private CrystalRangeSeekbar bpmRangeBar;
    private ArrayList<RouteDataPoint> dataPointArrayList = new ArrayList<>();
    private ArrayList<String> IDArrayList = new ArrayList<>();
    private double lat;
    private double lng;
    private int bpm;
    private long starttime;
    private double activity;
    private double distance;
    private boolean running = false;
    private boolean sensors = false;
    private RequestQueue queue;

    public StartRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartRunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartRunFragment newInstance(String param1, String param2) {
        StartRunFragment fragment = new StartRunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        queue = Volley.newRequestQueue(getContext());
        View view = inflater.inflate(R.layout.fragment_start_run, container, false);
        textViewBPM = view.findViewById(R.id.textViewBPM);
        textViewActivity = view.findViewById(R.id.textViewActivity);
        textViewASpeed = view.findViewById(R.id.textViewASpeed);
        textViewDistance = view.findViewById(R.id.textViewDistance);
        IDSpinner = view.findViewById(R.id.spinnerID);
        IDAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, IDArrayList);
        IDSpinner.setAdapter(IDAdapter);
        startSensors();
        button = view.findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("kimmo", "onClick: ");
                if(!running){
                    button.setText("Stop");
                    running = true;
                    startSensors();
                }
                else{
                    running = false;
                    button.setText("Start");
                    //stopSensors();
                    if(dataPointArrayList.size()!=0){
                        saveRoute(dataPointArrayList);
                        updateUser(dataPointArrayList);
                        dataPointArrayList = new ArrayList<>();
                    }
                }
            }
        });
        bpmRangeBar = view.findViewById(R.id.bpmRangeBar);
        final TextView bpmLow = view.findViewById(R.id.bpmLow);
        final TextView bpmHigh = view.findViewById(R.id.bpmHigh);
        bpmRangeBar.setMinStartValue(80);
        bpmRangeBar.setMaxStartValue(170);
        bpmRangeBar.apply();
        bpmRangeBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                bpmLow.setText(String.valueOf(minValue));
                bpmHigh.setText(String.valueOf(maxValue));
            }
        });
        bpmRangeBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void startSensors(){
        Log.d("logi", "startSensors: ");
        if(!sensors){
            //receiver = new SensorReceiver();
            IntentFilter filter = new IntentFilter("sensor");
            getContext().registerReceiver(receiver, filter);
            getContext().startService(new Intent(getContext(), SensorListenerService.class));
            sensors = true;
        }
    }

    public void stopSensors(){
        if(sensors){
            Log.d("logi", "stopSensors: ");
            getContext().stopService(new Intent(getContext(), SensorListenerService.class));
            getContext().unregisterReceiver(receiver);
            sensors = false;
        }
    }

    public void updateUser(ArrayList<RouteDataPoint> arrayList){
        User user = ((Application) getActivity().getApplication()).getUser();
        RouteDataPoint dp = arrayList.get(arrayList.size()-1);
        user.setKilometersRun(user.getKilometersRun()+distance);
        user.setTotalTime(user.getTotalTime()+dp.getTime());
        double time = Double.valueOf(user.getTotalTime());
        time = time/3600000;
        user.setAverageSpeed(user.getKilometersRun()/time);
        user.setRunsCompleted(user.getRunsCompleted()+1);
        user.setAverageDistance(user.getKilometersRun()/user.getRunsCompleted());
        if(user.getLongestRun()<distance){
            user.setLongestRun(distance);
        }
        ((Application) getActivity().getApplication()).setUser(user);
        JSONObject js = new JSONObject();
        try {
            js.put("kilometersrun", user.getKilometersRun());
            js.put("totaltime", user.getTotalTime()); //ms
            js.put("averagespeed", user.getAverageSpeed()); //km/s
            js.put("runscompleted", user.getRunsCompleted());
            js.put("longestrun", user.getLongestRun());
            js.put("averagedistance", user.getAverageDistance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                "https://polarapp-oamk.herokuapp.com/users/"+user.getID(),
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("kimmo", "onResponse: success");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("kimmo", "onErrorResponse: error");
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjReq);
    }

    public void saveRoute(ArrayList<RouteDataPoint> arrayList){
        JSONObject js = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            double aBpm = 0;
            double maksBpm = 0;
            for (RouteDataPoint datapoint:arrayList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lat", datapoint.getLat());
                jsonObject.put("lng", datapoint.getLng());
                jsonObject.put("activity", datapoint.getActivity());
                jsonObject.put("bpm", datapoint.getBpm());
                jsonObject.put("time", datapoint.getTime());
                jsonArray.put(jsonObject);
                aBpm += datapoint.getBpm();
                if(datapoint.getBpm()>maksBpm){
                    maksBpm = datapoint.getBpm();
                }
            }
            aBpm = aBpm/dataPointArrayList.size();
            js.put("datapoints", jsonArray);
            js.put("distance", distance);
            js.put("owner", ((Application) getActivity().getApplication()).getUser().getID());
            js.put("time", dataPointArrayList.get(dataPointArrayList.size()-1).getTime());
            js.put("bpm_average", aBpm);
            js.put("bpm_maks", maksBpm);
            js.put("low_bpm",bpmRangeBar.getSelectedMinValue());
            js.put("high_bpm",bpmRangeBar.getSelectedMaxValue());
            js.put("speed_average_kmh", distance/((dataPointArrayList.get(dataPointArrayList.size()-1).getTime())/1000/3600));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    "https://polarapp-oamk.herokuapp.com/routes",
                    js,
                    new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("kimmo", "onResponse: success");
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("kimmo", "onErrorResponse: error");
                    }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("x-access-token", ((Application) getActivity().getApplication()).getToken());
                return headers;
            }
        };
        queue.add(jsonObjReq);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("kimmo", "onDestroyView: StartRun");
        stopSensors();
    }

    public double calcDistance(double lat1, double lat2, double lng1, double lng2){
        if ((lat1 == lat2) && (lng1 == lng2)) {
            return 0;
        }
        else {
            double theta = lng1 - lng2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 111.18958;
            return (dist);
        }
    }

    private class SensorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("action")) {
                case "activity":
                    Log.d("logi", "onReceive: activity");
                    activity = intent.getDoubleExtra("activity", 0);
                    textViewActivity.setText(String.format("%.2f", activity) + " activity");
                    break;
                case "location":
                    Log.d("logi", "onReceive: location");
                    if(running){
                        lat = intent.getDoubleExtra("lat", 0);
                        lng = intent.getDoubleExtra("lng", 0);
                        RouteDataPoint datapoint = new RouteDataPoint();
                        datapoint.setActivity(activity);
                        datapoint.setBpm(bpm);
                        datapoint.setLat(lat);
                        datapoint.setLng(lng);
                        dataPointArrayList.add(datapoint);
                        if(dataPointArrayList.size()==1){
                            starttime = System.currentTimeMillis();
                            datapoint.setTime(0);
                            distance = 0;
                        }
                        else{
                            double oldLat = dataPointArrayList.get(dataPointArrayList.size()-2).getLat();
                            double oldLng = dataPointArrayList.get(dataPointArrayList.size()-2).getLng();
                            datapoint.setTime(System.currentTimeMillis() - starttime);
                            distance += calcDistance(oldLat, lat, oldLng, lng);

                            //textViewDistance.setText(distance + " km");
                            textViewDistance.setText(String.format("%.2f km",distance));
                            double time = dataPointArrayList.get(dataPointArrayList.size()-1).getTime();
                            double speed = distance/(time/1000/3600);
                            textViewASpeed.setText(String.format("%.2f km/h", speed));
                            Log.d("kimmo", "distance: " + distance);
                            Log.d("kimmo", "time: " + time);
                            Log.d("kimmo", "speed: " + speed);
                        }
                    }
                    break;
                case "hr":
                    Log.d("logi", "onReceive: hr");
                    bpm = intent.getIntExtra("hr", 0);
                    String id = intent.getStringExtra("id");
                    boolean flag = false;
                    for (String str:IDArrayList) {
                        if(str.equals(id)){
                            flag = true;
                        }
                    }
                    if(!flag){
                        IDArrayList.add(id);
                        IDAdapter.notifyDataSetChanged();
                    }
                    if(IDSpinner.getSelectedItem()!=null){
                        if(IDSpinner.getSelectedItem().toString().equals(id)){
                            textViewBPM.setText(bpm + " bpm");
                            if(bpm>160){
                                try{
                                    Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(500);
                                }
                                catch (Exception e){

                                }
                            }
                        }
                    }
                    break;
                case "destroyed":
                    Log.d("kimmo", "onReceive: destroyed");
                    sensors = false;
                    /*if(running){
                        stopSensors();
                        startSensors();
                    }*/
                    break;
            }
        }
    }
}
