package com.example.polarproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.polarproject.Adapters.MyProfileFragmentStateAdapter;
import com.example.polarproject.Classes.HerokuDataBase;
import com.example.polarproject.Classes.PictureInterface;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyProfileFragment extends Fragment implements TabLayoutMediator.TabConfigurationStrategy, PictureInterface, HerokuDataBase.DatabaseSetImageListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private User user;
    private OnFragmentInteractionListener mListener;
    MyProfileFragmentStateAdapter fragmentStateAdapter;
    ViewPager2 viewPager;
    CircleImageView imageProfile;
    HerokuDataBase herokuDataBase;


    public MyProfileFragment() {

    }

    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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

        user = ((Application) getActivity().getApplication()).getUser();
        View  rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        TextView textViewName = rootView.findViewById(R.id.textViewName);
        textViewName.setText(user.getFirstName() + " " + user.getLastName());
        imageProfile = rootView.findViewById(R.id.imageProfile);

        MainActivity activity = (MainActivity) getActivity();
        activity.setPictureInterface(this);

        herokuDataBase = new HerokuDataBase(getActivity().getApplicationContext());
        herokuDataBase.setDatabaseSetImageListener(this);

        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("HOHOHO", "My Profile Image Clicked");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, 3);
            }
        });

        try
        {
            Glide.with(this).load("https://polarapp-pictures.s3.eu-north-1.amazonaws.com/" + user.getID()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.userdefaultimagecropped).into(imageProfile);
        }
        catch (Exception e)
        {
            Log.d("USUSUS", "Glide error!");
        }


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
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
    {
        if (position == 0)
        {
            tab.setText("STATS");
        }
        else
        {
            tab.setText("AWARDS");
        }
    }

    @Override
    public void getData(Bitmap pic) {
        Log.d("WEWEWE", "MyProfileFragment getData()");
        herokuDataBase.sendPicture(pic, user.getID());
        imageProfile.setImageBitmap(pic);
    }

    //TODO Fix bug when loading image first time
    @Override
    public void imageSetSuccess() {
        Log.d("KLKLKL", "MyProf imageSetSuccess");
        try
        {
            //Glide.with(this).load("https://polarapp-pictures.s3.eu-north-1.amazonaws.com/" + user.getID()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(R.drawable.userdefaultimagecropped).into(imageProfile);
            Toast.makeText(getContext(), "Image Saved successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void imageSetError() {
        Log.d("KLKLKL", "MyProf imageSetError");
        try
        {
            Toast.makeText(getContext(), "Image saving failed...", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {

        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
