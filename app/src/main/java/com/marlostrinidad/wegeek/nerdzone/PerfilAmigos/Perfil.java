package com.marlostrinidad.wegeek.nerdzone.PerfilAmigos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.marlostrinidad.wegeek.nerdzone.Activits.ChatActivity;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Fragments.perfil.Contos_perfil_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.perfil.FanArts_perfil_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.perfil.Topico_Perfil_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Helper.Main;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.Seguidores.Perfil.SeguidoresPerfil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.vanniktech.emoji.EmojiTextView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil extends AppCompatActivity implements Main, View.OnClickListener {

    private TextView n_seguidores,n_topicos,n_contos,n_arts;
    private EmojiTextView nome,fraserapida;
    private CircleImageView imgperfil;
    private ProgressBar progressBar;
    private SimpleDraweeView capausuario;
    private ChildEventListener ChildEventListenerperfil;
    private DatabaseReference database_perfil,seguidores_ref,usuarioLogadoRef;
    private ViewPager mViewPager;
    private LinearLayout botao_voltar,btn_topico_click,btn_conto_click,btn_fanats_click,btn_seguidor_click;
    private Button botao_seguir,botao_msg;
    private String id_do_usuario;
    private String id_usuariologado;
    private Usuario usuarioLogado,usuarioselecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        id_do_usuario=getIntent().getStringExtra("id");

        //Configuraçoes Originais
        progressBar = findViewById(R.id.progressbar_perfil);
        id_usuariologado =  UsuarioFirebase.getIdentificadorUsuario();
        database_perfil = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        seguidores_ref = ConfiguracaoFirebase.getDatabase().getReference().child("seguidores");
        btn_conto_click=findViewById(R.id.conto_click_perfil);
        btn_conto_click.setOnClickListener(this);
        btn_topico_click = findViewById(R.id.topico_click_perfil);
        btn_topico_click.setOnClickListener(this);
        btn_fanats_click = findViewById(R.id.fanats_click_perfil);
        btn_fanats_click.setOnClickListener(this);
        btn_seguidor_click = findViewById(R.id.seguidores_click_perfil);
        btn_seguidor_click.setOnClickListener(this);
        nome = findViewById(R.id.nomeusuario_perfil);
        n_contos=findViewById(R.id.num_contos_perfil);
        n_topicos=findViewById(R.id.num_topicos_perfil);
        n_arts=findViewById(R.id.num_fantars_perfil);
        n_seguidores= findViewById(R.id.n_seguidores);
        fraserapida= findViewById(R.id.fraserapida_perfil);
        capausuario = findViewById(R.id.capaperfilusuario);
        imgperfil= findViewById(R.id.circleImageViewFotoPerfilusuario);
        botao_voltar = findViewById(R.id.perfil_button_back_perfil);
        botao_voltar.setOnClickListener(this);
        botao_msg = findViewById(R.id.botaomensagemperfil);
        botao_msg.setOnClickListener(this);
        botao_seguir = findViewById(R.id.botaoseguirperfil);
        botao_seguir.setOnClickListener(this);
        //Configurar Abas
        final FragmentPagerItemAdapter adapter= new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("TÓPICOS", Topico_Perfil_Fragment.class)
                        .add("CONTOS", Contos_perfil_Fragment.class)
                        .add("WEARTS",FanArts_perfil_Fragment.class)
                        //.add("COMÉRCIO",Comercio_perfil_Fragment.class)
                        // .add("EVENTO",Evento_perfil_Fragment.class)
                        // .add("Tops", RankFragment.class)
                        .create()
        );
        SmartTabLayout ViewPageTab = findViewById(R.id.SmartTabLayoutperfilusuario);
        mViewPager = findViewById(R.id.viewPagerperfilusuario);
        mViewPager.setAdapter(adapter);
        ViewPageTab.setViewPager(mViewPager);





    }

    @Override
    protected void onStart() {
        super.onStart();
        CarregarDados_do_Usuario();
        recuperarDadosUsuarioLogado();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterEventBus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerEventBus();
    }

    @Override
    public void registerEventBus() {

        try {
            EventBus.getDefault().register(this);
        }catch (Exception Err){


        }

    }

    @Override
    public void unregisterEventBus() {
        try {
            EventBus.getDefault().unregister(this);
        }catch (Exception e){

        }

    }




    @Override
    protected void onStop() {
        super.onStop();
        database_perfil.removeEventListener(ChildEventListenerperfil);
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId()){
            case R.id.perfil_button_back_perfil:
                finish();
                break;
            case R.id.botaomensagemperfil:
                Intent it = new Intent(Perfil.this, ChatActivity.class);
                it.putExtra("id",id_do_usuario);
                startActivity(it);
                break;
            case R.id.topico_click_perfil:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.conto_click_perfil:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fanats_click_perfil:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.seguidores_click_perfil:
                Intent it_id = new Intent(Perfil.this, SeguidoresPerfil.class);
                it_id.putExtra("id",usuarioselecionado.getId());
                startActivity(it_id);
        }
    }
    private void CarregarDados_do_Usuario(){
        ChildEventListenerperfil=database_perfil.orderByChild("id")
                .equalTo(id_do_usuario).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        usuarioselecionado = dataSnapshot.getValue(Usuario.class );
                        assert usuarioselecionado != null;
                        if(usuarioselecionado!=null) {
                            progressBar.setVisibility(View.VISIBLE);
                            nome.setText(usuarioselecionado.getNome());
                            fraserapida.setText(usuarioselecionado.getFrase());
                            n_topicos.setText(String.valueOf(usuarioselecionado.getTopicos()));
                            n_contos.setText(String.valueOf(usuarioselecionado.getContos()));
                            n_arts.setText(String.valueOf(usuarioselecionado.getArts()));
                            n_seguidores.setText(String.valueOf(usuarioselecionado.getSeguidores()));

                            Uri capa = Uri.parse(usuarioselecionado.getCapa());
                            if (capa == null) {
                                capausuario.setBackgroundResource(R.drawable.gradiente_toolbar);
                            } else {
                                DraweeController controllerOne = Fresco.newDraweeControllerBuilder()
                                        .setUri(capa)
                                        .build();
                                capausuario.setController(controllerOne);
                                progressBar.setVisibility(View.GONE);
                            }

                            String fotoperfil = usuarioselecionado.getFoto();
                            if (!Perfil.this.isFinishing()) {
                                Glide.with(Perfil.this)
                                        .load(fotoperfil)
                                        .into(imgperfil);
                            }


                            //EventBUS
                            EventBus.getDefault().postSticky(usuarioselecionado.getId());

                        }else{

                        }
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        usuarioselecionado = dataSnapshot.getValue(Usuario.class );
                        assert usuarioselecionado != null;
                        n_topicos.setText(String.valueOf(usuarioselecionado.getTopicos()));
                        n_contos.setText(String.valueOf(usuarioselecionado.getContos()));
                        n_arts.setText(String.valueOf(usuarioselecionado.getArts()));
                        n_seguidores.setText(String.valueOf(usuarioselecionado.getSeguidores()));
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



    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef= database_perfil.child(id_usuariologado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usuarioLogado = dataSnapshot.getValue(Usuario.class);

                /*verifica se o  usuario esta seguindo*/
                VerificaSegueUsuarioAmigo();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void VerificaSegueUsuarioAmigo(){

        DatabaseReference seguidor_ref=seguidores_ref
                .child(usuarioselecionado.getId())
                .child(id_usuariologado);
        seguidor_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HabilitarBotaoSeguir(true);
                }else{
                    HabilitarBotaoSeguir(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void HabilitarBotaoSeguir(Boolean segueUsuario){
        if(segueUsuario){
            botao_seguir.setText(R.string.botao_txt_seguindo);
            botao_seguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder msgbox = new AlertDialog.Builder(Perfil.this);
                    //configurando o titulo
                    msgbox.setTitle("Deixa de seguir");
                    // configurando a mensagem
                    msgbox.setMessage("Deseja Realmente Deixa de seguir "+usuarioselecionado.getNome()+" ?");
                    // Botao negativo

                    msgbox.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int wich) {
                                    RemoverSeguidor(usuarioLogado,usuarioselecionado);
                                }

                            });


                    msgbox.setNegativeButton("Não",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int wich) {
                                    dialog.dismiss();
                                }
                            });
                    msgbox.show();

                }
            });

        }else{
            botao_seguir.setText(R.string.botao_txt_seguir);

            //Adiciona Evento para seguir Usuario
            botao_seguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Salvar Seguidor
                    SalvarSeguidor(usuarioLogado,usuarioselecionado);
                }
            });
        }
    }

    private void SalvarSeguidor(Usuario ulogado,Usuario uamigo){

        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("id", ulogado.getId() );

        DatabaseReference seguidorRef = seguidores_ref
                .child(uamigo.getId())
                .child(ulogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        botao_seguir.setText(R.string.botao_txt_seguindo);
        botao_seguir.setText(R.string.botao_txt_seguir);
        botao_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoverSeguidor(usuarioLogado,usuarioselecionado);
            }
        });
        Toast toast = Toast.makeText(this, "Agora você verá tudo que o "+uamigo.getNome()+" Postar", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
        //Incrementar seguidores do amigo
        int seguidores = uamigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores );
        DatabaseReference usuarioSeguidores = database_perfil
                .child( uamigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );
        botao_seguir.setText(R.string.botao_txt_seguindo);
        //Incrementar seguindo do usuário logado
        int seguindo = ulogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo );
        DatabaseReference usuarioSeguindo = database_perfil
                .child( ulogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

    }
    private void RemoverSeguidor(Usuario ulogado,Usuario uamigo){

        DatabaseReference seguidorRef = seguidores_ref
                .child(uamigo.getId())
                .child(ulogado.getId());
        seguidorRef.removeValue();

        botao_seguir.setText(R.string.botao_txt_seguir);
        botao_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvarSeguidor(usuarioLogado,usuarioselecionado);
            }
        });

        Toast.makeText(this, "Não verá mais atualização  "+uamigo.getNome()+" Postar", Toast.LENGTH_SHORT).show();

        //deletar seguidores do amigo
        int seguidores = uamigo.getSeguidores() - 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores );
        DatabaseReference usuarioSeguidores = database_perfil
                .child( uamigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );


        //deletar seguindo
        int seguindo = ulogado.getSeguindo() -1 ;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo );
        DatabaseReference usuarioSeguindo = database_perfil
                .child( ulogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );
    }

}
