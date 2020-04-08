package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Status;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Status_view_Banca_HQ extends RecyclerView.Adapter<Adapter_Status_view_Banca_HQ.MyViewHolder> {
    private List<Status> list_Status;
    private Context context;
    private String identificadorUsuario;
    private FirebaseFirestore db;
    private Dialog dialog;
    public Adapter_Status_view_Banca_HQ(List<Status> list,Context context){
        this.context=context;
        this.list_Status=list;
    }
    public List<Status> getStatus(){
        return this.list_Status;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_status_viu, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        final Status status = list_Status.get(position);
        db = FirebaseFirestore.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'/'MM'/'y kk:mm",java.util.Locale.getDefault());// MM'/'dd'/'y;
        String tempoMensagem = simpleDateFormat.format(status.getData());

        if (status.getView()!=null) {
            holder.quant_vizu.setText(String.valueOf(status.getView().size()));
        }else{
            holder.quant_vizu.setText("0");
        }
        if(status.getData()!=null){
            holder.data.setText(tempoMensagem);
        }
        String Stringcapa = status.getUrl_img();
        Picasso.get()
                .load(Stringcapa)
                .into(holder.capa);


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final MediaPlayer dialog_music = MediaPlayer.create(context,R.raw.navi_ei);
                dialog_music.start();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
                TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
                mensagem.setText("Deseja Realmente Deletar ?");
                builder.setView(view);
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_music.stop();


                        db.collection("Status_banca")
                                .document(status.getId_loja())
                                .collection("Status").whereEqualTo("id",status.getId())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Log.i("sodksdo763",document.getId());
                                        Status status = document.toObject(Status.class);
                                        db.collection("Status_banca").document(status.getId_loja())
                                                .collection("Status") .document(document.getId()).delete();



                                        //limpa da loja
                                        limpar(status.getId_loja(),status.getId());

                                        StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage()
                                                .child("imagens")
                                                .child("status")
                                                .child(status.getId_loja())
                                                .child(status.getNome_img_storage());
                                        storageReference.delete();
                                    }

                                    Toast.makeText(context, "Deletado com Sucesso.", Toast.LENGTH_SHORT).show();

                                }
                            }

                        });

                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        dialog_music.stop();
                    }
                });
                dialog = builder.create();
                dialog.show();

            }
        });

    }
    private void limpar(String id_loja, final String idStatus){

        db.collection("Banca_HQ").whereEqualTo("id",id_loja)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        final Map<String, Object> DeleteIdToArrayMap = new HashMap<>();
                        DeleteIdToArrayMap.put("status_ids", FieldValue.arrayRemove(idStatus));
                        db.collection("Banca_HQ").document(document.getId())
                                .update(DeleteIdToArrayMap);
                    }

                    Toast.makeText(context, "Deletado com Sucesso.", Toast.LENGTH_SHORT).show();

                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return list_Status.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView quant_vizu,data;
        CircleImageView capa;
        ImageView delete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quant_vizu = itemView.findViewById(R.id.quant_vizu);
            capa = itemView.findViewById(R.id.circleImageViewStatus);
            data=itemView.findViewById(R.id.data_vizu);
            delete=itemView.findViewById(R.id.img_delete);
        }
    }
}
