package com.example.polarproject.Adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.polarproject.Classes.Route;
import com.example.polarproject.R;

import java.util.ArrayList;

public class RouteListAdapter extends ArrayAdapter<Route> {

    public RouteListAdapter(Context context, ArrayList<Route> routes) {
        super(context, 0, routes);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Route route = getItem(position);

        if (convertView == null) {
            int layoutId = 0;
            layoutId = R.layout.list_item_routes;
            convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.textViewRouteName);
        return convertView;
    }

}