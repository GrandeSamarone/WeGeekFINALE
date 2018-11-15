package com.marlostrinidad.wegeek.nerdzone.Seguidores.MinhaConta;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.ContatosAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeusSeguidores extends AppCompatActivity {


    private DatabaseReference mDatabaseseguidores;
    private CircleImageView icone;
    private MaterialSearchView SeachView;
    private TextView erro;
    private ContatosAdapter adapterseguidores;
    private ArrayList<Usuario> ListaSeguidores = new ArrayList<>();
    private ChildEventListener valueEventListenerSeguidores;
    private Toolbar toolbar;
    private String ids;
    private LinearLayout lineerro;
    private RecyclerView recyclerMeusSeguidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_seguidores);

        toolbar = findViewById(R.id.toolbarsecundario_sem_foto);
        toolbar.setTitle(R.string.Meus_seguidores);
        setSupportActionBar(toolbar);

        mDatabaseseguidores= ConfiguracaoFirebase.getDatabase().getReference().child("seguidores");
        erro = findViewById(R.id.textoerrobusca_contato);
        lineerro = findViewById(R.id.linearinformacoeserro_contato);
        //Configuracoes Originais
        recyclerMeusSeguidores=findViewById(R.id.lista_Meus_seguidores_minha_conta);

        adapterseguidores= new ContatosAdapter(ListaSeguidores,this);

        //Adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerMeusSeguidores.setLayoutManager(layoutManager);
        recyclerMeusSeguidores.setHasFixedSize(true);
        recyclerMeusSeguidores.setAdapter(adapterseguidores);

        SeachView = findViewById(R.id.materialSeach_toolbar_sem_foto);
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
                   PesquisarContato(newText.toLowerCase());

                }else{
                   RecarregarContato();
                }

                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        //RecuperarEventos();
        TrocarFundos_status_bar();
        RecuperarSeguidores();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseseguidores.removeEventListener(valueEventListenerSeguidores);
    }


    public void RecuperarSeguidores(){
        ListaSeguidores.clear();
        ids=getIntent().getStringExtra("id");
        valueEventListenerSeguidores =mDatabaseseguidores.child(ids).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    ListaSeguidores.add(0, usuario);

                    adapterseguidores.notifyDataSetChanged();


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
    /*public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sem_filtro_sem_foto,menu);

        //Botao Pesquisa

        MenuItem item = menu.findItem(R.id.menuPesquisa);
        SeachView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }
*/
    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);

                    finish();

                break;



            default:break;
        }

        return true;
    }

    private void PesquisarContato(String texto) {
       // String nomeuser =us.getDisplayName();


        String contato_null = getString(R.string.erro_busca_contato);
        List<Usuario> listausuarioBusca = new ArrayList<>();
        for (Usuario usuario : ListaSeguidores) {
            String nome=usuario.getNome().toLowerCase();
            if(nome.contains(texto)){
                listausuarioBusca.add(usuario);

            }else if(listausuarioBusca.size()==0){
                lineerro.setVisibility(View.VISIBLE);
                erro.setVisibility(View.VISIBLE);
           erro.setText(contato_null);
            }else{

            }

        }
        adapterseguidores = new ContatosAdapter(listausuarioBusca, MeusSeguidores.this);
        recyclerMeusSeguidores.setAdapter(adapterseguidores);
        adapterseguidores.notifyDataSetChanged();
    }

    private void RecarregarContato() {
        lineerro.setVisibility(View.GONE);
        erro.setVisibility(View.GONE);
        adapterseguidores = new ContatosAdapter(ListaSeguidores, MeusSeguidores.this);
        recyclerMeusSeguidores.setAdapter(adapterseguidores);
        adapterseguidores.notifyDataSetChanged();
    }

}
