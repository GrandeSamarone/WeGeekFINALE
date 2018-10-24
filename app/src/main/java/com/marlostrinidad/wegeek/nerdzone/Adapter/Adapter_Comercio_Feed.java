package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Comercio_Feed extends RecyclerView.Adapter<Adapter_Comercio_Feed.MyViewHolder> {

    private List<Comercio> comercios;
    private Context context;
    private FirebaseDatabase databases=FirebaseDatabase.getInstance();
    private DatabaseReference databasesUsuario= ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
    private ChildEventListener ChildEventListenerperfil;

    public Adapter_Comercio_Feed(List<Comercio> comercio, Context context){
        this.comercios = comercio;
        this.context=context;
    }
    public List<Comercio> getmercados(){
        return this.comercios;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mercado_feed,parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Comercio comercio = comercios.get(position);

        if (comercio.getTitulo() != null) {
            holder.titulo.setText(comercio.getTitulo());
        }
        if (comercio.getDescricao() != null) {
            holder.legenda.setText(comercio.getFraserapida());
        }

        if (comercio.getCategoria() != null) {
            holder.categoria.setText(comercio.getCategoria());
        }
        if (comercio.getEstado() != null) {
            holder.estado.setText(comercio.getEstado());
        }

        List<String> urlFotos = comercio.getFotos();
        if (urlFotos != null) {
            String stringcapa = urlFotos.get(0);
            if (stringcapa != null) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(stringcapa))
                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(true)
                        .build();

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .build();
                holder.capa.setController(controller);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(1f);
                roundingParams.setRoundAsCircle(true);
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setRoundingParams(roundingParams)
                        .setProgressBarImage(new CircleProgressDrawable())
                        //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                        .build();
                holder.capa.setHierarchy(hierarchy);

            } else {
                Toast.makeText(context, "erro", Toast.LENGTH_SHORT).show();
            }
        }



        ChildEventListenerperfil=databasesUsuario.orderByChild("id").equalTo(comercio.getIdAutor()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

        holder.nomeUsuario.setText(perfil.getNome());
                String icone = perfil.getFoto();
                Glide.with(context)
                        .load(icone)
                        .into(holder.icone_user);

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

/*
        final DatabaseReference ref = databases.getReference("ratingbar").child("comercio")
                .child("")
                .child(comercio.getIdMercado());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    float rating = Float.parseFloat(dataSnapshot.getValue().toString());
                    // float d= (float) ((number*5) /100);
                    holder.rating.setRating(rating);
                         }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        holder.rating.setOnRatingBarChangeListener(new RatingBar_Comercio.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar_Comercio ratingBar, float rating, boolean fromUser) {
                if (fromUser) ref.setValue(rating);
            }
        });
*/

    }



    @Override
    public int getItemCount() {
        return comercios.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

       private TextView titulo;
        private TextView legenda;
        private TextView categoria;
        private  TextView estado;
        private  SimpleDraweeView capa;
        private  CircleImageView icone_user;
        private TextView nomeUsuario;
        private RatingBar rating;



        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.feed_texttitulo);
            legenda = itemView.findViewById(R.id.feed_textlegenda);
            categoria = itemView.findViewById(R.id.feed_textcategoria);
            estado = itemView.findViewById(R.id.feed_textestado);
            capa = itemView.findViewById(R.id.feed_capamercado);
            rating = itemView.findViewById(R.id.feed_rating_mercado);
            icone_user= itemView.findViewById(R.id.feed_user);
            nomeUsuario = itemView.findViewById(R.id.feed_nome);
        }
    }
}
