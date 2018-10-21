package com.esprit.rentagro.Activities.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.esprit.rentagro.Activities.test;
import com.esprit.rentagro.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.MesAnnoncesAdapter;
import Entities.Annonces;
import Util.Constants;
import Util.RecyclerAnnonceTouchHelper;
import android.graphics.Color;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Util.AppSingleton;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class MesAnnoncesFragment extends Fragment implements MesAnnoncesAdapter.ClickListener, RecyclerAnnonceTouchHelper.RecyclerAnnonceTouchHelperListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SweetAlertDialog sDialog;

    private static final String TAG = MesAnnoncesFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Annonces> cartList;
    private MesAnnoncesAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefresh;
    // url to fetch menu json
    private static final String URL =  Constants.ROOT_URL + "rentagro/annonces/getAnnonceByUser.php?id=";
    String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";

    SweetAlertDialog alertdialog;
    SweetAlertDialog pDialog;
    int idUser ;

    public MesAnnoncesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MesAnnoncesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MesAnnoncesFragment newInstance(String param1, String param2) {
        MesAnnoncesFragment fragment = new MesAnnoncesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_mes_annonces, container, false);
        System.out.println(URL);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userpref", test.MODE_PRIVATE);
        idUser=sharedPreferences.getInt("id", -1);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);

        getActivity().setTitle("My Adverts");
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<>();
        mAdapter = new MesAnnoncesAdapter(getActivity().getApplicationContext(), cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(this );


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerAnnonceTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        FloatingActionButton fcontact = (FloatingActionButton) getActivity().findViewById(R.id.fcontact);
        fcontact.setVisibility(View.GONE);
        ImageView img = (ImageView) getActivity().findViewById(R.id.backdrop);
        img.setImageResource(0);
        TextView tv = (TextView) getActivity().findViewById(R.id.love_music) ;
        tv.setTextSize(25);
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        tv.setText("My Adverts");
        tv.setBackgroundColor(Color.TRANSPARENT);
        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(false);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.toolbar);




        // making http call and fetching menu json

        prepareCart();
        swipeRefresh.setOnRefreshListener(onSwipeRefresh);
        return view;
    }




    private void prepareCart() {
        //   cartList.clear();
        AlertLoading();
        System.out.println(URL+idUser);
        JsonObjectRequest request = new JsonObjectRequest(URL+idUser,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                           pDialog.dismissWithAnimation();
                            Alert("Oups","Couldn't fetch adverts! Pleas try again." );
                            return;
                        }

                        try {

                            JSONArray resp =  response.getJSONArray("annonce");
                            List<Annonces> items = new Gson().fromJson(resp.toString(), new TypeToken<List<Annonces>>() {
                            }.getType());
                            cartList.clear();
                            cartList.addAll(items);

                            // refreshing recycler view
                            mAdapter.notifyDataSetChanged();
                         swipeRefresh.setRefreshing(false);
                         pDialog.dismissWithAnimation();
                        } catch (JSONException e) {

                            JSONObject resp = null;
                            try {
                                resp = response.getJSONObject("annonce");
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            Annonces items = new Gson().fromJson(resp.toString(), new TypeToken<Annonces>() {
                            }.getType());
                            cartList.clear();
                            cartList.add(items);

                            // refreshing recycler view
                            mAdapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                            pDialog.dismissWithAnimation();


                         /*   e.printStackTrace();
                            pDialog.dismissWithAnimation();
                            Alert("Oups","Couldn't fetch adverts! Pleas try again." );*/
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                pDialog.dismissWithAnimation();
                Alert("Oups","Couldn't fetch adverts! Pleas try again." );
                //Toast.makeText(getActivity().getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(request,REQUEST_TAG);


    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        final RecyclerView.ViewHolder viewHolder1 = viewHolder;
        if (viewHolder instanceof MesAnnoncesAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getTitre();

            // backup of removed item for undo purpose
            final Annonces deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();


            new SweetAlertDialog(getActivity().getWindow().getContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Won't be able to recover this file!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            AlertLoading();

                            delete(position);
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelText("No,Restore Item!")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.cancel();
                        }
                    })
                    .show();



        }
    }

    public void delete(final int position) {
       int id = cartList.get(position).getId();
       String path = cartList.get(position).getPath();
        String path1 = path.replaceAll(" ","%20");
        String url = Constants.ROOT_URL +"/rentagro/deleteAnnonce.php?id="+id+"&path="+path1;
        System.out.println(url);
       StringRequest stringRequest = new StringRequest(Request.Method.GET,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resp = response.toString();

                if(resp.equals("success")) {
                    pDialog.dismissWithAnimation();
                    DeleteSuccess();
                    mAdapter.removeItem(position);
                }
                else {
                    pDialog.dismissWithAnimation();

                    Alert("Oups","Delete Fail!");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                Alert("Oups","Delete Fail!");
                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest,REQUEST_TAG);

    }

    public void DeleteSuccess(){

        sDialog = new SweetAlertDialog(getActivity().getWindow().getContext());
                sDialog.setTitleText("Deleted!")
                .setContentText("Your imaginary file has been deleted!")
                .setConfirmText("OK")
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            prepareCart();
        }
    };




    public void updateEtat(int id,int etat){
        int status;
        if (etat == 1){
            status = 0;
        }else {
            status = 1;
        }
        System.out.println("he is here");
        String url = Constants.ROOT_URL +"/rentagro/annonces/Update_annonce.php?id="+id+"&etat="+status;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String resp = response.toString();
                System.out.println("ok fine here");
                if(resp.equals("success"))
                    Toast.makeText(getActivity().getApplicationContext(), "Update Success", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Update Fail", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        });
        AppSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest,REQUEST_TAG);


    }


    @Override
    public void itemClicked(View view, int position) {

       Annonces annonce =  cartList.get(position);
       if(annonce.getValidite()==1) {
           updateEtat(annonce.getId(), annonce.getEtat());
           prepareCart();
          // prepareCart();
       }else {
           //Toast.makeText(getActivity().getApplicationContext(), "Application en attente de validation", Toast.LENGTH_SHORT).show();
           Warning();
       }
      //  Toast.makeText(getActivity().getApplicationContext(), annonce.getId()+"    "+annonce.getEtat(), Toast.LENGTH_SHORT).show();
    }




    public void Alert(String title, String text) {

        alertdialog = new SweetAlertDialog(getActivity().getWindow().getContext(), SweetAlertDialog.ERROR_TYPE);
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
        pDialog = new SweetAlertDialog(getActivity().getWindow().getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
    }





    public void Warning(){
        new SweetAlertDialog(getActivity().getWindow().getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Please Wait")
                .setContentText("Advert waiting for Admin Confirmation")
                .setConfirmText("Dismiss")
                .show();
    }

    public void Success() {
        new SweetAlertDialog(getActivity().getWindow().getContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText("Advert Added with Success!")
                .show();
    }

}
