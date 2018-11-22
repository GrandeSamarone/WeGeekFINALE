package com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopicoAdapterPagInicial  extends RecyclerView.Adapter<TopicoAdapterPagInicial.MyviewHolder> {

    private Context context;
    private List<Topico> topicos;
    private DatabaseReference database;
    private ChildEventListener ChildEventListenerperfil;

    public TopicoAdapterPagInicial(List<Topico> listatopicos, Context c){

        this.context=c;
        this.topicos=listatopicos;
    }

    public List<Topico> getTopicos(){
        return this.topicos;
    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_topico_pag_inicial,parent,false);

        return new MyviewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {

        final Topico top = topicos.get(position);
        holder.topiconome.setText(top.getTitulo());

        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        ChildEventListenerperfil=database.orderByChild("id").equalTo(top.getIdauthor())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Usuario perfil = dataSnapshot.getValue(Usuario.class );
                        assert perfil != null;

                        holder.author_nome.setText(perfil.getNome());

                        if(perfil.getFoto()!=null){
                            Uri uri = Uri.parse(perfil.getFoto());
                            Glide.with(context.getApplicationContext())
                                    .load(uri)
                                    .into( holder.icone );
                        }else{
                            holder.icone.setImageResource(R.drawable.fundo_user);
                        }

                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Usuario perfil = dataSnapshot.getValue(Usuario.class );
                        assert perfil != null;


                        if(perfil.getFoto()!=null){
                            Uri uri = Uri.parse(perfil.getFoto());
                            Glide.with(context.getApplicationContext())
                                    .load(uri)
                                    .into( holder.icone );
                        }else{
                            holder.icone.setImageResource(R.drawable.fundo_user);
                        }
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
        return topicos.size();
    }






    public class MyviewHolder extends RecyclerView.ViewHolder {

        CircleImageView icone;
        TextView topiconome,author_nome;
        LinearLayout eventolayout;
        CardView card;
        public MyviewHolder(View itemView) {
            super(itemView);
            author_nome=itemView.findViewById(R.id.topico_autor_pag_inicial);
          topiconome = itemView.findViewById(R.id.titulotopico_adapter);
         icone = itemView.findViewById(R.id.iconetopico_adapter);


        }
    }
}