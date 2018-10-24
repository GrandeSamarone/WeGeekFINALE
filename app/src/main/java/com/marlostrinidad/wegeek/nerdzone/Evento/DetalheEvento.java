package com.marlostrinidad.wegeek.nerdzone.Evento;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comentario;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento_Visualizacao;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalheEvento extends AppCompatActivity {


    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private DatabaseReference ComentarioReference;

    private Evento eventoselecionado;
    private ProgressBar progressBar;
    private TextView Author_evento_View;
    private SimpleDraweeView eventobanner;
    private CircleImageView AuthorFoto_evento_View;
    private TextView titutoevento,datainicial,datafinal;
    private TextView mensagem_evento,quant_visu_evento;
    private EmojiEditText edit_chat_emoji;
    private ImageView botao_icone;
    private  android.support.v7.widget.AppCompatButton botao_env_msg;
    private DatabaseReference mDatabase;
    private static RecyclerView recyclerViewcomentarios;
    private String usuarioLogado;
    private Adapter_comentario adapter;
    private List<Comentario> listcomentario = new ArrayList<>();
    private DatabaseReference database_evento,database_usuario;
    private com.google.firebase.database.ChildEventListener ChildEventListenerevento;
    private View root_view;
    private EmojiPopup emojiPopup;
    private ChildEventListener ChildEventListeneruser;
    private String ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_evento);

         toolbar = (Toolbar) findViewById(R.id.toolbarlayout);
        setSupportActionBar(toolbar);


        //configuracoes iniciais
        progressBar= findViewById(R.id.progressbar_detalhe_Evento);
        database_evento = ConfiguracaoFirebase.getDatabase().getReference().child("evento");
        database_usuario = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        collapsingToolbarLayout = findViewById(R.id.collapseLayoutevento);
        EmojiManager.install(new GoogleEmojiProvider());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usuarioLogado =  UsuarioFirebase.getIdentificadorUsuario();

        // Initialize Views
       // datafinal = findViewById(R.id.datafinal);
      //  datainicial = findViewById(R.id.datainicial);
       // toolbarnomeevento= findViewById(R.id.nomeevento_toolbaar);
        titutoevento = findViewById(R.id.detalhe_evento_titulo);
        botao_env_msg = findViewById(R.id.button_postar_comentario);
        botao_env_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvarComentario();
            }
        });
        Author_evento_View = findViewById(R.id.author_evento);
        quant_visu_evento = findViewById(R.id.quantvisualizacao_detalhe);
        mensagem_evento = findViewById(R.id.detalhe_evento_mensagem);
        AuthorFoto_evento_View=findViewById(R.id.icone_author);
        eventobanner = findViewById(R.id.bannereventocapa);
        edit_chat_emoji = findViewById(R.id.caixa_de_texto_comentario);
        botao_icone = findViewById(R.id.botao_post_icone);
        recyclerViewcomentarios = findViewById(R.id.recycler_comentario);
        //configurar adapter
        adapter = new Adapter_comentario(listcomentario, getApplicationContext());

        //configurando recycleview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewcomentarios.setLayoutManager(layoutManager);
        //recyclerViewcomentarios.setHasFixedSize(true);
        recyclerViewcomentarios.setAdapter(adapter);


        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.background));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.branco));

        //emotion
        root_view=findViewById(R.id.root_view);
        emojiPopup = EmojiPopup.Builder.fromRootView(root_view).build(edit_chat_emoji);
        setUpEmojiPopup();
        botao_icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();

            }
        });


        CarregarDados_do_Evento();
        CarregarDados_Comentario_Evento();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CarregarDados_do_Evento(){

         ids=getIntent().getStringExtra("id_do_evento");
        String estado= getIntent().getStringExtra("UR_do_evento");
        ChildEventListenerevento=database_evento    .child(estado).orderByChild("uid")
                .equalTo(ids).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                eventoselecionado = dataSnapshot.getValue(Evento.class );
                assert eventoselecionado != null;


                Uri uri = Uri.parse(eventoselecionado.getCapaevento());

                DraweeController controllerOne = Fresco.newDraweeControllerBuilder()
                        .setUri(uri)
                        .setAutoPlayAnimations(true)
                        .build();

                eventobanner.setController(controllerOne);
                progressBar.setVisibility(View.GONE);


                eventobanner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(DetalheEvento.this, AbrirImagem.class);
                        it.putExtra("id_foto",eventoselecionado.getCapaevento());
                        it.putExtra("nome_foto",  eventoselecionado.getTitulo());
                        startActivity(it);
                    }
                });

                mensagem_evento.setText(eventoselecionado.getMensagem());
                quant_visu_evento.setText(String.valueOf(eventoselecionado.getQuantVisualizacao()));
                Author_evento_View.setText(eventoselecionado.getAuthor());
               titutoevento.setText(eventoselecionado.getTitulo());
                collapsingToolbarLayout.setTitle(eventoselecionado.getTitulo());
                CarregarDados_do_Criador_do_Evento(eventoselecionado.getIdUsuario());

                ContaQuatAcesso(eventoselecionado);


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

    public void SalvarComentario(){
        String textoComentario = edit_chat_emoji.getText().toString();
        if(textoComentario!=null && !textoComentario.equals(""))
        {
            Comentario comentario = new Comentario();
            comentario.setId_postagem(ids);
            comentario.setId_author(usuarioLogado);
            comentario.setText(textoComentario);
            comentario.salvar();

        }else{
            Toast.makeText(this, "Insira um comentário antes da salvar!",
                    Toast.LENGTH_LONG).show();
        }
        //Limpar comentario
        edit_chat_emoji.setText("");
    }
    private void CarregarDados_do_Criador_do_Evento(String idusuario){
        ChildEventListeneruser=database_usuario.orderByChild("id")
                .equalTo(idusuario).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Usuario user = dataSnapshot.getValue(Usuario.class );
                        assert user != null;

                        String foto =user.getFoto();
                        Glide.with(DetalheEvento.this)
                                .load(foto)
                                .into(AuthorFoto_evento_View );

                        Author_evento_View.setText(user.getNome());
                        if(!usuarioLogado.equals(user.getId())){
                        Author_evento_View.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent it = new Intent(DetalheEvento.this, Perfil.class);
                                it.putExtra("id",user.getId());
                                startActivity(it);
                            }
                        });
                            AuthorFoto_evento_View.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(DetalheEvento.this, Perfil.class);
                                    it.putExtra("id",user.getId());
                                    startActivity(it);
                                }
                            });
                        }else {
                            Author_evento_View.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent it = new Intent(DetalheEvento.this, MinhaConta.class);
                                    it.putExtra("id",user.getId());
                                    startActivity(it);
                                }
                            });
                            AuthorFoto_evento_View.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(DetalheEvento.this, MinhaConta.class);
                                    it.putExtra("id",user.getId());
                                    startActivity(it);
                                }
                            });
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


    private void CarregarDados_Comentario_Evento(){
        ComentarioReference = FirebaseDatabase.getInstance().getReference()
                .child("comentario-evento").child(ids);
        ChildEventListeneruser=ComentarioReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Comentario comentario = dataSnapshot.getValue(Comentario.class);
                listcomentario.add(comentario);

                recyclerViewcomentarios.scrollToPosition(listcomentario.size()-1);
                adapter.notifyItemInserted(listcomentario.size()-1);
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


    public void ContaQuatAcesso(final Evento evento){

         //Contando as Visualizacoes
        DatabaseReference eventoscurtidas= ConfiguracaoFirebase.getFirebaseDatabase()
                .child("evento-visualizacao")
                .child(evento.getUid());
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdVisu = 0;
                if(dataSnapshot.hasChild("qtdvisualizacao")){
                    Evento_Visualizacao eventoVisu = dataSnapshot.getValue(Evento_Visualizacao.class);
                    QtdVisu =eventoVisu.getQtdvisualizacao();
                }

                //Montar objeto postagem curtida
                Evento_Visualizacao visualizacao = new Evento_Visualizacao();
                visualizacao.setEvento(evento);
                visualizacao.setQtdvisualizacao(QtdVisu);

                visualizacao.Salvar();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(root_view)
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
                        botao_icone.setImageResource(R.drawable.ic_teclado);
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
                        botao_icone.setImageResource(R.drawable.ic_emotion_chat);
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



}
