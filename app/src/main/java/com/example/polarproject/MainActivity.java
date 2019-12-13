package com.example.polarproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    AppBarConfiguration appBarConfiguration;
    BottomNavigationView bottomNavigationView;
    PictureInterface pictureInterface;
    boolean drawerIsOpen = false;

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


        //TEST
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {


                Log.d("GTGTGT", "Drawer opened");
                ActionBar actionBar = getSupportActionBar();
                String title = actionBar.getTitle().toString();
                Log.d("GTGTGT", title);
                drawerIsOpen = true;

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                Log.d("GTGTGT", "Drawer closed");
                drawerIsOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

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
        navController.navigate(R.id.routesFragment);
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
    public void openRoute(String routeId) {
        //Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        //intent.putExtra("routeId", routeId);
        //MainActivity.this.startActivity(intent);
        //MainActivity.this.finish();

        Bundle bundle = new Bundle();
        bundle.putSerializable("routeId", routeId);
        navController.navigate(R.id.mapsActivity, bundle);
    }
  
    @Override
    public void newRoute() {
        navController.navigate(R.id.startRunFragment);
        drawerLayout.closeDrawer(GravityCompat.START);
    }
  
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.d("FRFRFR", menuItem.toString());
        switch (menuItem.getItemId()) {

            case R.id.drawer_myprofile: {
                navController.navigate(R.id.myProfileFragment);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            }

            case R.id.drawer_new_run: {
                if(checkLocationPermission()&&checkBTPermission()){
                    navController.navigate(R.id.startRunFragment);
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
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


    //Called when user clicks recycleview item
    @Override
    public void itemClicked(User user) {
        closeKeyboard();
        Bundle bundle = new Bundle();
        bundle.putSerializable("name", user);
        navController.navigate(R.id.profileFragment, bundle);
    }

    private void closeKeyboard()
    {
        try
        {
            View view = getCurrentFocus();
            if (view != null)
            {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        catch (Exception e)
        {

        }

    }

    public void closeDrawer()
    {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        closeKeyboard();
        if (drawerIsOpen)
        {
            closeDrawer();
            Log.d("VRVRVR", item.toString());
            return true;

        }
        else
        {
            return super.onOptionsItemSelected(item);
        }

    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    public boolean checkBTPermission(){
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 2);
            return false;
        }
        else{
            return true;
        }
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

        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Log.d("HYHYHY","Location permission ok");
            }
        }
        else if (requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                Log.d("HYHYHY","Bluetooth permission ok");
            }
        }

        else if (requestCode == 3)
        {
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
}
