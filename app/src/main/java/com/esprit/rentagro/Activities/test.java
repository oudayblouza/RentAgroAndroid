package com.esprit.rentagro.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.esprit.rentagro.Activities.Fragments.CategorieFragment;
import com.esprit.rentagro.Activities.Fragments.MesAnnoncesFragment;
import com.esprit.rentagro.R;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class test extends AppCompatActivity  {
    SharedPreferences sharedpreferences;
    String requiredPermission1 = "android.permission.READ_EXTERNAL_STORAGE";
    String requiredPermission2 = "android.permission.WRITE_EXTERNAL_STORAGE";
    String requiredPermission3 = "android.permission.CAMERA";
    String requiredPermission4 = "android.permission.ACCESS_FINE_LOCATION";
ResideMenu resideMenu;
MaterialMenu materialMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

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
                int checkVal1 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission1);
                int checkVal2 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission2);
                int checkVal3 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission3);
                int checkVal4 = getApplicationContext().checkCallingOrSelfPermission(requiredPermission4);
                if (checkVal1 != PackageManager.PERMISSION_GRANTED || checkVal2 != PackageManager.PERMISSION_GRANTED || checkVal3 != PackageManager.PERMISSION_GRANTED || checkVal4 != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(test.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA ,Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    Toast.makeText(getApplicationContext(), "Please check all Permissions !", Toast.LENGTH_LONG).show();

                }else {
                    Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                    startActivity(intent);
                }


            }
        });


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Handle your drawable state here

                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon((Drawable) materialMenu);
      /*
        resideMenu.setMenuListener(new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
                materialMenu.setIconState(MaterialMenuDrawable.IconState.ARROW);
            }

            @Override
            public void closeMenu() {
                materialMenu.setIconState(MaterialMenuDrawable.IconState.BURGER);
            }
        });
        */


        setTitle("Category Advert");

        TextView tv = (TextView) findViewById(R.id.love_music) ;
        tv.setTextSize(25);
        tv.setText("Select Category");
        tv.setBackgroundColor(Color.TRANSPARENT);

        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(false);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.toolbar);


        String fragment = "categorie";

        Bundle b = getIntent().getExtras();

        if(b !=null)
         fragment = b.getString("fragment");

        if(fragment.equals("mesannonces")) {
            getFragmentManager().beginTransaction().replace(R.id.container, new MesAnnoncesFragment()).addToBackStack(null).commit();
        }else if(fragment.equals("categorie"))
            getFragmentManager().beginTransaction().replace(R.id.container, new CategorieFragment()).addToBackStack(null).commit();


        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        // create menu items;
        String titles[] = { "Home", "My Adverts", "Shops", "Settings" };
        int icon[] = { R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon2, R.drawable.icon_settings };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);

            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
        resideMenu.getMenuItems().get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new MesAnnoncesFragment()).addToBackStack(null).commit();
                resideMenu.closeMenu();
            }

        });
        resideMenu.getMenuItems().get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.container, new CategorieFragment()).addToBackStack(null).commit();
                resideMenu.closeMenu();
            }

        });
        resideMenu.getMenuItems().get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i1);
            }

        });
        resideMenu.getMenuItems().get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i1);
            }

        });

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }
}
