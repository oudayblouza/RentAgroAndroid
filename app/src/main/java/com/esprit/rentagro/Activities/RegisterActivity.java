package com.esprit.rentagro.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.esprit.rentagro.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.PhotoLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Entities.Delegation;
import Entities.Users;
import Entities.Ville;
import Util.AppSingleton;
import Util.BCrypt;
import Util.Constants;
import Util.MyCommand;
import Util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {


    @BindView(R.id.lout)
    LinearLayout lout;
    SweetAlertDialog pDialog, alertdialog;
    List<Ville> lstVille = new ArrayList();
    List<Delegation> lstdel = new ArrayList();
    @BindView(R.id.btn_signup)
    AppCompatButton btnSignup;
    @BindView(R.id.link_login)
    TextView linkLogin;
    @BindView(R.id.input_username)
    EditText inputUsername;
    @BindView(R.id.input_email)
    EditText inputEmail;
    @BindView(R.id.input_prenom)
    EditText inputPrenom;

    @BindView(R.id.input_ville)
    Spinner inputVille;
    @BindView(R.id.input_del)
    Spinner inputDel;
    @BindView(R.id.input_password)
    EditText inputPassword;
    @BindView(R.id.input_password1)
    EditText inputPassword1;
    @BindView(R.id.input_tenum)
    EditText inputTenum;
    private ViewGroup mSelectedImagesContainer;
    private static final String TAG = "RegisterActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    ArrayList<Uri> image_uris = new ArrayList<Uri>();

    Double lat = 0.0;
    Double longi = 0.0;
    @BindView(R.id.input_adresse)
    EditText inputAdresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        fillVilles();
        rq = Volley.newRequestQueue(this);
        inputAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertLoading();
                getLocation();
            }
        });
        inputVille.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = inputVille.getSelectedItemPosition();
                fillDelegation(lstVille.get(index).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                fillDelegation(lstVille.get(0).getId());
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    AlertLoading();
                    verifyusername();
                }
            }
        });


        mSelectedImagesContainer = (ViewGroup) findViewById(R.id.selected_photos_container);


        View getImages2 = findViewById(R.id.get_images2);
        getImages2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Config config = new Config();
                config.setCameraHeight(R.dimen.app_camera_height);
                config.setToolbarTitleRes(R.string.custom_title);
                config.setSelectionMin(0);
                config.setSelectionLimit(1);
                config.setSelectedBottomHeight(R.dimen.bottom_height);
                config.setFlashOn(false);


                getImages(config);
            }
        });


    }

    String resp = "";
    RequestQueue rq;
    public void addUser(Users user) {

        String url = Constants.ROOT_URL + "/rentagro/User/ajouterUser.php?username="+user.getUsername()+"&email="+user.getEmail()+"&password="+user.getPassword()+"&roles="+user.getRoles()+"&delegation_id="+user.getDelegation().getId()+"&longitude="+user.getLongitude()+"&latitude="+user.getLatitude()+"&numtel="+user.getNumtel()+"&prenom="+user.getPrenom()+"&rate="+user.getRate()+"&voters="+user.getVoters();
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                resp = response.toString();
                if(resp.equals("success")){
                    pDialog.dismissWithAnimation();
                     Success();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

                else{
                    pDialog.dismissWithAnimation();

                    Alert("Error", "Failed to add Advert");
                    System.out.println(resp+"test herer");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();

                Alert("Error", "Failed to add Advert");
            }
        });

        rq.add(stringRequest);


    }

    private void verifyusername() {
        System.out.println("he is here");
        final String lusername = inputUsername.getText().toString().replaceAll(" ", "%20");
        String url = Constants.ROOT_URL + "/rentagro/User/find_by_Username.php?username=" + lusername;
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (response.toString().equals("success")) {
                        pDialog.dismissWithAnimation();
                        Alert("Oups", "Username already exist");


                    } else {
                        System.out.println("erreur ici");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(lusername);
                                verifyemail();

                            }
                        });
                    }
                } catch (Exception e1) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismissWithAnimation();
                            // Toast.makeText(getApplicationContext(), "Login ou password Incorrect", Toast.LENGTH_LONG).show();
                            Alert("Oups", "Username already exist");
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                //  Toast.makeText(getApplicationContext(),"Erreur Connection",Toast.LENGTH_LONG).show();
                Alert("Error", " Connection Error");
                error.printStackTrace();
            }
        });


        rq.add(stringRequest);


    }

    private void verifyemail() {
        System.out.println("he is here");
        String url = Constants.ROOT_URL + "/rentagro/User/find_by_Email.php?email=" + inputEmail.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {


                    if (response.toString().equals("success")) {

                        pDialog.dismissWithAnimation();
                        Alert("Oups", "Email already exist");


                    } else {
                        System.out.println("erreur ici");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                PrepareForPush();
                            }
                        });
                    }
                } catch (Exception e1) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismissWithAnimation();
                            // Toast.makeText(getApplicationContext(), "Login ou password Incorrect", Toast.LENGTH_LONG).show();
                            Alert("Oups", "Email already exist");
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                //  Toast.makeText(getApplicationContext(),"Erreur Connection",Toast.LENGTH_LONG).show();
                Alert("Error", " Connection Error");
                error.printStackTrace();
            }
        });


        rq.add(stringRequest);


    }

    public void PrepareForPush() {
        Users user = new Users();
        user.setUsername(inputUsername.getText().toString().replaceAll(" ", "%20"));
        user.setUsername_canonical(inputUsername.getText().toString().replaceAll(" ", "%20"));
        user.setEnabled(1);


        user.setRoles("Amateur");
        user.setEmail(inputEmail.getText().toString());
        user.setPrenom(inputPrenom.getText().toString().replaceAll(" ", "%20"));
        user.setNumtel(Integer.parseInt(inputTenum.getText().toString()));
        user.setEmail_canonical(inputEmail.getText().toString());


        int index1 = inputDel.getSelectedItemPosition();
        Delegation delegation = new Delegation();
        delegation = lstdel.get(index1);
        user.setDelegation(delegation);
        user.setLatitude(lat);
        user.setLongitude(longi);

        user.setRate(0);
        user.setVoters(0);

        String password = inputPassword.getText().toString();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(13));
        user.setPassword(hashedPassword);

        System.out.println(user);
        uploadImage(user);

    }



    public boolean validate() {
        boolean valid = true;


        String prenom = inputPrenom.getText().toString();
        String numtel = inputTenum.getText().toString();

        String password = inputPassword.getText().toString();
        String password1 = inputPassword1.getText().toString();


        String username = inputUsername.getText().toString();
        String email = inputEmail.getText().toString();

        if (username.isEmpty() || username.length() > 20) {
            inputUsername.setError("Enter a Title under 20 char");
            valid = false;
        } else {
            inputUsername.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }


        if (prenom.isEmpty() || prenom.length() > 20) {
            inputPrenom.setError("Enter a Title under 20 char");
            valid = false;
        } else {
            inputPrenom.setError(null);
        }

        if (numtel.isEmpty() || numtel.length() > 8) {
            System.out.println("zebbi non");
            inputTenum.setError("Enter a valid Tel number (without +216)");
            valid = false;
        } else {
            inputTenum.setError(null);
        }

        if (password.isEmpty()) {
            inputPassword.setError("must choose password");
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        if (password1.isEmpty()) {
            inputPassword.setError("must type password");
            valid = false;
        } else if (!password1.equals(password)) {
            inputPassword.setError("passwords mismatch");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }


    public void fillDelegation(final int idville) {
        AlertLoading();
        String url = Constants.ROOT_URL + "rentagro/Delegation/select_Del.php?ville=" + idville;
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<String> lstnomdelegation = new ArrayList<>();
                        try {
                            lstdel.clear();
                            for (int i = 0; i < response.getJSONArray("delegation").length(); i++) {
                                Delegation delegation = new Delegation();
                                JSONObject v = (JSONObject) response.getJSONArray("delegation").get(i);
                                delegation.setId(v.getInt("id"));
                                delegation.setNom(v.getString("nom"));
                                Ville vl = new Ville();
                                vl.setId(idville);
                                delegation.setVille(vl);
                                lstdel.add(delegation);
                                lstnomdelegation.add(delegation.getNom());


                            }
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, lstnomdelegation);
                            adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            inputDel.setAdapter(adapter2);
                            pDialog.dismissWithAnimation();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismissWithAnimation();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                Alert("Error", "Something went wrong!");
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }

    public void fillVilles() {

        AlertLoading();

        String url = Constants.ROOT_URL + "rentagro/ville/select.php";
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        List<String> lstnomville = new ArrayList<>();
                        try {

                            for (int i = 0; i < response.getJSONArray("ville").length(); i++) {
                                Ville ville = new Ville();
                                JSONObject v = (JSONObject) response.getJSONArray("ville").get(i);
                                ville.setId(v.getInt("id"));
                                ville.setNom(v.getString("nom"));
                                lstVille.add(ville);

                                lstnomville.add(ville.getNom());


                            }
                            System.out.println(lstnomville);
                            System.out.println(lstVille);
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, lstnomville);
                            adapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            inputVille.setAdapter(adapter1);
                            pDialog.dismissWithAnimation();

                        } catch (JSONException e) {
                            pDialog.dismissWithAnimation();
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                Alert("Error", "Something went wrong!");
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);


    }
    public void Success() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Advert Added with Success!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        RegisterActivity.super.onBackPressed();
                    }
                })
                .show();
    }

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

    public void AlertLoading() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void AlertLoading2() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Uploading Images ...");
        pDialog.setCancelable(false);
        pDialog.show();
    }




    void uploadImage(Users user) {
        final Users user1 = user;
        if (image_uris.isEmpty()) {
            Alert("Oups", "Please Upload Images");
            pDialog.dismissWithAnimation();
            return;
        }
        final MyCommand myCommand = new MyCommand(getApplicationContext());
        AlertLoading2();



            try {
                Bitmap bitmap = PhotoLoader.init().from(image_uris.get(0).getPath()).requestSize(512, 512).getBitmap();

                final String encodedString = ImageBase64.encode(bitmap);

                String url = Utils.urlUploadProfil + "?&directory=" + user1.getUsername();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                              addUser(user1);



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        addUser(user1);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("image", encodedString);
                        return params;
                    }
                };

                myCommand.add(stringRequest);

            } catch (FileNotFoundException e) {
                pDialog.dismissWithAnimation();
                Alert("Error", "Error Uploading Images");
                return;
            }



        myCommand.execute();


    }









    private void getImages(Config config) {


        ImagePickerActivity.setConfig(config);

        Intent intent = new Intent(this, ImagePickerActivity.class);

        if (image_uris != null) {
            intent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, image_uris);
        }


        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {

                image_uris = intent.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (image_uris != null) {
                    showMedia();
                }


            }
        }
    }


    private void showMedia() {

        mSelectedImagesContainer.removeAllViews();
        if (image_uris.size() >= 1) {
            mSelectedImagesContainer.setVisibility(View.VISIBLE);
        }

        int wdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int htpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        for (Uri uri : image_uris) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = (ImageView) imageHolder.findViewById(R.id.media_image);

            Glide.with(this)
                    .load(uri.toString())
                    .fitCenter()
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(wdpx, htpx));


        }

    }




    GoogleApiClient mGoogleApiClient;

    public void getLocation() {
        System.out.println("test location ");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        System.out.println("test location 0");
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        LocationRequest mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("no permission papa");
            pDialog.dismissWithAnimation();
            lat = Double.valueOf(0);
            longi = Double.valueOf(0);

            Alert("Location Permission", "You must active location on your phone");

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {
            lat = Double.valueOf(0);
            longi = Double.valueOf(0);
            pDialog.dismissWithAnimation();
            Alert("Activate Location", "cannot locate you");
            return;

        }
        System.out.println(mLastLocation.getLatitude()+"test");
        Address address = getAddress(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        inputAdresse.setText("");
        String add = address.getAddressLine(0);
        if (address.getAddressLine(1) != null) {
            add = add + "\n" + address.getAddressLine(1);
        }
        if (address.getAddressLine(2) != null) {
            add = add + "\n" + address.getAddressLine(2);
        }
        inputAdresse.setText(add);
        lat = mLastLocation.getLatitude();
        longi = mLastLocation.getLongitude();
        pDialog.dismissWithAnimation();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        System.out.println("test location 2");
        mGoogleApiClient.connect();
    }


    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return (Address) addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
