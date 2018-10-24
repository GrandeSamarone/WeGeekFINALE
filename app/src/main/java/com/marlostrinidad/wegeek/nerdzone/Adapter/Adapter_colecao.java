package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.varunest.sparkbutton.SparkButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_colecao extends RecyclerView.Adapter<Adapter_colecao.MyViewHolder> {

    private List<Conto> conto;
    private Context context;
    private ChildEventListener ChildEventListenercoleco;
    private DatabaseReference database;

    public Adapter_colecao(List<Conto> listconto,Context c){
        this.context=c;
        this.conto=listconto;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapterconto,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return conto.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView conto,nome,n_curtida,txt_add_colecao;
        private CircleImageView imgperfil;
        private SparkButton botaocurtir,botao_add_colecao;

        public MyViewHolder(View itemView) {
            super(itemView);

            conto = itemView.findViewById(R.id.conto_mensagem);
            txt_add_colecao = itemView.findViewById(R.id.txt_add_colecao);
            n_curtida = itemView.findViewById(R.id.conto_num_curit);
            nome = itemView.findViewById(R.id.conto_author);
            //  imgperfil = itemView.findViewById(R.id.conto_foto_autor);
            botaocurtir = itemView.findViewById(R.id.botaocurtirconto);
            botao_add_colecao = itemView.findViewById(R.id.botao_add_a_colecao);
        }
    }
}
