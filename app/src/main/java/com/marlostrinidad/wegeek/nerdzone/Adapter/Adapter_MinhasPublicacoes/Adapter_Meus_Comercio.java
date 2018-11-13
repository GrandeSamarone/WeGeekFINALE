package com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.marlostrinidad.wegeek.nerdzone.Activits.Minhas_Publicacoes;
import com.marlostrinidad.wegeek.nerdzone.Edit.Edit_Loja_Activity;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Detalhe_Mercado;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

public class Adapter_Meus_Comercio extends RecyclerView.Adapter<Adapter_Meus_Comercio.MyviewHolder> {

    private Context context;
    private List<Comercio> comercio;

    public Adapter_Meus_Comercio(List<Comercio> listamercado, Context c) {

        this.context = c;
        this.comercio = listamercado;
    }
    public List<Comercio> getmercados() {

        return this.comercio;
    }
        @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meus_mercado, parent,
                false);

        return new Adapter_Meus_Comercio.MyviewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(MyviewHolder holder, final int position) {

        final Comercio loja = comercio.get(position);
        holder.comercionome.setText(loja.getTitulo());
        holder.quantvisu.setText(String.valueOf(loja.getQuantVisualizacao()));
        Log.i("sdsds",String.valueOf(loja.getQuantVisualizacao()));
        List<String> urlFotos = loja.getFotos();
        if (urlFotos != null) {
            String stringcapa = urlFotos.get(0);
            if (stringcapa != null) {

                Uri uri = Uri.parse(stringcapa);
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                        .setProgressiveRenderingEnabled(true)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setTapToRetryEnabled(true)
                        .setImageRequest(request)
                        .setOldController(holder.comerciocapa.getController())
                        .build();
            /*mSimpleDraweeView.setController(controller);
            DraweeController controllerOne = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setAutoPlayAnimations(true)
                    .build();
*/
                holder.comerciocapa.setController(controller);
//            holder.foto.getHierarchy().setRetryImage(R.drawable.emoji_google_1f3ca_1f3fb);
            }
            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, Detalhe_Mercado.class);
                    it.putExtra("id_do_evento", loja.getIdMercado());
                    it.putExtra("UR_do_evento", loja.getEstado());
                    context.startActivity(it);
                }
            });

        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, Edit_Loja_Activity.class);
                it.putExtra("id_do_mercado", loja.getIdMercado());
                it.putExtra("UF_do_mercado", loja.getEstado());
                it.putExtra("CAT_do_mercado", loja.getCategoria());
                context.startActivity(it);
            }
        });

        holder.excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
                //configurando o titulo
                msgbox.setTitle("Excluir");
                // configurando a mensagem
                msgbox.setMessage("Deseja Realmente excluir  "+loja.getTitulo()+" ?");
                // Botao negativo

                msgbox.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int wich) {
                                loja.remover();
                                Intent it = new Intent(context, Minhas_Publicacoes.class);
                                context.startActivity(it);
                                Toast toast = Toast.makeText(context, "Deletado com sucesso!", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();  }

                        });


                msgbox.setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int wich) {

                            }
                        });
                msgbox.show();
            }
        });
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Comercio> listComercioAtualizado = getmercados();
                Comercio mercadoselecionado = listComercioAtualizado.get(position);
                Intent it = new Intent(context, Detalhe_Mercado.class);
                it.putExtra("mercadoelecionado", mercadoselecionado);
                context.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return comercio.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView comerciocapa;
            TextView comercionome,editar,excluir,quantvisu;
            LinearLayout eventolayout;
            CardView card;

            public MyviewHolder(View itemView) {
                super(itemView);

               quantvisu = itemView.findViewById(R.id.quantvisu);
                comerciocapa = itemView.findViewById(R.id.iconemercado);
                card = itemView.findViewById(R.id.cardcomercio);
                comercionome = itemView.findViewById(R.id.nomemercado);
                editar = itemView.findViewById(R.id.editarmercado);
                excluir = itemView.findViewById(R.id.excluirmercado);
        }
    }
}

