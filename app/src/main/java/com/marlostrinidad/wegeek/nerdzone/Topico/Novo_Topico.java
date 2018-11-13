package com.marlostrinidad.wegeek.nerdzone.Topico;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Topico;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Novo_Topico extends AppCompatActivity {

    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private Toolbar toolbar;
    private CircleImageView icone;
    private ImageView img_topico;
    private Button botaosalvar;
    private DatabaseReference databaseusuario,databasetopico,SeguidoresRef;
    private DataSnapshot seguidoresSnapshot;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private EditText titulo_topico,mensagem_topico;
    private Topico topico = new Topico();
    private Usuario perfil;
    private StorageReference storageReference;
    private Dialog dialog;
    private   Uri url;
    private ChildEventListener ChildEventListenerSeguidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo__topico);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Novo Tópico");
        setSupportActionBar(toolbar);


        //Configuraçoes Originais
        databaseusuario = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        databasetopico = ConfiguracaoFirebase.getDatabase().getReference().child("topico");
        SeguidoresRef =ConfiguracaoFirebase.getDatabase().getReference().child("seguidores");
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        titulo_topico = findViewById(R.id.nome_topico);
        mensagem_topico = findViewById(R.id.desc_topico);
        botaosalvar = findViewById(R.id.botaosalvartopico);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosTopico();
            }
        });
        img_topico = findViewById(R.id.imageTopicoCadastro);
        img_topico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(Novo_Topico.this);
                startActivityForResult(intent, SELECAO_GALERIA);
            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        CarregarSeguidores();
        CarregarDados_do_Usuario();
        TrocarFundos_status_bar();
    }







    private void CarregarDados_do_Usuario(){
        usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerperfil=databaseusuario.orderByChild("tipoconta")
                .equalTo(email).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                         perfil = dataSnapshot.getValue(Usuario.class );
                        assert perfil != null;
                        String icone = perfil.getFoto();

                        IconeUsuario(icone);

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

    private void CarregarSeguidores(){
        String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
        //Recuperar Seguidores
        DatabaseReference seguidoresref =SeguidoresRef.child(usuariologado);
        seguidoresref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seguidoresSnapshot=dataSnapshot;
                Log.i("asdsds", String.valueOf(seguidoresSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void IconeUsuario(String url) {
        //Imagem do icone do usuario
        icone = findViewById(R.id.icone_user_toolbar);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Novo_Topico.this, MinhaConta.class);
                startActivity(it);

            }
        });
        Glide.with(Novo_Topico.this)
                .load(url)
                .into(icone);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

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


                }
                if(imagem!=null) {
                    //Recuperar dados da imagem  para o  Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("topico")
                            .child(perfil.getId())
                            .child(topico.getUid());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    LayoutInflater layoutInflater = LayoutInflater.from(Novo_Topico.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando, null);
                    ImageView imageViewgif = view.findViewById(R.id.gifimage);

                    Glide.with(this)
                            .asGif()
                            .load(R.drawable.gif_analizando)
                            .into(imageViewgif);
                    builder.setView(view);

                    dialog = builder.create();
                    dialog.show();
                    ;
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    //caso de errado
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dialog.dismiss();

                                    Glide.with(getApplicationContext())
                                            .load(uri)
                                            .into(img_topico);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dialog.dismiss();
                                    Toast.makeText(Novo_Topico.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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

    private Topico configurarTopico(){
        String titulo = titulo_topico.getText().toString();
        String mensagem = mensagem_topico.getText().toString();
        final Calendar calendartempo = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'-'MM'-'y",java.util.Locale.getDefault());// MM'/'dd'/'y;
        String data = simpleDateFormat.format(calendartempo.getTime());

        topico.setIdauthor(perfil.getId());
        topico.setTitulo(titulo);
        topico.setMensagem(mensagem);
        topico.setData(data);
        if(url!=null) {
            topico.setFoto(String.valueOf(url));
        }else{

        }
        return  topico;

    }
    public void validarDadosTopico() {
        topico = configurarTopico();

        if (TextUtils.isEmpty(topico.getTitulo())) {
            titulo_topico.setError(padrao);
            return;
        }
        if (TextUtils.isEmpty(topico.getMensagem())) {
            mensagem_topico.setError(padrao);
            return;
        }
        topico.SalvarTopico(seguidoresSnapshot);
        int qtdTopicos = perfil.getTopicos() + 1;
        perfil.setTopicos(qtdTopicos);
        perfil.atualizarQtdTopicos();
        Toast.makeText(Novo_Topico.this, "Tópico Criado Com Sucesso!", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(Novo_Topico.this, ListaTopicos.class);
        startActivity(it);
        finish();




    }



    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, ListaTopicos.class));
                finish();
        }
              /*
        case android.R.id.home:
        // NavUtils.navigateUpFromSameTask(this);
        startActivity(new Intent(this, MainActivity.class)); //O efeito ao ser pressionado do botão (no caso abre a activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            finish();
        }

        break;
        */

        return super.onOptionsItemSelected(item);
    }


    private void TrocarFundos_status_bar(){
        //mudando a cor do statusbar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_toolbarstatusbar);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_toolbarstatusbar);
            //  systemBarTintManager.setStatusBarTintDrawable(Mydrawable);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.parseColor("#1565c0"));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setNavigationBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_toolbarstatusbar);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
