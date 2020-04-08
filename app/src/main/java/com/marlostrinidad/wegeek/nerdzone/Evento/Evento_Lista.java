package com.marlostrinidad.wegeek.nerdzone.Evento;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Status_evento;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Evento_Adapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Evento.Status.VerStatusEvento;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Status;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Evento_Lista extends TrocarFundo {

    private Toolbar toolbar;
    private TextView textToolbar;
    private Adapter_Status_evento adapter_status;
    private FloatingActionButton novo_evento;
    private RecyclerView recyclerVieweventoPublico,rec_evento_status;
    private Evento_Adapter adapter;
    private ArrayList<Evento> lista_evento = new ArrayList<>();
    private ArrayList<Evento> lista_status = new ArrayList<>();
    private Dialog dialog;
    private AlertDialog alerta;
    private CircleImageView icone;
    private LinearLayoutManager mManager;
    private FirebaseFirestore db;
    private  long tempo_atual;
    private RelativeLayout rel_status;
    private StorageReference storageReference;
    private ListenerRegistration registration,registration_status;
    private String Id_doc_evento;
    SharedPreferences sPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento__lista);


        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar=findViewById(R.id.app_toolbar_title_secundario);
        textToolbar.setText("Próximos Eventos");
        setSupportActionBar(toolbar);
        //Configuraçoes iniciais
        rel_status=findViewById(R.id.rel_status);
        tempo_atual = System.currentTimeMillis();
        db = FirebaseFirestore.getInstance();
        icone = findViewById(R.id.icone_user_toolbar);

        rec_evento_status=findViewById(R.id.RecycleViewstatus_eventos);
        rec_evento_status.setHasFixedSize(true);

        @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager_status =
                new LinearLayoutManager(Evento_Lista.this, LinearLayoutManager.HORIZONTAL, false);
        rec_evento_status.setLayoutManager(layoutManager_status);
        adapter_status = new Adapter_Status_evento(lista_status, this);
        rec_evento_status.setAdapter(adapter_status);

        recyclerVieweventoPublico = findViewById(R.id.recycleview_eventos);
        recyclerVieweventoPublico.setHasFixedSize(true);
        //recycleview
        mManager = new LinearLayoutManager(this);
        recyclerVieweventoPublico.setLayoutManager(mManager);
        adapter = new Evento_Adapter(lista_evento, this);

        recyclerVieweventoPublico.setAdapter(adapter);

        novo_evento = findViewById(R.id.buton_novo_evento);
        novo_evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Evento_Lista.this, Cadastrar_Novo_Evento.class);
                startActivity(it);
                finish();



            }
        });
        //Aplicar Evento click
        recyclerVieweventoPublico.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerVieweventoPublico, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Evento> listComercioAtualizado = adapter.getevento();

                if (lista_evento.size() > 0) {
                    Evento evento_selecionado = listComercioAtualizado.get(position);
                    Intent it = new Intent(Evento_Lista.this, DetalheEvento.class);
                    it.putExtra("id",evento_selecionado.getId());
                    startActivity(it);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        rec_evento_status.addOnItemTouchListener(new RecyclerItemClickListener(this,
                rec_evento_status, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Evento> list_status_atualizado = adapter_status.getStatus();

                if (list_status_atualizado.size() > 0) {
                    Evento status_selecionado = list_status_atualizado.get(position);
                    Intent it = new Intent(Evento_Lista.this, VerStatusEvento.class);
                    it.putExtra("userid",status_selecionado.getIdauthor());
                    it.putExtra("id_loja",status_selecionado.getId());
                    it.putExtra("nome_loja",status_selecionado.getTitulo());
                    startActivity(it);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Recuperar_Evento_geral();
        Recuperar_status_geral();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration!= null) {
            registration.remove();
            registration = null;
        }
        if (registration_status!= null) {
            registration_status.remove();
            registration_status = null;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (registration!= null) {
            registration.remove();
            registration = null;
        }
    }

    public void Recuperar_Evento_geral(){
        lista_evento.clear();
        Query query= db.collection("Evento")
                .whereEqualTo("analizado",true);
        registration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Evento evento = change.getDocument().toObject(Evento.class);
                    Log.i("sdsdsd", change.getDocument().getId());
                    long timecurrent = System.currentTimeMillis();
                    Id_doc_evento = change.getDocument().getId();

                    if (evento.getData_fim_min() > timecurrent) {

                        switch (change.getType()) {
                            case ADDED:
                                lista_evento.add(0, evento);

                                if (lista_evento.size() > 0) {
                                    //  linear_nada_cadastrado.setVisibility(View.GONE);
                                }
                                adapter.notifyDataSetChanged();
                                Log.d("ad", "New city: " + change.getDocument().getData());
                                break;
                            case MODIFIED:
                                for (Evento ct : lista_evento) {

                                    if (evento.getId().equals(ct.getId())) {
                                        lista_evento.remove(ct);
                                        break;
                                    }
                                }
                                lista_evento.add(0, evento);
                                if (lista_evento.size() > 0) {

                                }
                                adapter.notifyDataSetChanged();
                                Log.d("md", "Modified city: " + change.getDocument().getData());
                                break;
                            case REMOVED:
                                for (Evento ct : lista_evento) {

                                    if (evento.getId().equals(ct.getId())) {

                                        lista_evento.remove(ct);
                                        break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                Log.d("rem", "Removed city: " + change.getDocument().getData());
                                break;
                        }
                    }
                }
            }
        });


    }
    public void Recuperar_status_geral( ){
        lista_status.clear();
        Query query= db.collection("Evento").orderBy("ultima_foto_data", Query.Direction.ASCENDING);
        registration_status=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd", change.getDocument().getId());
                    Evento status = change.getDocument().toObject(Evento.class);

                    //deleta status vencido
                    Verificar_Status_vencido(status.getId());

                    if (status.getUrl_status_boolean()) {
                        if (status.getStatus_ids().size() > 0) {
                            switch (change.getType()) {
                                case ADDED:


                                    lista_status.add(0, status);
                                    if (lista_status.size() > 0) {
                                        rel_status.setVisibility(View.VISIBLE);
                                    }
                                    adapter_status.notifyDataSetChanged();
                                    Log.d("ad", "New city: " + change.getDocument().getData());
                                    break;

                                case MODIFIED:
                                    for (Evento ct : lista_status) {

                                        if (status.getId().equals(ct.getId())) {
                                            lista_status.remove(ct);
                                            break;
                                        }
                                    }
                                    lista_status.add(0, status);
                                    if (lista_status.size() > 0) {
                                        //linear_nada_cadastrado.setVisibility(View.GONE);
                                    }
                                    adapter_status.notifyDataSetChanged();
                                    Log.d("md", "Modified city: " + change.getDocument().getData());
                                    break;
                                case REMOVED:
                                    for (Evento ct : lista_status) {

                                        if (status.getId().equals(ct.getId())) {
                                            lista_status.remove(ct);

                                            break;
                                        }
                                    }
                                    adapter_status.notifyDataSetChanged();
                                    Log.d("rem", "Removed city: " + change.getDocument().getData());
                                    break;
                            }
                        }
                    }
                }
            }
        });

    }
    private void Verificar_Status_vencido(final String id_loja){
        db.collection("Status_evento")
                .document(id_loja)
                .collection("Status")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        long timecurrent = System.currentTimeMillis();
                        final Status status = document.toObject(Status.class);
                        if (document.exists()) {
                            if (status.getId_loja().equals(id_loja) && (timecurrent > status.getData_fim())) {
                                Log.i("odsfko445", status.getId_loja());
                                db.collection("Status_evento").document(status.getId_loja())
                                        .collection("Status").document(document.getId()).delete();
                                storageReference = ConfiguracaoFirebase.getFirebaseStorage()
                                        .child("imagens")
                                        .child("status_evento")
                                        .child(status.getId_loja())
                                        .child(status.getNome_img_storage());
                                storageReference.delete();

                                db.collection("Evento").whereEqualTo("id",status.getId_loja())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot document : task.getResult()) {

                                            final Map<String, Object> DeleteIdToArrayMap = new HashMap<>();
                                            DeleteIdToArrayMap.put("status_ids", FieldValue.arrayRemove(status.getId()));
                                            db.collection("Evento").document(document.getId())
                                                    .update(DeleteIdToArrayMap);


                                            adapter_status.notifyDataSetChanged();
                                        }

                                    }
                                });

                            }
                        }

                    }

                }

            }
        });

    }
    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                Intent it = new Intent(Evento_Lista.this,MainActivity.class);
                startActivity(it);
                finish();
                break;
            case R.id.menufiltro:

                break;


            default:break;
        }

        return true;
    }

}
