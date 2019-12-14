package com.example.polarproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.polarproject.Adapters.MyProfileFragmentStateAdapter;
import com.example.polarproject.Classes.HerokuDataBase;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements TabLayoutMediator.TabConfigurationStrategy, View.OnClickListener, HerokuDataBase.DatabaseFollowUserListener, HerokuDataBase.DatabaseUnfollowListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    MyProfileFragmentStateAdapter fragmentStateAdapter;
    ViewPager2 viewPager;

    Button buttonFollow = null;
    View rootView;

    User profileUser = new User();
    User user = new User();

    HerokuDataBase herokuDataBase;

    boolean followOrUnfollow;

    public ProfileFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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


        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView tv = rootView.findViewById(R.id.textViewName);

        profileUser = (User) getArguments().getSerializable("name");
        Log.d("BBBB", profileUser.getID());

        tv.setText(profileUser.getEmail());


        buttonFollow = rootView.findViewById(R.id.buttonFollow);
        buttonFollow.setOnClickListener(this);

        herokuDataBase = new HerokuDataBase(getContext());
        herokuDataBase.setDatabaseFollowListener(this);
        herokuDataBase.setDatabaseUnfollowListener(this);

        if (profileUser.getIsFollowed())
        {
            buttonFollow.setText("Unfollow");
        }
        else
        {
            buttonFollow.setText("Follow");
        }

        ImageView imageProfile = rootView.findViewById(R.id.imageProfile);
        Glide.with(this).load("https://polarapp-pictures.s3.eu-north-1.amazonaws.com/" + profileUser.getID()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.userdefaultimagecropped).into(imageProfile);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        fragmentStateAdapter = new MyProfileFragmentStateAdapter(this);
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(fragmentStateAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        /*new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText("PAGE " + (position + 1))
        ).attach();*/
        TabLayoutMediator tabLayoutMediator;
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, this);
        tabLayoutMediator.attach();



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
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

        if (position == 0)
        {
            tab.setText("STATS");
        }
        else
        {
            tab.setText("AWARDS");
        }

    }

    //TODO
    @Override
    public void onClick(View view) {
        if (view == rootView.findViewById(R.id.buttonFollow))
        {
            if(!profileUser.getIsFollowed())
            {
                Application application = (Application) getContext().getApplicationContext();
                User user = application.getUser();
                herokuDataBase.createNewFollow(user.getID(), profileUser.getID());
            }
            else
            {
                Application application = (Application) getContext().getApplicationContext();
                User user = application.getUser();
                herokuDataBase.unFollow(user.getID(), profileUser.getID());
            }

        }
    }

    @Override
    public void userFollowed() {
        try
        {
            Toast.makeText(getContext(), "Followed: " + profileUser.getEmail(), Toast.LENGTH_SHORT).show();
            profileUser.setIsFollowed(true);
            buttonFollow.setText("Unfollow");
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public void userFollowError() {

    }

    @Override
    public void userUnfollowed() {
        try
        {
            Toast.makeText(getContext(), "UnFollowed: " + profileUser.getEmail(), Toast.LENGTH_SHORT).show();
            profileUser.setIsFollowed(false);
            buttonFollow.setText("Follow");
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void userUnfollowedError() {

    }

    public User getProfileUser()
    {
        return this.profileUser;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
