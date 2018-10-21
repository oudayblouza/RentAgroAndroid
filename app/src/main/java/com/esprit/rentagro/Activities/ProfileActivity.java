package com.esprit.rentagro.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.esprit.rentagro.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Entities.Delegation;
import Entities.Ville;
import Util.AppSingleton;
import Util.BCrypt;
import Util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.tvNumber1)
    TextView tvNumber1;
    @BindView(R.id.tvNumber3)
    TextView tvNumber3;
    @BindView(R.id.tvNumber5)
    TextView tvNumber5;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.UndertvN1)
    TextView UndertvN1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.userphoto)
    ImageView userphoto;
    @BindView(R.id.logout)
    Button logout;
    SharedPreferences sharedPreferences;
    @BindView(R.id.love_music)
    TextView loveMusic;
    String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";


    ResideMenu resideMenu;
    MaterialMenu materialMenu;
    double lat;
    double longi;
    String add;
    @BindView(R.id.addressV)
    RelativeLayout addressV;
    SweetAlertDialog addressAlert;
    SweetAlertDialog NumAlert;
    SweetAlertDialog PassAlert;
    SweetAlertDialog pDialog;
    @BindView(R.id.NumRL)
    RelativeLayout NumRL;
    @BindView(R.id.passworBtn)
    Button passworBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("userpref", ProfileActivity.this.MODE_PRIVATE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rateme(sharedPreferences.getFloat("rate", 0) / sharedPreferences.getInt("voters", 0), sharedPreferences.getInt("voters", 0));
            }
        });


        tvNumber1.setText(sharedPreferences.getInt("numtel", 0) + "");
        UndertvN1.setText(sharedPreferences.getString("prenom", "none"));
        String username = sharedPreferences.getString("username", "none").replaceAll(" ", "%20");
        System.out.println(Constants.ROOT_URL + "/rentagro/images/premium/" + username + "/" + username + ".jpeg");

        Picasso.with(getApplicationContext()).load(Constants.ROOT_URL + "/rentagro/images/premium/" + username + "/" + username + ".jpeg")
                .error(R.drawable.image_error)
                .placeholder(R.drawable.placeholder)
                .into(userphoto);
        //  toolbar.setTitle(sharedPreferences.getString("username", "none"));
        tvNumber3.setText(sharedPreferences.getString("email", "none"));
        loveMusic.setText(sharedPreferences.getString("username", "none"));

        longi = Double.longBitsToDouble(sharedPreferences.getLong("longitude", Double.doubleToLongBits(0)));
        lat = Double.longBitsToDouble(sharedPreferences.getLong("latitude", Double.doubleToLongBits(0)));
        if(longi!=0 && lat != 0) {
            Address address = getAddress(lat, longi);
            if (address != null) {
                add = address.getAddressLine(0);
                if (address.getAddressLine(1) != null) {
                    add = add + "\n" + address.getAddressLine(1);
                }
                if (address.getAddressLine(2) != null) {
                    add = add + "\n" + address.getAddressLine(2);
                }
                tvNumber5.setText(add);
            }
        }else {
            int del_id = sharedPreferences.getInt("delegation", 1);
            FindDelby(del_id);
        }

        addressV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAdress(add);
            }
        });

        NumRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNum();
            }
        });

        passworBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPassword();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences("userpref", ProfileActivity.this.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i1 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i1);
            }
        });


        setSupportActionBar(toolbar);
        this.setTitle("Profile");
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // Handle your drawable state here

                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationIcon((Drawable) materialMenu);
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
                Intent i1 = new Intent(getApplicationContext(), test.class);
                i1.putExtra("fragment", "mesannonces");
                startActivity(i1);
                // getFragmentManager().beginTransaction().replace(R.id.container, new MesAnnoncesFragment()).addToBackStack(null).commit();
                resideMenu.closeMenu();
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
        initCollapsingToolbar();
    }


    public void Rateme(float rate, int voters) {
        final RatingBar rate1 = new RatingBar(this);
        rate1.setRating(rate);
        rate1.setIsIndicator(true);
        TextView voter = new TextView(this);
        voter.setText(voters + " voters");

        voter.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(voter);
        ll.addView(rate1);


        SweetAlertDialog rateAlert = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("My Rate")
                .setConfirmText("Ok")
                .setCustomView(ll);

        rateAlert.show();


    }


    public boolean validate(TextView tvnum, TextView tvresp) {
        boolean valid = true;

        String num = tvnum.getText().toString();
        String responsable = tvresp.getText().toString();

        if (num.length() != 8) {
            tvnum.setError("Tel num must be 8 numbers");
            Toast.makeText(getApplicationContext(), "Tel num must be 8 numbers", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            tvnum.setError(null);
        }

        if (responsable.isEmpty() || responsable.length() < 2 || responsable.length() > 20) {
            tvresp.setError("enter a valid name");
            Toast.makeText(getApplicationContext(), "enter a valid name", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            tvresp.setError(null);
        }

        return valid;
    }

    public void setNum() {

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);


        TextView tvNum = new TextView(this);
        tvNum.setText("Tel Number :");

        tvNum.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        ll.addView(tvNum);

        final EditText edNum = new EditText(this);
        edNum.setText(tvNumber1.getText());
        edNum.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        edNum.setInputType(InputType.TYPE_CLASS_NUMBER);
        ll.addView(edNum);

        TextView tvResp = new TextView(this);
        tvResp.setText("Person In Charge :");
        tvResp.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        ll.addView(tvResp);

        final EditText edResp = new EditText(this);
        edResp.setText(UndertvN1.getText());
        edResp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ll.addView(edResp);


        NumAlert = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Update ")
                .setConfirmText("Ok")
                .setCancelText("Cancel")
                .setCustomView(ll);
        NumAlert.showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });
        NumAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                if (validate(edNum, edResp)) {
                    updatenumresp(Integer.parseInt(edNum.getText().toString()), edResp.getText().toString());
                }
            }
        });

        NumAlert.show();
    }

    String password;

    public void setPassword() {

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);


        final TextView tvopass = new TextView(this);
        tvopass.setText("Current Password :");

        tvopass.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        ll.addView(tvopass);

        final EditText edopass = new EditText(this);
        edopass.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        edopass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(edopass);

        final TextView tvnewpass1 = new TextView(this);
        tvnewpass1.setText("Type new Password :");
        tvnewpass1.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        ll.addView(tvnewpass1);

        final EditText ednewpass1 = new EditText(this);
        ednewpass1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ednewpass1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(ednewpass1);

        final TextView tvnewpass2 = new TextView(this);
        tvnewpass2.setText("Retype new Password :");
        tvnewpass2.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        ll.addView(tvnewpass2);

        final EditText ednewpass2 = new EditText(this);
        ednewpass2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ednewpass2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(ednewpass2);

        PassAlert = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Update ")
                .setConfirmText("Ok")
                .setCancelText("Cancel")
                .setCustomView(ll);
        PassAlert.showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });
        PassAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                if (edopass.getText().toString().equals(sharedPreferences.getString("password", "none"))) {


                    if (ednewpass1.getText().toString().length() > 4) {


                        if (ednewpass1.getText().toString().equals(ednewpass2.getText().toString())) {

                            password = ednewpass1.getText().toString();
                            PassAlert.dismissWithAnimation();
                            AlertLoading();
                            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(13));


                            updatepass(hashedPassword);
                        } else {
                            PassAlert.dismissWithAnimation();
                            Alert("Nope", "New Passwords don't match");
                        }
                    } else {
                        PassAlert.dismissWithAnimation();
                        Alert("Nope", "Password must have at least 4 charachters");
                    }


                } else {
                    PassAlert.dismissWithAnimation();
                    Alert("Nope", "Wrong Password !");
                }
            }
        });

        PassAlert.show();
    }


    public void SetAdress(String address) {


        final EditText adresse = new EditText(this);

        adresse.setClickable(false);
        adresse.setCursorVisible(false);
        adresse.setFocusable(false);
        adresse.setFocusableInTouchMode(false);

        adresse.setText(address);

        adresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLocation();

                adresse.setText(add);
            }
        });


        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(adresse);


        addressAlert = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Click to change Adress")
                .setConfirmText("Ok")
                .setCancelText("Cancel")
                .setCustomView(ll);
        addressAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                updateAdresse(longi, lat);
            }
        });
        addressAlert.showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });

        addressAlert.show();

    }


    public void updatenumresp(int num, String respo) {
        System.out.println(respo);
        respo = respo.replaceAll(" ", "%20");
        respo = respo.replaceAll("'", "%20");
        String url = Constants.ROOT_URL + "/rentagro/User/Update_users_numresp.php?num=" + num + "&resp=" + respo + "&id=" + sharedPreferences.getInt("id", 0);
        System.out.println(url);
        final int x = num;
        final String z = respo.replaceAll("%20", " ");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resp = response.toString();
                System.out.println("ok fine here");
                if (resp.equals("success")) {
                    NumAlert.dismissWithAnimation();
                    tvNumber1.setText(x + "");
                    UndertvN1.setText(z);


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("numtel", x);
                    editor.putString("prenom", z);
                    editor.commit();

                    Success();


                } else {
                    NumAlert.dismissWithAnimation();
                    Alert1();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NumAlert.dismissWithAnimation();
                Alert1();
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);


    }

    public void updatepass(String pass) {
        System.out.println(pass);

        String url = Constants.ROOT_URL + "/rentagro/User/Update_users_pass.php?pass=" + pass + "&id=" + sharedPreferences.getInt("id", 0);
        System.out.println(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resp = response.toString();
                System.out.println("ok fine here");
                if (resp.equals("success")) {
                    pDialog.dismiss();
                 //   PassAlert.dismissWithAnimation();


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("password", password);
                    editor.commit();

                    Success();


                } else {
                    pDialog.dismiss();
                 //   PassAlert.dismissWithAnimation();
                    Alert1();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
              //  PassAlert.dismissWithAnimation();
                Alert1();
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);


    }

    public void updateAdresse(Double longitude, Double latitude) {
        String url = Constants.ROOT_URL + "/rentagro/User/Update_users_adress.php?longitude=" + longitude + "&latitude=" + latitude + "&id=" + sharedPreferences.getInt("id", 0);
        System.out.println(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resp = response.toString();
                System.out.println("ok fine here");
                if (resp.equals("success")) {
                    addressAlert.dismissWithAnimation();
                    tvNumber5.setText(add);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putLong("longitude", Double.doubleToRawLongBits(longi));
                    editor.putLong("latitude", Double.doubleToRawLongBits(lat));
                    editor.commit();
                    Success();


                } else {
                    addressAlert.dismissWithAnimation();
                    Alert1();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                addressAlert.dismissWithAnimation();
                Alert1();
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REQUEST_TAG);


    }


    GoogleApiClient mGoogleApiClient;


    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            System.out.println(addresses);
            return (Address) addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getLocation() {
        System.out.println("test location ");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private static final String TAG = "ProfileActivity";

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        System.out.println("test location 0");
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    SweetAlertDialog alertdialog;

    public void Alert(String title, String text) {

        alertdialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        alertdialog.setTitleText(title);
        alertdialog.setContentText(text);
        alertdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        alertdialog.show();
    }

    @Override
    public void onConnected(Bundle arg0) {

        LocationRequest mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("no permission papa");


            Alert("Location Permission", "You must active location on your phone");

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {

            Alert("Location Permission", "cannot locate you");
            return;

        }
        Address address = getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());


        add = address.getAddressLine(0);
        if (address.getAddressLine(1) != null) {
            add = add + "\n" + address.getAddressLine(1);
        }
        if (address.getAddressLine(2) != null) {
            add = add + "\n" + address.getAddressLine(2);
        }

        lat = mLastLocation.getLatitude();
        longi = mLastLocation.getLongitude();


    }

    @Override
    public void onConnectionSuspended(int arg0) {
        System.out.println("test location 2");
        mGoogleApiClient.connect();
    }


    public void Success() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Advert Updated with Success!")
                .show();
    }

    public void Alert1() {
        alertdialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        alertdialog.setTitleText("Oops...");
        alertdialog.setContentText("Something went wrong!");
        alertdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        alertdialog.show();
    }

    public void AlertLoading() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.setExpanded(true);
        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {

                    collapsingToolbar.setTitle(sharedPreferences.getString("username","none"));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
    public void FindDelby(int id) {
        String url = Constants.ROOT_URL + "rentagro/Delegation/select_delegation_ById.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        List<String> lstnomdelegation = new ArrayList<>();
                        try {

                            Delegation delegation = new Delegation();
                            JSONObject v = (JSONObject) response.getJSONObject("delegation");
                            delegation.setId(v.getInt("id"));
                            delegation.setNom(v.getString("nom"));
                            Ville vl = new Ville();
                            vl.setId(v.getInt("ville"));
                            delegation.setVille(vl);

                            FindVilby(vl.getId(), delegation);


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }


    public void FindVilby(int id, final Delegation delegation) {
        String url = Constants.ROOT_URL + "rentagro/ville/select_ville_Byid.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        List<String> lstnomdelegation = new ArrayList<>();
                        try {

                            Ville ville = new Ville();
                            JSONObject v = (JSONObject) response.getJSONObject("ville");
                            ville.setId(v.getInt("id"));
                            ville.setNom(v.getString("nom"));

                            tvNumber5.setText(delegation.getNom() + "-" + ville.getNom());

                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }
}
