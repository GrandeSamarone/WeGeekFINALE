package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.marlostrinidad.wegeek.nerdzone.Model.Banca_noticia;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

public class Adapter_banca_Noticia_Geral extends RecyclerView.Adapter<Adapter_banca_Noticia_Geral.MyViewHolder> {

    private List<Banca_noticia> banca;
    private Context context;
    public Adapter_banca_Noticia_Geral(List<Banca_noticia> lista_banca, Context context){
        this.banca = lista_banca;
        this.context=context;
    }
    public List<Banca_noticia> getBanca(){
        return this.banca;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_banca,parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Banca_noticia list_banca = banca.get(position);

        if(list_banca.getNome_banca()!=null){
            holder.titulo.setText(list_banca.getNome_banca());
        }

        if(list_banca.getDesc_banca()!=null){
            holder.legenda.setText(list_banca.getDesc_banca());
        }

        if(list_banca.getIcone_banca()!=null){
            String stringcapa = list_banca.getIcone_banca();
            if (stringcapa != null) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(stringcapa))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(true)
                        .setResizeOptions(new ResizeOptions(200, 200))
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
        return banca.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView legenda;
        TextView categoria;
        TextView estado;
        SimpleDraweeView capa;
        RatingBar rating;



        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.texttitulo_banca);
            legenda = itemView.findViewById(R.id.textlegenda_banca);
            capa = itemView.findViewById(R.id.icone_banca);
            //  categoria=itemView.findViewById(R.id.textcategoria);
        }
    }
}
