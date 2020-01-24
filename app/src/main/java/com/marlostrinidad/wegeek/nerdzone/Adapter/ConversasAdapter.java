package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conversa;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

/**
 * Created by fulanoeciclano on 12/05/2018.
 */

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversaChatparticulars;
    private Context context;
    private String identificadorUsuario;
    private FirebaseFirestore db;
    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversaChatparticulars =lista;
        this.context=c;
    }

        public List<Conversa> getConversaChatparticulars(){
            return this.conversaChatparticulars;
        }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemlist= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversas,parent,false);
       return new MyViewHolder(itemlist);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
    Conversa chat = conversaChatparticulars.get(position);
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
   holder.ultimaMensagem.setText(chat.getUltimamensagem());

        String stringcapa=chat.getIcone_destinatario();
        if (stringcapa != null) {
            Picasso.get()
                    .load(stringcapa)
                    .into(holder.foto);
        }

      holder.nome.setText(chat.getNome_destinatario());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'/'MM'/'y",java.util.Locale.getDefault());// MM'/'dd'/'y;

        String tempoMensagem = simpleDateFormat.format(chat.getTempo());

        holder.tempo.setText(tempoMensagem);

      holder.ultimaMensagem.setText(chat.getUltimamensagem());

        db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Usuarios").document(chat.getId_destinatario());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                 //   Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Usuario usuario = snapshot.toObject(Usuario.class);
                    if(usuario.getOnline().equals("online")){
                        holder.online.setText("Online");
                     //   holder.foto.setBorderColor(context.getResources().getColor(R.color.primary));
                    }else{
                     //   holder.foto.setBorderColor(context.getResources().getColor(R.color.cinzaclaro));
                      holder.online.setText("");
                    }
                } else {
                 //   Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return conversaChatparticulars.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
     private CircleImageView foto;
     private TextView nome,ultimaMensagem,tempo,online,quant_msg;

    public MyViewHolder(View itemView) {
        super(itemView);
        quant_msg=itemView.findViewById(R.id.quant);
        online=itemView.findViewById(R.id.online);
     foto=itemView.findViewById(R.id.imageViewFotoConversa);
    tempo=itemView.findViewById(R.id.data_conversa);
      nome= itemView.findViewById(R.id.textNomeConversa);
      ultimaMensagem = itemView.findViewById(R.id.UltimaMsgConversa);



    }


}


}
