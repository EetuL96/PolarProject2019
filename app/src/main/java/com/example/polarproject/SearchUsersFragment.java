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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.polarproject.Adapters.RecyclerViewAdapter;
import com.example.polarproject.Classes.HerokuDataBase;

import java.util.ArrayList;



public class SearchUsersFragment extends Fragment implements HerokuDataBase.DataBaseAllUsersListener, HerokuDataBase.DataBaseSearchUserListener, HerokuDataBase.DatabaseGetAllUserAndCheckIfFollowedListener, View.OnClickListener, HerokuDataBase.DatabaseSearchUserByEmailAndGetFollowedLister {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> dataset = new ArrayList<>();
    private Button buttonSearch = null;
    private EditText editTextSeacrh = null;
    User user = null;

    HerokuDataBase herokuDataBase;
    View parent;

    public SearchUsersFragment() {

    }

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

        user = ((Application) getActivity().getApplication()).getUser();
        Log.d("IIII", "OnCreateView(): ");
        herokuDataBase = new HerokuDataBase(getActivity().getApplicationContext());
        herokuDataBase.setDatabaseAllUsersListener(this);
        herokuDataBase.setDatabaseSearchListener(this);

        herokuDataBase.setDatabaseGetAllUserAndCheckIfFollowedListener(this);
        herokuDataBase.getAllUsersAndCheckIfFollowed(user.getID());

        herokuDataBase.setDatabaseSearchUserByEmailAndGetFollowedLister(this);

        return parent;
    }

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
    public void userFound(User user) {
        dataset.add(user);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void allUsersError() {

    }

    @Override
    public void onClick(View view) {
        if (view == parent.findViewById(R.id.buttonSearch))
        {
            String email = editTextSeacrh.getText().toString();
            if (!TextUtils.isEmpty(email))
            {
                herokuDataBase.searchUserByEmailAndGetFollowed(email, user.getID());
            }
            else
            {
                dataset.clear();
                mAdapter.notifyDataSetChanged();
                herokuDataBase.getAllUsersAndCheckIfFollowed(user.getID());
            }
            closeKeyboard();
        }
    }

    @Override
    public void userSearchFound(User user) {
        dataset.clear();
        dataset.add(user);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void userSeacrhNotFound() {
        dataset.clear();
        mAdapter.notifyDataSetChanged();
        Toast toast = Toast.makeText(getContext(), "User not found...", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void getUserAndCheckIfFollowed(User user) {
        dataset.add(user);
        mAdapter.notifyDataSetChanged();
    }

    //SearchUserByEmailAndGetFollowed
    @Override
    public void searchSuccess(User user) {
        dataset.clear();
        dataset.add(user);
        mAdapter.notifyDataSetChanged();
    }

    //SearchUserByEmailAndGetFollowed
    @Override
    public void searchFailed() {
        dataset.clear();
        mAdapter.notifyDataSetChanged();
        try
        {
            Toast toast = Toast.makeText(getContext(), "User not found...", Toast.LENGTH_SHORT);
            toast.show();
        }
        catch (Exception e)
        {

        }
    }

    private void closeKeyboard()
    {
        View view = getActivity().getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
