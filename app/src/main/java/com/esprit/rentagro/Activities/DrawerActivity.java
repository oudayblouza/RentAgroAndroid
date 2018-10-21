package com.esprit.rentagro.Activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.esprit.rentagro.Activities.Fragments.CategorieFragment;
import com.esprit.rentagro.Activities.Fragments.MesAnnoncesFragment;
import com.esprit.rentagro.R;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static FragmentManager app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
   /*     super.onCreate(savedInstanceState);
        app = getFragmentManager();
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);


        FloatingActionButton fcontact = (FloatingActionButton) findViewById(R.id.fcontact);
        fcontact.setVisibility(View.GONE);


        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fill the form to add Advert", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setTitle("Category Advert");

        TextView tv = (TextView) findViewById(R.id.love_music) ;
        tv.setTextSize(25);
        tv.setText("Select Category");
        tv.setBackgroundColor(Color.TRANSPARENT);

        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(false);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.toolbar);




        getFragmentManager().beginTransaction().replace(R.id.container, new CategorieFragment()).addToBackStack(null).commit();


        */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            getFragmentManager().beginTransaction().replace(R.id.container, new CategorieFragment()).addToBackStack(null).commit();

        } else if (id == R.id.nav_gallery) {
            getFragmentManager().beginTransaction().replace(R.id.container, new MesAnnoncesFragment()).addToBackStack(null).commit();

        } else if (id == R.id.nav_slideshow) {
            Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i1);

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





}
