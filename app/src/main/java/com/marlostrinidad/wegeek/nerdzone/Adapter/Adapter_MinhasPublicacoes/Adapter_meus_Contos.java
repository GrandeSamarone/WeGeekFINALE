package com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_MinhasPublicacoes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

public class Adapter_meus_Contos extends RecyclerView.Adapter<Adapter_meus_Contos.MyviewHolder> {

    private List<Conto> listaconto;
    private Context context;
    Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();

    public Adapter_meus_Contos(List<Conto> conto,Context c){
        this.listaconto=conto;
        this.context=c;
    }
    public List<Conto> getConto(){
        return this.listaconto;}

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meus_conto,parent,false);

        return  new MyviewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyviewHolder holder, int position) {
        final Conto conto = listaconto.get(position);
        holder.conto.setText(conto.getMensagem());


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


                //Montar objeto postagem curtida
                ContoLike like = new ContoLike();
                like.setConto(conto);
                like.setUsuario(usuariologado);
                like.setQtdlikes(QtdLikes);


                holder.n_curtida.setText(String.valueOf(like.getQtdlikes()));
                Log.i("asasg", String.valueOf(like.getQtdlikes()));
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


                //Montar objeto postagem curtida
                Conto_colecao colecao = new Conto_colecao();
                colecao.setConto(conto);
                colecao.setUsuario(usuariologado);
                colecao.setQtdadd(QtdAdd);



                        holder.n_add_colecao.setText(String.valueOf(colecao.getQtdadd()));

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     holder.colecao.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Total de pessoas que adicionaram sua publicação em suas coleções", Toast.LENGTH_LONG).show();
    }
});
        holder.curtir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Total de pessoas que curtiram sua publicação", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaconto.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
      private ImageView colecao,curtir;
        private TextView conto,n_curtida,n_add_colecao,txt_add_colecao;
        public MyviewHolder(View itemView) {
            super(itemView);

            conto = itemView.findViewById(R.id.meus_contos_mensagem);
           colecao = itemView.findViewById(R.id.botao_add_a_colecao);
           curtir=itemView.findViewById(R.id.botaocurtirconto);
            n_curtida = itemView.findViewById(R.id.meus_conto_num_curt);
            n_add_colecao = itemView.findViewById(R.id.meus_conto_num_colecao);

            //  imgperfil = itemView.findViewById(R.id.conto_foto_autor);

        }
    }
}