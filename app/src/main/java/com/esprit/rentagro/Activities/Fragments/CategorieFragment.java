package com.esprit.rentagro.Activities.Fragments;


import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.esprit.rentagro.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.CategorieAdapter;


import Entities.Categorie;
import Util.AppSingleton;
import Util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategorieFragment extends Fragment implements CategorieAdapter.ClickListener{

    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private CategorieAdapter adapter;
    private List<Categorie> cartList;

    private static final String URL =  Constants.ROOT_URL + "rentagro/categorie/select.php";
    private static final String TAG = CategorieFragment.class.getSimpleName();
    private SwipeRefreshLayout.OnRefreshListener onSwipeRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadWallpapers();
        }
    };

    private void loadWallpapers() {

        System.out.println(URL);
        String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
        JsonObjectRequest request = new JsonObjectRequest(URL,null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Couldn't fetch the menu! Pleas try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {

                            JSONArray resp =  response.getJSONArray("categorie");
                            System.out.println(resp);
                            List<Categorie> items = new Gson().fromJson(resp.toString(), new TypeToken<List<Categorie>>() {
                            }.getType());
                            System.out.println(items);
                            cartList.clear();
                            cartList.addAll(items);

                            // refreshing recycler view
                            adapter.refresh(cartList);
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            swipeRefresh.setRefreshing(false);
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
    public CategorieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cartList = new ArrayList<>();

        getActivity().setTitle("Category Advert");
        View view = inflater.inflate(R.layout.fragment_categorie, container, false);


        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        ImageView img = (ImageView) getActivity().findViewById(R.id.backdrop);
        img.setImageResource(0);
        TextView tv = (TextView) getActivity().findViewById(R.id.love_music) ;
        tv.setTextSize(25);
        tv.setText("Select Category");
        tv.setBackgroundColor(Color.TRANSPARENT);
        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(false);
        FloatingActionButton fcontact = (FloatingActionButton) getActivity().findViewById(R.id.fcontact);
        fcontact.setVisibility(View.GONE);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.toolbar);



        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new CategorieAdapter(getActivity().getApplicationContext(), new ArrayList<Categorie>());
        recyclerView.setAdapter(adapter);
        loadWallpapers();
        adapter.setClickListener(this);
        swipeRefresh.setOnRefreshListener(onSwipeRefresh);

        return view;
    }

    @Override
    public void itemClicked(View view, int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = AnnonceCategFragment.newInstance(cartList.get(position));
        ft.replace(R.id.container, fragment).addToBackStack("back");
        ft.commit();
    }
}
