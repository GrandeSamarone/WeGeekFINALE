package com.marlostrinidad.wegeek.nerdzone.Mercado;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
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
import com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial.Adapter_item_tela_inicial;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_status;
import com.marlostrinidad.wegeek.nerdzone.Adapter.MercadoAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Status.Ver_StatusActivity;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Item_loja;
import com.marlostrinidad.wegeek.nerdzone.Model.Status;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;


public class MercadoActivity extends TrocarFundo {
    private static final String OPCAO_PREFERENCIA = "opcao_preferencia";
    private SharedPreferences opcao_sPreferences=null;
    private Toolbar toolbar;
    private TextView textToolbar;
    private Adapter_status adapter_status;
    private Adapter_item_tela_inicial adapter_item;
    private FloatingActionButton novoMercado;
    private RecyclerView recyclerViewMercadoPublico,RecycleView_status,RecycleView_itens;
    private MercadoAdapter adapter;
    private Comercio comercio;
    private ArrayList<Item_loja> lista_itens = new ArrayList<>();
    private ArrayList<Comercio> listamercado = new ArrayList<>();
    private ArrayList<Comercio> lista_status = new ArrayList<>();
    private Dialog dialog;
    private AlertDialog alerta;
    private CircleImageView icone;
    private LinearLayoutManager mManager,mManager_iten;
    private FirebaseFirestore db;
    private  long tempo_atual;
    private RelativeLayout rel_status;
    private  StorageReference storageReference;
    private ListenerRegistration registration,registration_status,registration_itens;
    private SharedPreferences dados_opcao;
    SharedPreferences sPreferences = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_mercado);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar=findViewById(R.id.app_toolbar_title_secundario);
        setSupportActionBar(toolbar);




        //Configuraçoes iniciais

        dados_opcao = getSharedPreferences(OPCAO_PREFERENCIA, MODE_PRIVATE);
        rel_status=findViewById(R.id.rel_status);
        tempo_atual = System.currentTimeMillis();
        db = FirebaseFirestore.getInstance();
        icone = findViewById(R.id.icone_user_toolbar);
        comercio = new Comercio();
        RecycleView_itens=findViewById(R.id.recycleview_itens);
        adapter_item=new Adapter_item_tela_inicial(lista_itens,getApplicationContext());
        //recycleview
        @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager_itens =
                new LinearLayoutManager(MercadoActivity.this, LinearLayoutManager.VERTICAL,false);
        RecycleView_itens.setLayoutManager(layoutManager_itens);
        RecycleView_itens.setHasFixedSize(true);
        RecycleView_itens.setAdapter(adapter_item);

        RecycleView_status=findViewById(R.id.RecycleViewstatus);
        RecycleView_status.setHasFixedSize(true);

        @SuppressLint("WrongConstant") RecyclerView.LayoutManager layoutManager_status =
                new LinearLayoutManager(MercadoActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RecycleView_status.setLayoutManager(layoutManager_status);
        adapter_status = new Adapter_status(lista_status, this);
        RecycleView_status.setAdapter(adapter_status);

        recyclerViewMercadoPublico = findViewById(R.id.recycleviewmercado);
        recyclerViewMercadoPublico.setHasFixedSize(true);
        //recycleview
        mManager = new LinearLayoutManager(this);
        recyclerViewMercadoPublico.setLayoutManager(mManager);
        adapter = new MercadoAdapter(listamercado, this);

        recyclerViewMercadoPublico.setAdapter(adapter);

        novoMercado = findViewById(R.id.buton_novo_mercado);
        novoMercado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MercadoActivity.this, Cadastrar_Novo_MercadoActivity.class);
                startActivity(it);
                finish();



            }
        });
        //Aplicar Evento click em loja
        recyclerViewMercadoPublico.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerViewMercadoPublico, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Comercio> listComercioAtualizado = adapter.getmercados();

                if (listComercioAtualizado.size() > 0) {
                    Comercio mercadoselecionado = listComercioAtualizado.get(position);
                    Intent it = new Intent(MercadoActivity.this, Detalhe_Loja.class);
                    it.putExtra("id",mercadoselecionado.getId());
                    //  it.putExtra("icone_loja",mercadoselecionado.getIcone());
                    //  it.putExtra("token_dono_loja",mercadoselecionado.getToken_author());
                    //   it.putExtra("id_dono_loja",mercadoselecionado.getIdauthor());
                    //  it.putExtra("categoria",mercadoselecionado.getCategoria());
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
        //Aplicar Evento click em item
        RecycleView_itens.addOnItemTouchListener(new RecyclerItemClickListener(this,
                RecycleView_itens, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Item_loja> listComercioAtualizado = adapter_item.get_item();

                if (listComercioAtualizado.size() > 0) {
                    Item_loja item_selecionado = listComercioAtualizado.get(position);
                    Intent it = new Intent(MercadoActivity.this, Detalhe_item_comercio.class);
                    it.putExtra("nome", item_selecionado.getTitulo());
                    it.putExtra("desc", item_selecionado.getDescricao());
                    it.putExtra("foto", item_selecionado.getItem_foto());
                    it.putExtra("preco", item_selecionado.getPreco());
                    it.putExtra("id_loja", item_selecionado.getId_loja());
                    it.putExtra("nome_loja", item_selecionado.getTitulo());
                    it.putExtra("id_item", item_selecionado.getId());
                    it.putExtra("categoria", item_selecionado.getCategoria());
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
        RecycleView_status.addOnItemTouchListener(new RecyclerItemClickListener(this,
                RecycleView_status, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Comercio> list_status_atualizado = adapter_status.getStatus();

                if (list_status_atualizado.size() > 0) {
                    Comercio status_selecionado = list_status_atualizado.get(position);
                    Intent it = new Intent(MercadoActivity.this, Ver_StatusActivity.class);
                    it.putExtra("userid",status_selecionado.getIdauthor());
                    it.putExtra("id_loja",status_selecionado.getId());
                    it.putExtra("nome_loja",status_selecionado.getTitulo());
                    it.putExtra("icone_loja",status_selecionado.getIcone());
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

        int[] location = new int[2];


        //Verifica se é a primeira vez da instalacao
        sPreferences = getSharedPreferences("primeiravez_Comercio", MODE_PRIVATE);
        if (sPreferences.getBoolean("primeiravez_Comercio", true)) {
            sPreferences.edit().putBoolean("primeiravez_Comercio", false).apply();
            Primeira_Vez();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filtro,menu);

        return super.onCreateOptionsMenu(menu);
    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menufiltro:

                SharedPreferences sharedPreferences =getSharedPreferences(OPCAO_PREFERENCIA, 0);
                final SharedPreferences.Editor editor = sharedPreferences.edit();



                final MediaPlayer dialog_music = MediaPlayer.create(MercadoActivity.this,R.raw.navi_veja);
                dialog_music.start();
                AlertDialog.Builder builder = new AlertDialog.Builder(MercadoActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(MercadoActivity.this);
                final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
                TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
                mensagem.setText("Deseja mostrar qual das opções abaixo?");
                builder.setView(view);
                builder.setPositiveButton("COMÉRCIOS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("opcao_comercio", "Comércios");
                        editor.apply();
                        RecycleView_itens.setVisibility(GONE);
                        Toast.makeText(MercadoActivity.this, " Carregadas com Sucesso", Toast.LENGTH_SHORT).show();

                        recyclerViewMercadoPublico.setVisibility(View.VISIBLE);
                        dialog.cancel();
                        dialog_music.stop();
                    }
                }).setNegativeButton("PRODUTOS/SERVIÇOS", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        editor.putString("opcao_comercio", "Produtos");
                        editor.apply();
                        dialog_music.stop();
                        Toast.makeText(MercadoActivity.this, " Carregadas com Sucesso", Toast.LENGTH_SHORT).show();
                        recyclerViewMercadoPublico.setVisibility(GONE);
                        RecycleView_itens.setVisibility(View.VISIBLE);
                        dialog.cancel();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            case android.R.id.home:

                finish();

                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Recuperar_Mercado_geral();
        Recuperar_status_geral();
        Recuperar_Itens_geral();
        String opcao = dados_opcao.getString("opcao_comercio", "");
        if(opcao.equals("Comércios")){
            textToolbar.setText("Comércios");
            RecycleView_itens.setVisibility(GONE);
            recyclerViewMercadoPublico.setVisibility(View.VISIBLE);
        }else{
            textToolbar.setText("Produtos e Serviços");
            recyclerViewMercadoPublico.setVisibility(GONE);
            RecycleView_itens.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration!= null) {
            registration.remove();
            registration = null;
        } if (registration_itens!= null) {
            registration_itens.remove();
            registration_itens = null;
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
        if (registration_itens!= null) {
            registration_itens.remove();
            registration_itens = null;
        }
        if (registration_status!= null) {
            registration_status.remove();
            registration_status = null;
        }

    }

    public void Recuperar_Mercado_geral(){
        listamercado.clear();
        Query query= db.collection("Comercio")
                .whereEqualTo("analizado",true);
        registration=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd",change.getDocument().getId());
                    Comercio comercio = change.getDocument().toObject(Comercio.class);
                    switch (change.getType()) {
                        case ADDED:
                            listamercado.add(0, comercio);

                            if (listamercado.size() > 0) {
                                //  linear_nada_cadastrado.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("ad", "New city: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            for (Comercio ct : listamercado) {

                                if(comercio.getId().equals(ct.getId())){
                                    listamercado.remove(ct);
                                    break;
                                }
                            }
                            listamercado.add(0, comercio);
                            if (listamercado.size() > 0) {
                                //linear_nada_cadastrado.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("md", "Modified city: " + change.getDocument().getData());
                            break;
                        case REMOVED:
                            for (Comercio ct : listamercado) {

                                if(comercio.getId().equals(ct.getId())){

                                    listamercado.remove(ct);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("rem", "Removed city: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });

    }
    public void Recuperar_Itens_geral(){
        lista_itens.clear();
        Query query= db.collection("Produtos_Geral")
                .whereEqualTo("analizado",true);
        registration_itens=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd",change.getDocument().getId());
                    Item_loja item_loja = change.getDocument().toObject(Item_loja.class);
                    switch (change.getType()) {
                        case ADDED:
                            lista_itens.add(0, item_loja);

                            if (lista_itens.size() > 0) {
                                //  linear_nada_cadastrado.setVisibility(View.GONE);
                            }
                            adapter_item.notifyDataSetChanged();
                            Log.d("ad", "New city: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            for (Item_loja ct : lista_itens) {

                                if(item_loja.getId().equals(ct.getId())){
                                    lista_itens.remove(ct);
                                    break;
                                }
                            }
                            lista_itens.add(0, item_loja);
                            if (lista_itens.size() > 0) {
                                //linear_nada_cadastrado.setVisibility(View.GONE);
                            }
                            adapter_item.notifyDataSetChanged();
                            Log.d("md", "Modified city: " + change.getDocument().getData());
                            break;
                        case REMOVED:
                            for (Item_loja ct : lista_itens) {

                                if(item_loja.getId().equals(ct.getId())){

                                    lista_itens.remove(ct);
                                    break;
                                }
                            }
                            adapter_item.notifyDataSetChanged();
                            Log.d("rem", "Removed city: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });

    }
    public void Recuperar_status_geral( ){
        lista_status.clear();
        Query query= db.collection("Comercio").orderBy("ultima_foto_data", Query.Direction.ASCENDING);
        registration_status=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd", change.getDocument().getId());
                    Comercio status = change.getDocument().toObject(Comercio.class);

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
                                    for (Comercio ct : lista_status) {

                                        if (status.getId().equals(ct.getId())) {
                                            lista_status.remove(ct);
                                            break;
                                        }
                                    }
                                    lista_status.add(0, status);
                                    adapter_status.notifyDataSetChanged();
                                    Log.d("md", "Modified city: " + change.getDocument().getData());
                                    break;
                                case REMOVED:
                                    for (Comercio ct : lista_status) {

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
        db.collection("Status_comercio")
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
                                db.collection("Status_comercio").document(status.getId_loja())
                                        .collection("Status").document(document.getId()).delete();
                                storageReference = ConfiguracaoFirebase.getFirebaseStorage()
                                        .child("imagens")
                                        .child("status")
                                        .child(status.getId_loja())
                                        .child(status.getNome_img_storage());
                                storageReference.delete();

                                db.collection("Comercio").whereEqualTo("id",status.getId_loja())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            final Map<String, Object> DeleteIdToArrayMap = new HashMap<>();
                                            DeleteIdToArrayMap.put("status_ids", FieldValue.arrayRemove(status.getId()));
                                            db.collection("Comercio").document(document.getId())
                                                    .update(DeleteIdToArrayMap);

                                            adapter_status.notifyDataSetChanged();
                                        }

                                    }
                                });

                            }
                        }else{

                        }

                    }

                }

            }
        });

    }
    private void Primeira_Vez() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(MercadoActivity.this);
        final View view = layoutInflater.inflate(R.layout.dialog_primeira_vez, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_click);
        mensagem.setText("Para alterar a busca\nCOMÉRCIO \npara\n PRODUTOS/SERVIÇO\n clique no menu\nsuperior\n lado direito.");
        builder.setView(view);
        builder.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();


    }

}
