package com.marlostrinidad.wegeek.nerdzone.Noticia.Noticia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.GeoLocation;
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Banca_noticia;
import com.marlostrinidad.wegeek.nerdzone.Model.Forum;
import com.marlostrinidad.wegeek.nerdzone.Model.Localizacao;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.Noticia.Banca.Detalhe_banca_Noticia;
import com.marlostrinidad.wegeek.nerdzone.Noticia.Forum_principal;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.APIService;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Client;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Data;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.MyResponse;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Sender;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Nova_noticia extends TrocarFundo {
    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private Toolbar toolbar;
    private ImageView img_novo_grupo;
    private String urlimg;
    private Button botaosalvar;
    private DatabaseReference databaseusuario, databasetopico, SeguidoresRef;
    private DataSnapshot seguidoresSnapshot;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private EditText titulo_topico, mensagem_topico;
    private Forum forum = new Forum();
    private Usuario perfil;
    private StorageReference storageReference;
    private Dialog dialog;
    private Uri url;
    // Uri for image path
    private static Uri imageUri = null;
    private Button botao_carregar_img;
    private ChildEventListener ChildEventListenerSeguidores;
    private RadioButton radio_grupo, radio_topico;
    private String identificadorUsuario;
    private RadioGroup grupo;
    private SharedPreferences sharedPreferences;
    private SharedPreferences nome_usuario;
    private Spinner campo_categoria_grupo;
    private TextView text_toolbar;
    private FirebaseFirestore db;
    private APIService apiService;
    private String id_banca,nome_banca,foto_banca;
    private Switch switch_publicar_insta;
    private Boolean verificando=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo__topico_forum);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        //Configuraçoes Originais
        switch_publicar_insta=findViewById(R.id.publicar_instagram);
        //  verificando=switch_publicar_insta.isChecked();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        botao_carregar_img=findViewById(R.id.carregar_img_topico);
        text_toolbar=findViewById(R.id.app_toolbar_title_secundario);
        text_toolbar.setText("Nova Notícia");
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        db = FirebaseFirestore.getInstance();
        titulo_topico = findViewById(R.id.nome_topico);
        mensagem_topico = findViewById(R.id.desc_topico);
        botaosalvar = findViewById(R.id.botaosalvar_Grupo);
        campo_categoria_grupo = findViewById(R.id.spinner_grupo_categoria);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDados_Grupo();
            }
        });
        img_novo_grupo = findViewById(R.id.image_topico_Cadastro);

        botao_carregar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // createInstagramIntent(type, mediaPath);
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(Nova_noticia.this)
                        .setType("image/*");
                startActivityForResult(intent, SELECAO_GALERIA);
            }
        });

        id_banca = getIntent().getStringExtra("id_banca");
        nome_banca = getIntent().getStringExtra("nome_banca");
        foto_banca = getIntent().getStringExtra("foto_banca");



        switch_publicar_insta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    verificando=true;
                } else {
                    verificando=false;
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    protected void onStart() {
        super.onStart();

        CarregarDadosSpinner();

        // Preferences pega o nome do usuario;
        nome_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_CAMERA:
                        //    imageUri = data.getData();// Get intent
                        CropImage.ActivityResult resultCAMERA = CropImage.getActivityResult(data);
                        Uri resultUriCAMERA = resultCAMERA.getUri();
                        imageUri = resultCAMERA.getOriginalUri();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUriCAMERA);
                        break;
                    case SELECAO_GALERIA:

                        CropImage.ActivityResult resultGALERIA = CropImage.getActivityResult(data);
                        Uri resultUriGALERIA = resultGALERIA.getUri();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUriGALERIA);
                        imageUri = resultGALERIA.getOriginalUri();

                        break;


                }

                if (imagem != null) {

                    //Recuperar dados da imagem  para o  Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    String nomeImagem = UUID.randomUUID().toString();
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("forum")
                            .child(identificadorUsuario)
                            .child(nomeImagem);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    LayoutInflater layoutInflater = LayoutInflater.from(Nova_noticia.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando, null);
                    SimpleDraweeView imageViewgif = view.findViewById(R.id.gifimage);
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri("asset:///gif_analizando.gif")
                            .setAutoPlayAnimations(true)
                            .build();
                    imageViewgif.setController(controller);
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
                                    urlimg = uri.toString();

                                    Picasso.get().load(uri)
                                            .into(img_novo_grupo);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dialog.dismiss();
                                    Toast.makeText(Nova_noticia.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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
    // Share image
    private void shareImage(Uri imagePath) {
        Log.i("oskdokdss", String.valueOf(imagePath));
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, imagePath);
        startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
        finish();
    }


    //carregar spinner
    private void CarregarDadosSpinner() {
        //
        String[] artista = getResources().getStringArray(R.array.categoria_grupo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campo_categoria_grupo.setAdapter(adapter);

        campo_categoria_grupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateSpinner(campo_categoria_grupo,"erro");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    boolean validateSpinner(Spinner spinner, String error){
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            TextView selectedTextView = (TextView) selectedView;
            if (selectedTextView.getText().equals("Selecione")) {
                selectedTextView.setError(error);
                return false;
            }
        }
        return true;
    }


    private Forum Configurar_Novo_Forum() {

        String token = nome_usuario.getString("token", "");
        String titulo = titulo_topico.getText().toString();
        String mensagem = mensagem_topico.getText().toString();
        String categoria_grupo = campo_categoria_grupo.getSelectedItem().toString();
        forum.setIdauthor(identificadorUsuario);
        forum.setToken_author(token);
        forum.setNomeauthor(nome_banca);
        forum.setTitulo(titulo);
        forum.setId_banca(id_banca);
        forum.setIcone_author(foto_banca);
        forum.setFoto(urlimg);
        forum.setDescricao(mensagem);
        forum.setCategoria(categoria_grupo);
        if (url != null) {
            forum.setFoto(String.valueOf(url));
        } else {

        }
        return forum;

    }


    public void validarDados_Grupo() {
        forum = Configurar_Novo_Forum();
        // verificando se  está vazio
        if (TextUtils.isEmpty(forum.getTitulo())) {
            titulo_topico.setError(padrao);
            return;
        }
        if (TextUtils.isEmpty(forum.getDescricao())) {
            mensagem_topico.setError(padrao);
            return;
        }
        if ( (!forum.getCategoria().equals("Selecione"))) {
            if (forum.getFoto() != null) {

                if(verificando){

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)Nova_noticia.this
                            .getSystemService(Nova_noticia.this.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("instagram",mensagem_topico.getText());
                    clipboard.setPrimaryClip(clip);

                    //  Nova_noticia.this.startActivity(shareIntent);

                    Toast toast = Toast.makeText(Nova_noticia.this,
                            "Sua descrição foi copiada com sucesso! agora é só colocar na legenda do instagram.",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
                    toast.show();

                    shareImage(imageUri);
                }else{
                    Dialog();
                }
                forum.SalvarForum();
                //
                sendNotifiaction_admin();

            }else{
                Toast toast = Toast.makeText(this, "Selecione uma Foto.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        }else {
            Toast toast = Toast.makeText(this, "Selecione uma Categoria", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        }

    }
    private void  Dialog(){
        final MediaPlayer dialog_music = MediaPlayer.create(Nova_noticia.this, R.raw.navi_veja);
        AlertDialog.Builder builder = new AlertDialog.Builder(Nova_noticia.this);
        LayoutInflater layoutInflater = LayoutInflater.from(Nova_noticia.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem = view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText("Sua Publicação foi para análise\n em breve estará disponível\n" +
                "acompanhe o processo em Minha Conta>Minhas Pubicações.");
        builder.setView(view);
        builder.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_music.stop();
                dialog.dismiss();
                finish();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog_music.start();
    }


    private void sendNotifiaction_admin( ) {
        Data data = new Data(identificadorUsuario, R.drawable.favicon,
                "Admin" + " " + "Temos novas publicacoes", "ATENÇÃO","", "WeGeeK_Admin");
        Sender sender = new Sender(data, "/topics/WeGeeK_Admin");

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (response.body().success != 1) {
                                //  Toast.makeText(Detalhe_noticia.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });
    }


    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                startActivity(new Intent(this, Forum_principal.class));
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



}

