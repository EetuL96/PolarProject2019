package com.example.polarproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polarproject.Classes.Award;
import com.example.polarproject.R;
import com.example.polarproject.User;

import java.util.ArrayList;

public class AwardsViewAdapter extends RecyclerView.Adapter<AwardsViewAdapter.AwardViewHolder> {

    Context context;
    private ArrayList<Award> awardsList = new ArrayList<>();
    private awardsListener callbackInterface = null;

    public AwardsViewAdapter(Context context, ArrayList<Award> list)
    {
        this.context = context;
        this.awardsList = list;

    }

    public interface awardsListener
    {
        void awardsItemClicked(Award award);
    }

    public void setAwardsListener(awardsListener listener)
    {
        callbackInterface = listener;
    }

    public static class AwardViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewAwardName;
        public ConstraintLayout parentLayout;

        public AwardViewHolder(View itemView){
            super(itemView);
            textViewAwardName= itemView.findViewById(R.id.textViewAwardName);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    @NonNull
    @Override
    public AwardsViewAdapter.AwardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.awards_list_item, parent, false);
        AwardViewHolder vh = new AwardViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AwardViewHolder holder, int position) {
        Award award = awardsList.get(position);
        holder.textViewAwardName.setText(award.getName());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackInterface.awardsItemClicked(award);
            }
        });
    }


    @Override
    public int getItemCount() {
        return awardsList.size();
    }
}
