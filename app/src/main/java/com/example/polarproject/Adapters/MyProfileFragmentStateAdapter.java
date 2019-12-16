package com.example.polarproject.Adapters;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.polarproject.AwardsFragment;
import com.example.polarproject.TestFragment3;
import com.example.polarproject.User;

import java.util.List;

public class MyProfileFragmentStateAdapter extends FragmentStateAdapter {
    public MyProfileFragmentStateAdapter(Fragment fragment) {
        super(fragment);
    }

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        if (position == 0)
        {
            Fragment fragment = new StatsFragment();
            Bundle args = new Bundle();
            args.putSerializable("user", user);
            fragment.setArguments(args);
            return fragment;
        }
        else
        {
            Fragment fragment = new AwardsFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(TestFragment3.ARG_OBJECT, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
