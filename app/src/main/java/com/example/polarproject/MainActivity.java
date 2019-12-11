package com.example.polarproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.example.polarproject.Adapters.RecyclerViewAdapter;
import com.example.polarproject.Classes.HerokuDataBase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements TestFragment.OnFragmentInteractionListener, TestFragment2.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, MyProfileFragment.OnFragmentInteractionListener, CreateMapFragment.OnFragmentInteractionListener, RoutesFragment.OnFragmentInteractionListener, StartRunFragment.OnFragmentInteractionListener, SearchUsersFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, RecyclerViewAdapter.ListenerInterface {

    private NavController navController;
    private DrawerLayout drawerLayout;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        drawerLayout = findViewById(R.id.drawer_layout);


        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.startRunFragment, R.id.myProfileFragment, R.id.routesFragment, R.id.createMapFragment, R.id.searchUsersFragment)
                        .setDrawerLayout(drawerLayout)
                        .build();

        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navView.setNavigationItemSelectedListener(this);

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
    public void itemClicked(String name) {
        Log.d("WWWW", name + " Item Clicked!");
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        navController.navigate(R.id.profileFragment, bundle);
    }

}
