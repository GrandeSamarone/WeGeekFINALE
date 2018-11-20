package com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial;

import android.content.Context;
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
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

/**
 * Created by fulanoeciclano on 17/07/2018.
 */

public class EventoAdapterPagInicial extends RecyclerView.Adapter<EventoAdapterPagInicial.MyviewHolder> {

    private Context context;
    private List<Evento> eventos;
    private DatabaseReference database;
    private ChildEventListener ChildEventListenerperfil;
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
    public void onBindViewHolder(final MyviewHolder holder, int position) {

        final Evento ev = eventos.get(position);
        holder.eventonome.setText(ev.getTitulo());

        if(ev.getCapaevento()!=null){

            Uri uri = Uri.parse(ev.getCapaevento());
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(200, 200))
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
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        ChildEventListenerperfil=database.orderByChild("id").equalTo(ev.getIdUsuario())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Usuario perfil = dataSnapshot.getValue(Usuario.class );
                        assert perfil != null;

                        holder.evento_author.setText(perfil.getNome());
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }






    public class MyviewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView eventocapa;
        TextView eventonome,evento_author;
        LinearLayout eventolayout;
        CardView card;
        public MyviewHolder(View itemView) {
            super(itemView);

          evento_author=itemView.findViewById(R.id.nomeautor_evento);
            eventocapa = itemView.findViewById(R.id.iconeevento);
            card = itemView.findViewById(R.id.cardevento);
            eventonome = itemView.findViewById(R.id.nomeevento);

        }
    }
}