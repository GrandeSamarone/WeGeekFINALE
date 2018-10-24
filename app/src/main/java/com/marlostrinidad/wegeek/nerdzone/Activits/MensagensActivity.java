package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Adapter.ConversasAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conversa;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import static com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity.setWindowFlag;

public class MensagensActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton Novo_BatePapo;
    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversas = new ArrayList<>();;
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ValueEventListener valueEventListenerContatos;
    private ChildEventListener childEventListenerConversas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);
        toolbar =findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Caixa de Mensagens");
        setSupportActionBar(toolbar);
        recyclerViewConversas= findViewById(R.id.recyclerviewConversas);
        adapter = new ConversasAdapter(listaConversas,MensagensActivity.this);

        String identificadoUsuario= UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef= database.child("conversas")
                .child(identificadoUsuario);
        //Configurar RecycleView
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(MensagensActivity.this);
        recyclerViewConversas.setLayoutManager(layoutManager);

        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter(adapter);

        //Configurar Evento de click
        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(
                MensagensActivity.this,
                recyclerViewConversas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Conversa> listConversaAtualizada = adapter.getConversas();

                        if (listConversaAtualizada.size() > 0) {
                            Conversa conversaselecionada = listConversaAtualizada.get(position);
                            Intent it = new Intent(MensagensActivity.this, ChatActivity.class);
                            it.putExtra("id", conversaselecionada.getIdDestinatario());
                            startActivity(it);

                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {/*
                        AlertDialog.Builder msgbox = new AlertDialog.Builder(MensagensActivity.this);
                        //configurando o titulo
                        msgbox.setTitle("Deletar Conversa");
                        // configurando a mensagem
                        msgbox.setMessage("Deseja Realmente deletar conversa com  "+usuarioselecionado.getNome()+" ?");
                        // Botao negativo

                        msgbox.setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int wich) {
                                        RemoverSeguidor(usuarioLogado,usuarioselecionado);
                                    }

                                });


                        msgbox.setNegativeButton("Não",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int wich) {
                                        dialog.dismiss();
                                    }
                                });
                        msgbox.show();
                        */
                        List<Conversa> listConversaAtualizada = adapter.getConversas();
                        Conversa conversaRemover = listConversaAtualizada.get(position);
                        conversaRemover.removerConversa();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));


        TrocarFundos_status_bar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:break;
        }

        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        RecuperarConversa();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(childEventListenerConversas);
    }

    public void PesquisarConversa(String texto) {

        List<Conversa> listaConversaBusca = new ArrayList<>();
        for (Conversa conversa : listaConversas) {

            if (conversa.getUsuarioExibicao() != null) {

                String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();

                if (nome.contains(texto) || ultimaMsg.contains(texto)) {
                    listaConversaBusca.add(conversa);
                }
            } else {
                String nome = conversa.getGrupo().getNome().toLowerCase();
                String ultimaMsg = conversa.getUltimaMensagem().toLowerCase();

                if (nome.contains(texto) || ultimaMsg.contains(texto)) {

                }

            }
            adapter = new ConversasAdapter(listaConversaBusca, MensagensActivity.this);
            recyclerViewConversas.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }
    }

    public void recarregarConversas(){
        adapter = new ConversasAdapter(listaConversas,MensagensActivity.this);
        recyclerViewConversas.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void RecuperarConversa(){
        listaConversas.clear();
        childEventListenerConversas =  conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Recuperar Conversa
                Conversa conversa=dataSnapshot.getValue(Conversa.class);
                listaConversas.add(conversa);
                adapter.notifyDataSetChanged();
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
    //Nao muito uteis
    private void TrocarFundos_status_bar(){
        //mudando a cor do statusbar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_toolbarstatusbar);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_toolbarstatusbar);
            //  systemBarTintManager.setStatusBarTintDrawable(Mydrawable);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.parseColor("#1565c0"));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_toolbarstatusbar);
        }
    }


}