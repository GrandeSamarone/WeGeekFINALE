package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marlostrinidad.wegeek.nerdzone.Adapter.ConversasAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conversa;
import com.marlostrinidad.wegeek.nerdzone.Model.Mensagem;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;
import java.util.List;

public class MeusAmigosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private List<Conversa> listaConversas = new ArrayList<>();
    private List<Mensagem> mensagens= new ArrayList<>();

    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private static FirebaseDatabase data;
    private ChildEventListener childEventListenerConversas;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_meus_amigos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Caixa de Mensagem");
        setSupportActionBar(toolbar);

        FloatingActionButton botao_Mais_Amigo= findViewById(R.id.btn_Mais_Amigo);
        recyclerViewChat= findViewById(R.id.recyclerviewConversas);
        botao_Mais_Amigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MeusAmigosActivity.this,GeralContatosActivity.class);
            startActivity(it);
            }
        });
        //  data = ConfiguracaoFirebase.getDatabase();
        //Configurar adapter
        adapter = new ConversasAdapter(listaConversas,MeusAmigosActivity.this);

        //Configurar RecycleView
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(MeusAmigosActivity.this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setAdapter(adapter);

        //Configurar Evento de click
        recyclerViewChat.addOnItemTouchListener(new RecyclerItemClickListener(
                MeusAmigosActivity.this,
                recyclerViewChat,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        List<Conversa> listaconversaatualizada = adapter.getConversas();
                        if (listaconversaatualizada.size()>0) {
                            Conversa conversaSelecionado = listaconversaatualizada.get(position);

                            if(conversaSelecionado.getIsGroup().equals("true")){
                                Intent it = new Intent(MeusAmigosActivity.this, ChatActivity.class);
                                it.putExtra("chatGrupo", conversaSelecionado.getGrupo());
                                startActivity(it);

                            }else{

                                Intent it = new Intent(MeusAmigosActivity.this, ChatActivity.class);
                                it.putExtra("chatcontato", conversaSelecionado.getUsuarioExibicao());
                                startActivity(it);
                            }

                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                     remove(listaConversas.get(position));

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));
        String identificadoUsuario= UsuarioFirebase.getIdentificadorUsuario();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef= database.child("Conversas")
                .child(identificadoUsuario);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
             finish();
                Intent it = new Intent(MeusAmigosActivity.this,MainActivity.class);
                startActivity(it);

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
            adapter = new ConversasAdapter(listaConversaBusca,this);
            recyclerViewChat.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }
    }

    public void recarregarConversas(){
        adapter = new ConversasAdapter(listaConversas,this);
        recyclerViewChat.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void RecuperarConversa(){
        listaConversas.clear();
        childEventListenerConversas =  conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Recuperar Conversa
                Conversa conversa=dataSnapshot.getValue(Conversa.class);
                conversa.setKey(dataSnapshot.getKey());
                listaConversas.add(0,conversa);
                adapter.notifyDataSetChanged();



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            /*    Conversa conversa =dataSnapshot.getValue(Conversa.class);
                String key =dataSnapshot.getKey();
                for(Conversa gb:listaConversas){
                    if(gb.getKey().equals(key)){
                        gb.setValues(conversa);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
*/
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
               conversasRef.child(key).removeValue();

                for (Conversa cv : listaConversas) {
                    if (key.equals(cv.getKey())) {
                        listaConversas.remove(cv);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void remove(final Conversa conversa){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja Excluir essa conversa?");
        builder.setCancelable(false);
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                conversasRef.child(conversa.getKey()).removeValue();

            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
