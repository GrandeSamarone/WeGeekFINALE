package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.TopicoLike;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.Topico.Detalhe_topico;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Topico extends RecyclerView.Adapter<Adapter_Topico.MyViewHolder> {

   private List<Topico> listatopicos;
    private Context context;
    Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();
    public Adapter_Topico(List<Topico> topico, Context c){
        this.context=c;
        this.listatopicos = topico;
    }
    public List<Topico> getTopicos(){
        return this.listatopicos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptertopico,parent,false);

        return new MyViewHolder(item);

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Topico topico = listatopicos.get(position);

        holder.titulo.setText(topico.getTitulo());
        holder.mensagem.setText(topico.getMensagem());


        DatabaseReference eventoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(topico.getIdauthor());
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario  user = dataSnapshot.getValue(Usuario.class);
                    holder.autor.setText(user.getNome());

                    Glide.with(context)
                            .load(user.getFoto())
                            .into(holder.foto_autor );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        holder.clicktambem.setOnClickListener(new View.OnClickListener() {
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
                    holder.botaocurtir.setChecked(true);
                }else {
                    holder.botaocurtir.setChecked(false);
                }

                //Montar objeto postagem curtida
                final TopicoLike like = new TopicoLike();
               like.setTopico(topico);
                like.setUsuario(usuariologado);
                like.setQtdlikes(QtdLikes);

                //adicionar evento para curtir foto
                holder.botaocurtir.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if(buttonState){
                            like.Salvar();
                            holder.num_curtida.setText(String.valueOf(like.getQtdlikes()));
                        }else{
                            like.removerlike();
                            holder.num_curtida.setText(String.valueOf(like.getQtdlikes()));
                        }
                    }
                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                    }
                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {
                    }
                });
                holder.num_curtida.setText(String.valueOf(like.getQtdlikes()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return listatopicos.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo,mensagem,autor,num_curtida,total_comentario;
        private CircleImageView foto_autor;
        private LinearLayout click;
        private RelativeLayout clicktambem;
        private SparkButton botaocurtir;

        public MyViewHolder(View itemView) {
            super(itemView);
        titulo = itemView.findViewById(R.id.topico_titulo);
        mensagem = itemView.findViewById(R.id.topico_mensagem);
        autor  = itemView.findViewById(R.id.topico_autor);
        foto_autor = itemView.findViewById(R.id.topico_foto_autor);
         click = itemView.findViewById(R.id.tituloline);
         clicktambem = itemView.findViewById(R.id.ico);
         botaocurtir = itemView.findViewById(R.id.botaocurtirtopico);
         num_curtida = itemView.findViewById(R.id.topico_num_curit);
         total_comentario = itemView.findViewById(R.id.topico_num_Coment);

        }
    }
}
