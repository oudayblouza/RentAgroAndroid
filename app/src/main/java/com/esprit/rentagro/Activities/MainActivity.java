package com.esprit.rentagro.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.esprit.rentagro.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Entities.Users;
import Util.AppSingleton;
import Util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by xmuSistone on 2016/9/18.
 */
public class MainActivity extends FragmentActivity {

    String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
    private static final String TAG = MainActivity.class.getSimpleName();
    ResideMenu resideMenu;
    private static final String URL = Constants.ROOT_URL + "rentagro/User/user_by_role.php?role=Professional";
    @BindView(R.id.icdot)
    View icdot;
    private TextView indicatorTv;
    private View positionView;
    private ViewPager viewPager;
    private List<Users> users = new ArrayList<>();
    private List<CommonFragment> fragments = new ArrayList<>();
    SweetAlertDialog pDialog, alertdialog;

    MaterialMenu materialMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);



        icdot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle your drawable state here

                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });


        positionView = findViewById(R.id.position_view);
        dealStatusBar();

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

        // create menu items;
        String titles[] = {"Home", "My Adverts", "Shops", "Settings"};
        int icon[] = {R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon2, R.drawable.icon_settings};

        for (int i = 0; i < titles.length; i++) {
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);

            resideMenu.addMenuItem(item, ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }
        resideMenu.getMenuItems().get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getApplicationContext(), test.class);
                i1.putExtra("fragment", "mesannonces");
                startActivity(i1);
                // getFragmentManager().beginTransaction().replace(R.id.container, new MesAnnoncesFragment()).addToBackStack(null).commit();
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


        resideMenu.getMenuItems().get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(getApplicationContext(), test.class);
                i1.putExtra("fragment", "categorie");
                startActivity(i1);
                // getFragmentManager().beginTransaction().replace(R.id.container, new MesAnnoncesFragment()).addToBackStack(null).commit();
                resideMenu.closeMenu();
            }

        });
        resideMenu.getMenuItems().get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i1);
            }

        });

        getPremiums();


    }

    /**
     * 填充ViewPager
     */
    private void fillViewPager(final List<Users> users1) {
        indicatorTv = (TextView) findViewById(R.id.indicator_tv);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // 1. viewPager添加parallax效果，使用PageTransformer就足够了
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));

        // 2. viewPager添加adapter
        for (int i = 0; i < users1.size(); i++) {
            // 预先准备10个fragment
            fragments.add(new CommonFragment());
        }

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                CommonFragment fragment = fragments.get(position % users1.size());

                fragment.id = users1.get(position).getId();
                fragment.add1 = users1.get(position).getUsername();

                fragment.add3 = users1.get(position).getPrenom();


                Address adresse = getAddress(users1.get(position).getLatitude(), users1.get(position).getLongitude());
                if (adresse != null) {
                    fragment.add4 = adresse.getLocality() + "," + adresse.getCountryName();
                }
                fragment.add5 = "Tel : " + users1.get(position).getNumtel();

                fragment.tel = users1.get(position).getNumtel();
                fragment.longi = users1.get(position).getLongitude();
                fragment.lat = users1.get(position).getLatitude();
                fragment.rate = users1.get(position).getRate();
                fragment.voters = users1.get(position).getVoters();

                String username = users1.get(position).getUsername().replaceAll(" ", "%20");
                fragment.bindData(Constants.ROOT_URL + "rentagro/images/premium/" + username + "/" + username + ".jpeg");
                return fragment;
            }

            @Override
            public int getCount() {
                return users1.size();
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicatorTv();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateIndicatorTv();
        pDialog.cancel();
    }


    private void updateIndicatorTv() {
        int totalNum = viewPager.getAdapter().getCount();
        int currentItem = viewPager.getCurrentItem() + 1;
        indicatorTv.setText(Html.fromHtml("<font color='#12edf0'>" + currentItem + "</font>  /  " + totalNum));
    }


    private void dealStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = getStatusBarHeight();
            ViewGroup.LayoutParams lp = positionView.getLayoutParams();
            lp.height = statusBarHeight;
            positionView.setLayoutParams(lp);
        }
    }

    private int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    @SuppressWarnings("deprecation")
    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(480, 800)

                .threadPoolSize(3)

                .threadPriority(Thread.NORM_PRIORITY - 1)

                .tasksProcessingOrder(QueueProcessingType.FIFO)

                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs().build();


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    public void getPremiums() {
        initImageLoader();
        JsonObjectRequest request = new JsonObjectRequest(URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch Entreprises! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {

                            JSONArray resp = response.getJSONArray("users");

                            List<Users> items = new Gson().fromJson(resp.toString(), new TypeToken<List<Users>>() {
                            }.getType());


                            users.clear();
                            users.addAll(items);


                            fillViewPager(users);

                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (pDialog.isShowing()) {
                    pDialog.cancel();
                }
                Alert();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request, REQUEST_TAG);

    }

    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses == null) {


                return null;

            }
            return (Address) addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void Refresh(View v) {

        getPremiums();
    }


    public void Alert() {
        alertdialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        alertdialog.setTitleText("Oops...");
        alertdialog.setContentText("Verify your Internet Connection!");
        alertdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        alertdialog.show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

}
