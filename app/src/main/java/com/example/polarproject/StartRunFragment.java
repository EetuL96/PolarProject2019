package com.example.polarproject;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.polarproject.Classes.Route;
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

    private SensorReceiver receiver = new SensorReceiver();
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<RouteDataPoint> dataPointArrayList = new ArrayList<>();
    double lat;
    double lng;
    int bpm;
    long starttime;
    double activity;
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

        View view = inflater.inflate(R.layout.fragment_start_run, container, false);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayList<String> list= new ArrayList<String>();
        list.add("Route 1");
        list.add("Route 2");
        list.add("Route 3");
        list.add("Route 4");
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        else{
            return true;
        }
    }

    public boolean checkBTPermission(){
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
            return false;
        }
        else{
            return true;
        }
    }

    public void startSensors(){
        Log.d("logi", "startSensors: ");
        receiver = new SensorReceiver();
        IntentFilter filter = new IntentFilter("sensor");
        getContext().registerReceiver(receiver, filter);
        getContext().startService(new Intent(getContext(), SensorListenerService.class));
    }

    public void saveRoute(ArrayList<RouteDataPoint> arrayList){
        JSONObject js = new JSONObject();
        try {
            js.put("owner", "random");
            js.put("datetime", "datetime");
            JSONArray jsonArray = new JSONArray();
            for (RouteDataPoint datapoint:arrayList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lat", datapoint.getLat());
                jsonObject.put("lng", datapoint.getLng());
                jsonObject.put("activity", datapoint.getActivity());
                jsonObject.put("bpm", datapoint.getBpm());
                jsonObject.put("time", datapoint.getTime());
                jsonArray.put(jsonObject);
            }
            js.put("datapoints", jsonArray);
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
                        return headers;
                    }
                };
        queue.add(jsonObjReq);
    }

    public void stopSensors(){
        Log.d("logi", "stopSensors: ");
        getContext().stopService(new Intent(getContext(), SensorListenerService.class));
        getContext().unregisterReceiver(receiver);
    }

    private class SensorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("logi", "onReceive: ");
            switch (intent.getStringExtra("action")) {
                case "activity":
                    activity = intent.getDoubleExtra("activity", 0);
                    break;
                case "location":
                    lat = intent.getDoubleExtra("lat", 0);
                    lng = intent.getDoubleExtra("lng", 0);
                    RouteDataPoint datapoint = new RouteDataPoint();
                    datapoint.setActivity(activity);
                    datapoint.setBpm(bpm);
                    datapoint.setLat(lat);
                    datapoint.setLng(lng);
                    if(dataPointArrayList.size()==0){
                        starttime = System.currentTimeMillis();
                        datapoint.setTime(0);
                    }
                    else{
                        datapoint.setTime(System.currentTimeMillis() - starttime);
                    }
                    //intent.getFloatExtra("accuracy", 0);
                    break;
                case "hr":
                    bpm = intent.getIntExtra("hr", 0);
                    if(bpm>120){
                        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                    }
                    break;
            }
        }
    }
}
