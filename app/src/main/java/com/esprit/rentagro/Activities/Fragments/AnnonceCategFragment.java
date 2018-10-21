package com.esprit.rentagro.Activities.Fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.esprit.rentagro.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import Adapters.listAnnoncesAdapters;
import Entities.Annonces;
import Entities.Categorie;
import Services.AnnoncesDAO;
import Util.Constants;
import Util.VolleyCallback;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AnnonceCategFragment extends Fragment implements listAnnoncesAdapters.ClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Categorie categorie = new Categorie();

    Unbinder unbinder;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String DESCRIBABLE_KEY = "describable_key";
    RequestQueue queue;
    SearchView sv;
    private RecyclerView recyclerView;
    AnnoncesDAO annoncesDAO;
    ArrayList<Annonces> annoncesList;
    private RecyclerView.LayoutManager mLayoutManager;

    public AnnonceCategFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment listAnnonces.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnonceCategFragment newInstance(String param1, String param2) {
        AnnonceCategFragment fragment = new AnnonceCategFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    public static AnnonceCategFragment newInstance(Categorie categ) {
        AnnonceCategFragment fragment = new AnnonceCategFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DESCRIBABLE_KEY, categ);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_annonce_categ, container, false);
        categorie = (Categorie) getArguments().getSerializable(
                DESCRIBABLE_KEY);

        getActivity().setTitle("Adverts");

        Collapse();
        recyclerView = (RecyclerView) view.findViewById(R.id.listAnnonces);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        annoncesDAO = new AnnoncesDAO();
        annoncesList = new ArrayList<>();


        recyclerView.setHasFixedSize(true);
        final listAnnoncesAdapters adapter = new listAnnoncesAdapters(getActivity().getApplicationContext(), annoncesList);
        new AnnoncesDAO().executeGetAnnonces(categorie, annoncesList, adapter, getActivity().getApplicationContext(), new VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                new AnnoncesDAO().getAnnonces(annoncesList, result);

            }
        });

        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


        sv = (SearchView) view.findViewById(R.id.mSearch);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                adapter.getFilter().filter(query);

                return false;
            }
        });


        initCollapsingToolbar();


        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    public void Collapse() {


        Picasso.with(getActivity().getApplicationContext()).load(Constants.ROOT_URL + categorie.getImageUrl())
//                        .error(R.drawable.image_error)
//                        .placeholder(R.drawable.placeholder)
                .into((ImageView) getActivity().findViewById(R.id.backdrop));

        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
        FloatingActionButton fcontact = (FloatingActionButton) getActivity().findViewById(R.id.fcontact);
        fcontact.setVisibility(View.GONE);

        TextView tv = (TextView) getActivity().findViewById(R.id.love_music);
        tv.setTextSize(25);
        tv.setText(categorie.getNom());
        tv.setBackgroundColor(Color.parseColor("#9500acc1"));
        appBarLayout.setExpanded(true, true);
        appBarLayout.setActivated(true);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        lp.height = (int) getResources().getDimension(R.dimen.detail_backdrop_height);

        CollapsingToolbarLayout cl = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
        cl.setTitle("Adverts");
    }

    @Override
    public void itemClicked(View view, int position) {


        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        Fragment fragment = detailAnnonces.newInstance(annoncesList.get(position));
        ft1.replace(R.id.container, fragment).addToBackStack("back");
        ft1.commit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appbar);
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
                    collapsingToolbar.setTitle("Adverts");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


}
