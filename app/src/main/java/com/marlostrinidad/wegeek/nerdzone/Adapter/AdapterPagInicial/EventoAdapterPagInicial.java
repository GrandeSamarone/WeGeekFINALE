package com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.marlostrinidad.wegeek.nerdzone.Evento.DetalheEvento;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

/**
 * Created by fulanoeciclano on 17/07/2018.
 */

public class EventoAdapterPagInicial extends RecyclerView.Adapter<EventoAdapterPagInicial.MyviewHolder> {

    private Context context;
    private List<Evento> eventos;

    public EventoAdapterPagInicial(List<Evento> listeventos, Context c){

        this.context=c;
        this.eventos=listeventos;
    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterevento,parent,false);

        return new  MyviewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, int position) {

        final Evento ev = eventos.get(position);
        holder.eventonome.setText(ev.getTitulo());

        if(ev.getCapaevento()!=null){

            Uri uri = Uri.parse(ev.getCapaevento());
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setProgressiveRenderingEnabled(true)
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
            holder.eventocapa.setController(controller);

            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setProgressBarImage(new CircleProgressDrawable())
                    //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                    .build();
            holder.eventocapa.setHierarchy(hierarchy);
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, DetalheEvento.class);
                it.putExtra("id_do_evento",ev.getUid());
                it.putExtra("UR_do_evento",ev.getEstado());
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }






    public class MyviewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView eventocapa;
        TextView eventonome;
        LinearLayout eventolayout;
        CardView card;
        public MyviewHolder(View itemView) {
            super(itemView);


            eventocapa = itemView.findViewById(R.id.iconeevento);
            card = itemView.findViewById(R.id.cardevento);
            eventonome = itemView.findViewById(R.id.nomeevento);

        }
    }
}