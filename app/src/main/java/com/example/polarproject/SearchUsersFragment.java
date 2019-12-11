package com.example.polarproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.polarproject.Adapters.RecyclerViewAdapter;
import com.example.polarproject.Classes.HerokuDataBase;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchUsersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchUsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchUsersFragment extends Fragment implements HerokuDataBase.DataBaseAllUsersListener, HerokuDataBase.DataBaseSearchUserListener, View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> dataset = new ArrayList<>();
    private Button buttonSearch = null;
    private EditText editTextSeacrh = null;

    HerokuDataBase herokuDataBase;
    View parent;

    public SearchUsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchUsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchUsersFragment newInstance(String param1, String param2) {
        SearchUsersFragment fragment = new SearchUsersFragment();
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
        dataset.clear();
        parent = inflater.inflate(R.layout.fragment_search_users, container, false);
        recyclerView = parent.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewAdapter(getContext(), dataset);
        recyclerView.setAdapter(mAdapter);

        editTextSeacrh = (EditText) parent.findViewById(R.id.editTextSearch);
        editTextSeacrh.setText("");
        buttonSearch = (Button) parent.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);

        Log.d("IIII", "OnCreateView(): ");
        herokuDataBase = new HerokuDataBase(getActivity().getApplicationContext());
        herokuDataBase.setDatabaseAllUsersListener(this);
        herokuDataBase.setDatabaseSearchListener(this);
        herokuDataBase.getAllUsers();

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
        editTextSeacrh.setText("");
        dataset.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void userFound(String email) {
        Log.d("LLLL", "User: " + email);
        dataset.add(email);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void allUsersError() {

    }

    @Override
    public void onClick(View view) {
        if (view == parent.findViewById(R.id.buttonSearch))
        {
            Log.d("RRRR", "Search button CLICKED!");
            String email = editTextSeacrh.getText().toString();
            if (!TextUtils.isEmpty(email))
            {
                herokuDataBase.searchUserByEmail(email);
            }
            else
            {
                editTextSeacrh.setError("Email cannot be empty");
                return;
            }
        }
    }

    @Override
    public void userSearchFound(String email) {
        dataset.clear();
        dataset.add(email);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void userSeacrhNotFound() {
        dataset.clear();
        mAdapter.notifyDataSetChanged();
        Toast toast = Toast.makeText(getContext(), "User not found...", Toast.LENGTH_LONG);
        toast.show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
