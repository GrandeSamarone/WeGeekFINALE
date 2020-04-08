package com.marlostrinidad.wegeek.nerdzone.HQ.Banca;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Status_view_Banca_HQ;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Status_viu;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Model.Status;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;

public class Info_Status_Banca extends TrocarFundo {


    private RecyclerView rec_status_viu;
    private Toolbar toolbar;
    private TextView textToolbar,nao_assinantes;
    private Adapter_Status_view_Banca_HQ adapter_status;
    private ArrayList<Status> lista_status = new ArrayList<>();
    private ListenerRegistration registration_status;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String id_loja_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__status__banca);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar = findViewById(R.id.app_toolbar_title_secundario);
        textToolbar.setText("Vizualizações");
        setSupportActionBar(toolbar);

        //configuracoes Basicas
        nao_assinantes=findViewById(R.id.nao_assinantes);
        id_loja_status= getIntent().getStringExtra("id_loja");
        db = FirebaseFirestore.getInstance();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        //    identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        rec_status_viu = findViewById(R.id.rec_status_viu);
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager_status =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rec_status_viu.setLayoutManager(layoutManager_status);
        adapter_status = new Adapter_Status_view_Banca_HQ(lista_status, this);
        rec_status_viu.setAdapter(adapter_status);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Recuperar_status_geral();
    }

    @Override
    protected void onStop() {
        super.onStop();
        registration_status.remove();
    }

    public void Recuperar_status_geral() {
        Log.i("sodksodksokd32",id_loja_status);
        lista_status.clear();
        Query query = db.collection("Status_banca")
                .document(id_loja_status)
                .collection("Status")
                .orderBy("data", Query.Direction.ASCENDING);
        ;
        registration_status = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd", change.getDocument().getId());
                    Status status = change.getDocument().toObject(Status.class);
                    switch (change.getType()) {
                        case ADDED:
                            lista_status.add(0, status);
                            if (lista_status.size() > 0) {
                                nao_assinantes.setVisibility(View.GONE);
                            }
                            adapter_status.notifyDataSetChanged();
                            Log.d("ad", "New city: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            for (Status ct : lista_status) {

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
                            for (Status ct : lista_status) {

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
        });

    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}
