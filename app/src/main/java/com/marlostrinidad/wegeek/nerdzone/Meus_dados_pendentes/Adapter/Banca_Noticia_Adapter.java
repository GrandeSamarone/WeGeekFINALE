package com.marlostrinidad.wegeek.nerdzone.Meus_dados_pendentes.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
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

public class Banca_Noticia_Adapter extends RecyclerView.Adapter<Banca_Noticia_Adapter.MyViewHolder> {

    private List<Banca_noticia> banca;
    private Context context;
    public Banca_Noticia_Adapter(List<Banca_noticia> lista_banca, Context context){
        this.banca = lista_banca;
        this.context=context;
    }
    public List<Banca_noticia> getBanca(){
        return this.banca;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_banca_user,parent,false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Banca_noticia list_banca = banca.get(position);
        if(list_banca!=null) {
            if(list_banca.getAnalizado()!=null) {
                if (list_banca.getAnalizado()) {
                    holder.resultado.setText("Publicado");
                    holder.botao.setBackgroundColor(context.getResources().getColor(R.color.verde));
                } else {
                    holder.resultado.setText("em análise");
                    holder.botao.setBackgroundColor(context.getResources().getColor(R.color.laranja));

                }
            }
            if (list_banca.getNome_banca() != null) {
                holder.titulo.setText(list_banca.getNome_banca());
            }

            if (list_banca.getIcone_banca() != null) {
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
    }



    @Override
    public int getItemCount() {
        return banca.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo,resultado;
        SimpleDraweeView capa;
        CardView botao;


        public MyViewHolder(View itemView) {
            super(itemView);
            resultado=itemView.findViewById(R.id.resultado);
            titulo = itemView.findViewById(R.id.nomeevento);
            capa = itemView.findViewById(R.id.iconeevento);
            botao=itemView.findViewById(R.id.botao_ver_loja);
        }
    }
}
