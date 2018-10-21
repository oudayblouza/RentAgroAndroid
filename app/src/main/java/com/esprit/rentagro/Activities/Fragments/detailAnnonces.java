package com.esprit.rentagro.Activities.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cocosw.bottomsheet.BottomSheet;
import com.esprit.rentagro.Activities.AddActivity;
import com.esprit.rentagro.Activities.MapViewCustom;
import com.esprit.rentagro.Activities.test;
import com.esprit.rentagro.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Adapters.HorizontalAdapter;
import Entities.Annonces;
import Entities.Categorie;
import Entities.Delegation;
import Entities.Type;
import Entities.Users;
import Entities.Ville;
import Util.AppSingleton;
import Util.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class detailAnnonces extends Fragment implements OnMapReadyCallback,HorizontalAdapter.ClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DrawaerActivity";
    FloatingActionButton fcontact;
    ProgressDialog progressDialog;
    Delegation delegation1;
    Users user1;
    String email = "";
    Drawable d;
    Unbinder unbinder;
    @BindView(R.id.customMap)
    MapViewCustom customMap;
    @BindView(R.id.tvtitle)
    TextView tvtitle;
    @BindView(R.id.tvprice)
    TextView tvprice;
    @BindView(R.id.tvduration)
    TextView tvduration;
    @BindView(R.id.tvnature)
    TextView tvnature;
    @BindView(R.id.tvcattype)
    TextView tvcattype;
    @BindView(R.id.tvdatedelay)
    TextView tvdatedelay;
    @BindView(R.id.tvvilledel)
    TextView tvvilledel;
    @BindView(R.id.description)
    EditText description;
    private HorizontalAdapter adapter;
    private List<Annonces> cartList;
SweetAlertDialog pDialog;
    // TODO: Rename and change types of parameters
    private Annonces annonce;
    private String mParam2;
    private static final String ANNONCE_KEY = "ANNONCE_KEY";
    String requiredPermission1 = "android.permission.CALL_PHONE";
    public detailAnnonces()  {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detailAnnonces.
     */
    // TODO: Rename and change types and number of parameters
    public static detailAnnonces newInstance(String param1, String param2) {
        detailAnnonces fragment = new detailAnnonces();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static detailAnnonces newInstance(Annonces annonce) {
        detailAnnonces fragment = new detailAnnonces();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ANNONCE_KEY, annonce);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            annonce = (Annonces) getArguments().getSerializable("annonce");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        deleteCollapse();
        annonce = (Annonces) getArguments().getSerializable(ANNONCE_KEY);

        View view = inflater.inflate(R.layout.fragment_detail_annonces, container, false);
        unbinder = ButterKnife.bind(this, view);



        cartList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity().getWindow().getContext());
        progressDialog.setMessage("Loading...");
        fillDetail();


        BannerSlider bannerSlider = (BannerSlider) view.findViewById(R.id.banner_slider1);
        List<Banner> banners = new ArrayList<>();
        //add banner using image url


        for(int i = 0;i<annonce.getNbrImg();i++){
            String path = annonce.getPath().replaceAll(" ","%20");
            banners.add(new RemoteBanner(Constants.ROOT_URL +"/rentagro/upload/"+path+"/"+i+".jpeg"));
        }

        bannerSlider.setBanners(banners);


        if ((annonce.getLongitude() != 0) & (annonce.getLatitude() != 0)) {
            customMap.onCreate(null);
            customMap.onResume();
            customMap.getMapAsync(this);
        } else
            customMap.setVisibility(View.GONE);




        MultiSnapRecyclerView firstRecyclerView = (MultiSnapRecyclerView) view.findViewById(R.id.recyclerSh);
        LinearLayoutManager firstManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        firstRecyclerView.setLayoutManager(firstManager);
        adapter = new HorizontalAdapter(getActivity().getApplicationContext(), new ArrayList<Annonces>());
        firstRecyclerView.setAdapter(adapter);
        loadTypeAnn(annonce.getType().getId());
        adapter.setClickListener(this);


        fcontact = (FloatingActionButton) getActivity().findViewById(R.id.fcontact);
        fcontact.setVisibility(View.VISIBLE);


        Finduserby(annonce.getUser().getId());
        d =getResources().getDrawable(R.drawable.icon);



        fcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheet.Builder(getActivity()).icon(d).title(user1.getUsername()).sheet(R.menu.list).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case R.id.call:
                                int checkVal1 = getActivity().getApplicationContext().checkCallingOrSelfPermission(requiredPermission1);

                                if (checkVal1 != PackageManager.PERMISSION_GRANTED ) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            1);
                                    Toast.makeText(getActivity().getApplicationContext(), "Please check call Permission !", Toast.LENGTH_LONG).show();

                                }else {
                                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", String.valueOf(annonce.getNumTel()), null));
                                    startActivity(intent);
                                }

                                break;
                            case R.id.share:
                                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                Uri data = Uri.parse("mailto:"+email);
                                intent1.setData(data);
                                startActivity(intent1);
                                break;

                        }
                    }
                }).show();
            }
        });

        return view;
    }

    @Override
    public void itemClicked(View view, int position) {

        AlertLoading();
        FindAnnby( cartList.get(position).getId());

    }
    public void fillDetail() {
        progressDialog.show();
        tvtitle.setText(annonce.getTitre());
        tvprice.setText(annonce.getPrix() + ".DT " + annonce.getRenttype());
        tvduration.setText(annonce.getDuree() + " jours");
        tvnature.setText(annonce.getNature());
        tvdatedelay.setText(String.valueOf(annonce.getDateDebut()));


        tvvilledel.setText(annonce.getDelegation().getNom());
        description.setText(annonce.getDescription());
        FindDelby(annonce.getDelegation().getId());
        Findtypeby(annonce.getType().getId());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    public void deleteCollapse() {
        getActivity().setTitle("Category Advert");


        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        ImageView img = (ImageView) getActivity().findViewById(R.id.backdrop);
        img.setImageResource(0);
        TextView tv = (TextView) getActivity().findViewById(R.id.love_music);
        tv.setTextSize(25);
        tv.setText("Select Category");
        tv.setBackgroundColor(Color.TRANSPARENT);
        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(false);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.toolbar);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getActivity().getApplicationContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        Address address = getAddress(annonce.getLatitude(), annonce.getLongitude());


        googleMap.addMarker(new MarkerOptions().position(new LatLng(annonce.getLatitude(), annonce.getLongitude())).title("Adress").snippet(address.getAddressLine(0) + "\n" + address.getAddressLine(1) + "\n" + address.getAddressLine(2)));
        CameraPosition Liberty = CameraPosition.builder().target(new LatLng(annonce.getLatitude(), annonce.getLongitude())).zoom(16).bearing(0).tilt(45).build();

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));

    }


    public Address getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List addresses;
        geocoder = new Geocoder(getActivity().getWindow().getContext(), Locale.getDefault());

        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return (Address) addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

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

                            tvvilledel.setText(delegation.getNom() + "-" + ville.getNom());

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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }


    public void Findtypeby(int id) {
        String url = Constants.ROOT_URL + "rentagro/Type/select_delegation_ById.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        try {

                            Type type = new Type();
                            JSONObject v = (JSONObject) response.getJSONObject("type");
                            type.setId(v.getInt("id"));
                            type.setNom(v.getString("nom"));
                            Categorie vl = new Categorie();
                            vl.setId(v.getInt("categorie"));
                            type.setCategorie(vl);

                            FindCategby(vl.getId(), type);


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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }

    public void FindCategby(int id, final Type type) {
        String url = Constants.ROOT_URL + "rentagro/categorie/select_categorie_Byid.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        try {

                            Categorie categorie = new Categorie();
                            JSONObject v = (JSONObject) response.getJSONObject("categorie");
                            categorie.setId(v.getInt("id"));
                            categorie.setNom(v.getString("nom"));
                            categorie.setImageUrl(v.getString("imageUrl"));
                            tvcattype.setText(type.getNom() + "-" + categorie.getNom());

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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }


    private Drawable getRoundedBitmap(int imageId) {
        Bitmap src = BitmapFactory.decodeResource(getResources(), imageId);
        Bitmap dst;
        if (src.getWidth() >= src.getHeight()) {
            dst = Bitmap.createBitmap(src, src.getWidth() / 2 - src.getHeight() / 2, 0, src.getHeight(), src.getHeight());
        } else {
            dst = Bitmap.createBitmap(src, 0, src.getHeight() / 2 - src.getWidth() / 2, src.getWidth(), src.getWidth());
        }
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), dst);
        roundedBitmapDrawable.setCornerRadius(dst.getWidth() / 2);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }



    public void Finduserby(int id) {
        String url = Constants.ROOT_URL + "rentagro/User/user_by_id.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        try {


                            Users user = new Users();
                            JSONObject v = (JSONObject) response.getJSONObject("users");
                            user.setId(v.getInt("id"));
                            user.setUsername(v.getString("username"));
                            Delegation del = new Delegation();
                            del.setId(v.getInt("delegation_id"));
                            user.setDelegation(del);
                            FindDel(del.getId(),user);

                            String username = user.getUsername().replaceAll(" ", "%20");
                            email = user.getEmail();
                            Picasso.with(getActivity().getWindow().getContext()).load(Constants.ROOT_URL + "/rentagro/images/premium/" + username + "/" + username + ".jpeg")
                                    .error(R.drawable.image_error)
                                    .placeholder(R.drawable.placeholder)
                                    .into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            d = new BitmapDrawable(getResources(),bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });


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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }

    public void FindDel(int id,Users user) {
        String url = Constants.ROOT_URL + "rentagro/Delegation/select_delegation_ById.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
        user1=user;
        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());

                        try {

                            Delegation delegation = new Delegation();
                            JSONObject v = (JSONObject) response.getJSONObject("delegation");
                            delegation.setId(v.getInt("id"));
                            delegation.setNom(v.getString("nom"));
                            delegation1 = delegation;
                            progressDialog.dismiss();

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
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }

    private void loadTypeAnn(int id ) {


        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
        System.out.println(Constants.ROOT_URL + "rentagro/annonces/getAnnonceByType.php?id="+id);
        JsonObjectRequest request = new JsonObjectRequest( Constants.ROOT_URL + "rentagro/annonces/getAnnonceByType.php?id="+id,null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {

                            JSONArray resp =  response.getJSONArray("annonce");
                            System.out.println(resp);
                            List<Annonces> items = new Gson().fromJson(resp.toString(), new TypeToken<List<Annonces>>() {
                            }.getType());

                            System.out.println(items);

                            cartList.clear();
                            cartList.addAll(items);

                            adapter.refresh(cartList);
                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request,REQUEST_TAG);



    }


    public void FindAnnby(int id) {
        String url = Constants.ROOT_URL + "rentagro/annonces/getAnnonceById.php?id=" + id;
        System.out.println(url);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  Log.d(TAG, response.toString());


                        try {

                            JSONObject resp = null;
                            resp = response.getJSONObject("annonce");

                            Annonces items = new Gson().fromJson(resp.toString(), new TypeToken<Annonces>() {
                            }.getType());



                                Delegation dl =new Delegation();
                                dl.setId(resp.getInt("delegation_id"));
                                items.setDelegation(dl);

                                Type tp = new Type();
                                tp.setId(resp.getInt("type_id"));
                                items.setType(tp);

                                Users us = new Users();
                                us.setId(resp.getInt("user_id"));
                                items.setUser(us);

                            FragmentTransaction ft1 = getFragmentManager().beginTransaction();
                            Fragment fragment = detailAnnonces.newInstance(items);
                            pDialog.dismiss();
                            ft1.replace(R.id.container, fragment).addToBackStack("back");
                            ft1.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();


            }
        });
        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);

    }



    public void AlertLoading() {
        pDialog = new SweetAlertDialog(getActivity().getWindow().getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }

}
