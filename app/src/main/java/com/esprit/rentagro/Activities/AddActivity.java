package com.esprit.rentagro.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Entities.Annonces;
import Entities.Categorie;
import Entities.Delegation;
import Entities.Type;
import Entities.Users;
import Entities.Ville;
import Util.AppSingleton;
import Util.Constants;
import Util.MyCommand;
import Util.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class AddActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    @BindView(R.id.input_title)
    EditText inputTitle;
    @BindView(R.id.input_price)
    EditText inputPrice;
    @BindView(R.id.input_categ)
    Spinner inputCateg;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.input_date)
    EditText inputDate;
    @BindView(R.id.input_ville)
    Spinner inputVille;
    SweetAlertDialog pDialog;
    SweetAlertDialog pDialog1;

    private static final String TAG = "AddActivity";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    //   private static final String TAG = "TedPicker";

    @BindView(R.id.input_duration)
    EditText inputDuration;
    @BindView(R.id.input_tel)
    EditText inputTel;
    @BindView(R.id.input_description)
    EditText inputDescription;
    @BindView(R.id.radiogroupe)
    RadioGroup radiogroupe;
    @BindView(R.id.input_renttype)
    Spinner inputRenttype;
    @BindView(R.id.input_adresse)
    EditText inputAdresse;


    private RadioButton radioButton;
    private ViewGroup mSelectedImagesContainer;

    List<Ville> lstVille = new ArrayList();
    List<Categorie> lstCategorie = new ArrayList();
    List<Delegation> lstdel = new ArrayList();
    List<Type> lsttyp = new ArrayList();

    @BindView(R.id.input_del)
    Spinner inputDel;
    @BindView(R.id.input_type)
    Spinner inputType;
    @BindView(R.id.add)
    Button add;


    private JSONObject jsonObject;
    ArrayList<Uri> image_uris = new ArrayList<Uri>();
    ArrayList<String> images_paths = new ArrayList<String>();

    Double lat;
    Double longi;
    SweetAlertDialog alertdialog;

    int idUser;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getSharedPreferences("userpref", AddActivity.this.MODE_PRIVATE);
        idUser = sharedPreferences.getInt("id", -1);
        username = sharedPreferences.getString("username", "--");

        inputAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btn ok");
                AlertLoading();
                getLocation();
            }
        });


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Rent, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        inputRenttype.setAdapter(adapter);


        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(999);
            }
        });

        rq = Volley.newRequestQueue(this);

        fillVilles();
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


        fillCategories();
        inputCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int index = inputCateg.getSelectedItemPosition();

                fillTypes(lstCategorie.get(index).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                fillDelegation(lstCategorie.get(0).getId());
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    Alert("Correct", "Some Fields are missing!");
                    return;
                }
                UploadAdvert();
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
                config.setSelectionLimit(10);
                config.setSelectedBottomHeight(R.dimen.bottom_height);
                config.setFlashOn(false);


                getImages(config);
            }
        });

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            inputDate.setText(arg0.getYear() + "/" + arg0.getMonth() + "/" + arg0.getDayOfMonth());
        }
    };


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

    public void fillTypes(final int idcateg) {
        AlertLoading1();

        String url = Constants.ROOT_URL + "rentagro/Type/select_Type.php?categorie=" + idcateg;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        List<String> lstnomtypes = new ArrayList<>();
                        try {
                            lsttyp.clear();

                            for (int i = 0; i < response.getJSONArray("type").length(); i++) {
                                Type type = new Type();
                                JSONObject v = (JSONObject) response.getJSONArray("type").get(i);
                                type.setId(v.getInt("id"));
                                type.setNom(v.getString("nom"));

                                Categorie categorie = new Categorie();
                                categorie.setId(idcateg);

                                type.setCategorie(categorie);
                                lsttyp.add(type);
                                lstnomtypes.add(type.getNom());


                            }
                            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, lstnomtypes);
                            adapter3.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            inputType.setAdapter(adapter3);
                            pDialog1.dismissWithAnimation();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog1.dismissWithAnimation();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog1.dismissWithAnimation();
                Alert("Error", "Something went wrong!");
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });

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

    public void fillCategories() {

        AlertLoading1();

        String url = Constants.ROOT_URL + "rentagro/categorie/select.php";
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        List<String> lstnomcategorie = new ArrayList<>();
                        try {

                            for (int i = 0; i < response.getJSONArray("categorie").length(); i++) {
                                Categorie categorie = new Categorie();
                                JSONObject v = (JSONObject) response.getJSONArray("categorie").get(i);
                                categorie.setId(v.getInt("id"));
                                categorie.setNom(v.getString("nom"));
                                lstCategorie.add(categorie);

                                lstnomcategorie.add(categorie.getNom());


                            }

                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, lstnomcategorie);
                            adapter2.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            inputCateg.setAdapter(adapter2);
                            pDialog1.dismissWithAnimation();

                        } catch (JSONException e) {
                            pDialog1.dismissWithAnimation();
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog1.dismissWithAnimation();
                Alert("Error", "Something went wrong!");
                error.printStackTrace();
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);


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


    public void UploadAdvert() {
        Annonces annonce = new Annonces();

        annonce.setTitre(inputTitle.getText().toString());
        annonce.setPrix(Integer.parseInt(inputPrice.getText().toString()));


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(inputDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            Alert("ERROR", "Something went Wrong !");
            return;
        }


        annonce.setLongitude(longi);
        annonce.setLatitude(lat);

        annonce.setEtat(1);
        annonce.setDateDebut(convertedDate);
        annonce.setDuree(Integer.parseInt(inputDuration.getText().toString()));
        annonce.setNumTel(Integer.parseInt(inputTel.getText().toString()));
        annonce.setDescription(inputDescription.getText().toString());
        RadioButton r = (RadioButton) radiogroupe.getChildAt(radiogroupe.getCheckedRadioButtonId());

        annonce.setValidite(1);

        int index = inputType.getSelectedItemPosition();
        Type type = new Type();
        type = lsttyp.get(index);
        System.out.println(type.getId() + "typetese" + inputType.getSelectedItem().toString());
        annonce.setType(type);


        int indexx = inputRenttype.getSelectedItemPosition();

        annonce.setRenttype(inputRenttype.getItemAtPosition(indexx).toString());

        int index1 = inputDel.getSelectedItemPosition();
        Delegation delegation = new Delegation();
        delegation = lstdel.get(index1);
        annonce.setDelegation(delegation);


        Users user = new Users();
        user.setId(idUser);
        user.setUsername(username);
        annonce.setUser(user);

        DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date1 = new Date();
        String usernamePath = user.getUsername().replaceAll(" ", "%20");
        annonce.setPath(usernamePath + dateFormat1.format(date1));
        int selectedId = radiogroupe.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        annonce.setNature(radioButton.getText().toString());

        annonce.setNbrImg(image_uris.size());

        uploadImage(annonce);


    }


    int verif = 0;
    ByteArrayOutputStream bytearrayoutputstream;
    void uploadImage(Annonces annonce) {

        bytearrayoutputstream = new ByteArrayOutputStream();
        final Annonces annonce1 = annonce;
        if (image_uris.isEmpty()) {
            Alert("Oups", "Please Upload Images");
            return;
        }
        final MyCommand myCommand = new MyCommand(getApplicationContext());
        int i = 0;
        AlertLoading2();

        for (Uri imguri : image_uris) {
            System.out.println("entrer boucle");
            //   images_paths.add(imguri.getPath());

            try {
                Bitmap bitmap = PhotoLoader.init().from(imguri.getPath()).requestSize(512, 512).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG,20,bytearrayoutputstream);
                final String encodedString = ImageBase64.encode(bitmap);

                String url = Utils.urlUpload + "?i=" + i + "&directory=" + annonce.getPath();
                i = i + 1;
                System.out.println("inside boucle");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("here on response"+verif);
                        System.out.println(response);
                        if (verif == image_uris.size() - 1) {
                            addAdvert(annonce1);
                            pDialog.dismissWithAnimation();
                            verif = 0;
                        } else
                            verif = verif + 1;
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("look here"+error.toString());
                        if (verif == image_uris.size() - 1) {
                            addAdvert(annonce1);
                            pDialog.dismissWithAnimation();
                            verif = 0;
                        } else
                            verif = verif + 1;
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

        }

        myCommand.execute();


    }


    String resp = "";
    RequestQueue rq;

    public void addAdvert(Annonces annonce) {
        annonce.setTitre(annonce.getTitre().replaceAll(" ", "%20"));
        annonce.setTitre(annonce.getTitre().replaceAll("'", "%20"));
        annonce.setDescription(annonce.getDescription().replaceAll(" ", "%20"));
        annonce.setDescription(annonce.getDescription().replaceAll("'", "%20"));
        java.sql.Date sqlDate = new java.sql.Date(annonce.getDateDebut().getTime());

        String url = Constants.ROOT_URL + "/rentagro/annonces/ajouterAnnonce.php?titre=" + annonce.getTitre() + "&nature=" + annonce.getNature() + "&description=" + annonce.getDescription() + "&prix=" + annonce.getPrix() + "&validite=" + annonce.getValidite() + "&dateDebut=" + sqlDate + "&duree=" + annonce.getDuree() +
                "&numTel=" + annonce.getNumTel() + "&user_id=" + annonce.getUser().getId() + "&type_id=" + annonce.getType().getId() + "&etat=" + annonce.getEtat() + "&renttype=" + annonce.getRenttype() + "&latitude=" + annonce.getLatitude() + "&longitude=" + annonce.getLongitude() + "&path=" + annonce.getPath() + "&nbrImg=" + annonce.getNbrImg() + "&delegation_id=" + annonce.getDelegation().getId() + "";
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                resp = response.toString();
                if (resp.equals("success"))
                    Success();
                else {
                    Alert("Error", "Failed to add Advert");
                    System.out.println(resp + "test herer");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Alert("Error", "Failed to add Advert");
            }
        });

        rq.add(stringRequest);


    }


    public boolean validate() {
        boolean valid = true;


        String title = inputTitle.getText().toString();
        String price = inputPrice.getText().toString();
        String date = inputDate.getText().toString();
        String duration = inputDuration.getText().toString();
        String numtel = inputTel.getText().toString();
        String description = inputDescription.getText().toString();


        if (radiogroupe.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getBaseContext(), "Please select Demande/Offre", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (title.isEmpty() || title.length() > 50) {
            inputTitle.setError("Enter a Title under 50 char");
            valid = false;
        } else {
            inputTitle.setError(null);
        }

        if (price.isEmpty() || price.length() > 20) {
            inputPrice.setError("Enter a valid price");
            valid = false;
        } else {
            inputPrice.setError(null);
        }

        if (date.isEmpty()) {
            inputDate.setError("Enter a date");
            valid = false;
        } else {
            inputDate.setError(null);
        }

        if (duration.isEmpty()) {
            inputDuration.setError("Enter a valid duration in days");
            valid = false;
        } else {
            inputDuration.setError(null);
        }

        if (numtel.isEmpty() || numtel.length() > 8) {
            inputTel.setError("Enter a valid Tel number (without +216)");
            valid = false;
        } else {
            inputTel.setError(null);
        }

        if (description.isEmpty() || description.length() > 500) {
            inputDescription.setError("Enter a description (under 500 char)");
            valid = false;
        } else {
            inputDescription.setError(null);
        }

        return valid;
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

            Alert("Location Problem", "cannot locate you");

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation == null) {
            lat = Double.valueOf(0);
            longi = Double.valueOf(0);
            pDialog.dismissWithAnimation();
            Alert("Location Problem", "cannot locate you");
            return;

        }
        System.out.println(mLastLocation.getLatitude() + "test");
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


    public void AlertLoading1() {
        pDialog1 = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog1.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog1.setTitleText("Loading");
        pDialog1.setCancelable(false);
        pDialog1.show();
    }

    public void AlertLoading2() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Uploading Images ...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void Success() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Advert Added with Success!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        AddActivity.super.onBackPressed();
                    }
                })
                .show();
    }

 /*   public void clean() {


        inputTitle.setText("");
        inputPrice.setText("");
        inputAdresse.setText("");

        inputDate.setText("");
        inputDuration.setText("");
        inputTel.setText("");
        inputDescription.setText("");

    }*/
}