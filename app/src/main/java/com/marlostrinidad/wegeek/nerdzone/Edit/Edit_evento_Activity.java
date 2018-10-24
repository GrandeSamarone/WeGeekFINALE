package com.marlostrinidad.wegeek.nerdzone.Edit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Activits.Minhas_Publicacoes;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Date.DatePickFragment;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity.setWindowFlag;

public class Edit_evento_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,View.OnClickListener {
    private static final String datainicio = "date picker";
    private static final String datafim = "date picker2";
    private static final String TITULO = "Obrigatório";
    private static final String SUBTITULO = "Obrigatório";
    private static final String MENSAGEM = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

    private StorageReference storageReference;
    private Usuario usuarioLogado;
    private String identificadorUsuario;
    private CircleImageView icone;
    private Toolbar toolbar;
    // [START declare_database_ref]
    private DatabaseReference mDatabaseEvento,meuDatabaseEvento;
    private Button botaoiniciodata, botaofimdata;
    // [END declare_database_ref]
    private String[] estados;
    private Evento eventos;
    private String estado,idAutor,estadocadastrado,ids;
    private EditText titulo, subtitulo, mensagem,data_inicio;
    private TextView data_fim,estado_edit;
    private ImageView imgevento;
    private AlertDialog alerta;
    private String urlimg;
    private DatePickerDialog datePickerDialog;
    private int dia, mes, ano;
    private AlertDialog dialog;


    private FloatingActionButton botaoSalvar;
    private ChildEventListener ChildEventListenerevento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_evento_);
        final Calendar calendar12 = Calendar.getInstance();
        Log.i("dataa", String.valueOf(calendar12.getTime()));
        //Configuraçoes
        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Editar Evento");
        setSupportActionBar(toolbar);


        //Configuracao Inicial
        mDatabaseEvento =  ConfiguracaoFirebase.getDatabase().getReference().child("evento");
        meuDatabaseEvento =  ConfiguracaoFirebase.getDatabase().getReference().child("meusevento");
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        imgevento = findViewById(R.id.img_edit_foto_evento);
        imgevento.setOnClickListener(this);
        titulo = findViewById(R.id.titulo_evento_edit);
        subtitulo = findViewById(R.id.subtitulo_evento_edit);
        data_inicio = findViewById(R.id.data_topico_inicio_edit);
        data_inicio.setOnClickListener(this);
        data_fim = findViewById(R.id.data_topico_fim_edit);
        data_fim.setOnClickListener(this);
        mensagem = findViewById(R.id.desc_evento_edit);
        botaoSalvar = findViewById(R.id.btn_salvar_topico_edit);
        botaoSalvar.setOnClickListener(this);
        estado_edit = findViewById(R.id.localidade_edit);


        TrocarFundos_status_bar();
        IconeUsuario();
        CarregarDados_do_Evento();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void CarregarDados_do_Evento(){

         ids=getIntent().getStringExtra("id_do_evento");
         estado= getIntent().getStringExtra("UR_do_evento");
        ChildEventListenerevento=mDatabaseEvento.child(estado).orderByChild("uid")
                .equalTo(ids).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        eventos = dataSnapshot.getValue(Evento.class );
                        assert eventos != null;

                      titulo.setText(eventos.getTitulo());
                        subtitulo.setText(eventos.getSubtitulo());
                       mensagem.setText(eventos.getMensagem());
                        data_fim.setText(eventos.getDatafim());
                        data_inicio.setText(eventos.getDatainicio());
                        estado_edit.setText(eventos.getEstado());
                        //getSelectedItem().toString();
                        final String capaevento = urlimg;



                        Glide.with(Edit_evento_Activity.this)
                                .load(eventos.getCapaevento())
                                .into(imgevento);


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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_edit_foto_evento:
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(Edit_evento_Activity.this);
                startActivityForResult(intent, SELECAO_GALERIA);
                break;

            case R.id.data_topico_inicio_edit:
                DialogFragment datePicker = new DatePickFragment();
                datePicker.show(getSupportFragmentManager(), datainicio);
                break;
            case R.id.data_topico_fim_edit:
                DatePicker datePickera = new DatePicker(Edit_evento_Activity.this);
                final Calendar calendar1 = Calendar.getInstance();
                Log.i("dataa", String.valueOf(calendar1.getTime()));
                ano = calendar1.get(Calendar.YEAR);
                mes = calendar1.get(Calendar.MONTH);
                dia = calendar1.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(Edit_evento_Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                        final String currentDate = DateFormat.getDateInstance().format(calendar1.getTime());
                        data_fim.setText(currentDate);
                    }
                }, ano, mes, dia);

                datePickerDialog.show();
                break;

            case R.id.btn_salvar_topico_edit:
                validardados();
                break;
        }
    }



    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int ano, int mes, int dia) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ano);
        calendar.set(Calendar.MONTH, mes);
        calendar.set(Calendar.DAY_OF_MONTH, dia);

        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());

        data_fim.setText(currentDateString);
        data_inicio.setText(currentDateString);


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
                    imgevento.setImageBitmap(imagem);
                    //Recuperar dados da imagem  para o  Firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();
                    String nomeImagem = UUID.randomUUID().toString();
                    //Salvar no Firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("evento")
                            .child(identificadorUsuario)
                            .child(eventos.getUid());
                    //Progress
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Aguarde..");
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
                                    Toast.makeText(Edit_evento_Activity.this, "Imagem Carregada com Sucesso", Toast.LENGTH_SHORT).show();

                                    urlimg = uri.toString();

                                    if(urlimg!=null){
                                        eventos.setCapaevento(urlimg);
                                    }
                                    Glide.with(Edit_evento_Activity.this)
                                            .load(uri)
                                            .into(imgevento);
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Edit_evento_Activity.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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



    private void validardados() {
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

       String imgcapa = eventos.getCapaevento();

            if (imgcapa != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                LayoutInflater layoutInflater = LayoutInflater.from(Edit_evento_Activity.this);
                final View view  = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando,null);
                ImageView imageViewgif = view.findViewById(R.id.gifimage);

                Glide.with(this)
                        .asGif()
                        .load(R.drawable.gif_analizando)
                        .into(imageViewgif);
                builder.setView(view);

                dialog = builder.create();
                dialog.show();
                Evento evento = new Evento();
                evento.setUid(eventos.getUid());
                evento.setIdUsuario(eventos.getIdUsuario());
                evento.setEstado(estado_edit.getText().toString());
                evento.setTitulo(titulo.getText().toString());
                evento.setSubtitulo(subtitulo.getText().toString());
                evento.setMensagem(mensagem.getText().toString());
                evento.setDatafim(data_fim.getText().toString());
                evento.setDatainicio(data_inicio.getText().toString());
                evento.setCapaevento(imgcapa);
                mDatabaseEvento.child(evento.getEstado()).child(evento.getUid()).setValue(evento).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                           }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                meuDatabaseEvento.child(idUsuario).child(evento.getUid()).setValue(evento).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        Intent it = new Intent(Edit_evento_Activity.this, Minhas_Publicacoes.class);
                        startActivity(it);
                        finish();
                        Toast.makeText(Edit_evento_Activity.this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Intent it = new Intent(Edit_evento_Activity.this, Minhas_Publicacoes.class);
                        startActivity(it);
                        finish();
                        Toast.makeText(Edit_evento_Activity.this, "Erro ao Atualizar, Tente Novamente.", Toast.LENGTH_SHORT).show();

                    }
                });

            }else{
                Toast.makeText(this, "Adicione uma imagem", Toast.LENGTH_SHORT).show();
            }
    }



    private void TrocarFundos_status_bar() {
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

    private void IconeUsuario() {
        //Imagem do icone do usuario
        icone = findViewById(R.id.icone_user_toolbar);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Edit_evento_Activity.this, MinhaConta.class);
                startActivity(it);
            }
        });
        FirebaseUser UsuarioAtual = UsuarioFirebase.getUsuarioAtual();
        String mPhotoUrl = UsuarioAtual.getPhotoUrl().toString();

        Glide.with(Edit_evento_Activity.this)
                .load(mPhotoUrl)
                .into(icone);
    }


}


