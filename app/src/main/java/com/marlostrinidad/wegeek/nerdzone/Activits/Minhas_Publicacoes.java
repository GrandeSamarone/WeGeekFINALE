package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Fragments.Minhas_publicacoes.Loja_Evento_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.Minhas_publicacoes.Minhas_arts_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.Minhas_publicacoes.Minhas_escritas_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class Minhas_Publicacoes extends AppCompatActivity {


    private Toolbar toolbar;
    private ChildEventListener ChildEventListenerperfil;
    private CircleImageView icone;
    private DatabaseReference databaseusuario;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);
        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Minhas Publicações");
        setSupportActionBar(toolbar);


        databaseusuario= ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");

        //Configurar Abas
        final FragmentPagerItemAdapter adapter= new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("COMÉRCIO & EVENTO",Loja_Evento_Fragment.class)
                        .add("TÓPICO & CONTO", Minhas_escritas_Fragment.class)
                        .add("FANARTS", Minhas_arts_Fragment.class)
                        // .add("Tops", RankFragment.class)
                        .create()
        );
        SmartTabLayout ViewPageTab = findViewById(R.id.SmartTabLayoutMinhasPublicacoes);
        mViewPager = findViewById(R.id.viewPagerMinhasPublicacoes);
        mViewPager.setAdapter(adapter);
        ViewPageTab.setViewPager(mViewPager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public void onStart() {
        super.onStart();
        TrocarFundos_status_bar();
        CarregarDados_do_Usuario();
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



            default:break;
        }

        return true;
    }
    private void CarregarDados_do_Usuario(){
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerperfil=databaseusuario.orderByChild("tipoconta").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;


                String icone = perfil.getFoto();
                IconeUsuario(icone);

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
    private void IconeUsuario(String url) {
        //Imagem do icone do usuario
        icone = findViewById(R.id.icone_user_toolbar);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Minhas_Publicacoes.this, MinhaConta.class);
                startActivity(it);

            }
        });


        Glide.with(Minhas_Publicacoes.this)
                .load(url)
                .into(icone);
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


}
