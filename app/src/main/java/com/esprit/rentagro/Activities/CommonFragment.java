package com.esprit.rentagro.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.esprit.rentagro.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

/**
 * Created by xmuSistone on 2016/9/18.
 */
public class CommonFragment extends Fragment implements DragLayout.GotoDetailListener {
    private ImageView imageView;
    public String add1,add3,add4,add5;
    public int id,tel,voters;
    public float rate;
    public double longi,lat;
    private View address1, address2, address3, address4, address5;
    private RatingBar ratingBar;
    private View head1, head2;
    private String imageUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_common, null);
        DragLayout dragLayout = (DragLayout) rootView.findViewById(R.id.drag_layout);
        imageView = (ImageView) dragLayout.findViewById(R.id.image);
        System.out.println(imageUrl);
        ImageLoader.getInstance().displayImage(imageUrl, imageView);
        ((TextView) dragLayout.findViewById(R.id.address1)).setText(add1);
        address1 = dragLayout.findViewById(R.id.address1);


        address2 = dragLayout.findViewById(R.id.address2);

        ((TextView) dragLayout.findViewById(R.id.address3)).setText(add3);
        address3 = dragLayout.findViewById(R.id.address3);

        ((TextView) dragLayout.findViewById(R.id.address4)).setText(add4);
        address4 = dragLayout.findViewById(R.id.address4);

        ((TextView) dragLayout.findViewById(R.id.address5)).setText(add5);
        address5 = dragLayout.findViewById(R.id.address5);

        ratingBar = (RatingBar) dragLayout.findViewById(R.id.rating);
        ratingBar.setRating(rate/voters);

        head1 = dragLayout.findViewById(R.id.head1);
        head1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });
        head2 = dragLayout.findViewById(R.id.head2);
        head2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locate();
            }
        });

        dragLayout.setGotoDetailListener(this);
        return rootView;
    }

    @Override
    public void gotoDetail() {
        Activity activity = (Activity) getContext();

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                new Pair(imageView, DetailActivity.IMAGE_TRANSITION_NAME),
                new Pair(address1, DetailActivity.ADDRESS1_TRANSITION_NAME),
                new Pair(address2, DetailActivity.ADDRESS2_TRANSITION_NAME),
                new Pair(address3, DetailActivity.ADDRESS3_TRANSITION_NAME),
                new Pair(address4, DetailActivity.ADDRESS4_TRANSITION_NAME),
                new Pair(address5, DetailActivity.ADDRESS5_TRANSITION_NAME),
                new Pair(ratingBar, DetailActivity.RATINGBAR_TRANSITION_NAME),
                new Pair(head1, DetailActivity.HEAD1_TRANSITION_NAME),
                new Pair(head2, DetailActivity.HEAD2_TRANSITION_NAME)

        );
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(DetailActivity.ADDRESS1_TRANSITION_NAME,  ((TextView)address1).getText());
        intent.putExtra(DetailActivity.ADDRESS3_TRANSITION_NAME,  ((TextView)address3).getText());
        intent.putExtra(DetailActivity.ADDRESS4_TRANSITION_NAME,  ((TextView)address4).getText());
        intent.putExtra(DetailActivity.ADDRESS5_TRANSITION_NAME,  ((TextView)address5).getText());
        intent.putExtra(DetailActivity.RATINGBAR_TRANSITION_NAME,ratingBar.getRating()+"");
        intent.putExtra(DetailActivity.ID_NAME,id+"");

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public void bindData(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void call(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",String.valueOf(tel), null));
        startActivity(intent);
    }

    public void locate(){
        String uri = String.format(Locale.getDefault(), "http://maps.google.com/maps?daddr=%f,%f (%s)", lat, longi, "Where the party is at");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
}
