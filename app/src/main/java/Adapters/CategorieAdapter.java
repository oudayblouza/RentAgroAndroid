package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.esprit.rentagro.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Entities.Categorie;
import Util.Constants;


/**
 * Created by oudayblouza on 01/12/2017.
 */

public class CategorieAdapter extends RecyclerView.Adapter<CategorieAdapter.MyViewHolder> {

    private List<Categorie> wallpapers;
    private Context mContext;
    private ClickListener clickListener;

    public CategorieAdapter(Context context, List<Categorie> wallpapers) {
        this.wallpapers = wallpapers;
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Categorie wallpaper = wallpapers.get(position);
        if (!TextUtils.isEmpty( wallpaper.getImageUrl())) {
            Picasso.with(mContext).load(Constants.ROOT_URL +wallpaper.getImageUrl())
                       .error(R.drawable.image_error)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imageView);
        }
        holder.textView.setText(Html.fromHtml(wallpaper.getNom()));

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_wallpaper, null);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return (wallpapers != null ? wallpapers.size() : 0);
    }

    public void refresh(List<Categorie> wallpapers) {
        this.wallpapers=wallpapers;
        this.notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView imageView;
        protected TextView textView;
        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this );
            this.imageView = view.findViewById(R.id.imageView);
            this.textView = view.findViewById(R.id.title);

        }

        @Override
        public void onClick(View view) {
            if(clickListener!=null){
                clickListener.itemClicked(view,getPosition() );
            }
        }
    }
public void setClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
}
    public interface ClickListener{
        public void itemClicked(View view,int position);
    }

}
