package com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

/**
 * Created by fulanoeciclano on 30/08/2018.
 */

public class AdapterMercado extends RecyclerView.Adapter<AdapterMercado.MyviewHolder> {
    private Context context;
    private List<Comercio> comercios;
    private DatabaseReference database;
    private ChildEventListener ChildEventListenerperfil;

    public AdapterMercado(List<Comercio> merc, Context cx){
        this.context = cx;
        this.comercios = merc;
    }
    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterevento,parent,false);

        return new AdapterMercado.MyviewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {

        Comercio comercio = comercios.get(position);
        if(comercio.getTitulo()!=null){
            holder.mercadonome.setText(comercio.getTitulo());
        }


        List<String> urlFotos = comercio.getFotos();
        String stringcapa = urlFotos.get(0);
        if (stringcapa != null) {
            Uri uri = Uri.parse(stringcapa);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setProgressiveRenderingEnabled(true)
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
            holder.mercadocapa.setController(controller);

            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setProgressBarImage(new CircleProgressDrawable())
                  //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                    .build();
            holder.mercadocapa.setHierarchy(hierarchy);
        }
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        ChildEventListenerperfil=database.orderByChild("id").equalTo(comercio.getIdAutor())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Usuario perfil = dataSnapshot.getValue(Usuario.class );
                        assert perfil != null;

                        holder.authornome.setText(perfil.getNome());
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
        return comercios.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        private  SimpleDraweeView mercadocapa;
        private  TextView mercadonome,authornome;
        private  LinearLayout mercadolayout;
        private  CardView card;
        private ProgressBar progresso;


        public MyviewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardevento);
            authornome=itemView.findViewById(R.id.nomeautor_evento);
            mercadocapa = itemView.findViewById(R.id.iconeevento);
            mercadonome = itemView.findViewById(R.id.nomeevento);
        }
    }
}
