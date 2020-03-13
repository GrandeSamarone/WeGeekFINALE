package com.marlostrinidad.wegeek.nerdzone.Noticia.Banca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.GeoLocation;
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Banca;
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

public class Configuracao_banca_noticia extends TrocarFundo implements View.OnClickListener {

    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private AppCompatEditText campotitulo, campodesc, campoendereco;
    private CircleImageView imagem1;
    private StorageReference storageReference;
    private Button botaosalvar,botao_deletar;
    private String identificadorUsuario, estadostring, lojastring;
    private Banca_noticia bancaNoticia = new Banca_noticia();
    private Toolbar toolbar;
    private AlertDialog dialog;
    private CircleImageView icone;
    private TextView textToolbar;
    private LatLng lat_long;
    private GeoPoint point_atualizado=null,point_antigo;
    private ListenerRegistration registration_loja;
    private GeoLocation geoLocation;
    private String urlimg="",id_loja,icone_img,nomeImagem,img_nome;
    private Localizacao localizacao = new Localizacao();
    private Boolean permissaoLocalizacao = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences dados_usuario;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private FirebaseFirestore db;
    private String id_doc;

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private String[] pegandoUrl;
    private Spinner campoloja;
    private APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao_banca_noticia);

        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar=findViewById(R.id.app_toolbar_title_secundario);
        textToolbar.setText("Atualizar Dados");
        setSupportActionBar(toolbar);

        //Configuração Basica
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        botao_deletar=findViewById(R.id.botaodeletar_banca);
        id_loja = getIntent().getStringExtra("id_banca");
        RecuperarDados_loja(id_loja);
        campotitulo = findViewById(R.id.nome_banca_conf);
        campodesc = findViewById(R.id.desc_banca_conf);
        imagem1 = findViewById(R.id.imagebancaCadastro1_conf);
        imagem1.setOnClickListener(this);


        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        botaosalvar = findViewById(R.id.botaosalvar_edit_banca);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validarDados();
            }
        });

        botao_deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Deletar_dados(id_loja);
                Log.i("oaskdoskdosdk",id_loja);
            }
        });

        /// /validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Preferences pega o nome do usuario;
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void sendNotifiaction_admin( ) {
        Data data = new Data(identificadorUsuario, R.drawable.favicon,
                "Admin" + " " + "Temos alterações em publicacoes", "ATENÇÃO","", "WeGeeK_Admin");
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
    private void Deletar_dados(final String id){
        Log.i("oaskdoskdosdk",id);
        final MediaPlayer dialog_music = MediaPlayer.create(Configuracao_banca_noticia.this,R.raw.navi_ei);
        dialog_music.start();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Configuracao_banca_noticia.this);
        LayoutInflater layoutInflater = LayoutInflater.from(Configuracao_banca_noticia.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText("Todo conteúdo dessa banca será deletado,deseja Continuar?");
        builder.setView(view);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_music.stop();

                db.collection("Banca_Noticia").whereEqualTo("id",id)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection("Banca_Noticia").document(document.getId()).delete();
                            }

                        }
                    }

                });
                db.collection("WeForum").whereEqualTo("id_banca",id)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                db.collection("Assinantes").document(id).delete();
                                db.collection("WeForum").document(document.getId()).delete();
                                db.collection("Chat").document(document.getId()).delete();

                            }
                        }
                    }

                });
                Toast.makeText(Configuracao_banca_noticia.this, "Deletado com Sucesso.", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(Configuracao_banca_noticia.this, Forum_principal.class);
                startActivity(it);
                finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
                dialog_music.stop();
            }
        });
        dialog = builder.create();
        dialog.show();



    }
    public void RecuperarDados_loja(String id){

        Query query= db.collection("Banca_Noticia").whereEqualTo("id",id);
        registration_loja=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Banca comercio_atual = change.getDocument().toObject(Banca.class);
                    switch (change.getType()) {
                        case ADDED:
                            id_doc=change.getDocument().getId();
                            DadosLoja(comercio_atual);

                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
            }
        });

    }

    private void DadosLoja(Banca comercio_atual) {
        //   img_nome=comercio_atual.getNome_img();
        if (comercio_atual.getNome_banca() != null) {
            campotitulo.setText(comercio_atual.getNome_banca());
        }else{
            campotitulo.setText("");
        }
        if (comercio_atual.getDesc_banca() != null) {
            campodesc.setText(comercio_atual.getDesc_banca());
        }else{
            campodesc.setText("");
        }

        if (comercio_atual.getIcone_banca() != null) {
            icone_img=comercio_atual.getIcone_banca();
            Picasso.get()
                    .load(comercio_atual.getIcone_banca())
                    .into(imagem1);
        }
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
                            .child("comercio")
                            .child(identificadorUsuario)
                            .child(nomeImagem);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    LayoutInflater layoutInflater = LayoutInflater.from(Configuracao_banca_noticia.this);
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
                                            .into(imagem1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dialog.dismiss();
                                    Toast.makeText(Configuracao_banca_noticia.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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

    private Banca_noticia configurarBanca() {

        String titulo = campotitulo.getText().toString();
        String descricao = campodesc.getText().toString();
        if(!urlimg.equals("")){
            bancaNoticia.setIcone_banca(urlimg);
        }else{
            bancaNoticia.setIcone_banca(icone_img);
        }
        bancaNoticia.setNome_banca(titulo);
        bancaNoticia.setDesc_banca(descricao);

        return bancaNoticia;
    }



    public void validarDados() {
        bancaNoticia = configurarBanca();
        if (bancaNoticia.getIcone_banca() != null) {
            if (TextUtils.isEmpty(bancaNoticia.getNome_banca())) {
                campotitulo.setError(padrao);
                return;
            }
            bancaNoticia.Atualizar(id_doc);

            sendNotifiaction_admin();
            Toast.makeText(this, "Atualização em análise.", Toast.LENGTH_LONG).show();
            Intent it = new Intent(Configuracao_banca_noticia.this, Detalhe_banca_Noticia.class);
            it.putExtra("id",id_loja);
            startActivity(it);
            finish();
        }
    }


    //click na imagem para cadastra comercio
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imagebancaCadastro1_conf:
                EscolherImagem(1);
                break;


        }
    }

    public void EscolherImagem(int requestCode) {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(Configuracao_banca_noticia.this);
        startActivityForResult(intent, SELECAO_GALERIA);
    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(Configuracao_banca_noticia.this, Detalhe_banca_Noticia.class);
                it.putExtra("id",id_loja);
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
        Intent it = new Intent(Configuracao_banca_noticia.this, Detalhe_banca_Noticia.class);
        it.putExtra("id",id_loja);
        startActivity(it);
        finish();
    }
}
