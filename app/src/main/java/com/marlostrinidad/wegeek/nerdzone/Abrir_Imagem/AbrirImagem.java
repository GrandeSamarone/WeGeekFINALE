package com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem;

import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.marlostrinidad.wegeek.nerdzone.R;

import me.relex.photodraweeview.PhotoDraweeView;

public class AbrirImagem extends AppCompatActivity {

    private String imageUrls ;
    private String nome_id;
    private AlertDialog alerta;
    private TextView tantasfotos,nome;
    private ImageView botaovoltar;
    private SharedPreferences primeiravez = null;
    private PhotoDraweeView photoDraweeView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_abrir_imagem2);


         photoDraweeView = findViewById(R.id.image_view_img_Unica);
         progressBar = findViewById(R.id.progressBarImg_unica);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0000FF,
                android.graphics.PorterDuff.Mode.MULTIPLY);
         nome=findViewById(R.id.nome_de_foto_unica);
        //Recebendo o link da pagina detalhe do comercio e armazenando
        imageUrls=getIntent().getStringExtra("id_foto");
        nome_id=getIntent().getStringExtra("nome_foto");
        nome.setText(nome_id);
        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(Uri.parse(imageUrls));
        controller.setOldController(photoDraweeView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                // int width = 400, height = 400;
                if (imageInfo == null) {
                    progressBar.setVisibility(View.VISIBLE);
                    return;
                }
                photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                progressBar.setVisibility(View.GONE);
            }
        });
        photoDraweeView.setController(controller.build());



        //Configuracoes Basicas
       // fotos_mercado = ConfiguracaoFirebase.getFirebaseDatabase().child("comercio");

        botaovoltar = findViewById(R.id.foto_button_back_unica);
        botaovoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        primeiravez = getSharedPreferences("primeiravezzom", MODE_PRIVATE);
        if (primeiravez.getBoolean("primeiravezzom", true)) {
            primeiravez.edit().putBoolean("primeiravezzom", false).apply();
            Zoom();
        }else{

        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }



    //dialog de opcoes
    private void Zoom() {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout tela_opcao_foto.xml_foto.xml na view
        View view = li.inflate(R.layout.dialog_zoom, null);
        //definimos para o botão do layout um clickListener


        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }

}