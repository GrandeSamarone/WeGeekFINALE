package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.marlostrinidad.wegeek.nerdzone.R;

public class Gifteste extends AppCompatActivity {

    private ImageView imageView;
    private AlertDialog dialog;
    private Button botao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifteste);
        botao = findViewById(R.id.botaoabrir);

       botao.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               SalvarMercado();
               SalvarMercado1();
           }
       });
    }

    public void  SalvarMercado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(Gifteste.this);
        final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando, null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_analizando)
                .into(imageViewgif);
        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }

    public void  SalvarMercado1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(Gifteste.this);
        final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando, null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_briguinha)
                .into(imageViewgif);
        builder.setView(view);

        dialog = builder.create();
        dialog.show();
    }
}
