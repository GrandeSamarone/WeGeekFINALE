package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.varunest.sparkbutton.SparkEventListener;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by fulanoeciclano on 01/09/2018.
 */

public class Evento_Adapter extends RecyclerView.Adapter<Evento_Adapter.MyViewHolder> {

    private List<Evento> listaevento;
    private Context context;
    private DatabaseReference mDatabase;
    Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();

    public Evento_Adapter(List<Evento> eventos, Context c){
        this.context =c;
        this.listaevento=eventos;
    }

    public List<Evento> getevento(){
        return this.listaevento;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_evento,parent,false);

        return new Evento_Adapter.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
         final Evento evento = listaevento.get(position);
        holder.titulo.setText(evento.getTitulo());
        holder.subpostagem.setText(evento.getMensagem());
      SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("EEEE HH:mm", Locale.getDefault());
        holder.data_inicia.setText(simpleDateFormat.format(evento.getData_inicio()));
        holder.data_termina.setText(simpleDateFormat.format(evento.getData_fim()));
        if(evento.getCapaevento()!=null) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(evento.getCapaevento()))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setProgressiveRenderingEnabled(true)
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
            holder.imgevento.setController(controller);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);

            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setRoundingParams(roundingParams)
                    .setProgressBarImage(new CircleProgressDrawable())
                    //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                    .build();
            holder.imgevento.setHierarchy(hierarchy);
        }

        holder.imgevento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


}

    @Override
    public int getItemCount() {
        return listaevento.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo,data_inicia,data_termina;
        private TextView subpostagem;
        private SimpleDraweeView imgevento;
        private CardView card;
        public MyViewHolder(View itemView) {
            super(itemView);
            data_inicia=itemView.findViewById(R.id.data_inicia);
            data_termina=itemView.findViewById(R.id.data_termina);
            titulo = itemView.findViewById(R.id.texttitulo_evento);
           subpostagem = itemView.findViewById(R.id.textlegenda_evento);
          //  card = itemView.findViewById(R.id.cardclickevento);
            imgevento = itemView.findViewById(R.id.capaevento);

        }


    }
}
