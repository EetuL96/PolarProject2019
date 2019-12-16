package com.example.polarproject.Adapters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.polarproject.Application;
import com.example.polarproject.R;
import com.example.polarproject.User;

import org.w3c.dom.Text;

public class StatsFragment extends Fragment {


    public static final String ARG_OBJECT = "object";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        User user = (User) getArguments().getSerializable("user");
        TextView textViewKMS = view.findViewById(R.id.textViewKms);
        TextView textViewTT = view.findViewById(R.id.textViewTotalTime);
        TextView textViewAS = view.findViewById(R.id.textViewAverageSpeed);
        TextView textViewRC = view.findViewById(R.id.textViewRunsCompleted);
        TextView textViewLR = view.findViewById(R.id.textViewLongestRun);
        TextView textViewAD = view.findViewById(R.id.textViewAverageDistance);
        //User user = ((Application) getActivity().getApplication()).getUser();

        String kms = String.format("%.0f KM", user.getKilometersRun());
        String tt = (user.getTotalTime()/1000/3600) + " H";
        String as = String.format("%.1f KM/H", user.getAverageSpeed());
        String rc = Long.toString(user.getRunsCompleted());
        String lr = String.format("%.1f KM", user.getLongestRun());
        String ad = String.format("%.1f KM", user.getAverageDistance());

        textViewKMS.setText("KMS RAN\n"+kms);
        textViewTT.setText("TOTAL TIME\n"+tt);
        textViewAS.setText("AVERAGE SPEED\n"+as);
        textViewRC.setText("RUNS COMPLETED\n"+rc);
        textViewLR.setText("LONGEST RUN\n"+lr);
        textViewAD.setText("AVERAGE DISTANCE\n"+ad);
    }
}
