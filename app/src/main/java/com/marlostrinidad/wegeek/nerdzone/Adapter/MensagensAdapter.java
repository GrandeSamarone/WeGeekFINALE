package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Mensagem;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

/**
 * Created by fulanoeciclano on 10/05/2018.
 */

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {
    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO=1;


    public MensagensAdapter(List<Mensagem> lista, Context c) {
        this.mensagens=lista;
        this.context=c;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item=null;
        if(viewType==TIPO_REMETENTE){
          item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente,parent,false);

        }else if(viewType==TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario,parent,false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Mensagem mensagem =mensagens.get(position);
        String msg = mensagem.getMensagem();
         String nome =mensagem.getNome();
        final String imagem = mensagem.getImagem();
        String Img_perfil = mensagem.getImg_Usuario();

        if(holder.tempo!=null){
            holder.tempo.setText(mensagem.getTempo());
        } else{
            holder.tempo.setText("00/00/0000");
        }

        holder.mensagem.setText(msg);

    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
   Mensagem mensagem =mensagens.get(position);
   String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

   if (idUsuario.equals(mensagem.getIdUsuario())){
return  TIPO_REMETENTE;
   }
   return TIPO_DESTINATARIO;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mensagem;
        TextView tempo,tempodatadochat;
        SimpleDraweeView Usuario_Foto;

        public MyViewHolder(View itemView) {
            super(itemView);
            mensagem= itemView.findViewById(R.id.textMensagemtexto);
            Usuario_Foto = itemView.findViewById(R.id.chat_img_usuario);
            tempo = itemView.findViewById(R.id.tempodochat);

        }
    }

}
