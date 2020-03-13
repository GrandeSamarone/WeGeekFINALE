package com.marlostrinidad.wegeek.nerdzone.Noticia.Noticia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagem;
import com.marlostrinidad.wegeek.nerdzone.Helper.CircleProgressDrawable;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Model.Forum;
import com.marlostrinidad.wegeek.nerdzone.R;

public class LerNoticia extends TrocarFundo {
    private String id_noticia_selecionada;
    private TextView textoTitulo,desc_texto,titulo;
    private FirebaseFirestore db;
    private SimpleDraweeView img_noticia;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ler_noticia);

        toolbar = findViewById(R.id.toolbar_detalhe_topico);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        titulo=findViewById(R.id.titulo_noticia);
        textoTitulo=findViewById(R.id.textViewtitulo_topico);
        desc_texto=findViewById(R.id.descricao_noticia);
        img_noticia=findViewById(R.id.img_detalhe_noticia_ler);
        id_noticia_selecionada = getIntent().getStringExtra("id_noticia_selecionada");
        db = FirebaseFirestore.getInstance();


        Recuperar_Dados_Noticia();
    }

    private void Recuperar_Dados_Noticia(){
        DocumentReference docRef = db.collection("Noticias").document(id_noticia_selecionada);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final Forum forum=document.toObject(Forum.class);
                        titulo.setText(forum.getTitulo());
                        textoTitulo.setText(forum.getTitulo());
                        desc_texto.setText(forum.getDescricao());
                        //   author_topico.setText(forum.getNomeauthor());
                        if (forum.getFoto() != null) {
                            String stringcapa = forum.getFoto();
                            if (stringcapa != null) {
                                img_noticia.setVisibility(View.VISIBLE);
                                ImageRequest request = ImageRequest.fromUri(forum.getFoto());
                                DraweeController controller = Fresco.newDraweeControllerBuilder()
                                        .setImageRequest(request)
                                        .setOldController(img_noticia.getController()).build();
                                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);

                                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
                                GenericDraweeHierarchy hierarchy = builder
                                        .setRoundingParams(roundingParams)
                                        .setProgressBarImage(new CircleProgressDrawable())
                                        //  .setPlaceholderImage(context.getResources().getDrawable(R.drawable.carregando))
                                        .build();
                                img_noticia.setHierarchy(hierarchy);
                                img_noticia.setController(controller);
                            }
                            img_noticia.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(LerNoticia.this, AbrirImagem.class);
                                    it.putExtra("id_foto", forum.getFoto());
                                    it.putExtra("nome_foto", forum.getTitulo());
                                    startActivity(it);
                                }
                            });

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
}