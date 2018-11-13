package com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.Minhas_Publicacoes;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Edit.Edit_Topico_Activity;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.TopicoLike;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.Topico.Detalhe_topico;

import java.util.List;

public class Adapter_Meus_Topicos extends RecyclerView.Adapter<Adapter_Meus_Topicos.MyViewHolder> {

    private List<Topico> listatopicos;
    private Context context;
    Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();
    private  String identificadorUsuario =  UsuarioFirebase.getIdentificadorUsuario();
    private Usuario user;
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




        DatabaseReference eventoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(identificadorUsuario);
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(Usuario.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });











        holder.EditarTopico.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent it = new Intent(context, Edit_Topico_Activity.class);
            it.putExtra("id_topico",topico.getUid());
            context.startActivity(it);
        }
    });

        holder.ExcluirTopico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                AlertDialog.Builder msgbox = new AlertDialog.Builder(context);
                //configurando o titulo
                msgbox.setTitle("Excluir");
                // configurando a mensagem
                msgbox.setMessage("Deseja Realmente excluir o Tópico "+topico.getTitulo()+" ?");
                // Botao negativo

                msgbox.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int wich) {
                                topico.remover();
                                int qtdTopicos = user.getTopicos() - 1;
                                user.setTopicos(qtdTopicos);
                                user.atualizarQtdTopicos();
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



    @Override
    public int getItemCount() {
        return listatopicos.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titulo,num_curtida,total_comentario;
        private LinearLayout click;
        private TextView EditarTopico,ExcluirTopico;
        private ImageView comentario_img,topico_img;

        public MyViewHolder(View itemView) {
            super(itemView);
            ExcluirTopico = itemView.findViewById(R.id.excluirtopico);
            EditarTopico = itemView.findViewById(R.id.editartopico);
            comentario_img = itemView.findViewById(R.id.img_comentario);
            topico_img = itemView.findViewById(R.id.botaocurtirtopico);
            titulo = itemView.findViewById(R.id.meus_topico_titulo);
            click = itemView.findViewById(R.id.tituloline);
            num_curtida = itemView.findViewById(R.id.meus_topico_num_curit);
            total_comentario = itemView.findViewById(R.id.meus_topico_num_Coment);

        }
    }
}

