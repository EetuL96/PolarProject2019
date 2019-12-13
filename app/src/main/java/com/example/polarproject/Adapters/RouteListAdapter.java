package com.example.polarproject.Adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        TextView textViewDate = convertView.findViewById(R.id.textViewRouteDate);
        textViewDate.setText(route.getDate());
        TextView textViewTime = convertView.findViewById(R.id.textViewRouteTime);
        long ms = route.getTime();
        int s = (int) ms / 1000;
        int m = s / 60;
        s -= m * 60;
        textViewTime.setText(m +" min , " + s + " s");
        TextView textViewDistance = convertView.findViewById(R.id.textViewRouteDistance);
        textViewDistance.setText(route.getDistance()+" km");
        return convertView;
    }

}