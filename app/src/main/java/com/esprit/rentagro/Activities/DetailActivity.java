package com.esprit.rentagro.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.esprit.rentagro.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Entities.Annonces;
import Util.AppSingleton;
import Util.Constants;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xmuSistone on 2016/9/19.
 */
public class DetailActivity extends FragmentActivity {

    public static final String EXTRA_IMAGE_URL = "detailImageUrl";
    String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String IMAGE_TRANSITION_NAME = "transitionImage";
    public static final String ADDRESS1_TRANSITION_NAME = "address1";
    public static final String ADDRESS2_TRANSITION_NAME = "address2";
    public static final String ADDRESS3_TRANSITION_NAME = "address3";
    public static final String ADDRESS4_TRANSITION_NAME = "address4";
    public static final String ADDRESS5_TRANSITION_NAME = "address5";
    public static final String RATINGBAR_TRANSITION_NAME = "ratingBar";

    public static final String HEAD1_TRANSITION_NAME = "head1";
    public static final String HEAD2_TRANSITION_NAME = "head2";


    public static final   String ID_NAME="id" ;
    private int id;
    private View address1, address2, address3, address4, address5;
    private ImageView imageView;
    private RatingBar ratingBar;
    SweetAlertDialog alertdialog;

    private LinearLayout listContainer;

    String numtel ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);




        imageView = (ImageView) findViewById(R.id.image);
        address1 = findViewById(R.id.address1);
        address2 = findViewById(R.id.address2);
        address3 = findViewById(R.id.address3);
        address4 = findViewById(R.id.address4);
        address5 = findViewById(R.id.address5);
        ratingBar = (RatingBar) findViewById(R.id.rating);
        listContainer = (LinearLayout) findViewById(R.id.detail_list_container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }



        id = Integer.parseInt(getIntent().getStringExtra(ID_NAME));


        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        ImageLoader.getInstance().displayImage(imageUrl, imageView);
        String add1 = getIntent().getStringExtra(ADDRESS1_TRANSITION_NAME);
        ((TextView)address1).setText(add1);
        String add3 = getIntent().getStringExtra(ADDRESS3_TRANSITION_NAME);
        ((TextView)address3).setText(add3);
        String add4 = getIntent().getStringExtra(ADDRESS4_TRANSITION_NAME);
        ((TextView)address4).setText(add4);

        String add5 = getIntent().getStringExtra(ADDRESS5_TRANSITION_NAME);
        ((TextView)address5).setText(add5);
        numtel = add5;

        String rate = getIntent().getStringExtra(RATINGBAR_TRANSITION_NAME);
        ratingBar.setRating(Float.parseFloat(rate));

        ViewCompat.setTransitionName(imageView, IMAGE_TRANSITION_NAME);
        ViewCompat.setTransitionName(address1, ADDRESS1_TRANSITION_NAME);
        ViewCompat.setTransitionName(address2, ADDRESS2_TRANSITION_NAME);
        ViewCompat.setTransitionName(address3, ADDRESS3_TRANSITION_NAME);
        ViewCompat.setTransitionName(address4, ADDRESS4_TRANSITION_NAME);
        ViewCompat.setTransitionName(address5, ADDRESS5_TRANSITION_NAME);
        ViewCompat.setTransitionName(ratingBar, RATINGBAR_TRANSITION_NAME);

        prepareCart();
    }


    private void dealListView( List<Annonces> annonces) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        for (int i = 0; i < annonces.size(); i++) {
            View childView = layoutInflater.inflate(R.layout.detail_list_item, null);
            listContainer.addView(childView);
            ImageView headView = (ImageView) childView.findViewById(R.id.head);
            TextView desc = (TextView) childView.findViewById(R.id.description);
            desc.setText(annonces.get(i).getDescription());
            TextView titre = (TextView) childView.findViewById(R.id.titreann);
            titre.setText(annonces.get(i).getTitre());
         /*   Picasso.with(this).load(Constants.ROOT_URL + "rentagro/images/annonces/test/1.jpeg")
                    .into(headView);*/
            String path = annonces.get(i).getPath().replaceAll(" ","%20");
            Picasso.with(this).load(Constants.ROOT_URL +"/rentagro/upload/"+path+"/0.jpeg")
                    .error(R.drawable.image_error)
                    .placeholder(R.drawable.placeholder)
                    .into(headView);

        }
    }


    public void CallPrem(View v){

        String tel = numtel.substring(6);
        System.out.println(tel+"test telephone");
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",String.valueOf(tel), null));
        startActivity(intent);
    }
    public  void Rateme(View v){
        final RatingBar rate = new RatingBar(this);
        SweetAlertDialog rateAlert = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Rate Me")
                .setConfirmText("Ok")
                .setCustomView(rate);
        rateAlert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                updateRate(rate.getRating());
                  sDialog
                          .setTitleText("Success")
                        .setContentText("Shop Rated Successfully!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(null)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

            }
        });
        rateAlert.show();


    }

    private void prepareCart() {
        //   cartList.clear();
        String URL =  Constants.ROOT_URL + "rentagro/annonces/getAnnonceByUser.php?id="+id;
        System.out.println(URL+"zouza");
        JsonObjectRequest request = new JsonObjectRequest(URL,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {

                            JSONArray resp =  response.getJSONArray("annonce");
                            List<Annonces> items = new Gson().fromJson(resp.toString(), new TypeToken<List<Annonces>>() {
                            }.getType());
                            dealListView(items);

                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
              //  Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Alert();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request,REQUEST_TAG);


    }
    public void Alert(){
        alertdialog=new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        alertdialog.setTitleText("Oops...");
        alertdialog.setContentText("This Shop have no items ,for the moment");
        alertdialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {
                sDialog.dismissWithAnimation();
            }
        });
        alertdialog.show();
    }


    public void updateRate(float x){
        String url = Constants.ROOT_URL +"/rentagro/User/Update_users_rate.php?id="+id+"&rate="+x;
        System.out.println(url);

        final float c = x;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resp = response.toString();
                System.out.println("ok fine here");
                if(resp.equals("success")){
                    System.out.println("ok");
                     }

                else {
                   Alert1();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Alert1();
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest,REQUEST_TAG);



    }

    public void Alert1(){
        alertdialog=new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
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
}
