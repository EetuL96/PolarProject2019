package com.example.polarproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polarproject.Adapters.AwardsViewAdapter;
import com.example.polarproject.Adapters.RecyclerViewAdapter;
import com.example.polarproject.Classes.Award;
import com.example.polarproject.Classes.HerokuDataBase;

import java.util.ArrayList;

public class AwardsFragment extends Fragment implements HerokuDataBase.AwardsListener, AwardsViewAdapter.awardsListener {

    private RecyclerView recyclerView;
    private AwardsViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Award> dataset = new ArrayList<>();
    private View parent;
    private static final int NUMBER_OF_COLUMNS = 3;
    HerokuDataBase herokuDataBase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        parent = inflater.inflate(R.layout.fragment_awards, container, false);

        //TEST DATA
        Award award1 = new Award();
        Award award2 = new Award();
        Award award3 = new Award();
        Award award4 = new Award();
        Award award5 = new Award();



        recyclerView = parent.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new AwardsViewAdapter(getContext(), dataset);
        mAdapter.setAwardsListener(this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        herokuDataBase = new HerokuDataBase(getContext());
        herokuDataBase.setAwardsListener(this);
        herokuDataBase.getAwardsByUserId(((Application) getActivity().getApplication()).getUser().getID());
        return parent;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

    }

    @Override
    public void AwardFound(Award award) {
        dataset.add(award);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void AwardError() {

    }

    @Override
    public void awardsItemClicked(Award award) {
        Log.d("JKL", award.getName() + " Clicked");
    }
}
