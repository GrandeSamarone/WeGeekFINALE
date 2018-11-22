package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial.AdapterMercado;
import com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial.Adapter_Conto_pag_inicial;
import com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial.Adapter_FanArtsInicial;
import com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial.EventoAdapterPagInicial;
import com.marlostrinidad.wegeek.nerdzone.Adapter.AdapterPagInicial.TopicoAdapterPagInicial;
import com.marlostrinidad.wegeek.nerdzone.Autenticacao.LoginActivity;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Conto.Detalhe_conto;
import com.marlostrinidad.wegeek.nerdzone.Conto.ListaConto;
import com.marlostrinidad.wegeek.nerdzone.Evento.DetalheEvento;
import com.marlostrinidad.wegeek.nerdzone.Evento.Evento_Lista;
import com.marlostrinidad.wegeek.nerdzone.FanArts.Lista_Arts;
import com.marlostrinidad.wegeek.nerdzone.Feed.FeedActivity;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Detalhe_Mercado;
import com.marlostrinidad.wegeek.nerdzone.Mercado.MercadoActivity;
import com.marlostrinidad.wegeek.nerdzone.MinhasColecoes.Minhas_Colecoes;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.FanArts;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.Politica_Privacidade.Politica_PrivacidadeActivity;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.Topico.Detalhe_topico;
import com.marlostrinidad.wegeek.nerdzone.Topico.ListaTopicos;
import com.marlostrinidad.wegeek.nerdzone.Votacao.Tela_Inicial_Votacao_Activity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerViewListaGibiMercado;
    private RecyclerView recyclerViewArts;
    private RecyclerView recyclerViewListaTopico;
    private RecyclerView recyclerViewListaConto;
    private RecyclerView recyclerVieweventos;
    private Adapter_Conto_pag_inicial adapterConto;
    private AdapterMercado adapterMercado;
    private EventoAdapterPagInicial adapterEvento;
    private TopicoAdapterPagInicial adapterTopico;
    private Adapter_FanArtsInicial adapterArte;
    private List<Comercio> listaGibiComercio = new ArrayList<>();
    private List<FanArts> listaArt = new ArrayList<>();
    private ArrayList<Topico> ListaTopico = new ArrayList<>();
    private ArrayList<Evento> ListaEvento = new ArrayList<>();
    private ArrayList<Conto> ListaContos = new ArrayList<>();
    private ArrayList<String> mKeys = new ArrayList<>();
    private DatabaseReference GibiMercado;
    private DatabaseReference Database_Topico;
    private DatabaseReference Database_Conto;
    private DatabaseReference GibiEventos;
    private DatabaseReference Database_Art;
    private ChildEventListener valueEventListenerArt;
    private ChildEventListener valueEventListenerMercado;
    private ChildEventListener valueEventListenerEvento;
    private ChildEventListener valueEventListenerTopico;
    private ChildEventListener valueEventListenerContos;
    private ChildEventListener ChildEventListenerperfil;
    private TextView maiseventoTxt,maiscomercioTxt,maistopicoTxt,maiscontoTxt,maisfanartsTxt;

    private Toolbar toolbar;
    private CardView votacao;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout mdrawer;
    private CircleImageView img_drawer,img_icone_toolbar;
    private ImageView capadrawer;
    private TextView email_drawer;
    private TextView nome_drawer;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private String mPhotoUrl;
    private Usuario usuarioLogado;
    private FirebaseUser usuario;
    private DatabaseReference database;
    private SwipeRefreshLayout swipe;
    SharedPreferences sPreferences = null;
    private LinearLayout line_conto,line_art,line_comercio,line_evento,line_topico;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Configuraçoes Originais
        img_icone_toolbar = findViewById(R.id.icone_img_toolbar);
        line_art= findViewById(R.id.nada_encontrado_art_inicial);
        line_comercio=findViewById(R.id.nada_encontrado_mercado_inicial);
        line_conto = findViewById(R.id.nada_encontrado_contos_inicial);
        line_evento = findViewById(R.id.nada_encontrado_evento_inicial);
        line_topico = findViewById(R.id.nada_encontrado_topico_inicial);
        votacao=findViewById(R.id.card_votacao);
        votacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,Tela_Inicial_Votacao_Activity.class);
                startActivity(it);
            }
        });
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        //Recycle
        recyclerViewListaConto = findViewById(R.id.RecycleViewConto);
        recyclerViewListaGibiMercado = findViewById(R.id.RecycleViewMercado);
        recyclerVieweventos = findViewById(R.id.RecycleViewEventos);
        recyclerViewListaTopico = findViewById(R.id.RecycleViewTopicos);
        recyclerViewArts = findViewById(R.id.RecycleViewFanArts);

        Database_Art = ConfiguracaoFirebase.getFirebaseDatabase().child("arts");
        GibiMercado = ConfiguracaoFirebase.getFirebaseDatabase().child("comercio");
        Database_Conto = ConfiguracaoFirebase.getFirebaseDatabase().child("conto");
        Database_Topico = ConfiguracaoFirebase.getDatabase().getReference().child("topico");
        GibiEventos = ConfiguracaoFirebase.getFirebaseDatabase().child("evento");

        //Configurar recycleView Evento
        LinearLayoutManager layoutManagerConto = new LinearLayoutManager(
                MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewListaConto.setLayoutManager(layoutManagerConto);
        recyclerViewListaConto.setHasFixedSize(true);
        adapterConto = new Adapter_Conto_pag_inicial(ListaContos,MainActivity.this);
        recyclerViewListaConto.setAdapter(adapterConto);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerVieweventos.setLayoutManager(layoutManager);
        recyclerVieweventos.setHasFixedSize(true);
        adapterEvento = new EventoAdapterPagInicial(ListaEvento,MainActivity.this);
        recyclerVieweventos.setAdapter(adapterEvento);
        //Configurar recycleView Mercado
        LinearLayoutManager layoutManagerMarvel = new LinearLayoutManager
                (MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewListaGibiMercado.setLayoutManager(layoutManagerMarvel);
        recyclerViewListaGibiMercado.setHasFixedSize(true);
        adapterMercado=new AdapterMercado(listaGibiComercio,MainActivity.this);
        recyclerViewListaGibiMercado.setAdapter(adapterMercado);
        //Configurar recycleView TOpico
        LinearLayoutManager layoutManagertopico = new LinearLayoutManager(
                MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewListaTopico.setLayoutManager(layoutManagertopico);
        recyclerViewListaTopico.setHasFixedSize(true);
        adapterTopico = new TopicoAdapterPagInicial(ListaTopico,MainActivity.this);
        recyclerViewListaTopico.setAdapter(adapterTopico);

        LinearLayoutManager layoutManagerArt = new LinearLayoutManager(
                MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewArts.setLayoutManager(layoutManagerArt);
        recyclerViewArts.setHasFixedSize(true);
        layoutManagerArt.setReverseLayout(true);
        layoutManagerArt.setStackFromEnd(true);
        adapterArte = new Adapter_FanArtsInicial(listaArt,MainActivity.this);
        recyclerViewArts.setAdapter(adapterArte);

        //Verifica se é a primeira vez da instalacao
        sPreferences = getSharedPreferences("primeiravez_Main_Activit", MODE_PRIVATE);
        if (sPreferences.getBoolean("primeiravez_Main_Activit", true)) {
            sPreferences.edit().putBoolean("primeiravez_Main_Activit", false).apply();
            Dialog_Primeiravez();
        }
        //Toolbar
        toolbar =findViewById(R.id.toolbarmain);
        // toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //carregando das informacoes do usuario no Drawer
        mdrawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mdrawer, R.string.open, R.string.close);
        mdrawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigat_id);
        navigationView.setNavigationItemSelectedListener(this);

        //esse cod desativa o padrao das cores da naevagacao
        //configuraçoes basica navigation
        navigationView.setItemIconTintList(null);
        View navHeaderView = navigationView.getHeaderView(0);
        img_drawer=navHeaderView.findViewById(R.id.Img_perfil_drawer);
        capadrawer= navHeaderView.findViewById(R.id.capadrawer);
        nome_drawer = navHeaderView.findViewById(R.id.Nome_usuario_drawer);
        email_drawer=navHeaderView.findViewById(R.id.Email_usuario_drawer);

        //Configuracoes Originais
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        usuarioLogado = new Usuario();


        //REFRESH
        swipe = findViewById(R.id.swipe_ac_main);
        swipe.setOnRefreshListener(this);
        swipe.post(new Runnable() {
            @Override
            public void run() {
                RecuperarArt();
                RecuperarConto();
                RecuperarMercado();
                RecuperarEvento();
                RecuperarTopico();
                botoes_Mais();
                CarregarDados_do_Usuario();
            }
        });

        swipe.setColorSchemeResources
                (R.color.colorPrimaryDark, R.color.amareloclaro,
                        R.color.accent);

        //todas configuraões do recycleview
        Recycleview();

        //Trocando Fundo statusbar
        TrocarFundos_status_bar();

    }

    private void Dialog_Primeiravez() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_informacao_main_activity, null);
        view.findViewById(R.id.botaoentendi).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //desfaz o dialog_opcao_foto.
                dialog.dismiss();
            }
        });
        //Dialog de tela
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }



    @Override
    public void onRefresh() {
        RecuperarArt();
        RecuperarMercado();
        RecuperarEvento();
        RecuperarConto();
        RecuperarTopico();
        CarregarDados_do_Usuario();

    }

    @Override
    public void onStop() {
        GibiEventos.removeEventListener(valueEventListenerEvento);
        GibiMercado.removeEventListener(valueEventListenerMercado);
        Database_Topico.removeEventListener(valueEventListenerTopico);
        Database_Conto.removeEventListener(valueEventListenerContos);
        Database_Art.removeEventListener(valueEventListenerArt);
        database.removeEventListener(ChildEventListenerperfil);
        super.onStop();
     }

    private void CarregarDados_do_Usuario(){
        final String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        ChildEventListenerperfil=database.orderByChild("id").equalTo(identificadorUsuario).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

                String capa = perfil.getCapa();
                if (!MainActivity.this.isFinishing()) {
                    Glide.with(getApplicationContext())
                            .load(capa)
                            .into(capadrawer);
                }
                if (!MainActivity.this.isFinishing()) {
                    String icone = perfil.getFoto();

                    Glide.with(getApplicationContext())
                            .load(icone)
                            .into(img_drawer);

                    Glide.with(getApplicationContext())
                            .load(icone)
                            .into(img_icone_toolbar);
                }
                //Imagem do icone do usuario
                img_icone_toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(MainActivity.this, MinhaConta.class);
                        startActivity(it);

                    }
                });


                nome_drawer.setText(perfil.getNome());
                email_drawer.setText(perfil.getTipoconta());
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



    //recupera e nao deixa duplicar
    public void RecuperarEvento(){
        ListaEvento.clear();
        line_evento.setVisibility(View.VISIBLE);
        valueEventListenerEvento =GibiEventos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot estado : dataSnapshot.getChildren()) {
                    Evento evento = estado.getValue(Evento.class);
                    ListaEvento.add(0, evento);
                    if(ListaEvento.size()>0){
                        line_evento.setVisibility(View.INVISIBLE);
                    }
                    adapterEvento.notifyDataSetChanged();
                    swipe.setRefreshing(false);
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


    //recupera e nao deixa duplicar
    public void RecuperarMercado(){
        listaGibiComercio.clear();
        line_comercio.setVisibility(View.VISIBLE);
        valueEventListenerMercado=GibiMercado.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot estado : dataSnapshot.getChildren()) {
                    for (DataSnapshot categoria : estado.getChildren()) {
                        Comercio comercio = categoria.getValue(Comercio.class);
                        listaGibiComercio.add(0,comercio);
                        if(listaGibiComercio.size()>0){
                            line_comercio.setVisibility(View.GONE);
                        }
                        adapterMercado.notifyDataSetChanged();
                        swipe.setRefreshing(false);

                    }
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

    //recupera e nao deixa duplicar
    public void RecuperarTopico(){
        ListaTopico.clear();
        line_topico.setVisibility(View.VISIBLE);
        valueEventListenerTopico =Database_Topico.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Topico topicos = dataSnapshot.getValue(Topico.class);
                ListaTopico.add(0, topicos);
                if(ListaTopico.size()>0){
                    line_topico.setVisibility(View.GONE);
                }
                adapterTopico.notifyDataSetChanged();
                swipe.setRefreshing(false);
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

    public void RecuperarConto(){
        ListaContos.clear();
        line_conto.setVisibility(View.VISIBLE);
        valueEventListenerContos =Database_Conto.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Conto conto = dataSnapshot.getValue(Conto.class);
                ListaContos.add(0,conto);
                if(ListaContos.size()>0){
                    line_conto.setVisibility(View.GONE);
                }
                adapterConto.notifyDataSetChanged();
                swipe.setRefreshing(false);
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


    public void RecuperarArt(){
        listaArt.clear();
        line_art.setVisibility(View.VISIBLE);
        valueEventListenerArt =Database_Art.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                    FanArts fanArts = categoria.getValue(FanArts.class);
                    listaArt.add(fanArts);
                    if (listaArt.size() > 0) {
                        line_art.setVisibility(View.GONE);
                    }
                    adapterArte.notifyDataSetChanged();
                    swipe.setRefreshing(false);
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

    private void Recycleview(){

        recyclerViewListaTopico.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerViewListaTopico, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Topico> listTopicoAtualizado = adapterTopico.getTopicos();

                if (listTopicoAtualizado.size() > 0) {
                    Topico topicoselecionado = listTopicoAtualizado.get(position);
                    Intent it = new Intent(MainActivity.this, Detalhe_topico.class);
                    it.putExtra("topicoselecionado", topicoselecionado);
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
        recyclerViewListaConto.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerViewListaConto, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Conto> listContoAtualizado = adapterConto.getContos();

                if (listContoAtualizado.size() > 0) {
                    Conto contoselecionado = listContoAtualizado.get(position);
                    Intent it = new Intent(MainActivity.this, Detalhe_conto.class);
                    it.putExtra("contoselecionado", contoselecionado);
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
        recyclerViewListaGibiMercado.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerViewListaGibiMercado, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Comercio mercadoselecionado = listaGibiComercio.get(position);
                Intent it = new Intent(MainActivity.this,Detalhe_Mercado.class);
                it.putExtra("mercadoelecionado",mercadoselecionado);
                startActivity(it);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        recyclerVieweventos.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerVieweventos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Evento eventoselecionado = ListaEvento.get(position);
                Intent it = new Intent(MainActivity.this,DetalheEvento.class);
                it.putExtra("id_do_evento",eventoselecionado.getUid());
                it.putExtra("UR_do_evento",eventoselecionado.getEstado());
                startActivity(it);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
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
        if (Build.VERSION.SDK_INT <= 19) {
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        // This will get you total fragment in the backStack
        int count = getFragmentManager().getBackStackEntryCount();
        switch(count){
            case 0:
                super.onBackPressed();
                break;
            case 1:

                super.onBackPressed();
            case 2:

                super.onBackPressed();
            default:
                getFragmentManager().popBackStack();
                break;
        }


    }

    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menufiltro:
                //abrirConfiguracoes();
                break;
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar

        }/*
        case android.R.id.home:
        // NavUtils.navigateUpFromSameTask(this);
        startActivity(new Intent(this, MainActivity.class)); //O efeito ao ser pressionado do botão (no caso abre a activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            finish();
        }

        break;
        */
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void botoes_Mais(){
        maiseventoTxt = findViewById(R.id.maisevento);
        maiseventoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, Evento_Lista.class);
                startActivity(it);
            }
        });

        maiscomercioTxt= findViewById(R.id.maiscomercio);
        maiscomercioTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent( MainActivity.this,MercadoActivity.class);
                startActivity(it);
            }
        });
        maistopicoTxt = findViewById(R.id.maistopicos);
        maistopicoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,ListaTopicos.class);
                startActivity(it);
            }
        });
        maiscontoTxt = findViewById(R.id.maisconto);
        maiscontoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,ListaConto.class);
                startActivity(it);
            }
        });
        maisfanartsTxt = findViewById(R.id.maisfanart);
        maisfanartsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this,Lista_Arts.class);
                startActivity(it);
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();
        if (id == R.id.feed_menu) {
            Intent it = new Intent(MainActivity.this,FeedActivity.class);
            startActivity(it);
        }else if (id == R.id.minhascolecoes_menu) {
            Intent it = new Intent(MainActivity.this,Minhas_Colecoes.class);
            startActivity(it);
        } else if (id == R.id.minhaloja_menu) {
            Intent it = new Intent(MainActivity.this,Minhas_Publicacoes.class);
            startActivity(it);
        } else if (id == R.id.mensagens_menu) {
            Intent it = new Intent(MainActivity.this,MensagensActivity.class);
            startActivity(it);
        }else if (id == R.id.minha_conta_menu) {
            Intent it = new Intent(MainActivity.this,MinhaConta.class);
            startActivity(it);
        } else if (id == R.id.comercio_menu) {
            Intent it = new Intent(MainActivity.this,MercadoActivity.class);
            startActivity(it);
        }else if (id == R.id.evento_menu) {
            Intent it = new Intent(MainActivity.this,Evento_Lista.class);
            startActivity(it);
        }else if (id == R.id.minha_privacidade_dmenu) {
            Intent it = new Intent(MainActivity.this,Politica_PrivacidadeActivity.class);
            startActivity(it);
        } else if (id == R.id.topico_menu) {
            Intent it = new Intent(MainActivity.this,ListaTopicos.class);
            startActivity(it);
        } else if (id == R.id.conto_menu) {
            Intent it = new Intent(MainActivity.this,ListaConto.class);
            startActivity(it);
        }else if (id == R.id.fanarts_menu) {
            Intent it = new Intent(MainActivity.this,Lista_Arts.class);
            startActivity(it);
        }else if (id == R.id.menu_sair) {
            AlertDialog.Builder msgbox = new AlertDialog.Builder(MainActivity.this);
            //configurando o titulo
            msgbox.setTitle("Deseja Sair?");
            msgbox.setPositiveButton("Sim",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            sendToLogin();


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

        }else if(id == R.id.avaliar_dmenu){
           // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.marlostrinidad.wegeek.nerdzone")));
            AbrirLoja_na_play_store();
        }else if(id == R.id.suport_dmenu){

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void AbrirLoja_na_play_store()
    {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try
        {
            getApplication().startActivity(myAppLinkToMarket);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(this, " Sorry, Not able to open!", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendToLogin() { //funtion
        GoogleSignInClient mGoogleSignInClient ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                new OnCompleteListener<Void>() {  //signout Google
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut(); //signout firebase
                        Intent setupIntent = new Intent(getBaseContext(), LoginActivity.class);
                        Toast.makeText(getBaseContext(), "Desconectado", Toast.LENGTH_LONG).show(); //if u want to show some text
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                        finish();
                    }
                });
    }


}

