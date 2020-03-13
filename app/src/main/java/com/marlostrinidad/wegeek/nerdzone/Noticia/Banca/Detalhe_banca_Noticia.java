package com.marlostrinidad.wegeek.nerdzone.Noticia.Banca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem;
import com.marlostrinidad.wegeek.nerdzone.Assinantes.MeusAssinantes;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Conversa.ChatActivity_loja;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Helper.MySpannable;
import com.marlostrinidad.wegeek.nerdzone.Helper.RecyclerItemClickListener;
import com.marlostrinidad.wegeek.nerdzone.Helper.SpacesItemDecoration;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Status.Pag_add_Status;
import com.marlostrinidad.wegeek.nerdzone.Model.Adapter_noticia_banca;
import com.marlostrinidad.wegeek.nerdzone.Model.Banca;
import com.marlostrinidad.wegeek.nerdzone.Model.Forum;
import com.marlostrinidad.wegeek.nerdzone.Noticia.Noticia.Detalhe_noticia;
import com.marlostrinidad.wegeek.nerdzone.Noticia.Noticia.Nova_noticia;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.APIService;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Client;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Data;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.MyResponse;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Sender;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;

public class Detalhe_banca_Noticia extends TrocarFundo {

    private SharedPreferences dados_usuario;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_STATUS = 10;
    private Toolbar toolbar;
    private CardView card;
    private TextView textToolbar, nome_loja, desc,txt_nada,desc_end_loja;
    private String id_banca,icone_loja,id_dono_loja,token_dono_loja;
    private SimpleDraweeView icon_loja;
    private StorageReference storageReference;
    private FloatingActionButton botao_nova_noticia,botao_novo_status;
    private String identificadorUsuario;

    private RecyclerView recyclerView_noticia;
    private LinearLayout line_games,line_assinante,line_iconchat,line_nada_encontrado_txt,line_status,line_config,rel_status_visitante,rel_status_admin,line_nada_encontrado,line_botoes;
    private Adapter_noticia_banca adapter_not;
    private RelativeLayout line_rec_status_admin,line_rec_status_vistante;
    private ArrayList<Forum> lista_item = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration registration_loja, registration_game;
    private AlertDialog dialog;
    private String urlimg,nome_loja_string;
    private CircleImageView circleImageView_status_visitante,circleImageView_status_admin;
    private ConstraintLayout progresso_carregando_visitante,progresso_carregando_admin;
    private APIService apiService;
    private Button minimizar,permitir,deleta;
    private SparkButton receber_novidades;
    private ViewGroup transitionsContainer;
    private ListenerRegistration registration_itens_banca;
    private CardView analise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_banca__noticia);


        receber_novidades=findViewById(R.id.botao_receber_novidades_not);
        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar = findViewById(R.id.app_toolbar_title_secundario);
        setSupportActionBar(toolbar);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        //configuracoes iniciais
        analise=findViewById(R.id.em_analise);
        permitir=findViewById(R.id.bton_permitir);
        deleta=findViewById(R.id.bton_deletar);
        minimizar=findViewById(R.id.botao_minimizar_not);
        card=findViewById(R.id.card_topo_not);
        recyclerView_noticia=findViewById(R.id.RecycleView_noticias);
        transitionsContainer=findViewById(R.id.card_topo_not);
        line_assinante=findViewById(R.id.line_assinante_not);
        line_iconchat=findViewById(R.id.icon_chat_not);
        txt_nada=findViewById(R.id.txt_nada_encontrado_inferior_not);
        line_nada_encontrado_txt=findViewById(R.id.line_txt_n_encontrado_not);
        line_status=findViewById(R.id.line_vizu_not);
        line_config=findViewById(R.id.line_config_not);
        rel_status_visitante=findViewById(R.id.line_menu_visitante_not);
        rel_status_admin=findViewById(R.id.line_menu_admin_not);
        line_nada_encontrado=findViewById(R.id.line_naotemnada_publicado_not);
        progresso_carregando_visitante=findViewById(R.id.const_carregando_icon_de_not_visitante);
        progresso_carregando_admin=findViewById(R.id.const_carregando_icon_de_not_admin);
        circleImageView_status_visitante=findViewById(R.id.circleImageViewStatus_visitante_not);
        circleImageView_status_admin=findViewById(R.id.circleImageViewStatus_admin);
        db = FirebaseFirestore.getInstance();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        line_botoes = findViewById(R.id.line_botoes);
        botao_novo_status=findViewById(R.id.buton_novo_status_not);
        icon_loja = findViewById(R.id.icon_not);
        nome_loja = findViewById(R.id.nome_not);
        desc = findViewById(R.id.desc_not);
        line_rec_status_vistante=findViewById(R.id.rel_status_detalhe_not_visitante);
        line_rec_status_admin=findViewById(R.id.rel_status_detalhe_not_admin);

        botao_nova_noticia=findViewById(R.id.buton_novo_item_not);


        recyclerView_noticia.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext (), 2     );
        recyclerView_noticia.setLayoutManager(layoutManager);
        recyclerView_noticia.addItemDecoration(new SpacesItemDecoration(5));
        adapter_not = new Adapter_noticia_banca(lista_item, this);
        recyclerView_noticia.setAdapter(adapter_not);
        //click no item
        recyclerView_noticia.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerView_noticia, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<Forum> lista_item_click = adapter_not.getItem();

                if (lista_item_click.size() > 0) {
                    String id_noticia_selecionada = adapter_not.getItem().get(position).getId();
                    // Toast.makeText(Detalhe_banca_Noticia.this, id_noticia_selecionada, Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(Detalhe_banca_Noticia.this, Detalhe_noticia.class);
                    it.putExtra("id_noticia_selecionada", id_noticia_selecionada);
                    startActivity(it);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));

        //id_categoria= getIntent().getStringExtra("categoria");
        id_banca = getIntent().getStringExtra("id");
        Log.i("oskdoskdsok",id_banca);
        RecuperarDados_loja(id_banca);

        minimizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (card.getVisibility() == View.VISIBLE) {
                    //efeito na transaicao
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(transitionsContainer);
                    }
                    Drawable image = getResources().getDrawable(R.drawable.ic_seta_pra_baixa);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    minimizar.setCompoundDrawables(null, null, image, null);

                    card.setVisibility(GONE);
                    minimizar.setText("MOSTRAR");
                } else {
                    //efeito na transaicao
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(transitionsContainer);
                    }
                    Drawable image = getResources().getDrawable(R.drawable.ic_seta_subir);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w, h);
                    minimizar.setCompoundDrawables(null, null, image, null);
                    card.setVisibility(View.VISIBLE);
                    minimizar.setText("ESCONDER");


                }
            }
        });


        receber_novidades.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if(buttonState){
                    InscreverUsuario();
                    Dialog("Você receberá novidades dessa banca.");
                }else{
                    desinscreverUsuario();
                    Dialog("Não receberá novidades dessa banca.");
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private void Dialog(String texto){
        final MediaPlayer dialog_music = MediaPlayer.create(Detalhe_banca_Noticia.this,R.raw.navi_veja);
        dialog_music.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(Detalhe_banca_Noticia.this);
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



    @Override
    protected void onStart() {
        super.onStart();
        Recuperar_noticia_da_banca();
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration_loja!= null) {
            registration_loja.remove();
            registration_loja = null;
        }
        if (registration_itens_banca!= null) {
            registration_itens_banca.remove();
            registration_itens_banca = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registration_loja!= null) {
            registration_loja.remove();
            registration_loja = null;
        }
        if (registration_itens_banca!= null) {
            registration_itens_banca.remove();
            registration_itens_banca = null;
        }


    }

    private void InscreverUsuario(){
        final String nomedousuario=dados_usuario.getString("nome", "");
        FirebaseMessaging.getInstance().subscribeToTopic(id_banca)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        sendNotifiaction(id_banca,nomedousuario,"Assinou para receber novidades!",token_dono_loja);
                        receber_novidades.setChecked(true);

                        if (!task.isSuccessful()) {
                            Toast.makeText(Detalhe_banca_Noticia.this, "erro", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        HashMap<String, Object> membrosMap = new HashMap<>();
        membrosMap.put("id","");
        membrosMap.put("id_usuario", identificadorUsuario);
        membrosMap.put("nome_usuario", nomedousuario);
        membrosMap.put("foto_usuario", dados_usuario.getString("foto_usuario", ""));
        membrosMap.put("bloqueio", false);
        db.collection("Assinantes").document(id_banca)
                .collection("Usuarios").document(identificadorUsuario).set(membrosMap);

    }


    private void desinscreverUsuario(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(id_banca)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        receber_novidades.setChecked(false);


                        //deleta
                        db.collection("Assinantes").document(id_banca)
                                .collection("Usuarios").document(identificadorUsuario).delete();
                        if (!task.isSuccessful()) {
                            Toast.makeText(Detalhe_banca_Noticia.this, "erro", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    private void sendNotifiaction(final String id_loja, final String nome_quem_mando, String msg, String token_destinatario) {

        Data data = new Data(id_loja, R.drawable.favicon,
                nome_quem_mando + " " + msg, nome_loja_string, id_loja, "assinantes");
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
    private void VerificaAssinante(String id_loja){
        db.collection("Assinantes").document(id_loja)
                .collection("Usuarios")
                .whereEqualTo("id_usuario", identificadorUsuario)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.i("sokdsodksdok",document.getId());
                                receber_novidades.setChecked(true);
                            }
                        } else {
                            receber_novidades.setChecked(false);
                        }
                    }
                });
    }
    public void Recuperar_noticia_da_banca(){
        lista_item.clear();
        Query query= db.collection("Noticias")
                .whereEqualTo("id_banca",id_banca)
                .orderBy("data", Query.Direction.ASCENDING);
        registration_itens_banca=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd",change.getDocument().getId());
                    Forum item_noticia = change.getDocument().toObject(Forum.class);
                    item_noticia.setId(change.getDocument().getId());
                    switch (change.getType()) {
                        case ADDED:
                            lista_item.add(0, item_noticia);
                            if(lista_item.size()>0){
                                recyclerView_noticia.setVisibility(View.VISIBLE);
                                line_nada_encontrado.setVisibility(GONE);
                            }

                            adapter_not.notifyDataSetChanged();
                            Log.d("ad", "New city: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            for (Forum ct : lista_item) {

                                if(item_noticia.getId().equals(ct.getId())){
                                    lista_item.remove(ct);
                                    break;
                                }
                            }
                            lista_item.add(0, item_noticia);
                            adapter_not.notifyDataSetChanged();
                            Log.d("md", "Modified city: " + change.getDocument().getData());
                            break;
                        case REMOVED:
                            for (Forum ct : lista_item) {

                                if(item_noticia.getId().equals(ct.getId())){

                                    lista_item.remove(ct);
                                    break;
                                }
                            }
                            adapter_not.notifyDataSetChanged();
                            Log.d("rem", "Removed city: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });

    }

    public void RecuperarDados_loja(String id){

        Query query= db.collection("Banca_Noticia").whereEqualTo("id",id);
        registration_loja=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    final Banca banca_atual = change.getDocument().toObject( Banca.class);
                    String id_doc=change.getDocument().getId();
                    switch (change.getType()) {
                        case ADDED:
                            DadosLoja(banca_atual,id_doc);

                            id_dono_loja=banca_atual.getId_autor();
                            token_dono_loja=banca_atual.getToken_autor();
                            VerificaAssinante(banca_atual.getId());

                            ImageRequest request = ImageRequest.fromUri(banca_atual.getIcone_banca());
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
                                    Intent it = new Intent(Detalhe_banca_Noticia.this, AbrirImagem.class);
                                    it.putExtra("id_foto",banca_atual.getIcone_banca());
                                    it.putExtra("nome_foto",banca_atual.getNome_banca());
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
    private void DadosLoja(final Banca banca, final String id_doc){
        String tipo_user=dados_usuario.getString("tipo_usuario", "");
        if(!banca.getAnalizado()){
            analise.setVisibility(View.VISIBLE);
            botao_novo_status.setVisibility(GONE);
            line_rec_status_admin.setVisibility(GONE);
        }else{
            analise.setVisibility(GONE);

        }
        if(tipo_user.equals("admin")){
            permitir.setVisibility(View.VISIBLE);
            deleta.setVisibility(View.VISIBLE);
        }else{
            permitir.setVisibility(GONE);
            deleta.setVisibility(GONE);
        }
        deleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Banca_Noticia").document(id_doc).delete();
                Toast.makeText(Detalhe_banca_Noticia.this, " Deletado COM SUCESSO", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        permitir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> atualizar = new HashMap<>();
                atualizar.put("analizado", true);
                db.collection("Banca_Noticia").document(id_doc)
                        .update(atualizar);
                Toast.makeText(Detalhe_banca_Noticia.this, " AuTORIZADA COM SUCESSO", Toast.LENGTH_LONG).show();
            }
        });
        textToolbar.setText(banca.getNome_banca());
        nome_loja.setText(banca.getNome_banca());
        nome_loja_string=banca.getNome_banca();
        desc.setText(banca.getDesc_banca());
        makeTextViewResizable(desc, 3, "..Veja Mais", true);


        if (banca.getId_autor().equals(identificadorUsuario)) {
            line_botoes.setVisibility(View.VISIBLE);
            rel_status_admin.setVisibility(View.VISIBLE);

            rel_status_visitante.setVisibility(View.GONE);
        }else{
            txt_nada.setText("EM BREVE NOVIDADES!");
            line_nada_encontrado_txt.setVisibility(View.GONE);
        }


        if (!banca.getUrl_img_status().equals("")) {
            Picasso.get()
                    .load(banca.getIcone_banca())
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
                    .load(banca.getIcone_banca())
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
                    .load(banca.getIcone_banca())
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


        line_assinante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_banca_Noticia.this, MeusAssinantes.class);
                it.putExtra("id_comercio", id_banca);
                //  it.putExtra("id_author", comercio.getIdauthor());
                startActivity(it);
            }
        });
        botao_nova_noticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_banca_Noticia.this, Nova_noticia.class);
                it.putExtra("id_banca", id_banca);
                it.putExtra("foto_banca", banca.getIcone_banca());
                it.putExtra("nome_banca", banca.getNome_banca());
                startActivity(it);
            }
        });
        botao_novo_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_banca_Noticia.this, Pag_add_Status.class);
                it.putExtra("id_loja",id_banca);
                it.putExtra("nome_loja",banca.getNome_banca());
                it.putExtra("url_icone_loja",banca.getIcone_banca());
                it.putExtra("tipo_loja","banca_noticia");
                startActivity(it);

            }
        });

        line_rec_status_vistante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =new Intent(Detalhe_banca_Noticia.this, Ver_status_banca_noticia.class);
                it.putExtra("userid", banca.getId_autor());
                it.putExtra("id_loja",id_banca);
                it.putExtra("nome_loja", banca.getNome_banca());
                it.putExtra("icone_loja", banca.getIcone_banca());
                startActivity(it);
            }
        });

        line_rec_status_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it =new Intent(Detalhe_banca_Noticia.this, Ver_status_banca_noticia.class);
                it.putExtra("userid",banca.getId_autor());
                it.putExtra("id_loja",id_banca);
                it.putExtra("nome_loja",banca.getNome_banca());
                it.putExtra("icone_loja",banca.getIcone_banca());
                startActivity(it);
            }
        });
        line_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_banca_Noticia.this, Configuracao_banca_noticia.class);
                it.putExtra("id_banca",id_banca);
                startActivity(it);
                finish();

            }
        });
        line_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_banca_Noticia.this, Info_status_banca_noticia.class);
                it.putExtra("id_loja",id_banca);
                startActivity(it);

            }
        });


        line_iconchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_banca_Noticia.this, ChatActivity_loja.class);
                it.putExtra("comercio_id",banca.getId());
                it.putExtra("id_destinatario",banca.getId_autor());
                it.putExtra("nome_destinatario",banca.getNome_banca());
                it.putExtra("icone_destinatario",banca.getIcone_banca());
                it.putExtra("token_destinatario",banca.getToken_autor());
                startActivity(it);
            }
        });
    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }




    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                // Intent it = new Intent(Detalhe_banca.this, Lista_geral_hq.class);
                // startActivity(it);
                finish();
                break;
            default:
                break;
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
}

