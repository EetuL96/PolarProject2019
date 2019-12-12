package com.example.polarproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.polarproject.Adapters.RecyclerViewAdapter;
import com.example.polarproject.Classes.HerokuDataBase;
import com.example.polarproject.Classes.PictureInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

import java.io.FileNotFoundException;
import java.io.InputStream;



public class MainActivity extends AppCompatActivity implements TestFragment.OnFragmentInteractionListener, TestFragment2.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, MyProfileFragment.OnFragmentInteractionListener, CreateMapFragment.OnFragmentInteractionListener, RoutesFragment.OnFragmentInteractionListener, StartRunFragment.OnFragmentInteractionListener, SearchUsersFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, FollowingFragment.OnFragmentInteractionListener, RecyclerViewAdapter.ListenerInterface, NavController.OnDestinationChangedListener, RoutesRecycleFragment.OnFragmentInteractionListener {

    private NavController navController;
    private DrawerLayout drawerLayout;
    AppBarConfiguration appBarConfiguration;
    BottomNavigationView bottomNavigationView;
    PictureInterface pictureInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        drawerLayout = findViewById(R.id.drawer_layout);


        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.startRunFragment, R.id.myProfileFragment, R.id.routesFragment, R.id.createMapFragment, R.id.searchUsersFragment, R.id.followingFragment, R.id.routesRecycleFragment)
                        .setDrawerLayout(drawerLayout)
                        .build();

        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setVisibility(View.GONE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.bottom_stats:
                        Log.d("HSHSHS", "Bottom Navigation Stats Clicked!");
                        //navController.navigate(R.id.action_routesRecycleFragment_to_profileFragment);
                        break;
                    case R.id.botton_routes:
                        Log.d("HSHSHS", "Bottom Navigation Routes Clicked!");
                        //navController.navigate(R.id.action_profileFragment_to_routesRecycleFragment);
                        break;
                }
                return false;
            }
        });
        navController.addOnDestinationChangedListener(this);
    }

    public void setPictureInterface(PictureInterface pictureInterface)
    {
        this.pictureInterface = pictureInterface;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.drawer_myprofile: {
                navController.navigate(R.id.myProfileFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }

            case R.id.drawer_new_run: {
                navController.navigate(R.id.startRunFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.drawer_create_map: {
                navController.navigate(R.id.createMapFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.drawer_routes: {
                navController.navigate(R.id.routesFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.drawer_following: {
                navController.navigate(R.id.followingFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.drawer_search_users: {
                navController.navigate(R.id.searchUsersFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.drawer_logout: {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
                break;
            }
        }

        return true;
    }

    @Override
    public void itemClicked(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("name", user);
        navController.navigate(R.id.profileFragment, bundle);
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        Log.d("ASAS", "OnDestinationChanged");
        Log.d("ASAS", destination.getNavigatorName());
        if (destination == navController.getGraph().findNode(R.id.profileFragment) || destination == navController.getGraph().findNode(R.id.routesRecycleFragment))
        {
            Log.d("ASAS", "Profile Fragment!");
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        else
        {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    //Used when user click profile pic on MyProfileFragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("HYHYHY", "onActivityResult");
        if (resultCode == RESULT_OK)
        {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                pictureInterface.getData(selectedImage);
                Log.d("HYHYHY", "Fine");

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Log.d("HYHYHY", e.getMessage());
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
            }

        }
    }

}
