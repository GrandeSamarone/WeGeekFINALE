package com.marlostrinidad.wegeek.nerdzone.Evento;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Activits.MinhaConta;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity.setWindowFlag;
import static com.marlostrinidad.wegeek.nerdzone.Helper.App.getUid;

public class Cadastrar_Novo_Evento extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

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
    private Spinner spinner;
    private CircleImageView icone;
    private Toolbar toolbar;
    // [START declare_database_ref]
    private DatabaseReference mDatabaseEvento,SeguidoresRef;
    private DataSnapshot seguidoresSnapshot;
    private Button botaoiniciodata, botaofimdata;
    // [END declare_database_ref]
    private String[] estados;
    private Evento eventos;
    private String estado,idAutor;
    private EditText titulo, subtitulo, mensagem;
    private TextView data_inicio, data_fim;
    private ImageView imgevento;
    private AlertDialog alerta;
    private String urlimg;
    private DatePickerDialog datePickerDialog;

    private int dia, mes, ano;
    private AlertDialog dialog;


    private FloatingActionButton botaoSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_evento);

        final Calendar calendar12 = Calendar.getInstance();
        Log.i("dataa", String.valueOf(calendar12.getTime()));
        //Configuraçoes
        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Criar Evento");
        setSupportActionBar(toolbar);


        //Configuracao Inicial
        eventos = new Evento();
        mDatabaseEvento = FirebaseDatabase.getInstance().getReference();
        SeguidoresRef =ConfiguracaoFirebase.getDatabase().getReference().child("seguidores");
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        botaofimdata = findViewById(R.id.botaodatafim);
        botaoiniciodata = findViewById(R.id.botaodatainicio);
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        imgevento = findViewById(R.id.img_add_foto_evento);
        imgevento.setOnClickListener(this);
        titulo = findViewById(R.id.titulo_evento);
        subtitulo = findViewById(R.id.subtitulo_evento);
        data_inicio = findViewById(R.id.data_topico_inicio);
        data_inicio.setOnClickListener(this);
        data_fim = findViewById(R.id.data_topico_fim);
        data_fim.setOnClickListener(this);
        mensagem = findViewById(R.id.desc_evento);
        botaoSalvar = findViewById(R.id.btn_salvar_topico);
        botaoSalvar.setOnClickListener(this);
        spinner = findViewById(R.id.spnilocalidade);


        TrocarFundos_status_bar();
        IconeUsuario();
        CarregarDadosSpinner();
        CarregarSeguidores();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_foto_evento:
                Intent intent = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(Cadastrar_Novo_Evento.this);
                startActivityForResult(intent, SELECAO_GALERIA);
                break;

            case R.id.data_topico_inicio:
                DialogFragment datePicker = new DatePickFragment();
                datePicker.show(getSupportFragmentManager(), datainicio);
                break;
            case R.id.data_topico_fim:
                DatePicker datePickera = new DatePicker(Cadastrar_Novo_Evento.this);
                final Calendar calendar1 = Calendar.getInstance();
                Log.i("dataa", String.valueOf(calendar1.getTime()));
                ano = calendar1.get(Calendar.YEAR);
                mes = calendar1.get(Calendar.MONTH);
                dia = calendar1.get(Calendar.DAY_OF_MONTH);


                datePickerDialog = new DatePickerDialog(Cadastrar_Novo_Evento.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                        final String currentDate = DateFormat.getDateInstance().format(calendar1.getTime());
                        data_fim.setText(currentDate);
                    }
                }, ano, mes, dia);

                datePickerDialog.show();
                break;

            case R.id.btn_salvar_topico:
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

    private void CarregarSeguidores(){
        String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
        //Recuperar Seguidores
        DatabaseReference seguidoresref =SeguidoresRef.child(usuariologado);
        seguidoresref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seguidoresSnapshot=dataSnapshot;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                        urlimg = String.valueOf(resultUriCAMERA);
                        Glide.with(Cadastrar_Novo_Evento.this)
                                .load(resultUriCAMERA)
                                .into(imgevento);
                        break;
                    case SELECAO_GALERIA:
                        CropImage.ActivityResult resultGALERIA = CropImage.getActivityResult(data);
                        Uri resultUriGALERIA = resultGALERIA.getUri();
                        urlimg = String.valueOf(resultUriGALERIA);
                        Glide.with(Cadastrar_Novo_Evento.this)
                                .load(resultUriGALERIA)
                                .into(imgevento);

                        break;


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private Evento configurarEvento() {
        final String tituloDoEvento = titulo.getText().toString();
        final String subtituloDoEvento = subtitulo.getText().toString();
        final String mensagemDoEvento = mensagem.getText().toString();
        final String dataDoEventoFim = data_fim.getText().toString();
        final String dataDoEventoInicio = data_inicio.getText().toString();
        final String estadoDoEvento = spinner.getSelectedItem().toString();
        final String id = identificadorUsuario;
        final String capaevento = urlimg;


        eventos.setTitulo(tituloDoEvento);
        eventos.setCurtirCount(0);
        eventos.setQuantVisualizacao(0);
        eventos.setIdUsuario(id);
        eventos.setSubtitulo(subtituloDoEvento);
        eventos.setMensagem(mensagemDoEvento);
        eventos.setDatainicio(dataDoEventoFim);
        eventos.setDatafim(dataDoEventoInicio);
        eventos.setEstado(estadoDoEvento);


        return eventos;
    }

    public void validardados() {
        eventos = configurarEvento();

        // verificando se o titulo está vazio
        if (TextUtils.isEmpty(eventos.getTitulo())) {
            titulo.setError(TITULO);
            return;
        }
        if (TextUtils.isEmpty(eventos.getSubtitulo())) {
            subtitulo.setError(SUBTITULO);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(eventos.getMensagem())) {
            mensagem.setError(MENSAGEM);
            return;
        }
        if ( (!eventos.getEstado().equals("Estado"))) {

            imgevento.setDrawingCacheEnabled(true);
            imgevento.buildDrawingCache();
            final Bitmap bitmap = imgevento.getDrawingCache();
            //Recuperar dados da imagem  para o  Firebase
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] dadosImagem= baos.toByteArray();

            StorageReference imagemRef = storageReference
                    .child("imagens")
                    .child("evento")
                    .child(identificadorUsuario)
                    .child(eventos.getUid());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_Evento.this);
            final View view  = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando,null);
            ImageView imageViewgif = view.findViewById(R.id.gifimage);

            Glide.with(this)
                    .asGif()
                    .load(R.drawable.gif_analizando)
                    .into(imageViewgif);
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
                            eventos.setCapaevento(String.valueOf(uri));
                            // SalvarPost(url);
                            urlimg = uri.toString();
                            eventos.salvar(seguidoresSnapshot);
                            Toast.makeText(Cadastrar_Novo_Evento.this, "Evento Criado Com Sucesso!", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent( Cadastrar_Novo_Evento.this,Evento_Lista.class);
                            startActivity(it);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            dialog.dismiss();
                            Toast.makeText(Cadastrar_Novo_Evento.this, "Erro ao Criar Evento", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });


        }else{
            Toast.makeText(this, "Selecione um Estado", Toast.LENGTH_SHORT).show();
        }
        // [START single_value_read]
        final String userId = getUid();

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
                Intent it = new Intent(Cadastrar_Novo_Evento.this, MinhaConta.class);
                startActivity(it);
            }
        });
        FirebaseUser UsuarioAtual = UsuarioFirebase.getUsuarioAtual();
        String mPhotoUrl = UsuarioAtual.getPhotoUrl().toString();

        Glide.with(Cadastrar_Novo_Evento.this)
                .load(mPhotoUrl)
                .into(icone);
    }

    //carregar spinner
    private void CarregarDadosSpinner() {
        //
        String[] artista = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estado = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}




