package com.example.polarproject.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.polarproject.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<String> list = new ArrayList<>();
    private Context context;
    private ListenerInterface callbackInterface = null;

    public interface ListenerInterface
    {
        void itemClicked(String name);
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textView;
        public RelativeLayout parentLayout;

        public MyViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.textView2);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<String> list)
    {
        this.context = context;
        callbackInterface = (ListenerInterface) context;
        this.list = list;
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.textView.setText(list.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = list.get(position);
                Log.d("LLL", list.get(position) + "Clicked!");
                callbackInterface.itemClicked(name);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
}