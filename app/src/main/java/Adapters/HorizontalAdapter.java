package Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esprit.rentagro.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Entities.Annonces;
import Util.Constants;
import butterknife.BindView;

/**
 * Created by oudayblouza on 04/01/2018.
 */

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    @BindView(R.id.thumb)
    ImageView thumb;
    private List<Annonces> titles;
    private Context mContext;
    private ClickListener clickListener;

    public HorizontalAdapter(Context context, List<Annonces> titles) {
        this.titles = titles;
        this.mContext = context;
    }

    public void refresh(List<Annonces> titles) {
        this.titles = titles;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horizental, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String title = titles.get(position).getTitre();
        String path = titles.get(position).getPath().replaceAll(" ","%20");
        Picasso.with(mContext).load(Constants.ROOT_URL +"/rentagro/upload/"+path+"/0.jpeg")
                .error(R.drawable.image_error)
                .placeholder(R.drawable.placeholder)
                .into(  holder.thumb);
        holder.title.setText(title);
        holder.tvdate.setText(titles.get(position).getPrix()+".DT");
    }

    @Override
    public int getItemCount() {
        return (titles != null ? titles.size() : 0);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView thumb;
        private TextView tvdate;

        ViewHolder(final View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.thumb = (ImageView) itemView.findViewById(R.id.thumb);
            tvdate = (TextView) itemView.findViewById(R.id.count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getPosition());
            }
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        public void itemClicked(View view, int position);
    }
}