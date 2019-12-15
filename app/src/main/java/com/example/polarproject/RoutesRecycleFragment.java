package com.example.polarproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.polarproject.Adapters.RouteListAdapter;
import com.example.polarproject.Classes.HerokuDataBase;
import com.example.polarproject.Classes.Route;

import java.util.ArrayList;


public class RoutesRecycleFragment extends Fragment implements HerokuDataBase.DataBaseGetRoutesByIdListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<Route> routes = new ArrayList<>();
    private RouteListAdapter routeListAdapter;
    ListView lv = null;

    HerokuDataBase herokuDataBase;


    public RoutesRecycleFragment() {
    }

    public static RoutesRecycleFragment newInstance(String param1, String param2) {
        RoutesRecycleFragment fragment = new RoutesRecycleFragment();
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
        // Inflate the layout for this fragment
        User profileUser = (User) getArguments().getSerializable("user");
        Log.d("MXMXMX", profileUser.getEmail());

        View rootView = inflater.inflate(R.layout.fragment_routes_recycle, container, false);
        lv = rootView.findViewById(R.id.listViewRoutes);


        herokuDataBase = new HerokuDataBase(getContext());
        herokuDataBase.setDatabaseGetRoutesListener(this);
        herokuDataBase.getRoutesByUserId(profileUser.getID());

        return rootView;
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

    @Override
    public void getRoute(Route route) {
        try
        {
            Log.d("FAFAFAFA", route.getDate());
            routes.add(route);
        }
        catch (Exception e)
        {
            Log.d("FAFAFAFA", "getRoute error");
        }

    }

    @Override
    public void getRoutesReady() {
        Log.d("FAFAFAFA", "getRoutes ready");
        try {
            routeListAdapter = new RouteListAdapter(getContext(), routes);
            lv.setAdapter(routeListAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Route route = (Route) parent.getItemAtPosition(position);
                    mListener.openRoute(route.getId());
                }
            });
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public void getRouteError() {

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void openRoute(String routeId);
    }
}
