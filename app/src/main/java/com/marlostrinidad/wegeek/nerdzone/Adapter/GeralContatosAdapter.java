package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by fulanoeciclano on 17/06/2018.
 */

public class GeralContatosAdapter extends RecyclerView.Adapter<GeralContatosAdapter.MyViewHolder>{

    private List<Usuario> contatos;
    private Context context;

    public GeralContatosAdapter(List<Usuario> listaContatos, Context c) {
        this.contatos=listaContatos;
        this.context= c;
    }

    public List<Usuario> getContatos(){
        return this.contatos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contatos,parent,false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Usuario usuario = contatos.get(position);
        boolean cabecalho = usuario.getNome().isEmpty();
        holder.nome.setText(usuario.getNome());

        if(usuario.getFoto()!=null){
            Uri uri= Uri.parse(usuario.getFoto());
            Glide.with(context)
                    .load(uri)
                    .into(holder.foto);
        }else {

                holder.foto.setImageResource(R.drawable.padrao);
            }



    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;

        public MyViewHolder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.imageViewFotoContato);
            nome= itemView.findViewById(R.id.textNomeContato);


        }
    }
}