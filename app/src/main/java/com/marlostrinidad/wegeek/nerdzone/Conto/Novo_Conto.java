package com.marlostrinidad.wegeek.nerdzone.Conto;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Novo_Conto extends AppCompatActivity {
    private static final String padrao = "Obrigatório";
    private Toolbar toolbar;
    private CircleImageView icone,img_topico;
    private Button botaosalvar;
    private DatabaseReference databaseusuario,databasetopico,SeguidoresRef;
    private DataSnapshot seguidoresSnapshot;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil,ChildEventListenerSeguidores;
    private EditText titulo_conto,mensagem_conto;
    private Conto conto = new Conto();
    private Usuario perfil;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conto);
        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Novo Conto");
        setSupportActionBar(toolbar);


        //Configuraçoes Originais
        databaseusuario = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        databasetopico = ConfiguracaoFirebase.getDatabase().getReference().child("conto");
        SeguidoresRef =ConfiguracaoFirebase.getDatabase().getReference().child("seguidores");
        titulo_conto = findViewById(R.id.titulo_conto);
        mensagem_conto = findViewById(R.id.desc_conto);
        botaosalvar = findViewById(R.id.botaosalvarconto);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosConto();
            }
        });




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private Conto configurarConto(){
        String titulo = titulo_conto.getText().toString();
        String mensagem = mensagem_conto.getText().toString();
        final Calendar calendartempo = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'-'MM'-'y",java.util.Locale.getDefault());// MM'/'dd'/'y;
        String data = simpleDateFormat.format(calendartempo.getTime());

        conto.setIdauthor(perfil.getId());

        conto.setTitulo(titulo);
        conto.setMensagem(mensagem);
        conto.setData(data);

        return  conto;

    }
    public void validarDadosConto() {
        conto = configurarConto();
        if (TextUtils.isEmpty(conto.getTitulo())) {
            titulo_conto.setError(padrao);
            return;
        }

        if (TextUtils.isEmpty(conto.getMensagem())) {
            mensagem_conto.setError(padrao);
            return;
        }
        conto.SalvarConto(seguidoresSnapshot);
        int qtdContos=perfil.getContos()+1;
        perfil.setContos(qtdContos);
        perfil.atualizarQtdContos();
        Intent it = new Intent(Novo_Conto.this,ListaConto.class);
        startActivity(it);
        Toast.makeText(this, "Postado com Sucesso", Toast.LENGTH_LONG).show();
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        CarregarDados_do_Usuario();
        TrocarFundos_status_bar();
        CarregarSeguidores();
    }

    private void CarregarDados_do_Usuario(){
        usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerperfil=databaseusuario.orderByChild("tipoconta")
                .equalTo(email).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        perfil = dataSnapshot.getValue(Usuario.class );
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

    private void CarregarSeguidores(){
        String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
        //Recuperar Seguidores
        DatabaseReference seguidoresref =SeguidoresRef.child(usuariologado);
        seguidoresref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seguidoresSnapshot=dataSnapshot;
                Log.i("asdsds", String.valueOf(seguidoresSnapshot));
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
                Intent it = new Intent(Novo_Conto.this, MinhaConta.class);
                startActivity(it);

            }
        });
        Glide.with(Novo_Conto.this)
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
