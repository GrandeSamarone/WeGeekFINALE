package com.marlostrinidad.wegeek.nerdzone.Noticia.Noticia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_Membro_Grupo;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_chat_grupo;
import com.marlostrinidad.wegeek.nerdzone.Conversa.ChatActivity_pessoal;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Chat_Grupo;
import com.marlostrinidad.wegeek.nerdzone.Model.Chat_Particular;
import com.marlostrinidad.wegeek.nerdzone.Model.Forum;
import com.marlostrinidad.wegeek.nerdzone.Model.Membro_Grupo;
import com.marlostrinidad.wegeek.nerdzone.Noticia.Banca.Detalhe_banca_Noticia;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.APIService;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Client;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Data;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.MyResponse;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Sender;
import com.marlostrinidad.wegeek.nerdzone.R;
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
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class Detalhe_noticia extends TrocarFundo implements View.OnClickListener {

    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";

    private Adapter_chat_grupo adapter;
    private RelativeLayout line_icon;
    private StorageReference storageReference;
    private Adapter_Membro_Grupo adapter_membro_grupo;
    private ArrayList<Membro_Grupo> list_membro_grupo = new ArrayList<>();
    private Chat_Grupo chat = new Chat_Grupo();
    private ArrayList<Chat_Grupo> list_conversa_grupo = new ArrayList<>();
    private RecyclerView recyclerView_chat, recycler_chat_online;
    private EmojiEditText edit_chat_emoji;
    private String id_noticia_selecionada,id_banca_selecionada,admin;
    private TextView titulo, descricao, author_topico, data_topico,fechar;
    private CircleImageView icone;
    private Toolbar toolbar;
    private TextView textToolbar;
    private FirebaseFirestore db;
    private String identificadorUsuario;
    private LinearLayoutManager layoutManager;
    private SimpleDraweeView img_topico;
    private View root;
    private EmojiPopup emojiPopup;
    private boolean typingStarted;
    private ImageView botaoicone;
    private SharedPreferences dados_usuario;
    private TextView quant_membro,texto_online;
    private Button botao_minimizar;
    private CardView cardView;
    private LinearLayout line_digita,line_chat;
    private ViewGroup transitionsContainer;
    private ListenerRegistration registration, registration_membro;
    private androidx.appcompat.widget.AppCompatButton botao_env_msg;
    private SparkButton assinar_topico;
    private String titulo_notification,textoComentario ;
    APIService apiService;
    boolean notify = false;
    private ShimmerFrameLayout shimmer;
    private AlertDialog dialog;
    private Button permitir,deletar;
    private CardView analise;
    boolean isKeyboardShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_noticia);

        //COnfiguracoes originais
        line_icon=findViewById(R.id.rel_nome);
        analise=findViewById(R.id.em_analise);
        line_digita=findViewById(R.id.digitar);
        line_chat=findViewById(R.id.line_chat);
        fechar=findViewById(R.id.fechar_topico);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);
        permitir=findViewById(R.id.bton_permitir);
        deletar=findViewById(R.id.bton_deletar);
        assinar_topico=findViewById(R.id.botaoassinartopico);
        shimmer=findViewById(R.id.shimmer_view_container_Detalhe_topico);

        line_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_noticia.this, Detalhe_banca_Noticia.class);
                it.putExtra("id",id_banca_selecionada);
                startActivity(it);
            }
        });

        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MediaPlayer dialog_music = MediaPlayer.create(Detalhe_noticia.this,R.raw.navi_ei);
                dialog_music.start();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Detalhe_noticia.this);
                LayoutInflater layoutInflater = LayoutInflater.from(Detalhe_noticia.this);
                final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
                TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
                mensagem.setText("Fechar tópico:\n não poderá enviar mensagens, somente ler.\n\n" +
                        "Alterar Tópico:\npoderá incluir ou excluir informações.");
                builder.setView(view);
                builder.setPositiveButton("ALTERAR/DELETAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_music.stop();
                        Intent it = new Intent(Detalhe_noticia.this, Editar_noticia.class);
                        it.putExtra("id_noticia",id_noticia_selecionada);
                        startActivity(it);
                        finish();

                    }
                }).setNegativeButton("FECHAR TÓPICO", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        HashMap<String, Object> atualizar_fechar = new HashMap<>();
                        atualizar_fechar.put("aberto_fechado","fechado");
                        db.collection("Noticias").document(id_noticia_selecionada)
                                .update(atualizar_fechar);
                        dialog_music.stop();
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
        assinar_topico.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(buttonState){
                    InscreverUsuario();
                    Toast.makeText(Detalhe_noticia.this, "Você receberá informações sobre esse tópico.", Toast.LENGTH_SHORT).show();
                }else{
                    desinscreverUsuario();
                }
            }
            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }
            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
            }
        });
        toolbar = findViewById(R.id.toolbar_detalhe_topico);
        toolbar.setTitle("");
        textToolbar = findViewById(R.id.textViewtitulo_topico);

        setSupportActionBar(toolbar);
        quant_membro=findViewById(R.id.quant_user_vizu);
        texto_online=findViewById(R.id.text_onlines);
        cardView = findViewById(R.id.cardView2);
        botaoicone = findViewById(R.id.botao_post_icone_detalhe_topico);
        botaoicone.setOnClickListener(this);
        edit_chat_emoji = findViewById(R.id.caixa_de_texto_comentario_detalhe_topico);
        botao_env_msg = findViewById(R.id.button_postar_comentario_detalhe_topico);
        botao_env_msg.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
        botao_minimizar = findViewById(R.id.botao_minimizar);
        botao_minimizar.setOnClickListener(this);
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        img_topico = findViewById(R.id.img_detalhe_topico);
        img_topico.setOnClickListener(this);
        img_topico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_noticia.this, LerNoticia.class);
                it.putExtra("id_noticia_selecionada",id_noticia_selecionada);
                //  it.putExtra("nome_foto", forum.getTitulo());
                startActivity(it);
            }
        });
        icone = findViewById(R.id.icon_usuario_topico);
        titulo = findViewById(R.id.titulo_topico);
        descricao = findViewById(R.id.descricao_topico);
        author_topico = findViewById(R.id.nome_usuario_topico);
        data_topico = findViewById(R.id.data_topico);
        //Recebendo o id do forum
        id_noticia_selecionada = getIntent().getStringExtra("id_noticia_selecionada");



        //RecycleView onlines
        LinearLayoutManager layoutManagerMembros = new LinearLayoutManager(
                Detalhe_noticia.this, LinearLayoutManager.HORIZONTAL, false);
        recycler_chat_online = findViewById(R.id.recycler_chat_online_Detalhe_topico);
        recycler_chat_online.setLayoutManager(layoutManagerMembros);
        adapter_membro_grupo = new Adapter_Membro_Grupo(list_membro_grupo, getApplicationContext());
        recycler_chat_online.setAdapter(adapter_membro_grupo);

        transitionsContainer =  findViewById(R.id.cardView2);
        recyclerView_chat = findViewById(R.id.recycler_chat_grupo_detalhe_topico);
        recyclerView_chat.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView_chat.setLayoutManager(layoutManager);
        adapter = new Adapter_chat_grupo(getApplicationContext(), list_conversa_grupo);
        recyclerView_chat.setAdapter(adapter);

        //emotion
        root = findViewById(R.id.detalhe_topico);
        emojiPopup = EmojiPopup.Builder.fromRootView(root).build(edit_chat_emoji);
        setUpEmojiPopup();



        recyclerView_chat.addOnItemTouchListener(new RecyclerItemClickListener(
                Detalhe_noticia.this, recyclerView_chat, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Chat_Grupo> listaAtualizada = adapter.getUser_chat();

                if(listaAtualizada.size()>0){
                    String id_usuario = adapter.getUser_chat().get(position).getId_usuario();
                    String icone_usuario = adapter.getUser_chat().get(position).getFoto_usuario();
                    Chat_Grupo grupo_selecionado = listaAtualizada.get(position);
                    //  Toast.makeText(Forum_principal.this, id_grupo_selecionado, Toast.LENGTH_SHORT).show();
                    if(!id_usuario.equals(identificadorUsuario)) {
                        Intent it = new Intent(Detalhe_noticia.this, ChatActivity_pessoal.class);
                        it.putExtra("id_destinatario", id_usuario);
                        it.putExtra("icone_destinatario", icone_usuario);
                        startActivity(it);
                    }

                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));



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

                            }
                        }
                        else {
                            // keyboard is closed
                            if (isKeyboardShowing) {
                                isKeyboardShowing = false;
                                onKeyboardVisibilityChanged(false);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.botao_post_icone_detalhe_topico:
                emojiPopup.toggle();
                break;
            case R.id.button_postar_comentario_detalhe_topico:
                textoComentario = edit_chat_emoji.getText().toString();
                if (textoComentario != null && !textoComentario.equals("")) {
                    SalvarMensagem();
                    // Mandar_Notificacao();
                } else {
                    //Balão informação
                    Toast.makeText(this, "Digite uma mensagem.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.botao_minimizar:
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

                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("oskdsodkos2222d",String.valueOf(notify));
        shimmer.startShimmerAnimation();

        Dados();
        Digitando();
        RecuperarUser_estao_no_chat();
        Recuperar_Dados_Noticia();
        Recuperar_Mensagens();
        Recuperar_membros_Online();
        //add usuario que vai responder o topico
        addMembro();
        VerificaAssinante();
        //recuperar que está digitando
        Recuperar_Membros_Digitand_online();
        // Preferences pega o nome do usuario;
        String foto =  dados_usuario.getString("foto_usuario","");
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("digitando", false);
        membrosMap.put("foto_usuario",foto);
        db.collection("Noticias").document(id_noticia_selecionada)
                .collection("Membros").document(identificadorUsuario).set(membrosMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //add_membro
        Log.i("oskdsodkos2222d",String.valueOf(notify));
        addMembro();
        vizualizou(true);

    }
    @Override
    protected void onPause() {
        super.onPause();
        //   notify=false;
        shimmer.stopShimmerAnimation();
        //remove
        vizualizou(false);
        Remove_membro();
        registration.remove();
        registration_membro.remove();
        //RemoveMembro
        // offline();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // notify=false;
        shimmer.stopShimmerAnimation();
        Remove_membro();
        if (registration != null) {
            registration.remove();
            registration = null;
        }
    }





    private void VerificaAssinante(){
        db.collection("Assinantes").document(id_noticia_selecionada)
                .collection("Usuarios")
                .whereEqualTo("id", identificadorUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                assinar_topico.setChecked(true);
                            }
                        } else {
                            assinar_topico.setChecked(false);
                        }
                    }
                });
    }
    private void Recuperar_Dados_Noticia(){
        DocumentReference docRef = db.collection("Noticias").document(id_noticia_selecionada);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        shimmer.stopShimmerAnimation();
                        shimmer.setVisibility(View.GONE);
                        Forum forum=document.toObject(Forum.class);

                        if(!forum.getAnalizado()){
                            analise.setVisibility(View.VISIBLE);
                        }else{
                            analise.setVisibility(GONE);

                        }


                        if(forum.getIdauthor().equals(identificadorUsuario)){
                            InscreverUsuario();
                            fechar.setVisibility(View.VISIBLE);
                            assinar_topico.setVisibility(GONE);
                        }
                        if(forum.getAberto_fechado().equals("fechado")){
                            line_digita.setVisibility(GONE);
                        }

                        if(forum.getData()!=null){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'/'MM'/'y HH:mm",java.util.Locale.getDefault());// MM'/'dd'/'y;
                            String tempoPostagem = simpleDateFormat.format(forum.getData());
                            data_topico.setText(tempoPostagem);
                        }else{
                            data_topico.setText("");
                        }

                        titulo_notification=forum.getTitulo();
                        id_banca_selecionada=forum.getId_banca();
                        titulo.setText(forum.getTitulo());
                        textToolbar.setText(forum.getTitulo());
                        descricao.setText(forum.getDescricao());
                        author_topico.setText(forum.getNomeauthor());
                        //  mensagem.setText(topicoselecionado.getDescricao());
                        // RecuperarIcone_e_nome_author(topicoselecionado.getIdauthor());
                        if (forum.getIcone_author() != null) {
                            String stringcapa = forum.getIcone_author();

                            Picasso.get()
                                    .load(stringcapa)
                                    .into(icone);

                        }

                        if (forum.getFoto() != null) {
                            String stringcapa = forum.getFoto();
                            if (stringcapa != null) {
                                img_topico.setVisibility(View.VISIBLE);
                                ImageRequest request = ImageRequest.fromUri(forum.getFoto());
                                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                        .setImageRequest(request)
                                        .setOldController(img_topico.getController()).build();
                                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);

                                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
                                GenericDraweeHierarchy hierarchy = builder
                                        .setRoundingParams(roundingParams)
                                        .setProgressBarImage(new CircleProgressDrawable())
                                        //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                                        .build();
                                img_topico.setHierarchy(hierarchy);
                                img_topico.setController(controller);
                            }


                        }
                    } else {
                        Log.d("sdsd", "No such document");
                    }
                } else {
                    Log.d("sdsd", "get failed with ", task.getException());
                }
            }
        });

    }

    private void Dados(){
        String tipo_user=dados_usuario.getString("tipo_usuario", "");


        if(tipo_user.equals("admin")){
            permitir.setVisibility(View.VISIBLE);
            deletar.setVisibility(View.VISIBLE);

        }else{
            permitir.setVisibility(GONE);
            deletar.setVisibility(GONE);
        }
        deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Noticias").document(id_noticia_selecionada)
                        .delete();
                db.collection("Chat").document(id_noticia_selecionada).delete();
                finish();
                Toast.makeText(Detalhe_noticia.this, " Deletado  COM SUCESSO", Toast.LENGTH_LONG).show();


            }
        });
        permitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> atualizar = new HashMap<>();
                atualizar.put("analizado", true);
                db.collection("Noticias").document(id_noticia_selecionada)
                        .update(atualizar);
                Toast.makeText(Detalhe_noticia.this, " AuTORIZADA COM SUCESSO", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendNotifiaction( final String nome_quem_mando, String msg,String nome_topico) {
        Log.i("oskdsodkosd",String.valueOf(notify));

        //  Toast.makeText(this, "passou!", Toast.LENGTH_SHORT).show();
        Data data = new Data(identificadorUsuario, R.drawable.favicon,
                nome_quem_mando + ": " + msg, nome_topico, id_noticia_selecionada, "topico");
        Sender sender = new Sender(data, "/topics/" + id_noticia_selecionada);

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                //Toast.makeText(Detalhe_noticia.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });

    }
    //add_usuario que ta lendo
    private void addMembro() {
        HashMap<String, Object> add_membro = new HashMap<>();
        add_membro.put("membros", FieldValue.arrayUnion(identificadorUsuario));
        db.collection("Noticias").document(id_noticia_selecionada).update(add_membro);
    }

    //remove_usuario
    private void Remove_membro() {
        HashMap<String, Object> add_membro = new HashMap<>();
        add_membro.put("membros", FieldValue.arrayRemove(identificadorUsuario));
        db.collection("Noticias").document(id_noticia_selecionada).update(add_membro);
    }

    private void Digitando() {




        edit_chat_emoji.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && s.toString().trim().length() == 1) {
                    Log.i("sdsdsd7474", String.valueOf(TextUtils.isEmpty(s.toString())));
                    typingStarted = true;
                    //  user_digitando.setText("Marlos trinidad");
                    Adicionar_Membro_digitando_true();
                    //send typing started status
                } else if (s.toString().trim().length() == 0 && typingStarted) {
                    //Log.i(TAG, “typing stopped event…”);
                    typingStarted = false;
                    //user_digitando.setText("Marlos trinidad");
                    Adicionar_Membro_digitando_false();
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





    private void SalvarMensagem() {
        //SharedPreferencies pegando a variavel e os dados
        String nome = dados_usuario.getString("nome", "");
        String foto = dados_usuario.getString("foto_usuario", "");
        String textoComentario = edit_chat_emoji.getText().toString();
        String id_mensagem_texto = UUID.randomUUID().toString();

        if (textoComentario != null && !textoComentario.equals("")) {
            chat.setId_grupo(id_noticia_selecionada);
            chat.setId_usuario(identificadorUsuario);
            Log.i("sdoskdosdk77", identificadorUsuario);
            chat.setNome_usuario(nome);
            chat.setMensagem_type("texto");
            chat.setFoto_usuario(foto);
            chat.setMensagem(textoComentario);
            chat.setId_mensagem(id_mensagem_texto);
            chat.Salvar_msg_Grupo();

            sendNotifiaction(nome,textoComentario,titulo.getText().toString());

            //Salvar Dados do assinante
            InscreverUsuario();

        }
        //Limpar comentario
        edit_chat_emoji.setText("");


    }

    private void Recuperar_Mensagens() {
        list_conversa_grupo.clear();
        Query query = db
                .collection("Chat").document(id_noticia_selecionada)
                .collection("Mensagens").orderBy("tempo", Query.Direction.ASCENDING);
        registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                if (documentChanges != null) {
                    for (DocumentChange doc : documentChanges) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Chat_Grupo coment = doc.getDocument().toObject(Chat_Grupo.class);
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

        final DocumentReference docRef = db.collection("Assinantes").document(id_noticia_selecionada)
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
                    Chat_Particular chat = snapshot.toObject(Chat_Particular.class);
                    if (chat.getVizu()!= null) {
                        if (chat.getVizu().equals(true)) {
                            notify = true;
                            Log.i("notify_testefull",String.valueOf(notify));
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(id_noticia_selecionada)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(Detalhe_noticia.this, "erro", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                            //  vizualizou.setVisibility(View.VISIBLE);
                            //  vizualizou.setText("Vizualizado");
                        } else {
                            notify = false;
                            FirebaseMessaging.getInstance().subscribeToTopic(id_noticia_selecionada)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(Detalhe_noticia.this, "erro", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    }
                }

            }
        });
    }

    //Recupera que está digitando
    private void Recuperar_Membros_Digitand_online() {
        list_membro_grupo.clear();
        Query query =
                db.collection("Noticias").document(id_noticia_selecionada)
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

    private void  Recuperar_membros_Online(){
        final DocumentReference docRef = db.collection("Noticias").document(id_noticia_selecionada);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //   Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Forum forum = snapshot.toObject(Forum.class);
                    if(forum.getMembros()!=null){
                        quant_membro.setVisibility(View.VISIBLE);
                        quant_membro.setText(String.valueOf(forum.getMembros().size()));
                        texto_online.setVisibility(View.VISIBLE);
                    }else{
                        quant_membro.setVisibility(GONE);
                        texto_online.setVisibility(GONE);
                    }
                }
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
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


    private void InscreverUsuario(){
        assinar_topico.setChecked(true);
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("id", identificadorUsuario);
        membrosMap.put("nome", dados_usuario.getString("nome", ""));
        membrosMap.put("foto_usuario", dados_usuario.getString("foto_usuario", ""));
        membrosMap.put("vizu", true);
        db.collection("Assinantes").document(id_noticia_selecionada)
                .collection("Usuarios").document(identificadorUsuario).set(membrosMap);
    }

    private void desinscreverUsuario(){
        assinar_topico.setChecked(false);
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("id", identificadorUsuario);
        membrosMap.put("nome", dados_usuario.getString("nome", ""));
        membrosMap.put("foto_usuario", dados_usuario.getString("foto_usuario", ""));
        db.collection("Assinantes").document(id_noticia_selecionada)
                .collection("Usuarios").document(identificadorUsuario).delete();

        Toast.makeText(Detalhe_noticia.this, "Você não receberá informações sobre esse tópico.", Toast.LENGTH_SHORT).show();
    }
    //envia se o usuario está digitando ou nao
    private void Adicionar_Membro_digitando_true() {
        String foto =  dados_usuario.getString("foto_usuario","");
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("digitando", true);
        membrosMap.put("foto_usuario",foto);
        db.collection("Noticias").document(id_noticia_selecionada)
                .collection("Membros").document(identificadorUsuario).update(membrosMap);

    }

    private void Adicionar_Membro_digitando_false() {
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("digitando", false);
        db.collection("Noticias").document(id_noticia_selecionada)
                .collection("Membros").document(identificadorUsuario).update(membrosMap);

    }

    private void vizualizou(Boolean vizu){

        HashMap<String, Object> vizualizacao = new HashMap<>();
        vizualizacao.put("vizu", vizu);
        db.collection("Assinantes").document(id_noticia_selecionada)
                .collection("Usuarios").document(identificadorUsuario).update(vizualizacao);
    }


}

