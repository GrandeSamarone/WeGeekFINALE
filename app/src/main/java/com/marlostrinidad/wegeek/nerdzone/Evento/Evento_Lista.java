package com.marlostrinidad.wegeek.nerdzone.Evento;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Evento_Adapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Evento_Lista extends AppCompatActivity  implements SwipeRefreshLayout.OnRefreshListener
{

    private DatabaseReference mDatabaseevento;
    private SwipeRefreshLayout swipeatualizar;
    private CircleImageView icone;
    private LinearLayout linear,linearerro;
    private MaterialSearchView SeachView;
    private FirebaseAuth autenticacao;
    private FirebaseAuth mFirebaseAuth;
    private FloatingActionButton Novo_Evento;
    private RecyclerView recyclerEvento;
    private Evento_Adapter adapterevento;
    private ArrayList<Evento> ListaEvento = new ArrayList<>();
    private ChildEventListener valueEventListenerEvento;
    private LinearLayoutManager mManager;
    private Usuario user;
    private Toolbar toolbar;
    private DatabaseReference database;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private String filtroEstado;
    private AlertDialog alerta;
    private TextView errobusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento__lista);


        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle(R.string.name_evento);
        setSupportActionBar(toolbar);

        linear = findViewById(R.id.linear_nada_cadastrado_evento);
        linearerro= findViewById(R.id.linearinformacoeserro);
        // linear.setBackgroundResource(R.drawable.fundo_da_capa_add_evento);
        errobusca = findViewById(R.id.textoerrobusca);
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        Novo_Evento = findViewById(R.id.fab_novo_evento);
        Novo_Evento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Evento_Lista.this,Cadastrar_Novo_Evento.class);
                startActivity(it);

            }
        });



        //ROLAR PARA ATUALIZAR
        swipeatualizar= findViewById(R.id.swipe_list_evento);
        swipeatualizar.setOnRefreshListener(Evento_Lista.this);

        swipeatualizar.post(new Runnable() {
            @Override
            public void run() {
                RecuperarEventos();

            }
        });
        swipeatualizar.setColorSchemeResources
                (R.color.colorPrimaryDark, R.color.amareloclaro,
                        R.color.accent);

        //Configuracoes Basicas
        icone = findViewById(R.id.icone_user_toolbar);
        recyclerEvento = findViewById(R.id.lista_evento);
        mDatabaseevento = ConfiguracaoFirebase.getFirebaseDatabase().child("evento");

        //Configura Adapter
        adapterevento = new Evento_Adapter(ListaEvento,this);

        //Adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerEvento.setLayoutManager(layoutManager);
        recyclerEvento.setHasFixedSize(true);
        recyclerEvento.setAdapter(adapterevento);
/*
        recyclerEvento.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerEvento, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Evento> liseventoatualizado = adapterevento.getevento();

                if (liseventoatualizado.size() > 0) {
                    Evento eventoselecionado = liseventoatualizado.get(position);
                    Intent it = new Intent(Evento_Lista.this, DetalheEvento.class);
                    it.putExtra("eventoselecionado", eventoselecionado);
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
*/
//Botao Pesquisa
        SeachView = findViewById(R.id.materialSeachComercio);
        SeachView.setHint("Pesquisar");
        SeachView.setHintTextColor(R.color.cinzaclaro);
        SeachView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                icone.setVisibility(View.GONE);
                if(newText!=null && !newText.isEmpty()){
                    PesquisarEvento(newText.toLowerCase());

                }else{

                    icone.setVisibility(View.VISIBLE);
                    recarregarEvento();
                }

                return true;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TrocarFundos_status_bar();
        CarregarDados_do_Usuario();

    }

    //recupera e nao deixa duplicar
    public void RecuperarEventos(){
        ListaEvento.clear();
        linear.setVisibility(View.VISIBLE);

        valueEventListenerEvento =mDatabaseevento.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot estado : dataSnapshot.getChildren()) {
                    Evento evento = estado.getValue(Evento.class);
                    ListaEvento.add(0, evento);
                    if(ListaEvento.size()>0){
                        linear.setVisibility(View.GONE);
                        linear.setBackgroundColor (getResources().getColor(R.color.background));
                    }
                    adapterevento.notifyDataSetChanged();
                    swipeatualizar.setRefreshing(false);

                }
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

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    //botao Pesquisar
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sem_filtro,menu);

        //Botao Pesquisa

        MenuItem item = menu.findItem(R.id.menuPesquisa);
        SeachView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                startActivity(new Intent(this, MainActivity.class)); //O efeito ao ser pressionado do botão (no caso abre a activity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }else{
                    finish();
                }
                break;
            case R.id.menufiltro:
                FiltrarPorEstadoeTempo();
                break;


            default:break;
        }

        return true;
    }
    private void CarregarDados_do_Usuario(){
        usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerperfil=database.orderByChild("tipoconta").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;


                String iconeurl = perfil.getFoto();
                icone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(Evento_Lista.this, MinhaConta.class);
                        startActivity(it);

                    }
                });
                Glide.with(Evento_Lista.this)
                        .load(iconeurl)
                        .into(icone);

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

    @Override
    public void onRefresh() {
        RecuperarEventos();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseevento.removeEventListener(valueEventListenerEvento);
    }



    private void PesquisarEvento(String texto) {
        String nomeuser =usuario.getDisplayName();


        String evento_null = getString(R.string.erro_evento_busca_evento,nomeuser,texto);
        List<Evento> listaEventoBusca = new ArrayList<>();
        for (Evento evento : ListaEvento) {
            String nome=evento.getTitulo().toLowerCase();
            if(nome.contains(texto)){
                listaEventoBusca.add(evento);

            }else if(listaEventoBusca.size()==0){
                linearerro.setVisibility(View.VISIBLE);
                errobusca.setVisibility(View.VISIBLE);
                errobusca.setText(evento_null);
            }else{
                linear.setBackgroundColor (getResources().getColor(R.color.background));
            }

        }
        adapterevento = new Evento_Adapter(listaEventoBusca, Evento_Lista.this);
        recyclerEvento.setAdapter(adapterevento);
        adapterevento.notifyDataSetChanged();
    }

    private void recarregarEvento() {
        linearerro.setVisibility(View.GONE);
        errobusca.setVisibility(View.GONE);
        adapterevento = new Evento_Adapter(ListaEvento, Evento_Lista.this);
        recyclerEvento.setAdapter(adapterevento);
        adapterevento.notifyDataSetChanged();
    }


    public void FiltrarPorEstadoeTempo(){

        LayoutInflater li = getLayoutInflater();

        //inflamos o layout dialog_opcao_foto.xml_foto.xml na view
        View view = li.inflate(R.layout.dialog_spinner_evento, null);
        //definimos para o botão do layout um clickListener
        final Spinner spinnerEstado = view.findViewById(R.id.spinnerFiltroEstado);
        String [] estado= getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterestado = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,estado);
        adapterestado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterestado);

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroEstado = spinnerEstado.getSelectedItem().toString();
                // RecuperarAnunciosPorEstado();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.findViewById(R.id.spinnerok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.

                filtroEstado = spinnerEstado.getSelectedItem().toString();

                if (filtroEstado.equals("Estado")) {
                    Toast.makeText(getApplicationContext(), "Selecione um Estado.", Toast.LENGTH_SHORT).show();
                } else {
                    RecuperarMercadoPorCategoriaeEstado(filtroEstado);
                    alerta.dismiss();
                }
            }
        });

        view.findViewById(R.id.spinnercancelar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.

                alerta.dismiss();

            }
        });

        view.findViewById(R.id.spinnerlimpar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.

                Intent it = new Intent(getApplicationContext(),Evento_Lista.class);
                startActivity(it);

            }
        });
        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar Evento");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }


    public void RecuperarMercadoPorCategoriaeEstado(String estado) {


        //Configurar por estado
        mDatabaseevento = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("evento")
                .child(estado);
        mDatabaseevento.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ListaEvento.clear();
                for (DataSnapshot eventos : dataSnapshot.getChildren()) {

                    Evento evento = eventos.getValue(Evento.class);
                    ListaEvento.add(evento);


                }
                adapterevento = new Evento_Adapter(ListaEvento, Evento_Lista.this);
                recyclerEvento.setAdapter(adapterevento);
                adapterevento.notifyDataSetChanged();
                //Collections.reverse(listaanuncios);
                //adapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
