package com.marlostrinidad.wegeek.nerdzone.Mercado;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Adapter.MercadoAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.marlostrinidad.wegeek.nerdzone.Activits.Minhas_Publicacoes.setWindowFlag;

public class MercadoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private FloatingActionButton novoMercado;
    private FirebaseAuth autenticacao;
    private FirebaseUser usuario;
    private SwipeRefreshLayout refresh;
    private MaterialSearchView SeachView;
    private RecyclerView recyclerViewMercadoPublico;
    private MercadoAdapter adapter;
    private DatabaseReference mercadopublico;
    private ChildEventListener valueMercadoListener;
    private Comercio comercio;
    private ArrayList<Comercio> listamercado = new ArrayList<>();
    private ImageView botaoPesquisar;
    private SharedPreferences preferences = null;
    private Dialog dialog;
    private AlertDialog alerta;
    private CircleImageView icone;
    private LinearLayoutManager mManager;
    private DatabaseReference database;
    private ChildEventListener ChildEventListenerperfil;
    private  String filtroEstado = "";
    private  String filtroCategoria = "";
    private Boolean filtrandoPorEstado=false;
    private TextView errobusca;
    private LinearLayout linear_nada_cadastrado,linearerro,linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_mercado);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle(R.string.comercio);
        setSupportActionBar(toolbar);


        preferences = getSharedPreferences("primeiravezcomercio", MODE_PRIVATE);
        if (preferences.getBoolean("primeiravezcomercio", true)) {
            preferences.edit().putBoolean("primeiravezcomercio", false).apply();
            Dialog_Primeiravez();
        }else{

        }

        //Configuraçoes iniciais
        icone = findViewById(R.id.icone_user_toolbar);
        linearerro=findViewById(R.id.linearinformacoeserro_mercado);
        linear_nada_cadastrado = findViewById(R.id.linear_nada_cadastrado);
        errobusca = findViewById(R.id.textoerrobusca_mercado);
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        comercio = new Comercio();
        refresh = findViewById(R.id.refreshmercado);
        refresh.setOnRefreshListener(this);
        refresh.post(new Runnable() {
            @Override
            public void run() {
                RecuperarMercadoPublicos();
            }
        });
        refresh.setColorSchemeResources
                (R.color.colorPrimaryDark, R.color.amareloclaro,
                        R.color.accent);

        mercadopublico = ConfiguracaoFirebase.getFirebaseDatabase().child("comercio");
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        recyclerViewMercadoPublico = findViewById(R.id.recycleviewmercado);
        recyclerViewMercadoPublico.setHasFixedSize(true);
        //recycleview
        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
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
        //Aplicar Evento click
        recyclerViewMercadoPublico.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerViewMercadoPublico, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Comercio> listComercioAtualizado = adapter.getmercados();

                if (listComercioAtualizado.size() > 0) {
                    Comercio mercadoselecionado = listComercioAtualizado.get(position);
                    Intent it = new Intent(MercadoActivity.this, Detalhe_Mercado.class);
                    it.putExtra("mercadoelecionado", mercadoselecionado);
                    startActivity(it);
                    finish();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

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

                if(newText!=null && !newText.isEmpty()){
                    PesquisarComercio(newText.toLowerCase());

                }else{


                    recarregarMercado();
                }

                return true;
            }
        });

        TrocarFundos_status_bar();
        CarregarDados_do_Usuario();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onRefresh() {
        refresh.setRefreshing(true);
        RecuperarMercadoPublicos();
    }


    public void onStop() {
        super.onStop();
        mercadopublico.removeEventListener(valueMercadoListener);
    }

    public void RecuperarMercadoPublicos() {
        linear_nada_cadastrado.setVisibility(View.VISIBLE);
        listamercado.clear();

        valueMercadoListener = mercadopublico.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot categorias:dataSnapshot.getChildren()){
                    for(DataSnapshot mercados:categorias.getChildren()){
                        Comercio comercio = mercados.getValue(Comercio.class);
                        listamercado.add(0, comercio);
                        if(listamercado.size()>0){
                            linear_nada_cadastrado.setVisibility(View.GONE);
                        }

                    }
                }
                adapter.notifyDataSetChanged();
                refresh.setRefreshing(false);

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



    public void PesquisarComercio(String texto) {
        String nomeuser =usuario.getDisplayName();
        String evento_null = getString(R.string.erro_evento_busca_comercio,nomeuser,texto);
        List<Comercio> listaComercioBusca = new ArrayList<>();
        for (Comercio mercados : listamercado) {
            String nome=mercados.getTitulo().toLowerCase();
            String descricao = mercados.getDescricao().toLowerCase();
            // String author = mercados.getAutor().toLowerCase();
            if(nome.contains(texto)|| descricao.contains(texto)){
                listaComercioBusca.add(mercados);

            }else if(listaComercioBusca.size()==0){
                linearerro.setVisibility(View.VISIBLE);
                errobusca.setVisibility(View.VISIBLE);
                errobusca.setText(evento_null);
            }else{
                linear.setBackgroundColor (getResources().getColor(R.color.background));
            }
        }
        adapter = new MercadoAdapter(listaComercioBusca, MercadoActivity.this);
        recyclerViewMercadoPublico.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void recarregarMercado(){
        linearerro.setVisibility(View.GONE);
        errobusca.setVisibility(View.GONE);
        adapter = new MercadoAdapter(listamercado, MercadoActivity.this);
        recyclerViewMercadoPublico.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void FiltrarPorEstadoeCategoria(){

        LayoutInflater li = getLayoutInflater();

        //inflamos o layout dialog_opcao_foto.xml_foto.xml na view
        View view = li.inflate(R.layout.dialog_spinner, null);
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
                filtrandoPorEstado=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Spinner spinnerCategoria =  view.findViewById(R.id.spinnerFiltroCategoria);
        String [] categoria= getResources().getStringArray(R.array.loja);
        ArrayAdapter<String> adaptercategoria
                = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,categoria);
        adaptercategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adaptercategoria);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.findViewById(R.id.spinnerok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.

                filtroEstado = spinnerEstado.getSelectedItem().toString();
                filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                if ((filtroEstado.equals("Estado")) && (filtroCategoria.equals("Categoria"))) {
                    Toast.makeText(getApplicationContext(), "Selecione uma das Opções.", Toast.LENGTH_SHORT).show();
                } else if (filtroEstado.equals("Estado")) {
                    Toast.makeText(getApplicationContext(), "Selecione um Estado.", Toast.LENGTH_SHORT).show();
                } else if (filtroCategoria.equals("Categoria")) {
                    Toast.makeText(getApplicationContext(), "Selecione uma Categoria", Toast.LENGTH_SHORT).show();
                } else {
                    RecuperarMercadoPorCategoriaeEstado(filtroEstado, filtroCategoria);
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

                Intent it = new Intent(getApplicationContext(),MercadoActivity.class);
                startActivity(it);

            }
        });
        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar Comércio");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }


    public void RecuperarMercadoPorCategoriaeEstado(String estado,String categoria) {

        //Configurar por estado
        mercadopublico = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comercio")
                .child(estado)
                .child(categoria);
        mercadopublico.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listamercado.clear();
                for (DataSnapshot mercados : dataSnapshot.getChildren()) {

                    Comercio comercio = mercados.getValue(Comercio.class);
                    listamercado.add(comercio);


                }
                adapter = new MercadoAdapter(listamercado, MercadoActivity.this);
                recyclerViewMercadoPublico.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //Collections.reverse(listaanuncios);
                //adapter.notifyDataSetChanged();

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
                        Intent it = new Intent(MercadoActivity.this, MinhaConta.class);
                        startActivity(it);

                    }
                });

                Glide.with(MercadoActivity.this)
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

    private void Dialog_Primeiravez() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_informar_click_foto, null);
        view.findViewById(R.id.botaoentendi).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //desfaz o dialog_opcao_foto.
                dialog.dismiss();
            }
        });
        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    //botao Pesquisar
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        //Botao Pesquisa

        MenuItem item = menu.findItem(R.id.menuPesquisa);
        SeachView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }
    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(MercadoActivity.this, MainActivity.class);
                startActivity(it);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }else{
                    finish();
                }
                break;
            case R.id.menufiltro:
                FiltrarPorEstadoeCategoria();
            default:
                break;
        }

        return true;
    }
}
