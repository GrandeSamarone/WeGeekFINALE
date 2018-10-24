package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comentario;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Comentario comentario = comentarios.get(position);
      Criador=comentario.getId_author();

        holder.mensagem.setText(comentario.getText());

        DatabaseReference eventoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(comentario.getId_author());
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario user = dataSnapshot.getValue(Usuario.class);
                holder.nome.setText(user.getNome());

                Glide.with(context)
                        .load(user.getFoto())
                        .into(holder.foto );


                if(!usuarioLogado.equals(user.getId())){
                   holder.nome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent it = new Intent(context, Perfil.class);
                            it.putExtra("id",user.getId());
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(it);
                        }
                    });
                    holder.foto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(context, Perfil.class);
                            it.putExtra("id",user.getId());
                            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(it);
                        }
                    });
                }else {
                   holder.nome.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent it = new Intent(context, MinhaConta.class);
                            it.putExtra("id",user.getId());
                            context.startActivity(it);
                        }
                    });
                    holder.foto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(context, MinhaConta.class);
                            it.putExtra("id",user.getId());
                            context.startActivity(it);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
