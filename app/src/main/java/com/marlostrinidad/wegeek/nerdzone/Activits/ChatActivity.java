package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Adapter.MensagensAdapter;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Conversa;
import com.marlostrinidad.wegeek.nerdzone.Model.Grupo;
import com.marlostrinidad.wegeek.nerdzone.Model.Mensagem;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.photodraweeview.PhotoDraweeView;

import static com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity.setWindowFlag;

public class ChatActivity extends AppCompatActivity  {

    private EmojiTextView textViewNome;
    private CircleImageView circleImageViewFoto;
    private Toolbar toolbarDialog;
    private Usuario usuariodestinatario;
    private Usuario usuarioRemetente;
    private PhotoDraweeView dialogimg;
    private Grupo grupo;
    private AlertDialog ImgDialog;
    private ViewPager viewPager;
    private EditText editMensagem;
    private ImageView imageCamera;
    private RecyclerView recyclemensagens;
    private MensagensAdapter adapter;
    private DatabaseReference mensagensRef,databaseusuario;
    private ChildEventListener childEventListenermensagens,childEventListenerusuario;
    private static final int SELECAO_GALERIA=100;
    private EmojiPopup emojiPopup;
    private ImageView botao_icone;
    private EmojiEditText chatField;
    private View root_view;
    private List<Mensagem> mensagens= new ArrayList<>();

    //identificado  usuario rementente e destinatario
    private String idUsuarioRemetente;
    private String idusuarioDestinatario;
    private DatabaseReference database;
    private StorageReference storage;
    private String id_do_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_chat);

        // Esta linha precisa ser executada antes de qualquer uso de EmojiTextView, EmojiEditText ou EmojiButton.
        EmojiManager.install(new GoogleEmojiProvider());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        id_do_usuario=getIntent().getStringExtra("id");
        idusuarioDestinatario=getIntent().getStringExtra("id");
        //Configuracoes iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclemensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera);
        viewPager = findViewById(R.id.viewpager);
        botao_icone = findViewById(R.id.button_chat_icone);
        chatField = findViewById(R.id.editMensagem);
        databaseusuario= ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
           //emotion
        root_view=findViewById(R.id.layoutroot);
        chatField.setBackgroundColor(0);
        emojiPopup = EmojiPopup.Builder.fromRootView(root_view).build(chatField);

        setUpEmojiPopup();

        botao_icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();

            }
        });



        //Recupera id do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdentificadorUsuario();
        //Recupera dados do usuario remetente
        usuarioRemetente= UsuarioFirebase.getDadosUsuarioLogado();

        //configurar adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext());

        //configurando recycleview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclemensagens.setLayoutManager(layoutManager);
        // recyclemensagens.setHasFixedSize(true);
        recyclemensagens.setAdapter(adapter);


        //
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        mensagensRef = database.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idusuarioDestinatario);


        //Evento de clique na camera
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(it.resolveActivity(getPackageManager())!=null)  {
                    startActivityForResult(it, SELECAO_GALERIA);
                }
            }
        });
        CarregarDados_do_Usuario_Mensagem();
        TrocarFundos_status_bar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void CarregarDados_do_Usuario_Mensagem(){

        childEventListenerusuario=databaseusuario.orderByChild("id").equalTo(id_do_usuario)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

                String capa = perfil.getFoto();
                Glide.with(ChatActivity.this)
                        .load(capa)
                        .into(circleImageViewFoto );

                circleImageViewFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(ChatActivity.this, Perfil.class);
                        it.putExtra("id",perfil.getId());
                        startActivity(it);

                    }
                });
                     textViewNome.setText(perfil.getNome());

                     textViewNome.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent it = new Intent(ChatActivity.this, Perfil.class);
                             it.putExtra("id",perfil.getId());
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



    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

           finish();

                break;

            default:break;
        }

        return true;
    }

      ///EMOTION
    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(root_view)

                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
                    @Override
                    public void onEmojiBackspaceClick(View v) {
                        if (emojiPopup.isShowing()) {
                            emojiPopup.dismiss();
                        }
                        Log.d("ss", "Clicked on Backspace");
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        botao_icone.setImageResource(R.drawable.ic_teclado);
                    }
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(final int keyBoardHeight) {
                        Log.d("ss", "Clicked on Backspace");
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        botao_icone.setImageResource(R.drawable.ic_emotion_chat);
                    }
                })
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        if (emojiPopup.isShowing()) {
                            emojiPopup.dismiss();
                        }
                        Log.d("ss", "Clicked on Backspace");
                    }
                })
                .build(chatField);

    }

    public void EnviarMensagem(View view){

        String textoMensagem =editMensagem.getText().toString();

        final Calendar calendartempo = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm   dd'/'MM'/'y",java.util.Locale.getDefault());// MM'/'dd'/'y;

        String tempoMensagem = simpleDateFormat.format(calendartempo.getTime());
        Usuario usuarioRemetentes= UsuarioFirebase.getDadosUsuarioLogado();
        if(!textoMensagem.isEmpty()){
            if(idusuarioDestinatario!=null){
                Mensagem mensagem= new Mensagem();
                mensagem.setIdUsuario(idUsuarioRemetente);
                mensagem.setNome(usuarioRemetentes.getNome());
                mensagem.setMensagem(textoMensagem);
                mensagem.setTempo(tempoMensagem);



                //salvar mensagem para o remetente
                SalvarMensagem(idUsuarioRemetente,idusuarioDestinatario,mensagem);

                //salvar mensagem para o Destinatario
                SalvarMensagem(idusuarioDestinatario,idUsuarioRemetente,mensagem);

                //Salvar Conversa Remetente
                SalvarConversa(idUsuarioRemetente,idusuarioDestinatario,usuariodestinatario,mensagem,false);

                //Salvar Conversa Destinatario
                Usuario usuarioRemetente= UsuarioFirebase.getDadosUsuarioLogado();
                SalvarConversa(idusuarioDestinatario,idUsuarioRemetente,usuarioRemetente,mensagem,false);

            }
        }else{
            Toast.makeText(this, "Digite uma mensagem para enviar!", Toast.LENGTH_SHORT).show();

        }

    }

    private void SalvarConversa(String idRemetente, String idDestinatario, Usuario usuarioExibicao, Mensagem msg, boolean isGroup) {

        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());


            conversaRemetente.setUsuarioExibicao(usuarioExibicao);
            conversaRemetente.setIsGroup("false");

        //Chamando o metodo Salvar
        conversaRemetente.salvar();



    }

    public void SalvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemref = database.child("mensagens");

        mensagemref.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);
        //limpando o texto
        editMensagem.setText("");
    }

    public void onStart() {
        super.onStart();
        RecuperarMensagem();

    }

    public void onStop(){
        super.onStop();
        mensagensRef.removeEventListener(childEventListenermensagens);
    }


    private  void RecuperarMensagem(){
        //nao deixa que as mensagem duplicam
        mensagens.clear();
        childEventListenermensagens =mensagensRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue( Mensagem.class );
                int initialSize  = mensagens.size();
                mensagens.add( mensagem );
                recyclemensagens.scrollToPosition(mensagens.size()-1);
                adapter.notifyItemRangeChanged(initialSize,mensagens.size()-1);
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
}

