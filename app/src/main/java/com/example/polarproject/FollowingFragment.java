package com.example.polarproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.polarproject.Adapters.RecyclerViewAdapter;
import com.example.polarproject.Classes.HerokuDataBase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment implements HerokuDataBase.DatabaseGetFollowedUsersListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> dataset = new ArrayList<>();
    private View parent;
    private HerokuDataBase herokuDataBase;


    public FollowingFragment() {
        // Required empty public constructor
    }

    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
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

        dataset.clear();
        parent = inflater.inflate(R.layout.fragment_following, container, false);
        recyclerView = parent.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), dataset);
        recyclerView.setAdapter(mAdapter);

        User user = ((Application) getActivity().getApplication()).getUser();
        herokuDataBase = new HerokuDataBase(getContext());
        herokuDataBase.setDatabaseGetFollowedListener(this);
        herokuDataBase.getFollowedUsers(user.getID(), ((Application) getActivity().getApplication()).getToken());

        return parent;

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
    public void userGetFollowed(User user) {
        dataset.add(user);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void userGetFollowError() {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
