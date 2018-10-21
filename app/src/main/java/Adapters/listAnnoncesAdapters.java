package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.esprit.rentagro.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import Entities.Annonces;
import Util.Constants;

/**
 * Created by ouazseddik on 20/11/2017.
 */

public class listAnnoncesAdapters extends Adapter<listAnnoncesAdapters.MyViewHolder> implements Filterable{



    private ClickListener clickListener;
    ArrayList<Annonces> annonces;
    ArrayList<Annonces> filterList;
    private List<Annonces> annoncesFiltred;
    private LayoutInflater layoutInflater;
    Context c;

    public listAnnoncesAdapters(Context context, ArrayList<Annonces> annonces)
    {
        this.c=context;
        layoutInflater = LayoutInflater.from(context);
        this.annonces=annonces;
        this.filterList=annonces;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.annonce_row,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {



        holder.tvtitrerow.setText(annonces.get(position).getTitre());

        holder.tvdate.setText(annonces.get(position).getDateDebut()+"");
        String path = annonces.get(position).getPath().replaceAll(" ","%20");
        Picasso.with(c).load(Constants.ROOT_URL +"/rentagro/upload/"+path+"/0.jpeg")
                .error(R.drawable.image_error)
                .placeholder(R.drawable.placeholder)
                .into(  holder.imagerow);
    }

    @Override
    public int getItemCount() {
        return annonces.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvtitrerow ;
        ImageView imagerow;
        TextView tvdate ;


        public MyViewHolder(final View itemView) {
            super(itemView);

            tvtitrerow = (TextView) itemView.findViewById(R.id.title);
            imagerow = (ImageView) itemView.findViewById(R.id.thumbnail);
            tvdate = (TextView) itemView.findViewById(R.id.count);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(clickListener!=null){
                clickListener.itemClicked(view,getPosition());
            }
        }
    }

    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    annoncesFiltred = filterList;
                } else {

                    List<Annonces> filteredList = new ArrayList<>();
                    for (Annonces row : filterList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitre().toLowerCase().contains(charString.toLowerCase()) || row.getNature().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    annoncesFiltred = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = annoncesFiltred;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                annonces = (ArrayList<Annonces>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public interface ClickListener{
        public void itemClicked(View view , int position);
    }


}
