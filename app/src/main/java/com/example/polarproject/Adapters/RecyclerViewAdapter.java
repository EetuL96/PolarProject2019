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
import com.example.polarproject.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private ArrayList<User> userList = new ArrayList<>();
    private Context context;
    private ListenerInterface callbackInterface = null;

    public interface ListenerInterface
    {
        void itemClicked(User user);
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textView;
        public RelativeLayout parentLayout;
        public TextView textViewIsFollowed;

        public MyViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.textView2);
            textViewIsFollowed = itemView.findViewById(R.id.textViewFollowed);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<User> list)
    {
        this.context = context;
        callbackInterface = (ListenerInterface) context;
        this.userList = list;
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
        User user = userList.get(position);

        try
        {
            if (user.getIsFollowed())
            {
                holder.textViewIsFollowed.setText("Followed");
            }
            else
            {
                holder.textViewIsFollowed.setText("");
            }
        }
        catch (Exception e)
        {
            holder.textViewIsFollowed.setText("");
        }

        holder.textView.setText(user.getEmail());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = userList.get(position);
                Log.d("LLL", userList.get(position).getEmail() + "Clicked!");
                callbackInterface.itemClicked(user);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return userList.size();
    }
}
