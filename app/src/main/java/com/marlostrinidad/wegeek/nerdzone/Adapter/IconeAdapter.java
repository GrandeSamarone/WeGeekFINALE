package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Model.Icones;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

/**
 * Created by fulanoeciclano on 20/05/2018.
 */

public class IconeAdapter   extends RecyclerView.Adapter<IconeAdapter.MyViewHolder> {

    View v;
    Context c;
    List<Icones> icones;
    public static final int SELECAO_ICONE = 34;

    public  IconeAdapter(Context context, List<Icones> Listicones) {
        this.c = context;
        this.icones = Listicones;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modelo_card_icone,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Icones icone  = icones.get(position);




        if(icone.getUrl() !=null){
            Uri uri = Uri.parse(icone.getUrl());

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setLocalThumbnailPreviewsEnabled(true)
                    .setProgressiveRenderingEnabled(true)
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .build();
            holder.draweeView.setController(controller);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(1f);
            roundingParams.setRoundAsCircle(true);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(c.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setRoundingParams(roundingParams)
                    .setProgressBarImage(new CircleProgressDrawable())
                    //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                    .build();
            holder.draweeView.setHierarchy(hierarchy);


        }else{
            holder.draweeView.setImageResource(R.drawable.gradiente_icone);
        }



        /*holder.carmodelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent=new Intent(c, Cadastrar_icon_nome_Activity.class);
                    intent.putExtra("caminho_foto",icones.get(position).getUrl());
                    c.startActivity(intent);
                Toast.makeText(c, "Carregando...", Toast.LENGTH_LONG).show();

            }
        });
*/

    }

    @Override
    public int getItemCount() {
        return icones.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{



        SimpleDraweeView draweeView;
        public MyViewHolder(View itemView) {
            super(itemView);
            draweeView= itemView.findViewById(R.id.drawee_foto);
        }
    }

}
