package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
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
import com.facebook.imagepipeline.common.ResizeOptions;
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

           Comercio loja = comercios.get(position);

           if(loja.getTitulo()!=null){
               holder.titulo.setText(loja.getTitulo());
           }

           if(loja.getDescricao()!=null){
               holder.legenda.setText(loja.getDescricao());
           }

           if(loja.getIcone()!=null){
               String stringcapa = loja.getIcone();
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
           capa = itemView.findViewById(R.id.capamercado);
           categoria=itemView.findViewById(R.id.textcategoria);
        }
    }
}
