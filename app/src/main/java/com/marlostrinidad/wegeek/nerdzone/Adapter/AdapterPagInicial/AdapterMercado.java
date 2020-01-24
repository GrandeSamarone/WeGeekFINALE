package com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial;

import android.content.Context;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Model.Item_loja;
import com.marlostrinidad.wegeek.nerdzone.R;


import java.util.List;

/**
 * Created by fulanoeciclano on 30/08/2018.
 */

public class AdapterMercado extends RecyclerView.Adapter<AdapterMercado.MyviewHolder> {
    private Context context;
    private List<Item_loja> comercios;
    private DatabaseReference database;
    private ChildEventListener ChildEventListenerperfil;

    public AdapterMercado(List<Item_loja> merc, Context cx){
        this.context = cx;
        this.comercios = merc;
    }
    public List<Item_loja> getmercados(){
        return this.comercios;
    }
    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mercado_inicial,parent,false);

        return new AdapterMercado.MyviewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, int position) {
        Item_loja comercio = comercios.get(position);

        if(comercio.getTitulo()!=null){
            holder.mercadonome.setText(comercio.getTitulo());
        }
        String capa = comercio.getItem_foto();
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(capa))
                .setLocalThumbnailPreviewsEnabled(true)
                .setProgressiveRenderingEnabled(true)
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        holder.mercadocapa.setController(controller);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setRoundingParams(roundingParams)
                .setProgressBarImage(new CircleProgressDrawable())
                //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                .build();
        holder.mercadocapa.setHierarchy(hierarchy);


    }

    @Override
    public int getItemCount() {
        return comercios.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        private  SimpleDraweeView mercadocapa;
        private  TextView mercadonome,autor;
        private  LinearLayout mercadolayout;
        private  CardView card;
        private ProgressBar progresso;


        public MyviewHolder(View itemView) {
            super(itemView);
            mercadocapa = itemView.findViewById(R.id.iconeevento);
            mercadonome = itemView.findViewById(R.id.nomeevento);
        }
    }
}
