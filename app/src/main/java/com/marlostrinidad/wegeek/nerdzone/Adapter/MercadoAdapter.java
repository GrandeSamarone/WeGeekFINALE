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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.database.FirebaseDatabase;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

/**
 * Created by fulanoeciclano on 29/08/2018.
 */

public class MercadoAdapter extends RecyclerView.Adapter<MercadoAdapter.MyViewHolder> {

    private List<Comercio> comercios;
    private Context context;
    private FirebaseDatabase databases=FirebaseDatabase.getInstance();
    public MercadoAdapter(List<Comercio> comercio, Context context){
        this.comercios = comercio;
        this.context=context;
    }
    public List<Comercio> getmercados(){
        return this.comercios;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mercado,parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

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

        TextView titulo;
        TextView legenda;
        TextView categoria;
        TextView estado;
        SimpleDraweeView capa;
        RatingBar rating;



        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.texttitulo);
            legenda = itemView.findViewById(R.id.textlegenda);
            categoria = itemView.findViewById(R.id.textcategoria);
            estado = itemView.findViewById(R.id.textestado);
           capa = itemView.findViewById(R.id.capamercado);
           rating = itemView.findViewById(R.id.rating_mercado);
        }
    }
}
