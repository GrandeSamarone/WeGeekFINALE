package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Adapter.GeralContatosAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class GeralContatosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewListaContatos;
    private TextView textoAviso;
    private TextView textonadaencontrado;
    private GeralContatosAdapter adapter;
    private List<Usuario> listaContatos = new ArrayList<>();
   // private List<Usuario>Lista_Pesquisa_Usuario = new ArrayList<>();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerContatos;
    private FirebaseUser usuarioAtual;
    private MaterialSearchView SeachView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geral_contatos);
        Toolbar toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Procurar Amigos");
        setSupportActionBar(toolbar);






        //Configuracoes iniciais
        recyclerViewListaContatos= findViewById(R.id.recycleViewListaGeralContatos);
        recyclerViewListaContatos.setVisibility(View.INVISIBLE);
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        textoAviso= findViewById(R.id.textoaviso);
        textonadaencontrado=findViewById(R.id.textoavisonadaencontrado);

        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //configurar adapter
        adapter = new GeralContatosAdapter(listaContatos,GeralContatosActivity.this);

        //configurar recycleview
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerViewListaContatos.setLayoutManager(layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter(adapter);
        //Configurar evento de click no recycleview
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        GeralContatosActivity.this,
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                List<Usuario> listUsuarioAtualizado = adapter.getContatos();

                                if ( listUsuarioAtualizado.size() > 0) {
                                    Usuario usuarioselecionado =  listUsuarioAtualizado.get(position);

                                        Intent it = new Intent(GeralContatosActivity.this, ChatActivity.class);
                                        it.putExtra("chatcontato", usuarioselecionado);
                                        startActivity(it);

                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );



        //Botao Pesquisa
      /*  SeachView = findViewById(R.id.materialSeachContatos);
        SeachView.setHint("Pesquisar");
        SeachView.setHintTextColor(R.color.cinzaclaro);
         SeachView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String query) {
                 return false;
             }

             @Override
             public boolean onQueryTextChange(String newText) {

                 if(newText!=null && !newText.isEmpty()){
                    PesquisarContatos(newText.toLowerCase());

                 }else{


                    recarregarContatos();
                 }

                 return true;
             }
         });
*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(GeralContatosActivity.this,MeusAmigosActivity.class);
                startActivity(it);

                break;

            default:break;
        }

        return true;
    }

    public void PesquisarContatos(String texto) {
        String nick_null =  getString(R.string.buscar_usuario, usuarioAtual.getDisplayName());
        List<Usuario> listaContatosBusca = new ArrayList<>();
        for (Usuario usuario : listaContatos) {
            String nome=usuario.getNome().toLowerCase();
            if(nome.contains(texto)){
                listaContatosBusca.add(usuario);
                textonadaencontrado.setVisibility(View.INVISIBLE);

            }else if(!nome.contains(texto)) {
                textonadaencontrado.setVisibility(View.VISIBLE);
                textonadaencontrado.setText(nick_null);
            }

        }
        textoAviso.setVisibility(View.INVISIBLE);
        recyclerViewListaContatos.setVisibility(View.VISIBLE);

        adapter = new GeralContatosAdapter(listaContatosBusca, GeralContatosActivity.this);
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarContatos(){
        textoAviso.setVisibility(View.VISIBLE);
        recyclerViewListaContatos.setVisibility(View.INVISIBLE);
        adapter = new GeralContatosAdapter(listaContatos, GeralContatosActivity.this);
        recyclerViewListaContatos.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperarContatos();
        Log.i("testecaralho","ok");
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerContatos);
    }




    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        //Botao Pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        SeachView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }
    public void recuperarContatos(){
        listaContatos.clear();

        /*Define usuário com e-mail vazio
     * em caso de e-mail vazio o usuário será utilizado como
     * cabecalho, exibindo novo grupo
      */
        Usuario itemGrupo = new Usuario();



        valueEventListenerContatos= usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dados: dataSnapshot.getChildren()){

                    Usuario usuario = dados.getValue(Usuario.class);
                    String nomeUsuarioAtual = usuarioAtual.getDisplayName();
                    if(!nomeUsuarioAtual.equals(usuario.getNome())){
                        listaContatos.add(usuario);
                    }


                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

