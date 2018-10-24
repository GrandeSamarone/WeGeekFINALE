package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conversa;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fulanoeciclano on 12/05/2018.
 */

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

private List<Conversa> conversas;
private Context context;
private ChildEventListener ChildEventListenerperfil;
    private DatabaseReference database;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas=lista;
        this.context=c;
    }

        public List<Conversa> getConversas(){
            return this.conversas;
        }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemlist= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas,parent,false);
       return new MyViewHolder(itemlist);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
    Conversa conversa = conversas.get(position);

    holder.ultimaMensagem.setText(conversa.getUltimaMensagem());




        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        ChildEventListenerperfil=database.orderByChild("id").equalTo(conversa.getIdDestinatario())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

                holder.nome.setText(perfil.getNome());

                if(perfil.getFoto()!=null){
                    Uri uri = Uri.parse(perfil.getFoto());
                    Glide.with(context)
                            .load(uri)
                            .into( holder.foto );
                }else{
                    holder.foto.setImageResource(R.drawable.fundo_user);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
     private CircleImageView foto;
     private TextView nome,ultimaMensagem,tempo;

    public MyViewHolder(View itemView) {
        super(itemView);
     foto=itemView.findViewById(R.id.imageViewFotoConversa);

      nome= itemView.findViewById(R.id.textNomeConversa);
      ultimaMensagem = itemView.findViewById(R.id.UltimaMsgConversa);



    }


}


}
