package com.marlostrinidad.wegeek.nerdzone.Topico;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_comentario;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comentario;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detalhe_topico extends AppCompatActivity {

    private Toolbar toolbar;
    private Adapter_comentario adapter;
    private List<Comentario> listcomentario = new ArrayList<>();
    private RecyclerView recyclerView_comentarios;
    private CircleImageView icone;
    private SimpleDraweeView foto;
    private TextView nome_autor,titulo,mensagem,titulotoolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhetopico);
        toolbar = findViewById(R.id.toolbartopico);
        toolbar.setTitle("Detalhe do Tópico");
        setSupportActionBar(toolbar);

        //Configuracoes Iniciais
        icone = findViewById(R.id.icon_topico_detalhe_author);
        foto = findViewById(R.id.foto_topico);
        nome_autor = findViewById(R.id.nome_topico__detalhe_autor);
        titulo = findViewById(R.id.detalhe_topico_titulo);
        mensagem = findViewById(R.id.detalhe_topico_mensagem);
       clickPerfil = findViewById(R.id.nome_foto_click);
       edit_chat_emoji=findViewById(R.id.caixa_de_texto_comentario_topico);
       botao_env_msg=findViewById(R.id.button_postar_comentario_topico);
       botaoicone=findViewById(R.id.botao_post_icone_topico);
        usuarioLogado =  UsuarioFirebase.getIdentificadorUsuario();
       //RecycleView
        recyclerView_comentarios = findViewById(R.id.recycler_comentario_topico);
        //configurar adapter
        adapter = new Adapter_comentario(listcomentario, getApplicationContext());
        //configurando recycleview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_comentarios.setLayoutManager(layoutManager);
        //recyclerViewcomentarios.setHasFixedSize(true);
        recyclerView_comentarios.setAdapter(adapter);

        //emotion
        root=findViewById(R.id.root_view_topico);
        emojiPopup = EmojiPopup.Builder.fromRootView(root).build(edit_chat_emoji);
        setUpEmojiPopup();
        botaoicone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();

            }
        });
        botao_env_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    SalvarComentario();
            }
        });
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
         topicoselecionado = (Topico)  getIntent().getSerializableExtra("topicoselecionado");
        if(topicoselecionado!=null){
            ComentarioReference = FirebaseDatabase.getInstance().getReference().child("comentario-topico").child(topicoselecionado.getUid());

            titulo.setText(topicoselecionado.getTitulo());
            mensagem.setText(topicoselecionado.getMensagem());
            RecuperarIcone_e_nome_author(topicoselecionado.getIdauthor());
            if(topicoselecionado.getFoto()!=null) {
                Uri capa = Uri.parse(topicoselecionado.getFoto());
                if (capa != null) {
                    foto.setVisibility(View.VISIBLE);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(capa)
                            .setLocalThumbnailPreviewsEnabled(true)
                            .setProgressiveRenderingEnabled(true)
                            .build();

                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .build();
                   foto.setController(controller);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(7f);
                    GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
                    GenericDraweeHierarchy hierarchy = builder
                            .setRoundingParams(roundingParams)
                            .setProgressBarImage(new CircleProgressDrawable())
                            //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                            .build();
                    foto.setHierarchy(hierarchy);
                    dialog.dismiss();
                }
            }else{
                foto.setVisibility(View.GONE);
            }

            foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   String ff = topicoselecionado.getFoto();
                    Intent it = new Intent(Detalhe_topico.this, AbrirImagem.class);
                    it.putExtra("id_foto",  topicoselecionado.getFoto());
                    it.putExtra("nome_foto",  topicoselecionado.getTitulo());
                    startActivity(it);
                }
            });
        }



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void GifCarregarDados() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(Detalhe_topico.this);
        final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_comscroop, null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.gif_self)
                .into(imageViewgif);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }



    @Override
    protected void onStart() {
        super.onStart();
        GifCarregarDados();
        CarregarDados_do_Usuario();
        TrocarFundos_status_bar();
        CarregarDados_Comentario_Evento();
    }



    public void SalvarComentario(){
        String textoComentario = edit_chat_emoji.getText().toString();
        if(textoComentario!=null && !textoComentario.equals(""))
        {
            Comentario comentario = new Comentario();
            comentario.setId_postagem(topicoselecionado.getUid());
            comentario.setId_author(usuarioLogado);
            comentario.setText(textoComentario);
            comentario.salvar_Topico();

        }else{
            Toast.makeText(this, "Insira um comentário antes da salvar!",
                    Toast.LENGTH_LONG).show();
        }
        //Limpar comentario
        edit_chat_emoji.setText("");
    }


    private void CarregarDados_Comentario_Evento(){
        listcomentario.clear();
        ChildEventListeneruser=ComentarioReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comentario comentario = dataSnapshot.getValue(Comentario.class);
                int initialSize  = listcomentario.size();
                listcomentario.add(comentario);

                recyclerView_comentarios.scrollToPosition(listcomentario.size()-1);
                adapter.notifyItemRangeChanged(initialSize,listcomentario.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("sdsd", String.valueOf(dataSnapshot.getChildrenCount()));
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


    private void RecuperarIcone_e_nome_author(String id ) {
        database.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Usuario  user = dataSnapshot.getValue(Usuario.class);
               nome_autor.setText(user.getNome());
                String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

                if(!user.getId().equals(identificadorUsuario)) {

                    clickPerfil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Detalhe_topico.this, Perfil.class);
                            it.putExtra("id", user.getId());
                            startActivity(it);
                        }
                    });
                }else{
                    clickPerfil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(Detalhe_topico.this, MinhaConta.class);
                            startActivity(it);
                        }
                    });
                }
                Glide.with(Detalhe_topico.this)
                        .load(user.getFoto())
                        .into(icone);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar

                    finish();

        }

        return super.onOptionsItemSelected(item);
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
        icone = findViewById(R.id.icone_user_toolbar);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_topico.this, MinhaConta.class);
                startActivity(it);

            }
        });
        Glide.with(Detalhe_topico.this)
                .load(url)
                .into(icone);
        dialog.dismiss();
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


    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(root)
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
                    @Override
                    public void onEmojiBackspaceClick(View v) {
                        if(emojiPopup.isShowing()){
                            emojiPopup.dismiss();
                        }
                        Log.d("ss","Clicked on Backspace");
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        botaoicone.setImageResource(R.drawable.ic_teclado);
                    }
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(final int keyBoardHeight) {
                        Log.d("ss","Clicked on Backspace");
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        botaoicone.setImageResource(R.drawable.ic_emotion_chat);
                    }
                })
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        if (emojiPopup.isShowing()){
                            emojiPopup.dismiss();
                        }
                        Log.d("ss","Clicked on Backspace");
                    }
                })
                .build(edit_chat_emoji);
    }
}
