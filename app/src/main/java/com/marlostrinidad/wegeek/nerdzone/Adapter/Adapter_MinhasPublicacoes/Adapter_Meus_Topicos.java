package com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.TopicoLike;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.Topico.Detalhe_topico;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Adapter_Meus_Topicos extends RecyclerView.Adapter<Adapter_Meus_Topicos.MyViewHolder> {

    private List<Topico> listatopicos;
    private Context context;
    Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();
    public Adapter_Meus_Topicos(List<Topico> topico, Context c){
        this.context=c;
        this.listatopicos = topico;
    }
    public List<Topico> getTopicos(){
        return this.listatopicos;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meus_topico,parent,false);

        return new MyViewHolder(item);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Topico topico = listatopicos.get(position);

        holder.titulo.setText(topico.getTitulo());


        DatabaseReference database_topico = FirebaseDatabase.getInstance().getReference()
                .child("comentario-topico").child(topico.getUid());
        database_topico.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String total= String.valueOf(dataSnapshot.getChildrenCount());
                holder.total_comentario.setText(total);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Topico> listTopicoAtualizado = getTopicos();

                if (listTopicoAtualizado.size() > 0) {
                    Topico topicoselecionado = listTopicoAtualizado.get(position);
                    Intent it = new Intent(context, Detalhe_topico.class);
                    it.putExtra("topicoselecionado", topicoselecionado);
                    context.startActivity(it);


                }
            }
        });

        DatabaseReference topicoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("topico-likes")
                .child(topico.getUid());
        topicoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdLikes = 0;
                if(dataSnapshot.hasChild("qtdlikes")){
                    TopicoLike topicoLike = dataSnapshot.getValue(TopicoLike.class);
                    QtdLikes = topicoLike.getQtdlikes();
                }
                //Verifica se já foi clicado
                if( dataSnapshot.hasChild( usuariologado.getId() ) ){

                }else {
                }

                //Montar objeto postagem curtida
                TopicoLike like = new TopicoLike();
                like.setTopico(topico);
                like.setUsuario(usuariologado);
                like.setQtdlikes(QtdLikes);


                holder.num_curtida.setText(String.valueOf(like.getQtdlikes()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

holder.comentario_img.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Total de comentario eu sua publicação", Toast.LENGTH_LONG).show();
    }
});
    holder.topico_img.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Total de curtida em sua publicação", Toast.LENGTH_SHORT).show();
        }
    });
    }



    @Override
    public int getItemCount() {
        return listatopicos.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo,num_curtida,total_comentario;
        private LinearLayout click;
        private ImageView comentario_img,topico_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            comentario_img = itemView.findViewById(R.id.img_comentario);
            topico_img = itemView.findViewById(R.id.botaocurtirtopico);
            titulo = itemView.findViewById(R.id.meus_topico_titulo);
            click = itemView.findViewById(R.id.tituloline);
            num_curtida = itemView.findViewById(R.id.meus_topico_num_curit);
            total_comentario = itemView.findViewById(R.id.meus_topico_num_Coment);

        }
    }
}

