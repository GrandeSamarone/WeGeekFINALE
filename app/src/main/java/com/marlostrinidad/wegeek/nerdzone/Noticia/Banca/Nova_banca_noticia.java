package com.marlostrinidad.wegeek.nerdzone.Noticia.Banca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.GeoLocation;
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Banca_noticia;
import com.marlostrinidad.wegeek.nerdzone.Model.Localizacao;
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

public class Nova_banca_noticia  extends TrocarFundo implements View.OnClickListener {
    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private AppCompatEditText campotitulo, campodesc, campoendereco;
    private CircleImageView imagem1;
    private StorageReference storageReference;
    private Button botaosalvar,botao_add_img;
    private String identificadorUsuario, estadostring, lojastring;
    private Banca_noticia banca = new Banca_noticia();
    private Toolbar toolbar;
    private AlertDialog dialog;
    private CircleImageView icone;
    private TextView textToolbar;
    private LatLng lat_long;
    private GeoPoint point;
    private GeoLocation geoLocation;
    private String urlimg,nomeImagem;
    private Localizacao localizacao = new Localizacao();
    private Boolean permissaoLocalizacao = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences dados_usuario;
    private MediaPlayer dialog_music,dialog_music_add;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE

    };
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private String[] pegandoUrl;
    private Spinner campoloja;
    private APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_banca_noticia);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar = findViewById(R.id.app_toolbar_title_secundario);
        textToolbar.setText("Nova Banca");
        setSupportActionBar(toolbar);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        campotitulo = findViewById(R.id.nome_banca_noticia);
        campodesc = findViewById(R.id.desc_banca_noticia);

        botao_add_img=findViewById(R.id.carregar_img_banca_banca_noti);
        botao_add_img.setOnClickListener(this);
        imagem1 = findViewById(R.id.imageLojaCadastro1_banca_noticia);



        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        botaosalvar = findViewById(R.id.botaosalvarbanca_noticia);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validarDados();
            }
        });

        /// /validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Preferences pega o nome do usuario;
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                if (imagem != null) {
                    //Recuperar dados da imagem  para o  Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    nomeImagem = UUID.randomUUID().toString();
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("banca")
                            .child(identificadorUsuario)
                            .child(nomeImagem);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    dialog_music_add= MediaPlayer.create(Nova_banca_noticia.this,R.raw.som_novo_item);
                    LayoutInflater layoutInflater = LayoutInflater.from(Nova_banca_noticia.this);
                    final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_carr_imagem, null);
                    SimpleDraweeView imageViewgif = view.findViewById(R.id.gifimage_C);
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri("asset:///gif_analizando.gif")
                            .setAutoPlayAnimations(true)
                            .build();
                    imageViewgif.setController(controller);
                    builder.setView(view);

                    dialog = builder.create();
                    dialog.show();
                    dialog_music_add.start();
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    //caso de errado
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dialog.dismiss();
                                    dialog_music_add.stop();
                                    urlimg = uri.toString();

                                    Picasso.get().load(uri)
                                            .into(imagem1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dialog.dismiss();
                                    dialog_music_add.stop();
                                    Toast.makeText(Nova_banca_noticia.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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

    private Banca_noticia configurarBamca() {

        String nome = dados_usuario.getString("nome", "");
        String foto_author = dados_usuario.getString("foto_usuario", "");
        String token = dados_usuario.getString("token", "");
        String titulo = campotitulo.getText().toString();
        String descricao = campodesc.getText().toString();
        banca.setIcone_banca(urlimg);
        banca.setNome_autor(nome);
        banca.setFoto_autor(foto_author);
        banca.setToken_autor(token);
        banca.setId_autor(identificadorUsuario);
        banca.setNome_banca(titulo);
        banca.setDesc_banca(descricao);

        return banca;
    }

    public void validarDados() {
        banca = configurarBamca();
        if (banca.getIcone_banca() != null) {
            if (TextUtils.isEmpty(banca.getNome_banca())) {
                campotitulo.setError(padrao);
                return;
            }
            if (TextUtils.isEmpty(banca.getDesc_banca())) {
                campodesc.setError(padrao);
                return;
            }


            banca.salvar();
            sendNotifiaction_admin();
            Dialog();

        } else {
            Toast.makeText(this, "Selecione uma Foto. ", Toast.LENGTH_SHORT).show();
        }
    }

    private void  Dialog(){
        final MediaPlayer dialog_music = MediaPlayer.create(Nova_banca_noticia.this, R.raw.navi_veja);
        AlertDialog.Builder builder = new AlertDialog.Builder(Nova_banca_noticia.this);
        LayoutInflater layoutInflater = LayoutInflater.from(Nova_banca_noticia.this);
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
                Intent it = new Intent(Nova_banca_noticia.this, Detalhe_banca_Noticia.class);
                it.putExtra("id", banca.getId())
                ;startActivity(it);
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
    //click na imagem para cadastra comercio
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.carregar_img_banca_banca_noti:
                EscolherImagem(1);
                break;


        }
    }

    public void EscolherImagem(int requestCode) {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(Nova_banca_noticia.this);
        startActivityForResult(intent, SELECAO_GALERIA);
    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(Nova_banca_noticia.this, Forum_principal.class);
                startActivity(it);
                finish();
                break;

            default:
                break;
        }

        return true;
    }


    //Permissao
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Permissao Negada");
        builder.setMessage("Para ultilizar o app é preciso aceita as permissoes");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(Nova_banca_noticia.this, Forum_principal.class);
        startActivity(it);
        finish();
    }
}


