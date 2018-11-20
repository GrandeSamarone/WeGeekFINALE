package com.marlostrinidad.wegeek.nerdzone.Mercado;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem.AbrirImagemComercio;
import com.marlostrinidad.wegeek.nerdzone.Activits.ChatActivity;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio_Visualizacao;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.PerfilAmigos.Perfil;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class Detalhe_Mercado extends AppCompatActivity {

    private CarouselView fotos;
    private FloatingActionButton fabcontato;
    private TextView titulo, legenda, descricao, endereco, categoria, estado, criador, num_rating,
            visualizacoes,valor;
    private Button botaoavaliar;
    private LinearLayout botaovoltar;
    private Comercio mercadoselecionado;
    private MaterialRatingBar ratingBar, ratingbarTotal;
    private Dialog dialog;
    private String identificadorUsuario;
    private DatabaseReference database, databasemercado, database_usuario;
    private ChildEventListener ChildEventListenermercado;
    private FirebaseDatabase databases;
    private AlertDialog alerta;
    private SharedPreferences preferences = null;
    private CircleImageView perfil;
    private ChildEventListener ChildEventListeneruser;
    private String usuarioLogado;
    private RelativeLayout click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_detalhe_mercados);


        //configuracoes iniciais
        usuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        visualizacoes = findViewById(R.id.quantvisualizacao_comercio);
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        databasemercado = ConfiguracaoFirebase.getDatabase().getReference().child("comercio");
        database = ConfiguracaoFirebase.getDatabase().getReference().child("rating");
        database_usuario = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        databases = FirebaseDatabase.getInstance();
        num_rating = findViewById(R.id.mercado_num_ratings);
        ratingBar = findViewById(R.id.rate_star);
        valor = findViewById(R.id.detalhe_mercado_valor);
        ratingbarTotal = findViewById(R.id.mercado_detalhe_rating);
        fotos = findViewById(R.id.carousel_foto_mercado);
        perfil = findViewById(R.id.icone_author_mercado);
        titulo = findViewById(R.id.detalhe_mercado_titulo);
        legenda = findViewById(R.id.detalhe_mercado_legenda);
        criador = findViewById(R.id.detalhe_mercado_criador);
        descricao = findViewById(R.id.detalhe_mercado_descricao);
        endereco = findViewById(R.id.detalhe_mercado_endereco);
        fabcontato = findViewById(R.id.fab_entrar_em_contato);
        click = findViewById(R.id.click_layout);
        botaovoltar = findViewById(R.id.mercado_button_back);
        botaovoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhe_Mercado.this, MercadoActivity.class);
                startActivity(it);
                finish();
            }
        });


        //recuperar mercado selecionado

        mercadoselecionado = (Comercio) getIntent().getSerializableExtra("mercadoelecionado");

        if (mercadoselecionado != null) {
            ContaQuatAcesso(mercadoselecionado);
        }
        titulo.setText(mercadoselecionado.getTitulo());
        descricao.setText(mercadoselecionado.getDescricao());
        endereco.setText(mercadoselecionado.getEndereco());
        if (!mercadoselecionado.getValor().equals("R$0,00")) {
            valor.setVisibility(View.VISIBLE);
            valor.setText(mercadoselecionado.getValor());
        }else{
            valor.setVisibility(View.GONE);
            legenda.setVisibility(View.VISIBLE);
            legenda.setText(mercadoselecionado.getFraserapida());
        }

        visualizacoes.setText(String.valueOf(mercadoselecionado.getQuantVisualizacao()));

        CarregarDados_do_Criador_do_Comercio(mercadoselecionado.getIdAutor());
        //   categoria.setText(mercadoselecionado.getCategoria());

        //carregar as imagens

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                String urlstring = mercadoselecionado.getFotos().get(position);
                if (!Detalhe_Mercado.this.isFinishing()) {
                    Glide.with(getApplicationContext())
                            .load(urlstring)
                            .into(imageView);
                }
            }
        };

        fotos.setPageCount(mercadoselecionado.getFotos().size());
        fotos.setImageListener(imageListener);

        fotos.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                    /*Bundle bundle = new Bundle();
                    bundle.putString("id", mercadoselecionado.getIdMercado());
                    Intent it = new Intent(Detalhe_Mercado.this, AbrirImagemComercio.class);
                    it.putExtras(bundle);
                    startActivity(it);
                    */


                List<String> ff = mercadoselecionado.getFotos();
                Intent it = new Intent(Detalhe_Mercado.this, AbrirImagemComercio.class);
                it.putExtra("fotoselecionada", (Serializable) ff);
                it.putExtra("nome", mercadoselecionado.getTitulo());
                startActivity(it);

            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        database_usuario.removeEventListener(ChildEventListeneruser);
    }

    private void CarregarDados_do_Criador_do_Comercio(String idusuario) {

        ChildEventListeneruser = database_usuario.orderByChild("id")
                .equalTo(idusuario).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        final Usuario user = dataSnapshot.getValue(Usuario.class);

                        assert user != null;
                        RatingBar(user);
                        String foto = user.getFoto();
                        if(!Detalhe_Mercado.this.isFinishing()) {
                            Glide.with(getApplicationContext())
                                    .load(foto)
                                    .into(perfil);
                            criador.setText(user.getNome());
                        }

                        if(!usuarioLogado.equals(user.getId())){
                            fabcontato.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(Detalhe_Mercado.this, ChatActivity.class);
                                    it.putExtra("id", mercadoselecionado.getIdAutor());
                                    startActivity(it);
                                }
                            });
                        }else{
                            fabcontato.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(Detalhe_Mercado.this, MinhaConta.class);
                                    it.putExtra("id", user.getId());
                                    startActivity(it);
                                }
                            });
                        }



                        if (!usuarioLogado.equals(user.getId())) {
                            click.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(Detalhe_Mercado.this, Perfil.class);
                                    it.putExtra("id", user.getId());
                                    startActivity(it);
                                }
                            });
                        } else {
                            click.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent it = new Intent(Detalhe_Mercado.this, MinhaConta.class);
                                    it.putExtra("id", user.getId());
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


    @Override
    protected void onStart() {
        super.onStart();
        //RatingBar_Comercio();
        preferences = getSharedPreferences("primeiravezdetalhe", MODE_PRIVATE);
        if (preferences.getBoolean("primeiravezdetalhe", true)) {
            preferences.edit().putBoolean("primeiravezdetalhe", false).apply();
            Dialog_click_foto();
        } else {

        }
    }

    private void Dialog_click_foto() {

        LayoutInflater li = getLayoutInflater();


        View view = li.inflate(R.layout.dialog_informar_click_foto, null);


        view.findViewById(R.id.botaoentendi).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {


                //desfaz o dialog_opcao_foto.
                dialog.dismiss();
            }
        });


        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }


                break;

            default:
                break;
        }

        return true;
    }

    private void RatingBar(Usuario user) {
        final DatabaseReference ratingcomercio = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("ratingbar-comercio")
                .child(mercadoselecionado.getIdMercado())
                .child(usuarioLogado);

        ratingcomercio.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdLikes = 0;
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    float rating = Float.parseFloat(dataSnapshot.getValue().toString());
                    //   float d= (float) ((rating*5) /100);
                    ratingBar.setRating(rating);
                    ratingbarTotal.setRating(rating);
                    mercadoselecionado.setTotalrating(rating);
                    Toast.makeText(Detalhe_Mercado.this, "total" + mercadoselecionado.getTotalrating(), Toast.LENGTH_SHORT).show();
                    //Montar objeto postagem curtida

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) ratingcomercio.setValue(rating);
            }
        });

    }

    public void ContaQuatAcesso(final Comercio comercio) {

        //Contando as Visualizacoes
        DatabaseReference eventoscurtidas = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comercio-visualizacao")
                .child(comercio.getIdMercado());
        eventoscurtidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int QtdVisu = 0;
                if (dataSnapshot.hasChild("qtdvisualizacao")) {
                    Comercio_Visualizacao comercioVisu = dataSnapshot.getValue(Comercio_Visualizacao.class);
                    QtdVisu = comercioVisu.getQtdvisualizacao();
                }

                //Montar objeto postagem curtida
                Comercio_Visualizacao visualizacao = new Comercio_Visualizacao();
                visualizacao.setComercio(comercio);
                visualizacao.setQtdvisualizacao(QtdVisu);

                visualizacao.Salvar();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}