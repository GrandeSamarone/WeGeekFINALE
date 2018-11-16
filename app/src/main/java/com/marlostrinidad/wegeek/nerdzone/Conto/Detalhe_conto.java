package com.marlostrinidad.wegeek.nerdzone.Conto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_comentario;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comentario;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto;
import com.marlostrinidad.wegeek.nerdzone.Model.ContoLike;
import com.marlostrinidad.wegeek.nerdzone.Model.Conto_colecao;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detalhe_conto extends AppCompatActivity {
    private Toolbar toolbar;
    private Conto contoselecionado;
    private Adapter_comentario adapter;
    private ArrayList<Comentario> listcomentario = new ArrayList<>();
    private RecyclerView recyclerView_comentarios;
    private CircleImageView icone;
    private SimpleDraweeView foto;
    private TextView nome_autor,titulo,mensagem,titulotoolbar,num_like,num_conto,texto_conto;
    private EmojiEditText edit_chat_emoji;
    private  android.support.v7.widget.AppCompatButton botao_env_msg;
    private EmojiPopup emojiPopup;
    private ImageView botaoicone;
    private View root;
    private DatabaseReference database,database_topico;
    private FirebaseUser usuario;
    private String usuarioLogado;
    private Topico topicoselecionado;
    private LinearLayout clickPerfil;
    private ChildEventListener ChildEventListenerdetalhe;
    private ChildEventListener ChildEventListeneruser;
    private DatabaseReference ComentarioReference;
    private AlertDialog dialog;
    private SparkButton botaocurtir,botao_add_colecao;
    String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_conto);

        toolbar = findViewById(R.id.toolbar_detalhe_conto);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        titulotoolbar=findViewById(R.id.nome_conto_detalhe);
        texto_conto =findViewById(R.id.txt_add_colecao_detalhe);
        botao_add_colecao=findViewById(R.id.botao_add_a_colecao_detalhe);
        botaocurtir=findViewById(R.id.botaocurtirconto_detalhe);
        titulo=findViewById(R.id.conto_titulo_detalhe);
        mensagem=findViewById(R.id.conto_mensagem_detalhe);
        nome_autor = findViewById(R.id.conto_author_detalhe);
        num_like = findViewById(R.id.conto_num_curit_detalhe);
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        contoselecionado = (Conto) getIntent().getSerializableExtra("contoselecionado");

        if(contoselecionado!=null){
            titulotoolbar.setText(contoselecionado.getTitulo());
            titulo.setText(contoselecionado.getTitulo());
            mensagem.setText(contoselecionado.getMensagem());
            num_like.setText(String.valueOf(contoselecionado.getLikecount()));
            CarregarNomedoAuthor(contoselecionado.getIdauthor());
            CarregarInfo_botoes(contoselecionado.getUid());

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        CarregarDados_do_Usuario();
        TrocarFundos_status_bar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.removeEventListener(ChildEventListenerdetalhe);
    }

    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar

                finish();

        }

        return super.onOptionsItemSelected(item);
    }

    private void CarregarNomedoAuthor(String idnome){
        DatabaseReference eventoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(idnome);
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario user = dataSnapshot.getValue(Usuario.class);

                nome_autor.setText(user.getNome());

                if(!user.getId().equals(identificadorUsuario)) {

                    nome_autor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Detalhe_conto.this, Perfil.class);
                            it.putExtra("id", user.getId());
                            startActivity(it);
                        }
                    });
                }else{
                    nome_autor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Detalhe_conto.this, MinhaConta.class);
                            it.putExtra("id", user.getId());
                            startActivity(it);
                        }
                    });
                }
            /*Glide.with(context)
                        .load(user.getFoto())
                        .into(holder.imgperfil );*/

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void CarregarDados_do_Usuario(){
        usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerdetalhe=database.orderByChild("tipoconta")
                .equalTo(email).addChildEventListener(new ChildEventListener() {
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
        icone = findViewById(R.id.icone_user_toolbar_detalhe);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_conto.this, MinhaConta.class);
                startActivity(it);

            }
        });
        Glide.with(Detalhe_conto.this)
                .load(url)
                .into(icone);

    }

    private void CarregarInfo_botoes(String idConto){
        final Usuario usuariologado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference topicoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto-likes")
                .child(idConto);
        topicoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdLikes = 0;
                if(dataSnapshot.hasChild("qtdlikes")){
                    ContoLike contoLike = dataSnapshot.getValue(ContoLike.class);
                    QtdLikes = contoLike.getQtdlikes();
                }
                if( dataSnapshot.hasChild( usuariologado.getId() ) ){
                    botaocurtir.setChecked(true);
                }else {
                    botaocurtir.setChecked(false);
                }

                //Montar objeto postagem curtida
                final ContoLike like = new ContoLike();
                like.setConto(contoselecionado);
                like.setUsuario(usuariologado);
                like.setQtdlikes(QtdLikes);
                Log.i("asas", String.valueOf(usuariologado));
                //adicionar evento para curtir foto
                botaocurtir.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if(buttonState){
                            like.Salvar();

                            num_like.setText(String.valueOf(like.getQtdlikes()));
                        }else{
                            like.removerlike();
                            num_like.setText(String.valueOf(like.getQtdlikes()));
                        }
                    }
                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                    }
                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {
                    }
                });
                num_like.setText(String.valueOf(like.getQtdlikes()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference conto_add_colecao= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto-colecao")
                .child(idConto);
        conto_add_colecao.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdAdd = 0;
                if (dataSnapshot.hasChild("qtdadd")) {
                    Conto_colecao conto_colecao = dataSnapshot.getValue(Conto_colecao.class);
                    QtdAdd = conto_colecao.getQtdadd();
                }
                //Verifica se já foi clicado
                Log.i("asasas",usuariologado.getId());
                if (dataSnapshot.hasChild(usuariologado.getId())) {
                    botao_add_colecao.setChecked(true);
                    texto_conto.setText(R.string.adicionado_colecao);
                } else {
                    botao_add_colecao.setChecked(false);
                    texto_conto.setText(R.string.adicionar_colecao);
                }

                //Montar objeto postagem curtida
                final Conto_colecao colecao = new Conto_colecao();
                colecao.setConto(contoselecionado);
                colecao.setUsuario(usuariologado);
                colecao.setQtdadd(QtdAdd);

                //adicionar evento para curtir foto
                botao_add_colecao.setEventListener(new SparkEventListener() {
                    @Override
                    public void onEvent(ImageView button, boolean buttonState) {
                        if (buttonState) {
                            colecao.Salvar();
                            contoselecionado.AdicioneiConto();
                            texto_conto.setText(R.string.adicionado_colecao);
                        } else {
                            colecao.removercolecao();
                            texto_conto.setText(R.string.adicionar_colecao);
                        }
                    }

                    @Override
                    public void onEventAnimationEnd(ImageView button, boolean buttonState) {
                    }

                    @Override
                    public void onEventAnimationStart(ImageView button, boolean buttonState) {
                    }
                });
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

}