package com.marlostrinidad.wegeek.nerdzone.Noticia.Noticia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Forum;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
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
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Editar_noticia extends TrocarFundo {
    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private Toolbar toolbar;
    private ImageView img_novo_grupo;
    private String urlimg="";
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
    private Button botao_carregar_img,botao_deletar;
    private ChildEventListener ChildEventListenerSeguidores;
    private RadioButton radio_grupo, radio_topico;
    private String identificadorUsuario;
    private RadioGroup grupo;
    private SharedPreferences sharedPreferences;
    private SharedPreferences nome_usuario;
    private Spinner campo_categoria_grupo;
    private TextView text_toolbar;
    private FirebaseFirestore db;
    private String id_noticia,img;
    private APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar__topico);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        //Configuraçoes Originais
        botao_deletar=findViewById(R.id.botaodeletar_Noticia);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        id_noticia = getIntent().getStringExtra("id_noticia");
        RecuperarDados_topico(id_noticia);
        botao_carregar_img=findViewById(R.id.carregar_img_topico_edit);
        text_toolbar=findViewById(R.id.app_toolbar_title_secundario);
        text_toolbar.setText("Editar Notícia");
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        db = FirebaseFirestore.getInstance();
        titulo_topico = findViewById(R.id.nome_topico_edit);
        mensagem_topico = findViewById(R.id.desc_topico_edit);
        botaosalvar = findViewById(R.id.botaosalvar_Grupo_edit);
        campo_categoria_grupo = findViewById(R.id.spinner_grupo_categoria_edit);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDados_Grupo();
            }
        });
        img_novo_grupo = findViewById(R.id.image_topico_Cadastro_edit);

        botao_carregar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(Editar_noticia.this);
                startActivityForResult(intent, SELECAO_GALERIA);
            }
        });

        botao_deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Deletar_dados(id_noticia);
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
    public void RecuperarDados_topico(String id){
        FirebaseFirestore dbs;
        dbs=FirebaseFirestore.getInstance();
        dbs.collection("Noticias").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Forum forum= task.getResult().toObject(Forum.class);
                        titulo_topico.setText(forum.getTitulo());
                        mensagem_topico.setText(forum.getDescricao());
                        Picasso.get().load(forum.getFoto()).into(img_novo_grupo);
                        if(forum.getFoto()!=null){
                            img=forum.getFoto();
                        }
                    }
                });
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
                    String nomeImagem = UUID.randomUUID().toString();
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("forum")
                            .child(identificadorUsuario)
                            .child(nomeImagem);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    LayoutInflater layoutInflater = LayoutInflater.from(Editar_noticia.this);
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
                                    Toast.makeText(Editar_noticia.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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
        String titulo = titulo_topico.getText().toString();
        String mensagem = mensagem_topico.getText().toString();
        String categoria_grupo = campo_categoria_grupo.getSelectedItem().toString();
        forum.setTitulo(titulo);
        forum.setFoto(urlimg);
        forum.setDescricao(mensagem);
        forum.setCategoria(categoria_grupo);
        if(!urlimg.equals("")){
            forum.setFoto(String.valueOf(url));
        }else{
            forum.setFoto(img);
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


                forum.AlterarForum(id_noticia);
                sendNotifiaction_admin();
                Toast.makeText(Editar_noticia.this, "Dados alterado Com Sucesso!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(Editar_noticia.this, Detalhe_noticia.class);
                it.putExtra("id_noticia_selecionada",id_noticia);
                startActivity(it);
                finish();
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
    private void sendNotifiaction_admin(){
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
        final MediaPlayer dialog_music = MediaPlayer.create(Editar_noticia.this,R.raw.navi_ei);
        dialog_music.start();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Editar_noticia.this);
        LayoutInflater layoutInflater = LayoutInflater.from(Editar_noticia.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText("Todo conteúdo dessa notícia será deletada,deseja Continuar?");
        builder.setView(view);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog_music.stop();

                db.collection("Noticias").document(id).delete();
                db.collection("Chat").document(id).delete();

                Toast.makeText(Editar_noticia.this, "Deletado com Sucesso.", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(Editar_noticia.this, Forum_principal.class);
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


    public boolean  onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent it=new Intent(Editar_noticia.this,Forum_principal.class);
                startActivity(it);
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

