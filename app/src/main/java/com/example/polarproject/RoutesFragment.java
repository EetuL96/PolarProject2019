package com.example.polarproject;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.polarproject.Adapters.RouteListAdapter;
import com.example.polarproject.Classes.HerokuDataBase;
import com.example.polarproject.Classes.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoutesFragment extends Fragment implements HerokuDataBase.DeleteRouteListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private OnFragmentInteractionListener mListener;
    //private ArrayList<Route> routes = new ArrayList<>();
    private RouteListAdapter routeListAdapter;
    private ConstraintLayout newRouteButton;
    ListView lv = null;
    RequestQueue mQueue;

    HerokuDataBase herokuDataBase;


    public RoutesFragment() {
    }


    public static RoutesFragment newInstance(String param1, String param2) {
        RoutesFragment fragment = new RoutesFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_routes, container, false);
        mQueue = Volley.newRequestQueue(getContext());
        lv = rootView.findViewById(R.id.listViewRoutes);
        newRouteButton = rootView.findViewById(R.id.newRouteButton);
        newRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLocationPermission()&&checkBTPermission()){
                    mListener.newRoute();
                }
            }
        });

        herokuDataBase = new HerokuDataBase(getContext());
        herokuDataBase.setDeleteRouteListener(this);

        getMyRoutes(((Application) getActivity().getApplication()).getUser().getID());
        return rootView;
    }

    public void getMyRoutes(String myId)
    {
        String url = "https://polarapp-oamk.herokuapp.com/routes/owner/" + myId;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try
                        {
                            ArrayList<Route> routes = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                Route route = new Route();
                                JSONObject jsObject = response.getJSONObject(i);
                                route.setId(jsObject.getString("_id"));
                                route.setDate(jsObject.getString("date"));
                                route.setDistance(jsObject.getDouble("distance"));
                                route.setTime(jsObject.getLong("time"));
                                routes.add(route);
                            }
                            routeListAdapter = new RouteListAdapter(getContext(), routes);
                            lv.setAdapter(routeListAdapter);
                            lv.setEnabled(true);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    lv.setEnabled(false);
                                    Route route = (Route) parent.getItemAtPosition(position);
                                    mListener.openRoute(route.getId());

                                }
                            });
                            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Log.d("ALALAL", "onItemLongClick!");
                                    Route route = (Route) adapterView.getItemAtPosition(i);
                                    Log.d("ALALAL", route.getDate());

                                    AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                                    adb.setTitle("Are you sure you want to delete: " + route.getDate() + "?");

                                    adb.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d("ALALAL", "Yes Button Clicked!");
                                            herokuDataBase.deleteRoute(route.getId(), ((Application) getActivity().getApplication()).getToken());
                                        }
                                    });

                                    adb.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d("ALALAL", "Cancel Button Clicked!");
                                        }
                                    });


                                    adb.show();
                                    return true;
                                }
                            });
                        }
                        catch (Exception e)
                        {

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
                headers.put("x-access-token", ((Application) getActivity().getApplication()).getToken());
                return headers;
            }
        };
        mQueue.add(jsonArrayRequest);
    }

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



    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    public boolean checkBTPermission(){
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void deleteRouteSuccess() {
        Log.d("ESJONEN", "deleteRouteSuccess");

        Toast.makeText(getContext(), "Route deleted successfully!", Toast.LENGTH_LONG).show();
        //routes.clear();
        routeListAdapter.clear();
        routeListAdapter.notifyDataSetChanged();
        lv.setAdapter(null);
        getMyRoutes(((Application) getActivity().getApplication()).getUser().getID());

    }

    @Override
    public void deleteRouteError() {
        Toast.makeText(getContext(), "Couldn't delete route...", Toast.LENGTH_SHORT);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void openRoute(String routeId);
        void newRoute();
    }

    @Override
    public void onResume() {
        super.onResume();
        lv.setEnabled(true);
    }
}
