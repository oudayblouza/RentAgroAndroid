package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esprit.rentagro.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Entities.Annonces;
import Util.Constants;

/**
 * Created by oudayblouza on 30/11/2017.
 */

public class MesAnnoncesAdapter extends  RecyclerView.Adapter<MesAnnoncesAdapter.MyViewHolder> {
    private Context context;
    private List<Annonces> annoncesList;
    private ClickListener clickListener;


    String REQUEST_TAG = "com.androidtutorialpoint.volleyJsonObjectRequest";
    public class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnLongClickListener {
        public TextView name, description, price,status,statushide;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            statushide = view.findViewById(R.id.statushide);
            status = view.findViewById(R.id.status);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            view.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View view) {
            if(clickListener!=null){
                clickListener.itemClicked(view,getPosition());
            }
            return true;
        }
    }


    public MesAnnoncesAdapter(Context context, List<Annonces> annoncesList) {
        this.context = context;
        this.annoncesList = annoncesList;
    }

    @Override
    public MesAnnoncesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mesannonces_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MesAnnoncesAdapter.MyViewHolder holder, int position) {
        final Annonces item = annoncesList.get(position);
        holder.name.setText(item.getTitre());
        holder.description.setText(item.getDescription());
        holder.price.setText(item.getPrix()+" .DT "+item.getRenttype());

/*        holder.statushide.setText(item.getId());
        holder.statushide.setVisibility(View.GONE);*/

        if(item.getValidite()==0){
            holder.status.setText("Waiting..");
            holder.status.setTextColor(Color.parseColor("#BDB76B"));

        }else if(item.getEtat()==0){
            holder.status.setText("Desactivated");
            holder.status.setTextColor(Color.parseColor("#800000"));
        }else if(item.getEtat()==1){
            holder.status.setText("Activated");
            holder.status.setTextColor(Color.parseColor("#008000"));
        }
        String path = item.getPath().replaceAll(" ","%20");
        Picasso.with(context).load(Constants.ROOT_URL +"/rentagro/upload/"+path+"/0.jpeg")
                        .error(R.drawable.image_error)
                        .placeholder(R.drawable.placeholder)
                .into(holder.thumbnail);
      /*  Glide.with(context)
                .load()
                .into(holder.thumbnail);*/
    }

    @Override
    public int getItemCount() {
        return annoncesList.size();
    }

    public void removeItem(int position) {
        annoncesList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Annonces item, int position) {
        annoncesList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }



    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }



public interface ClickListener{
         public void itemClicked(View view , int position);
    }

}
