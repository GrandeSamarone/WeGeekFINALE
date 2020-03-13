package com.marlostrinidad.wegeek.nerdzone.Noticia.Banca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.marlostrinidad.wegeek.nerdzone.Helper.IFireStoreLoadDone;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Mercado.Status.Pag_add_Status;
import com.marlostrinidad.wegeek.nerdzone.Model.Status;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class Ver_status_banca_noticia  extends AppCompatActivity  implements IFireStoreLoadDone {


    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;
    private EmojiTextView texto_status;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private StoriesProgressView storiesProgressView;
    private ImageView img_status;
    private TextView story_username,quant_vizu;
    private LinearLayout r_seen;
    private ImageView story_delete;
    private Button botao_verLoja;
    private ArrayList<String> images =new ArrayList();
    private ArrayList<String> storyids=new ArrayList();;
    private String id_quem_crio,id_loja,nome_loja,icone_loja;
    private String identificadorUsuario;
    private FirebaseFirestore db;
    private CircleImageView img_icone_loja;
    private ListenerRegistration registration;
    private SharedPreferences dados_usuario;
    private IFireStoreLoadDone iFireStoreLoadDone;
    private ConstraintLayout layout_carregando;
    private ProgressBar progressBar;
    private LinearLayout line_legenda;
    private RelativeLayout perfil_loja;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ver_status_banca_noticia);
        //configuracoes inciais
        botao_verLoja=findViewById(R.id.botao_ver_loja);
        perfil_loja=findViewById(R.id.rel_pag_loja);
        line_legenda=findViewById(R.id.line_legenda_img);
        texto_status=findViewById(R.id.legenda_status);
        progressBar=findViewById(R.id.img_carregando);
        layout_carregando=findViewById(R.id.const_carregando);
        img_icone_loja=findViewById(R.id.status_foto_loja);
        quant_vizu=findViewById(R.id.seen_number);
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        iFireStoreLoadDone=this;
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        storiesProgressView = findViewById(R.id.stories);
        img_status=findViewById(R.id.image_status);
        story_username=findViewById(R.id.status_nome_loja);

        r_seen = findViewById(R.id.r_seen);
        story_delete = findViewById(R.id.story_delete);

        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        id_quem_crio = getIntent().getStringExtra("userid");
        id_loja=getIntent().getStringExtra("id_loja");
        nome_loja=getIntent().getStringExtra("nome_loja");
        icone_loja=getIntent().getStringExtra("icone_loja");
        if(id_loja!=null){
            Recuperar_Status_selecionado(id_loja);
        }
        if(nome_loja!=null){
            story_username.setText(nome_loja);
        }

        if(icone_loja!=null){
            Picasso.get()
                    .load(icone_loja)
                    .into(img_icone_loja);
        }
        //
        if (id_quem_crio.equals(identificadorUsuario)){
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
            botao_verLoja.setText("Adicionar");
            botao_verLoja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(Ver_status_banca_noticia.this, Pag_add_Status.class);
                    it.putExtra("id_loja",id_loja);
                    it.putExtra("nome_loja",nome_loja);
                    it.putExtra("url_icone_loja",icone_loja);
                    it.putExtra("tipo_loja","banca_noticia");
                    startActivity(it);
                    finish();
                }
            });
        }else {
            botao_verLoja.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(Ver_status_banca_noticia.this, Detalhe_banca_Noticia.class);
                    it.putExtra("id",id_loja);
                    it.putExtra("icone_loja",icone_loja);
                    startActivity(it);
                    finish();
                }
            });
        }

        perfil_loja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Ver_status_banca_noticia.this, Detalhe_banca_Noticia.class);
                it.putExtra("id",id_loja);
                it.putExtra("icone_loja",icone_loja);
                startActivity(it);
                finish();
            }
        });

        //Volta_Foto
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);
        //proxFoto
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Ver_status_banca_noticia.this, Detalhe_banca_Noticia.class);
                intent.putExtra("id", id_loja);
                intent.putExtra("storyid", storyids.get(counter));
                intent.putExtra("title", "views");
                startActivity(intent);
            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }



    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }


    private void Recuperar_Status_selecionado(String id){
        CollectionReference collection=  db.collection("Status_banca_noticia")
                .document(id).collection("Status");
        collection.orderBy("data", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            List<Status> statusList=new ArrayList<>();
                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                Status status=snapshot.toObject(Status.class);
                                statusList.add(status);
                                Log.i("sodksodk",snapshot.getId());
                                addView(status.getId());

                            }
                            iFireStoreLoadDone.onFireStoreLoadSuccess(statusList);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iFireStoreLoadDone.onFireStoreLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onFireStoreLoadSuccess(final List<Status> statusList) {
        storiesProgressView.setStoriesCount(statusList.size());
        storiesProgressView.setStoryDuration(5000L);
        if(statusList.size()>0) {
            Picasso.get().load(statusList.get(0).getUrl_img()).into(img_status, new Callback() {
                @Override
                public void onSuccess() {
                    storiesProgressView.startStories();
                    if (!statusList.get(0).getLegenda_img().equals("")) {
                        line_legenda.setVisibility(View.VISIBLE);
                        texto_status.setText(statusList.get(0).getLegenda_img());
                    }
                    layout_carregando.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }else{
            Picasso.get()
                    .load(R.drawable.img_nem_novidade)
                    .into(img_status);
            layout_carregando.setVisibility(View.GONE);
        }
        storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
            @Override
            public void onNext() {
                line_legenda.setVisibility(View.GONE);
                layout_carregando.setVisibility(View.VISIBLE);
                if(counter<statusList.size()){
                    counter++;
                    if(!statusList.get(counter).getLegenda_img().equals("")){
                        line_legenda.setVisibility(View.VISIBLE);
                        texto_status.setText(statusList.get(counter).getLegenda_img());
                    }
                    Picasso.get().load(statusList.get(counter).getUrl_img()).into(img_status, new Callback() {
                        @Override
                        public void onSuccess() {
                            layout_carregando.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }

            @Override
            public void onPrev() {
                line_legenda.setVisibility(View.GONE);
                layout_carregando.setVisibility(View.VISIBLE);
                if(counter>0){
                    counter--;
                    if(!statusList.get(counter).getLegenda_img().equals("")){
                        line_legenda.setVisibility(View.VISIBLE);
                        texto_status.setText(statusList.get(counter).getLegenda_img());
                    }
                    Picasso.get().load(statusList.get(counter).getUrl_img()).into(img_status, new Callback() {
                        @Override
                        public void onSuccess() {
                            layout_carregando.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }
            }

            @Override
            public void onComplete() {
                counter=0;
                finish();
            }
        });
        // addView(storyids.get(counter));
        // Ler_quant_view(storyids.get(counter));
    }

    @Override
    public void onFireStoreLoadFailed(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }


    //
    private void addView(String Status_id) {
        String nome = dados_usuario.getString("nome", "");
        String foto = dados_usuario.getString("foto_usuario", "");
        String token = dados_usuario.getString("token", "");

        Query add_View = db.collection("Status_banca_noticia").document(id_loja)
                .collection("Status").whereEqualTo("id",Status_id);

        add_View.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        document.getId();

                        final Map<String, Object> viewStatus = new HashMap<>();
                        viewStatus.put("view", FieldValue.arrayUnion(identificadorUsuario));
                        db.collection("Status_banca_noticia").document(id_loja)
                                .collection("Status").document(document.getId())
                                .update(viewStatus);
                    }

                }


            }
        });
    }


}