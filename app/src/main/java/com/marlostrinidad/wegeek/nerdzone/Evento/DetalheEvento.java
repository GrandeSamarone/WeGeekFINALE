package com.marlostrinidad.wegeek.nerdzone.Evento;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Membro_Grupo;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_chat_grupo;
import com.marlostrinidad.wegeek.nerdzone.Assinantes.MeusAssinantes;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Conversa.ChatActivity_loja;
import com.marlostrinidad.wegeek.nerdzone.Evento.Status.Info_Status_Evento;
import com.marlostrinidad.wegeek.nerdzone.Evento.Status.VerStatusEvento;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Helper.MySpannable;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Status.Pag_add_Status;
import com.marlostrinidad.wegeek.nerdzone.Model.Chat_Grupo;
import com.marlostrinidad.wegeek.nerdzone.Model.Chat_Particular;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Item_loja;
import com.marlostrinidad.wegeek.nerdzone.Model.Membro_Grupo;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.APIService;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Client;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Data;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.MyResponse;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Sender;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;

public class DetalheEvento extends TrocarFundo {

    private SharedPreferences dados_usuario;
    private CardView cardView;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_STATUS = 10;
    private Toolbar toolbar;
    private TextView textToolbar, nome_loja, desc,txt_nada,desc_end_loja,txt_valor_entrada,data_inicio,data_fim;
    private String id_evento,icone_loja,id_dono_loja,token_dono_loja;
    private SimpleDraweeView icon_loja;
    private StorageReference storageReference;
    private FloatingActionButton botao_novo_produto_categoria;
    private String identificadorUsuario;
    private Item_loja item_loja;
    private FirebaseFirestore db;
    private AlertDialog dialog;
    private ArrayList<Chat_Grupo> list_conversa_grupo = new ArrayList<>();
    private Chat_Grupo chat = new Chat_Grupo();
    private RecyclerView recyclerView_chat, recycler_chat_online;
    private LinearLayoutManager layoutManager;
    private Adapter_chat_grupo adapter;
    private Adapter_Membro_Grupo adapter_membro_grupo;
    private ArrayList<Membro_Grupo> list_membro_grupo = new ArrayList<>();
    private Button botao_minimizar;
    private EmojiEditText edit_chat_emoji;
    private View root;
    private EmojiPopup emojiPopup;
    private androidx.appcompat.widget.AppCompatButton botao_env_msg;
    private String urlimg,nome_loja_string;
    private CircleImageView circleImageView_status_visitante,circleImageView_status_admin;
    private ConstraintLayout progresso_carregando_visitante,progresso_carregando_admin;
    private APIService apiService;
    private ImageView botaoicone;
    private ViewGroup transitionsContainer;
    private SparkButton receber_novidades;
    private LinearLayout rel_status_admin,rel_status_visitante,line_config,
            line_status,line_nada_encontrado_txt,comochegar,line_iconchat,line_assinante,line_botoes;
    private RelativeLayout line_rec_status_admin,line_rec_status_vistante;
    private ListenerRegistration registration_loja;
    private String textoComentario;
    private boolean typingStarted;
    private ListenerRegistration registration_membro,registration,registration_status;
    private boolean notify;
    private CardView analise;
    private Button permitir,deletar;
    private  SimpleDateFormat simpleDateFormat_dia,simpleDateFormat_dia_mes,simpleDateFormat_mes,simpleDateFormat_hora;
    boolean isKeyboardShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_evento);
        toolbar = findViewById(R.id.toolbar_detalhe_evento);
        toolbar.setTitle("");
        textToolbar = findViewById(R.id.textViewtitulo_evento);
        setSupportActionBar(toolbar);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

           analise=findViewById(R.id.em_analise);
           permitir=findViewById(R.id.bton_permitir);
        deletar=findViewById(R.id.bton_deletar);
        //configuracoes iniciais d HH:mm
        simpleDateFormat_dia = new SimpleDateFormat("EEEE",java.util.Locale.getDefault());// MM'/'dd'/'y;
         simpleDateFormat_dia_mes = new SimpleDateFormat("d",java.util.Locale.getDefault());// MM'/'dd'/'y;
         simpleDateFormat_mes = new SimpleDateFormat("MMM",java.util.Locale.getDefault());// MM'/'dd'/'y;
       simpleDateFormat_hora = new SimpleDateFormat("HH:mm",java.util.Locale.getDefault());// MM'/'dd'/'y;
        data_inicio=findViewById(R.id.data_evento_inicio);
        data_fim=findViewById(R.id.data_evento_fim);
        txt_valor_entrada=findViewById(R.id.txt_valor_entrada);
        botaoicone = findViewById(R.id.botao_post_icone_detalhe_evento);
        edit_chat_emoji = findViewById(R.id.caixa_de_texto_comentario_detalhe_evento);
        botao_env_msg = findViewById(R.id.button_postar_comentario_detalhe_evento);
        botao_minimizar = findViewById(R.id.botao_minimizar_evento);
        cardView=findViewById(R.id.card_topo);
        transitionsContainer=findViewById(R.id.card_topo);
        receber_novidades=findViewById(R.id.botao_receber_novidades_evento);
        line_assinante=findViewById(R.id.line_assinante_evento);
        line_iconchat=findViewById(R.id.icon_chat_evento);
        desc_end_loja=findViewById(R.id.desc_end_loja_evento);
        comochegar=findViewById(R.id.como_chegar_evento);
        line_status=findViewById(R.id.line_vizu_evento);
        line_config=findViewById(R.id.line_config_evento);
        rel_status_visitante=findViewById(R.id.line_menu_visitante_evento);
        rel_status_admin=findViewById(R.id.line_menu_admin_evento);
        progresso_carregando_visitante=findViewById(R.id.const_carregando_icon_de_loja_visitante_evento);
        progresso_carregando_admin=findViewById(R.id.const_carregando_icon_de_loja_admin_evento);
        circleImageView_status_visitante=findViewById(R.id.circleImageViewStatus_visitante_evento);
        circleImageView_status_admin=findViewById(R.id.circleImageViewStatus_admin_evento);
        db = FirebaseFirestore.getInstance();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        line_botoes = findViewById(R.id.line_botoes_evento);

        icon_loja = findViewById(R.id.icon_loja_evento);
        nome_loja = findViewById(R.id.nome_loja_evento);
        desc = findViewById(R.id.desc_loja_evento);
        //RecycleView onlines
        LinearLayoutManager layoutManagerMembros = new LinearLayoutManager(
                DetalheEvento.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_chat_online = findViewById(R.id.recycler_chat_online_Detalhe_evento);
        recycler_chat_online.setLayoutManager(layoutManagerMembros);
        adapter_membro_grupo = new Adapter_Membro_Grupo(list_membro_grupo, getApplicationContext());
        recycler_chat_online.setAdapter(adapter_membro_grupo);


        //RecycleView Chat
        recyclerView_chat = findViewById(R.id.recycler_chat_grupo_detalhe_evento);
        recyclerView_chat.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView_chat.setLayoutManager(layoutManager);
        adapter = new Adapter_chat_grupo(DetalheEvento.this, list_conversa_grupo);
        recyclerView_chat.setAdapter(adapter);


        line_rec_status_vistante=findViewById(R.id.rel_status_detalhe_loja_visitante_evento);
        line_rec_status_admin=findViewById(R.id.rel_status_detalhe_loja_admin_evento);


        //emotion
        root = findViewById(R.id.detalhe_evento);
        emojiPopup = EmojiPopup.Builder.fromRootView(root).build(edit_chat_emoji);
        setUpEmojiPopup();

        //id_categoria= getIntent().getStringExtra("categoria");
        id_evento = getIntent().getStringExtra("id");
        Log.i("osdksodksodks",id_evento);
        RecuperarDados_Evento(id_evento);



        botao_minimizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardView.getVisibility() == View.VISIBLE) {
                    //efeito na transaicao
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(transitionsContainer);
                    }
                    Drawable image = getResources().getDrawable(R.drawable.ic_seta_pra_baixa);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    botao_minimizar.setCompoundDrawables(null, null, image, null);

                    cardView.setVisibility(GONE);
                    botao_minimizar.setText("MOSTRAR");
                } else {
                    //efeito na transaicao
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(transitionsContainer);
                    }
                    Drawable image = getResources().getDrawable(R.drawable.ic_seta_subir);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    botao_minimizar.setCompoundDrawables(null, null, image, null);
                    cardView.setVisibility(View.VISIBLE);
                    botao_minimizar.setText("ESCONDER");


                }
            }
        });



        botaoicone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle();
            }
        });
        botao_env_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textoComentario = edit_chat_emoji.getText().toString();
                if (textoComentario != null && !textoComentario.equals("")) {
                    SalvarMensagem();
                    // Mandar_Notificacao();
                } else {
                    //Balão informação
                    Toast.makeText(DetalheEvento.this, "Digite uma mensagem.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        receber_novidades.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(buttonState){
                    InscreverUsuario();
                    Dialog(
                            "Você receberá novidades desse evento.");
                    FirebaseMessaging.getInstance().subscribeToTopic(id_evento)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    if (!task.isSuccessful()) {
                                        //    Toast.makeText(DetalheEvento.this, "erro", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                    Toast.makeText(DetalheEvento.this, "Você  receberá informações sobre esse Evento.", Toast.LENGTH_SHORT).show();
                }else{
                    desinscreverUsuario();
                    Dialog("Não receberá novidades desse evento.");
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });


        // Quando o usuario abrir o chat ele fecha a tela de detalhes
        edit_chat_emoji.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        edit_chat_emoji.getWindowVisibleDisplayFrame(r);
                        int screenHeight = edit_chat_emoji.getRootView().getHeight();

                        // r.bottom is the position above soft keypad or device button.
                        // if keypad is shown, the r.bottom is smaller than that before.
                        int keypadHeight = screenHeight - r.bottom;

                        //  Log.d(TAG, "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                            // keyboard is opened
                            if (!isKeyboardShowing) {
                                isKeyboardShowing = true;
                                onKeyboardVisibilityChanged(true);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(transitionsContainer);
                                }
                                Drawable image = getResources().getDrawable(R.drawable.ic_seta_pra_baixa);
                                int h = image.getIntrinsicHeight();
                                int w = image.getIntrinsicWidth();
                                image.setBounds(0, 0, w, h);
                                botao_minimizar.setCompoundDrawables(null, null, image, null);

                                cardView.setVisibility(GONE);
                                botao_minimizar.setText("MOSTRAR");
                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);

                                //efeito na transaicao
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    TransitionManager.beginDelayedTransition(transitionsContainer);
                                }
                                Drawable image = getResources().getDrawable(R.drawable.ic_seta_subir);
                                int h = image.getIntrinsicHeight();
                                int w = image.getIntrinsicWidth();
                                image.setBounds(0, 0, w, h);
                                botao_minimizar.setCompoundDrawables(null, null, image, null);
                                cardView.setVisibility(View.VISIBLE);
                                botao_minimizar.setText("ESCONDER");
                            }
                        }
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    //aqui ele recebe e muda
    void onKeyboardVisibilityChanged(boolean opened) {
        if(opened){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
            }
            Drawable image = getResources().getDrawable(R.drawable.ic_seta_pra_baixa);
            int h = image.getIntrinsicHeight();
            int w = image.getIntrinsicWidth();
            image.setBounds(0, 0, w, h);
            botao_minimizar.setCompoundDrawables(null, null, image, null);

            cardView.setVisibility(GONE);
            botao_minimizar.setText("MOSTRAR");
        }else{
            //efeito na transaicao
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
            }
            Drawable image = getResources().getDrawable(R.drawable.ic_seta_subir);
            int h = image.getIntrinsicHeight();
            int w = image.getIntrinsicWidth();
            image.setBounds(0, 0, w, h);
            botao_minimizar.setCompoundDrawables(null, null, image, null);
            cardView.setVisibility(View.VISIBLE);
            botao_minimizar.setText("ESCONDER");
        }
    }
    private void SalvarMensagem() {
        //SharedPreferencies pegando a variavel e os dados
        String nome = dados_usuario.getString("nome", "");
        String foto = dados_usuario.getString("foto_usuario", "");
        String textoComentario = edit_chat_emoji.getText().toString();
        String id_mensagem_texto = UUID.randomUUID().toString();

        if (textoComentario != null && !textoComentario.equals("")) {
            chat.setId_grupo(id_evento);
            chat.setId_usuario(identificadorUsuario);
            Log.i("sdoskdosdk77", identificadorUsuario);
            chat.setNome_usuario(nome);
            chat.setMensagem_type("texto");
            chat.setFoto_usuario(foto);
            chat.setMensagem(textoComentario);
            chat.setId_mensagem(id_mensagem_texto);
            chat.Salvar_msg_Evento();

            sendNotifiaction_evento_chat(nome,textoComentario,nome_loja.getText().toString());

            //Salvar Dados do assinante
            InscreverUsuario();

        }
        //Limpar comentario
        edit_chat_emoji.setText("");

    }


/*   final Map<String, Object> addCategoria_loja = new HashMap<>();
        addCategoria_loja.put("membros", FieldValue.arrayUnion(identificadorUsuario));
        db.collection("WeForum").document(Id_Forum_selecionado)
                .update(addUserToArrayMap);*/

    private void Digitando() {
        edit_chat_emoji.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() == 1) {
                    Log.i("sdsdsd7474", String.valueOf(TextUtils.isEmpty(s.toString())));
                    typingStarted = true;
                    //  user_digitando.setText("Marlos trinidad");
                  //  Adicionar_Membro_digitando_true();

                    //send typing started status
                } else if (s.toString().trim().length() == 0 && typingStarted) {
                    //Log.i(TAG, “typing stopped event…”);
                    typingStarted = false;
                    //user_digitando.setText("Marlos trinidad");
                 //   Adicionar_Membro_digitando_false();
                    //send typing stopped status
                }

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

                Log.i("sdsdsd1", s.subSequence(start, start + count).toString());

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                s.toString();
                Log.i("sdsdsd2", s.subSequence(start, start + count).toString());
            }
        });
    }

    //envia se o usuario está digitando ou nao
    private void Adicionar_Membro_digitando_true() {
        String foto =  dados_usuario.getString("foto_usuario","");
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("digitando", true);
        membrosMap.put("foto_usuario",foto);
        db.collection("Evento").document("")
                .collection("Membros").document(identificadorUsuario).update(membrosMap);

    }

    private void Adicionar_Membro_digitando_false() {
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("digitando", false);
        db.collection("Evento").document("")
                .collection("Membros").document(identificadorUsuario).update(membrosMap);

    }
    @Override
    protected void onStart() {
        super.onStart();
        RecuperarUser_estao_no_chat();
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);
        Recuperar_Mensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration!= null) {
            registration.remove();
            registration = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (registration!= null) {
            registration.remove();
            registration = null;
        }
    }
    private void Recuperar_Mensagens() {
        list_conversa_grupo.clear();
        Query query = db
                .collection("Chat").document(id_evento)
                .collection("Mensagens").orderBy("tempo", Query.Direction.ASCENDING);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                if (documentChanges != null) {
                    for (DocumentChange doc : documentChanges) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Chat_Grupo coment = doc.getDocument().toObject(Chat_Grupo.class);
                            Log.i("oskdoskd",coment.getMensagem());
                            list_conversa_grupo.add(coment);
                            adapter.notifyDataSetChanged();
                            recyclerView_chat.smoothScrollToPosition(recyclerView_chat.getAdapter().getItemCount());
                        }


                    }
                }
            }
        });

    }
    private void RecuperarUser_estao_no_chat() {
        db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("Assinantes").document(id_evento)
                .collection("Usuarios").document(identificadorUsuario);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //   Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    receber_novidades.setChecked(true);
                    Log.i("recebe","true");
                    Chat_Particular chat = snapshot.toObject(Chat_Particular.class);
                    if (chat.getVizu()!= null) {
                        if (chat.getVizu().equals(true)) {
                            notify = true;
                            Log.i("oskdsodkos2222d",String.valueOf(notify));
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(id_evento)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (!task.isSuccessful()) {
                                               // Toast.makeText(DetalheEvento.this, "erro", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                            //  vizualizou.setVisibility(View.VISIBLE);
                            //  vizualizou.setText("Vizualizado");
                        } else {
                            notify = false;

                            FirebaseMessaging.getInstance().subscribeToTopic(id_evento)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (!task.isSuccessful()) {
                                            //    Toast.makeText(DetalheEvento.this, "erro", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }
                    }
                }else{
                    receber_novidades.setChecked(false);
                    Log.i("recebe","false");
                }

            }
        });
    }

    //Recupera que está digitando
    private void Recuperar_Membros_Digitand_online() {
        list_membro_grupo.clear();
        Query query =
                db.collection("Evento").document(id_evento)
                        .collection("Membros")
                        .whereEqualTo("digitando", true);
        registration_membro = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd", change.getDocument().getId());
                    Membro_Grupo membro_grupo = change.getDocument().toObject(Membro_Grupo.class);
                    membro_grupo.setId(change.getDocument().getId());
                    //  Log.i("sdsdsd",change.getDocument().getId());
                    // Log.i("sdsdsd2",conto.getUid());
                    switch (change.getType()) {
                        case ADDED:
                            list_membro_grupo.add(0, membro_grupo);

                            if (list_membro_grupo.size() > 0) {
                                recycler_chat_online.setVisibility(View.VISIBLE);


                            }

                            adapter_membro_grupo.notifyDataSetChanged();
                            Log.d("ad", "New city: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            for (Membro_Grupo ct : list_membro_grupo) {

                                if (membro_grupo.getId().equals(ct.getId())) {
                                    list_membro_grupo.remove(ct);
                                    break;
                                }
                            }
                            list_membro_grupo.add(0, membro_grupo);
                            if (list_membro_grupo.size() > 0) {
                                recycler_chat_online.setVisibility(View.VISIBLE);

                            }

                            adapter_membro_grupo.notifyDataSetChanged();
                            Log.d("md", "Modified city: " + change.getDocument().getData());
                            break;
                        case REMOVED:
                            for (Membro_Grupo ct : list_membro_grupo) {

                                if (membro_grupo.getId().equals(ct.getId())) {
                                    list_membro_grupo.remove(ct);
                                    break;
                                }
                            }
                            recycler_chat_online.setVisibility(View.VISIBLE);
                            adapter_membro_grupo.notifyDataSetChanged();
                            Log.d("rem", "Removed city: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });
    }


    private void InscreverUsuario(){
        String nomedousuario=dados_usuario.getString("nome", "");


        sendNotifiaction(id_evento,nomedousuario,"Assinou para receber novidades!",token_dono_loja);
        receber_novidades.setChecked(true);

        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("id","");
        membrosMap.put("id_usuario", identificadorUsuario);
        membrosMap.put("nome_usuario", nomedousuario);
        membrosMap.put("foto_usuario", dados_usuario.getString("foto_usuario", ""));
        membrosMap.put("bloqueio", false);
        db.collection("Assinantes").document(id_evento)
                .collection("Usuarios").document(identificadorUsuario).set(membrosMap);

        //adicionar




    }
    private void desinscreverUsuario(){
        //remove
        receber_novidades.setChecked(false);

        //deleta
        db.collection("Assinantes").document(id_evento)
                .collection("Usuarios").document(identificadorUsuario).delete();

    }
    private void Dialog(String texto){
        final MediaPlayer dialog_music = MediaPlayer.create(DetalheEvento.this,R.raw.navi_veja);
        dialog_music.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(DetalheEvento.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText(texto);
        builder.setView(view);
        builder.setPositiveButton("ok,entendi.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog_music.stop();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
    private void sendNotifiaction(final String id_loja, final String nome_quem_mando, String msg, String token_destinatario) {

        Data data = new Data(id_loja, R.drawable.favicon,
                nome_quem_mando + " " + msg, nome_loja_string, id_loja, "assinantes_evento");
        Sender sender = new Sender(data, token_destinatario);

        apiService.sendNotification(sender)
                .enqueue(new retrofit2.Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                // Toast.makeText(ChatActivity_loja.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });

    }

    private void sendNotifiaction_evento_chat( final String nome_quem_mando, String msg,String nome_topico) {
        Log.i("oskdsodkosd",String.valueOf(notify));

           // Toast.makeText(this, "passou!", Toast.LENGTH_SHORT).show();
            Data data = new Data(identificadorUsuario, R.drawable.favicon,
                    nome_quem_mando + ": " + msg, nome_topico, id_evento, "evento");
            Sender sender = new Sender(data, "/topics/" + id_evento);

            apiService.sendNotification(sender)
                    .enqueue(new retrofit2.Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
                                  //  Toast.makeText(DetalheEvento.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });

    }
  /*  private void VerificaAssinante(String id_loja){
        db.collection("Assinantes").document(id_loja)
                .collection("Usuarios")
                .whereEqualTo("id_usuario", identificadorUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                receber_novidades.setChecked(true);
                            }
                        } else {
                            receber_novidades.setChecked(false);
                        }
                    }
                });
    }*/
    public void RecuperarDados_Evento(String id){

        Query query= db.collection("Evento").whereEqualTo("id",id);
        registration_loja=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    final Evento evento_atual = change.getDocument().toObject(Evento.class);
                    Log.i("osadksodk32",evento_atual.getTitulo());
                    String id=change.getDocument().getId();
                    switch (change.getType()) {
                        case ADDED:
                            DadosLoja(evento_atual,id);
                             String data_dia = simpleDateFormat_dia.format(evento_atual.getData_inicio());
                            String data_dia_mes = simpleDateFormat_dia_mes.format(evento_atual.getData_inicio());
                            String data_hora = simpleDateFormat_hora.format(evento_atual.getData_inicio());
                            String data_mes = simpleDateFormat_mes.format(evento_atual.getData_inicio());
                            String data_dia_fim = simpleDateFormat_dia.format(evento_atual.getData_fim());
                            String data_dia_mes_fim = simpleDateFormat_dia_mes.format(evento_atual.getData_fim());
                            String data_hora_fim = simpleDateFormat_hora.format(evento_atual.getData_fim());
                            String data_mes_fim = simpleDateFormat_mes.format(evento_atual.getData_fim());
                         //"\nAté \n"+data_dia_fim+","+data_dia_mes_fim+" de "+data_mes_fim+"\n às "+data_hora_fim
                            data_inicio.setText(data_dia+","+data_dia_mes+" de "+data_mes+" às "+data_hora);
                            data_fim.setText(data_dia_fim+","+data_dia_mes_fim+" de "+data_mes_fim+" às "+data_hora_fim);
                            if(evento_atual.getPreco_ingresso().equals("R$0,00")){
                                 txt_valor_entrada.setText("Entrada Gratuita");
                            }else {
                                txt_valor_entrada.setText(evento_atual.getPreco_ingresso());
                            }
                            id_dono_loja=evento_atual.getIdauthor();
                            token_dono_loja=evento_atual.getToken_author();
                            ImageRequest request = ImageRequest.fromUri(evento_atual.getCapaevento());
                            DraweeController controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(icon_loja.getController()).build();
                            RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);

                            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
                            GenericDraweeHierarchy hierarchy = builder
                                    .setRoundingParams(roundingParams)
                                    .setProgressBarImage(new CircleProgressDrawable())
                                    //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                                    .build();
                            icon_loja.setHierarchy(hierarchy);
                            icon_loja.setController(controller);
                            icon_loja.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it=new Intent(DetalheEvento.this, AbrirImagem.class);
                                    it.putExtra("id_foto",evento_atual.getCapaevento());
                                    it.putExtra("nome_foto",evento_atual.getTitulo());
                                    startActivity(it);
                                }
                            });
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
            }
        });

    }






    @SuppressLint("RestrictedApi")
    private void DadosLoja(final Evento evento, final String id_doc){

        if(!evento.getAnalizado()){
            analise.setVisibility(View.VISIBLE);
            line_rec_status_admin.setVisibility(GONE);
        }else{
            analise.setVisibility(GONE);

        }

        String tipo_user=dados_usuario.getString("tipo_usuario", "");

        if(tipo_user.equals("admin")){
            permitir.setVisibility(View.VISIBLE);
            deletar.setVisibility(View.VISIBLE);
        }else{
            deletar.setVisibility(GONE);
            permitir.setVisibility(GONE);
        }
        deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Evento").document(id_doc).delete();
               db.collection("Chat").document(id_evento).delete();
                Toast.makeText(DetalheEvento.this, " Deletado COM SUCESSO", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        permitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> atualizar = new HashMap<>();
                atualizar.put("analizado", true);
                db.collection("Evento").document(id_doc)
                        .update(atualizar);
                Toast.makeText(DetalheEvento.this, " AuTORIZADA COM SUCESSO", Toast.LENGTH_LONG).show();
            }
        });


      Log.i("sodksodksdo",evento.getTitulo());
        textToolbar.setText(evento.getTitulo());
        nome_loja.setText(evento.getTitulo());
        nome_loja_string=evento.getTitulo();
        desc.setText(evento.getMensagem());
        makeTextViewResizable(desc, 3, "..Veja Mais", true);
        desc_end_loja.setText(evento.getEndereco());


        if (evento.getIdauthor().equals(identificadorUsuario)) {
            line_botoes.setVisibility(View.VISIBLE);
            rel_status_admin.setVisibility(View.VISIBLE);

            rel_status_visitante.setVisibility(View.GONE);
        }

        line_assinante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DetalheEvento.this, MeusAssinantes.class);
                it.putExtra("id_comercio", id_evento);
                //  it.putExtra("id_author", comercio.getIdauthor());
                startActivity(it);
            }
        });



        if (!evento.getUrl_img_status().equals("")) {
            Picasso.get()
                    .load(evento.getCapaevento())
                    .into(circleImageView_status_admin, new Callback() {
                        @Override
                        public void onSuccess() {
                            progresso_carregando_admin.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
            Picasso.get()
                    .load(evento.getCapaevento())
                    .into(circleImageView_status_visitante, new Callback() {
                        @Override
                        public void onSuccess() {
                            line_rec_status_vistante.setVisibility(View.VISIBLE);
                            progresso_carregando_visitante.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

        }else {
            Picasso.get()
                    .load(evento.getCapaevento())
                    .into(circleImageView_status_admin, new Callback() {
                        @Override
                        public void onSuccess() {
                            progresso_carregando_admin.setVisibility(View.GONE);


                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }



        circleImageView_status_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final MediaPlayer dialog_music = MediaPlayer.create(DetalheEvento.this,R.raw.navi_veja);
                dialog_music.start();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(DetalheEvento.this);
                LayoutInflater layoutInflater = LayoutInflater.from(DetalheEvento.this);
                final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
                TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
                mensagem.setText("Oque Deseja fazer?");
                builder.setView(view);
                builder.setPositiveButton("Adicionar Status", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_music.stop();


                        Intent it = new Intent(DetalheEvento.this, Pag_add_Status.class);
                        it.putExtra("id_loja", id_evento);
                        it.putExtra("nome_loja", evento.getTitulo());
                        it.putExtra("url_icone_loja", evento.getCapaevento());
                        it.putExtra("tipo_loja", "evento");
                        startActivity(it);


                    }
                });
                builder.setNegativeButton("Ver Status", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent it = new Intent(DetalheEvento.this, VerStatusEvento.class);
                        it.putExtra("userid", evento.getIdauthor());
                        it.putExtra("id_loja", evento.getId());
                        it.putExtra("nome_loja", evento.getTitulo());
                        it.putExtra("icone_loja", evento.getCapaevento());
                        startActivity(it);
                        dialog.cancel();
                        dialog_music.stop();
                    }
                });
                dialog = builder.create();
                dialog.show();



            }
        });

        line_rec_status_vistante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =new Intent(DetalheEvento.this, VerStatusEvento.class);
                it.putExtra("userid", evento.getIdauthor());
                it.putExtra("id_loja",id_evento);
                it.putExtra("nome_loja", evento.getTitulo());
                it.putExtra("icone_loja", evento.getCapaevento());
                startActivity(it);
            }
        });

        line_rec_status_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =new Intent(DetalheEvento.this, VerStatusEvento.class);
                it.putExtra("userid",evento.getIdauthor());
                it.putExtra("id_loja",id_evento);
                it.putExtra("nome_loja", evento.getTitulo());
                it.putExtra("icone_loja",evento.getCapaevento());
                startActivity(it);
            }
        });
        line_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DetalheEvento.this, Configuracao_evento.class);
                it.putExtra("id_evento",id_evento);
                startActivity(it);
                finish();

            }
        });
        line_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DetalheEvento.this, Info_Status_Evento.class);
                it.putExtra("id_loja",id_evento);
                startActivity(it);

            }
        });

        comochegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AbrirLocalizacao(comercio.getGeoPoint().getLatitude(),comercio.getGeoPoint().getLongitude());
                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();

                }else{
                    AbrirLocalizacao(evento.getGeoPoint().getLatitude(),evento.getGeoPoint().getLongitude());
                }


            }
        });

        line_iconchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(DetalheEvento.this, ChatActivity_loja.class);
                it.putExtra("comercio_id",evento.getId());
                it.putExtra("id_destinatario",evento.getIdauthor());
                it.putExtra("nome_destinatario",evento.getTitulo());
                it.putExtra("icone_destinatario",evento.getCapaevento());
                it.putExtra("token_destinatario",evento.getToken_author());
                startActivity(it);
            }
        });
    }

    private void buildAlertMessageNoGps() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(DetalheEvento.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText("Para continuar \n o GPS precisa tá ativado, \n deseja ativar agora?");
        builder.setView(view);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void AbrirLocalizacao(Double latitude,Double longitude){
        String uri = "http://maps.google.com/maps?daddr=" +  latitude + "," + longitude + " (" + "comece a parti daqui" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
        try
        {
            startActivity(intent);
        }
        catch(ActivityNotFoundException ex)
        {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx)
            {
                Toast.makeText(this, "Para acessa a localização Você precisa ter o mapa instalado.", Toast.LENGTH_LONG).show();
            }
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
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


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "..Veja Menos", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "..Veja Mais", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }
    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(root)
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
                        botaoicone.setImageResource(R.drawable.ic_teclado);
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
                        botaoicone.setImageResource(R.drawable.ic_emotion_chat);
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
                .build(edit_chat_emoji);
    }
}
