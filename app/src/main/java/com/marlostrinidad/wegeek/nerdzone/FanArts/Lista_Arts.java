package com.marlostrinidad.wegeek.nerdzone.FanArts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem_Art;
import com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_FanArts;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.FanArts;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Lista_Arts extends AppCompatActivity  {
    private Toolbar toolbar;
    private CircleImageView icone;
    private DatabaseReference database,database_fanarts;
    private FirebaseUser usuario;
    private MaterialSearchView SeachViewTopico;
    private Adapter_FanArts adapter_fanArts;
    private ChildEventListener ChildEventListenerperfil;
    private FloatingActionButton botaoMaisArts;
    private RecyclerView recyclerView_lista_arts;
    private ArrayList<FanArts> ListaFanarts = new ArrayList<>();
    private ChildEventListener valueEventListenerFanarts;
    private LinearLayout linear_nada_cadastrado;
    private SharedPreferences preferences = null;
    private Dialog dialog;
    private AlertDialog alerta;
    private String filtroCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__arts);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("WeArt");
        setSupportActionBar(toolbar);


        icone = findViewById(R.id.icone_user_toolbar);

        //Configuraçoes Basicas
        linear_nada_cadastrado=findViewById(R.id.linear_nada_cadastrado_fanats);
        botaoMaisArts=findViewById(R.id.buton_nova_fanarts);
        botaoMaisArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Lista_Arts.this,Nova_Arts.class);
                startActivity(it);
                finish();
            }
        });
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        database_fanarts = ConfiguracaoFirebase.getDatabase().getReference().child("arts");

        recyclerView_lista_arts = findViewById(R.id.recycleview_fanarts);
        adapter_fanArts  = new Adapter_FanArts(ListaFanarts,this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (2, LinearLayoutManager.VERTICAL);

        recyclerView_lista_arts.setLayoutManager(staggeredGridLayoutManager);
        recyclerView_lista_arts.setHasFixedSize(true);
        recyclerView_lista_arts.setAdapter(adapter_fanArts);


        recyclerView_lista_arts.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),
                recyclerView_lista_arts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FanArts arteselecionada = ListaFanarts.get(position);
                Intent it = new Intent(Lista_Arts.this, AbrirImagem_Art.class);
                it.putExtra("id_foto",arteselecionada.getArtfoto());
                it.putExtra("id",arteselecionada.getId());
                it.putExtra("nome_foto",arteselecionada.getLegenda());
                startActivity(it);
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
        CarregarDados_do_Usuario();
        TrocarFundos_status_bar();
        RecuperarArts();
        preferences = getSharedPreferences("primeiravezats", MODE_PRIVATE);
        if (preferences.getBoolean("primeiravezats", true)) {
            preferences.edit().putBoolean("primeiravezats", false).apply();
            Dialog_Primeiravez();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        database_fanarts.removeEventListener(valueEventListenerFanarts);
        database.removeEventListener(ChildEventListenerperfil);
    }

    private void RecuperarArts(){
        linear_nada_cadastrado.setVisibility(View.VISIBLE);
        ListaFanarts.clear();
        valueEventListenerFanarts = database_fanarts.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                    FanArts fanArts = categoria.getValue(FanArts.class);
                    ListaFanarts.add(0, fanArts);
                    if (ListaFanarts.size() > 0) {
                        linear_nada_cadastrado.setVisibility(View.GONE);
                    }
                    adapter_fanArts.notifyDataSetChanged();
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

    //botao Pesquisar
  /*  public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro,menu);


        return super.onCreateOptionsMenu(menu);
    }
*/

    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menufiltro:
                FiltrarPorCategoria();
                break;
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, MainActivity.class)); //O efeito ao ser pressionado do botão (no caso abre a activity)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void FiltrarPorCategoria(){

        LayoutInflater li = getLayoutInflater();

        //inflamos o layout dialog_opcao_foto.xml_foto.xml na view
        View view = li.inflate(R.layout.dialog_spinner_art, null);
        final Spinner spinnerCategoria =  view.findViewById(R.id.spinnerFiltroCategoria_art);
        String [] categoria= getResources().getStringArray(R.array.fanartcategoria);
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

                filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                if (filtroCategoria.equals("Categoria")) {
                    Toast.makeText(getApplicationContext(), "Selecione uma Categoria", Toast.LENGTH_SHORT).show();
                } else {
                    RecuperarArtPorCategoria(filtroCategoria);
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

                Intent it = new Intent(getApplicationContext(),Lista_Arts.class);
                startActivity(it);

            }
        });
        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar por Categoria");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }

    private void RecuperarArtPorCategoria(String filtroCategoria) {

        //Configurar por estado
        database_fanarts = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("arts")
                .child(filtroCategoria);
        database_fanarts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ListaFanarts.clear();
                for (DataSnapshot eventos : dataSnapshot.getChildren()) {

                    FanArts art = eventos.getValue(FanArts.class);
                    ListaFanarts.add(art);


                }
                adapter_fanArts = new Adapter_FanArts(ListaFanarts, Lista_Arts.this);
                recyclerView_lista_arts.setAdapter(adapter_fanArts);
                adapter_fanArts.notifyDataSetChanged();
                //Collections.reverse(listaanuncios);
                //adapter.notifyDataSetChanged();

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



    private void CarregarDados_do_Usuario(){
        usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerperfil=database.orderByChild("tipoconta").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;
                String iconeurl = perfil.getFoto();

                Glide.with(Lista_Arts.this)
                        .load(iconeurl)
                        .into(icone);

                icone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(Lista_Arts.this, MinhaConta.class);
                        startActivity(it);
                    }
                });
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
        View view = li.inflate(R.layout.dialog_informacao_art, null);
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

}
