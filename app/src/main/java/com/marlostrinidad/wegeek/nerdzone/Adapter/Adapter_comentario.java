package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comentario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.vanniktech.emoji.EmojiTextView;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_comentario extends RecyclerView.Adapter<Adapter_comentario.MyViewHolder> {

    private List<Comentario> comentarios;
    private Context context;
    private  String usuarioLogado =  UsuarioFirebase.getIdentificadorUsuario();
    private String Criador;

    public Adapter_comentario(List<Comentario> comentario,Context c){
        this.comentarios=comentario;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item=null;
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_commentario, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Comentario comentario = comentarios.get(position);

        holder.mensagem.setText(comentario.getText());


    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nome;
        private CircleImageView foto;
        private EmojiTextView mensagem;

        public MyViewHolder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.commentario_author);
            mensagem = itemView.findViewById(R.id.comentario_mensagem);
            foto = itemView.findViewById(R.id.commentario_foto);
        }
    }
}
