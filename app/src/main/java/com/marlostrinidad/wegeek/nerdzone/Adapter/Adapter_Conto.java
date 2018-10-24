package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.Model.ContoLike;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto_colecao;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Conto extends RecyclerView.Adapter<Adapter_Conto.MyviewHolder> {

    private List<Conto> listaconto;
    private Context context;
    Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();

    public Adapter_Conto(List<Conto> conto,Context c){
        this.listaconto=conto;
        this.context=c;
    }
    public List<Conto> getConto(){
       return this.listaconto;}

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapterconto,parent,false);

       return  new MyviewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder holder, int position) {
        final Conto conto = listaconto.get(position);
        holder.conto.setText(conto.getMensagem());


        DatabaseReference eventoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(conto.getIdauthor());
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario  user = dataSnapshot.getValue(Usuario.class);

            holder.author.setText(user.getNome());
            holder.author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(context, Perfil.class);
                    it.putExtra("id", user.getId());
                    context.startActivity(it);
                }
            });

            /*Glide.with(context)
                        .load(user.getFoto())
                        .into(holder.imgperfil );*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference topicoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto-likes")
                .child(conto.getUid());
        topicoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdLikes = 0;
                if(dataSnapshot.hasChild("qtdlikes")){
                    ContoLike contoLike = dataSnapshot.getValue(ContoLike.class);
                    QtdLikes = contoLike.getQtdlikes();
                }
                //Verifica se já foi clicado
                if( dataSnapshot.hasChild( usuariologado.getId() ) ){
                    holder.botaocurtir.setChecked(true);
                }else {
                    holder.botaocurtir.setChecked(false);
                }

                //Montar objeto postagem curtida
                final ContoLike like = new ContoLike();
                like.setConto(conto);
                like.setUsuario(usuariologado);
                like.setQtdlikes(QtdLikes);
               Log.i("asas", String.valueOf(usuariologado));
                //adicionar evento para curtir foto
                holder.botaocurtir.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if(buttonState){
                            like.Salvar();

                            holder.n_curtida.setText(String.valueOf(like.getQtdlikes()));
                        }else{
                            like.removerlike();
                            holder.n_curtida.setText(String.valueOf(like.getQtdlikes()));
                        }
                    }
                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                    }
                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {
                    }
                });
                holder.n_curtida.setText(String.valueOf(like.getQtdlikes()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference conto_add_colecao= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto-colecao")
                .child(conto.getUid());
        conto_add_colecao.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdAdd = 0;
                if (dataSnapshot.hasChild("qtdadd")) {
                    Conto_colecao conto_colecao = dataSnapshot.getValue(Conto_colecao.class);
                    QtdAdd = conto_colecao.getQtdadd();
                }
                //Verifica se já foi clicado
                if (dataSnapshot.hasChild(usuariologado.getId())) {
                    holder.botao_add_colecao.setChecked(true);
                   holder.txt_add_colecao.setText(R.string.adicionado_colecao);
                } else {
                    holder.botao_add_colecao.setChecked(false);
                   holder.txt_add_colecao.setText(R.string.adicionar_colecao);
                }

                //Montar objeto postagem curtida
                final Conto_colecao colecao = new Conto_colecao();
                colecao.setConto(conto);
                colecao.setUsuario(usuariologado);
                colecao.setQtdadd(QtdAdd);

                //adicionar evento para curtir foto
                holder.botao_add_colecao.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {
                            colecao.Salvar();
                            conto.AdicioneiConto();
                            holder.txt_add_colecao.setText(R.string.adicionado_colecao);
                        } else {
                            colecao.removercolecao();
                            holder.txt_add_colecao.setText(R.string.adicionar_colecao);
                        }
                    }

                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                    }

                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return listaconto.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        private TextView conto,nome,n_curtida,txt_add_colecao,author;
        private CircleImageView imgperfil;
        private SparkButton botaocurtir,botao_add_colecao;
        public MyviewHolder(View itemView) {
            super(itemView);
            author= itemView.findViewById(R.id.conto_author);
            conto = itemView.findViewById(R.id.conto_mensagem);
            txt_add_colecao = itemView.findViewById(R.id.txt_add_colecao);
            n_curtida = itemView.findViewById(R.id.conto_num_curit);
           // nome = itemView.findViewById(R.id.conto_author);
          //  imgperfil = itemView.findViewById(R.id.conto_foto_autor);
            botaocurtir = itemView.findViewById(R.id.botaocurtirconto);
            botao_add_colecao = itemView.findViewById(R.id.botao_add_a_colecao);

        }
    }
}
