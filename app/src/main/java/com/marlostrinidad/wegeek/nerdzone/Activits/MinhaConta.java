package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Fragments.MinhaConta.Art_MinhaConta_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.MinhaConta.Contos_MinhaConta_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.MinhaConta.Topicos_MinhaConta_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Helper.Main;
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Icons.PageIcon;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.Seguidores.MinhaConta.MeusSeguidores;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MinhaConta extends AppCompatActivity implements Main, View.OnClickListener {

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButtonCamera,imageButtonGaleria;
    private static final int SELECAO_CAMERA=100;
    private static final int SELECAO_CORTADA=300;
    private static final int SELECAO_CORTADA_CAPA=350;
    private static final int SELECAO_GALERIA=200;
    private static final int SELECAO_CAPA=50;
    private static final int SELECAO_ICONE=300;
    private static final int MINHA_CONTA=12;
    private CircleImageView circleImageViewperfil;
    private SimpleDraweeView capa_perfil;
    private ImageView botaoEdit;
    private LinearLayout btn_voltar,topicoclick,contoclick,fanatsclick,seguidor_click;
    private StorageReference storageReference;
    private String identificadorUsuario;
    private TextView nome,fraserapida,n_topicos,n_contos,n_fanats,n_seguidores;
    private FloatingActionButton botaotrocarfoto;
    private Usuario usuarioLogado,perfil;
    private Usuario user;
    private FirebaseUser usuario;
    private RelativeLayout relative;
    private AlertDialog alerta;
    private ViewPager mViewPager;
    private DatabaseReference database;
    private com.google.firebase.database.ChildEventListener ChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_minha_conta);


        //configuracoes iniciais
        botaoEdit = findViewById(R.id.botao_edit);
        botaoEdit.setOnClickListener(this);
       identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuario = UsuarioFirebase.getUsuarioAtual();
        database = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        n_topicos=findViewById(R.id.num_topicos);
        n_seguidores=findViewById(R.id.num_total_seguidores);
        n_contos=findViewById(R.id.num_contos);
        n_fanats=findViewById(R.id.num_fantars);
        circleImageViewperfil=findViewById(R.id.circleImageViewFotoPerfil);
        nome= findViewById(R.id.nomeusuario_perfil);
        fraserapida = findViewById(R.id.fraserapida_perfil);
        botaotrocarfoto = findViewById(R.id.fabminhaconta);
        botaotrocarfoto.setOnClickListener(this);
        capa_perfil= findViewById(R.id.capameuperfil);
        capa_perfil.setOnClickListener(this);
        btn_voltar=findViewById(R.id.perfil_button_back_perfil);
        btn_voltar.setOnClickListener(this);
        topicoclick=findViewById(R.id.topico_click);
        topicoclick.setOnClickListener(this);
        contoclick=findViewById(R.id.contos_click);
        contoclick.setOnClickListener(this);
        fanatsclick=findViewById(R.id.fanats_click);
        fanatsclick.setOnClickListener(this);
        seguidor_click=findViewById(R.id.seguidores_click);
        seguidor_click.setOnClickListener(this);
        usuarioLogado=UsuarioFirebase.getDadosUsuarioLogado();
        user = new Usuario();

        //validar permissoes
        Permissoes.validarPermissoes(permissoesNecessarias,this,1);





        //Configurar Abas
        final FragmentPagerItemAdapter adapter= new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)

                        .add("TÓPICOS",Topicos_MinhaConta_Fragment.class )
                        // .add("Noticia",Noticia_Fragment.class)
                        .add("CONTOS", Contos_MinhaConta_Fragment.class)
                        .add("WEARTS", Art_MinhaConta_Fragment.class)

                        .create()

        );
        SmartTabLayout ViewPageTab = findViewById(R.id.SmartTabLayoutperfil);
        mViewPager = findViewById(R.id.viewPagerperfil);
        mViewPager.setAdapter(adapter);
        ViewPageTab.setViewPager(mViewPager);



    }

    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {
        //Botão adicional na ToolBar voltar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, MainActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
               finish();
                break;
            default:break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.capameuperfil:
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(this);
                startActivityForResult(intent,SELECAO_CAPA );
                break;
            case R.id.fabminhaconta:
                Escolher_Foto_Perfil();
                break;
            case R.id.perfil_button_back_perfil:
              //  startActivity(new Intent(this, MainActivity.class));  //O efeito ao ser pressionado do botão (no caso abre a activity)
                finish();
                break;
            case R.id.topico_click:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.contos_click:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.fanats_click:
                mViewPager.setCurrentItem(2);
                break;
            case  R.id.botao_edit:
                EditNome();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        RecuperarIcone();
        CarregarDados_do_Usuario();

    }

    @Override
    protected void onStop() {
        super.onStop();
        database.removeEventListener(ChildEventListener);
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

    private void CarregarDados_do_Usuario(){
//Progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Aguarde");
        progressDialog.setMessage("Carregando..");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String email = usuario.getEmail();
        ChildEventListener=database.orderByChild("tipoconta").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

                Uri  capa = Uri.parse(perfil.getCapa());
                if(capa==null){
                    capa_perfil.setBackgroundResource(R.drawable.fundo_da_capa_add_evento);
                }else {
                    DraweeController controllerOne = Fresco.newDraweeControllerBuilder()
                            .setUri(capa)
                            .setAutoPlayAnimations(true)
                            .build();
                    capa_perfil.setController(controllerOne);
                }


                String icone = perfil.getFoto();
                if (!MinhaConta.this.isFinishing()) {
                    Glide.with(MinhaConta.this)
                            .load(icone)
                            .into(circleImageViewperfil);
                }
                nome.setText(perfil.getNome());
                fraserapida.setText(perfil.getFrase());
                n_topicos.setText(String.valueOf(perfil.getTopicos()));
                n_contos.setText(String.valueOf(perfil.getContos()));
                n_fanats.setText(String.valueOf(perfil.getArts()));
                n_seguidores.setText(String.valueOf(perfil.getSeguidores()));
                progressDialog.dismiss();
                //Meus Seguidores
          seguidor_click.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent it = new Intent(MinhaConta.this, MeusSeguidores.class);
                  it.putExtra("id",perfil.getId());
                  startActivity(it);
              }
          });
                //eventoBUS
                EventBus.getDefault().postSticky(perfil.getId());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

                String capa = perfil.getCapa();
                if(capa==null){
                    capa_perfil.setBackgroundResource(R.drawable.fundo_da_capa_add_evento);
                }else {
                    Glide.with(getApplicationContext())
                            .load(capa)
                            .into(capa_perfil);
                }
                String icone = perfil.getFoto();
                Glide.with(getApplicationContext())
                        .load(icone)
                        .into(circleImageViewperfil );

                nome.setText(perfil.getNome());
                fraserapida.setText(perfil.getFrase());
                n_topicos.setText(String.valueOf(perfil.getTopicos()));
                n_contos.setText(String.valueOf(perfil.getContos()));
                n_fanats.setText(String.valueOf(perfil.getArts()));
                n_seguidores.setText(String.valueOf(perfil.getSeguidores()));
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
    //Recebendo Icone
    private void RecuperarIcone() {

        // final Bundle it = getIntent().getExtras();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        if (getIntent().hasExtra("caminho_foto")) {
            final Uri url = Uri.parse(((getIntent().getStringExtra("caminho_foto"))));

            final StorageReference storageReference = storage.getReferenceFromUrl(String.valueOf(url));

            //Progresso
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Aguarde");
            progressDialog.setMessage("carregando... ");
            progressDialog.show();


            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    atualizaFotoUsuario(uri);

                    Glide.with(MinhaConta.this)
                            .load(uri)
                            .into(circleImageViewperfil);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    progressDialog.dismiss();

                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;
            Bitmap capa = null;


            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        CropImage.ActivityResult resultCAMERA = CropImage.getActivityResult(data);
                        Uri resultUriCAMERA = resultCAMERA.getUri();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUriCAMERA);
                        break;
                    case SELECAO_GALERIA:
                        CropImage.ActivityResult resultGALERIA = CropImage.getActivityResult(data);
                        Uri resultUriGALERIA = resultGALERIA.getUri();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUriGALERIA);
                        break;
                    case SELECAO_CAPA:
                        CropImage.ActivityResult resultCAPA = CropImage.getActivityResult(data);
                        Uri resultUriCAPA = resultCAPA.getUri();
                        capa = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUriCAPA);
                        break;
                }
                if (imagem != null) {
                    //Recuperar dados da imagem  para o  Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar no Firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario)
                            .child("perfil.jpg");
                    //Progress
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Aguarde");
                    progressDialog.setMessage("Carregando...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    //caso de errado
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MinhaConta.this, "Imagem Carregada com Sucesso", Toast.LENGTH_SHORT).show();

                                    atualizaFotoUsuario(uri);

                                    Glide.with(getApplicationContext())
                                            .load(uri)
                                            .into(circleImageViewperfil);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MinhaConta.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }else if(capa!=null){
                    //Recuperar dados da imagem  para o  Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    capa.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar no Firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(identificadorUsuario)
                            .child("capa.jpg");
                    //Progress
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Aguarde");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    //caso de errado
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MinhaConta.this, "Imagem Carregada com Sucesso", Toast.LENGTH_SHORT).show();

                                    atualizaCapaUsuario(uri);

                                    Glide.with(MinhaConta.this)
                                            .load(uri)
                                            .into(capa_perfil);
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MinhaConta.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void atualizaFotoUsuario(Uri url) {
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if (retorno) {
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizar();
        }
        Toast.makeText(this, "Sua foto foi alterada", Toast.LENGTH_SHORT).show();
    }
    private void atualizaCapaUsuario(Uri url) {
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno){
            usuarioLogado.setCapa(url.toString());
            usuarioLogado.atualizarCapa();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permissaoResultado: grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private  void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissôes Negadas");
        builder.setMessage("Para ultilizar o app é nescessario aceitar as permissôes");
        builder.setCancelable(false);
        builder.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(MinhaConta.this, MainActivity.class);
                startActivity(it);

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //dialog de opcoes
    private void  Escolher_Foto_Perfil() {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout dialog_opcao_foto.xml_foto.xml na view
        View view = li.inflate(R.layout.dialog_opcao_foto, null);
        //definimos para o botão do layout um clickListener
        view.findViewById(R.id.botaocamera).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.
                if(Build.VERSION.SDK_INT>=24){
                    try{
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(MinhaConta.this);
                startActivityForResult(intent,SELECAO_CAMERA ); //desfaz o dialog_opcao_foto.
                alerta.dismiss();


            }
        });

        view.findViewById(R.id.botaogaleria).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(MinhaConta.this);
                startActivityForResult(intent,SELECAO_GALERIA );
                alerta.dismiss();


            }
        });
        view.findViewById(R.id.botaoicones).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.


                Intent it = new Intent(MinhaConta.this,PageIcon.class);
                it.putExtra("minhaconta",MINHA_CONTA);
                startActivityForResult(it, SELECAO_ICONE);
                //desfaz o dialog_opcao_foto.
                alerta.dismiss();
                finish();

            }
        });

        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Foto");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }

    //dialog de opcoes
    private void  EditNome() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_edit_nome, null);

        //Configuracoes
        final EmojiPopup emojiPopup_nome,emojiPopup_frase;
        View root_view;
        final EmojiEditText nome_edit,frase_edit;
        Button botao_salvar_alteracao;
        ImageView botaoIcon_Nome,botaoIcon_Frase;
        botaoIcon_Nome = view.findViewById(R.id.button_chat_icone_nome);
        botaoIcon_Frase=view.findViewById(R.id.button_chat_icone_frase);
        nome_edit = view.findViewById(R.id.edit_nome_usuario);
        frase_edit = view.findViewById(R.id.edit_frase);
        root_view = view.findViewById(R.id.layoutroot_dialog);
        emojiPopup_nome = EmojiPopup.Builder.fromRootView(root_view).build(nome_edit);
        emojiPopup_frase = EmojiPopup.Builder.fromRootView(root_view).build(frase_edit);
        //setUpEmojiPopup();
        botaoIcon_Nome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup_nome.toggle();
            }
        });
        botaoIcon_Frase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup_frase.toggle();
            }
        });
        nome_edit.setText(perfil.getNome());
        frase_edit.setText(perfil.getFrase());
        botao_salvar_alteracao = view.findViewById(R.id.botaosalvaralteracao);
        botao_salvar_alteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = nome_edit.getText().toString();
                String frase = frase_edit.getText().toString();
                boolean retorno = UsuarioFirebase.atualizarNomeUsuario(nome);
                if(retorno){
                    usuarioLogado.setNome(nome);
                    usuarioLogado.setFrase(frase);
                    usuarioLogado.atualizar();
                 ToastShow();

                    alerta.dismiss();
                }
            }
        });

        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
      //  builder.setTitle("Alterar Foto");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }
public void ToastShow(){
    Toast toast = Toast.makeText(this, "Alterado com sucesso!", Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
    toast.show();
}




}