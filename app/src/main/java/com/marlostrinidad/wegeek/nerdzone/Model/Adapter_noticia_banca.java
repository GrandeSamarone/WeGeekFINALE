package com.marlostrinidad.wegeek.nerdzone.Model;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

public class Adapter_noticia_banca extends RecyclerView.Adapter<Adapter_noticia_banca.MyViewHolder>  {

    private List<Forum> item_not;
    private Context context;
    public Adapter_noticia_banca(List<Forum> itens, Context context){
        this.item_not = itens;
        this.context=context;
    }
    public List<Forum> getItem(){
        return this.item_not;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_loja,parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Forum not = item_not.get(position);


        holder.titulo.setText(not.getTitulo());

        if(not.getFoto()!=null){
            String stringcapa = not.getFoto();
            if (stringcapa != null) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(stringcapa))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(true)
                        .setResizeOptions(new ResizeOptions(250, 250))
                        .build();

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .build();
                holder.capa.setController(controller);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);

                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setRoundingParams(roundingParams)
                        .setProgressBarImage(new CircleProgressDrawable())
                        //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                        .build();
                holder.capa.setHierarchy(hierarchy);
            }
        }
    }

    @Override
    public int getItemCount() {
        return item_not.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView legenda;
        TextView categoria;
        TextView estado;
        SimpleDraweeView capa;
        RatingBar rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.texttitulo);
            legenda = itemView.findViewById(R.id.textlegenda);
            capa = itemView.findViewById(R.id.capamercado);
        }
    }
}