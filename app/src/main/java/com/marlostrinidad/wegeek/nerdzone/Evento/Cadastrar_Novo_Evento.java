package com.marlostrinidad.wegeek.nerdzone.Evento;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.florent37.singledateandtimepicker.dialog.DoubleDateAndTimePickerDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.GeoLocation;
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Evento;
import com.marlostrinidad.wegeek.nerdzone.Model.Localizacao;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.APIService;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Client;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Data;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.MyResponse;
import com.marlostrinidad.wegeek.nerdzone.Notificacao.Sender;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Cadastrar_Novo_Evento extends TrocarFundo implements View.OnClickListener {

    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private AppCompatEditText campotitulo, campodesc, campoendereco;
    private CircleImageView imagem1;
    private StorageReference storageReference;
    private Button botaosalvar;
    private String identificadorUsuario, estadostring, lojastring;
    private Evento evento = new Evento();
    private Toolbar toolbar;
    private AlertDialog dialog;
    private CircleImageView icone;
    private TextView textToolbar,data_evento_inicio,data_evento_fim;
    private LatLng lat_long;
    private GeoPoint point;
    private LinearLayout line_cash;
    private GeoLocation geoLocation;
    private String urlimg;
    private long time_Fim_InMilliseconds,time_inicio_InMilliseconds;
    private Localizacao localizacao = new Localizacao();
    private Boolean permissaoLocalizacao = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SharedPreferences dados_usuario;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private String[] pegandoUrl;
    private Spinner campoloja;
    private Date date_inicio,date_fim;
    private SimpleDateFormat simpleDateFormat;
    private Button botao_data;
    private RadioGroup grupo;
    private String isChecked;
    private CurrencyEditText campovalor;
    private MediaPlayer dialog_music_publicar;
   private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_evento);


        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("Publicar novo evento");
        setSupportActionBar(toolbar);



        //Configuração Basica
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        campovalor=findViewById(R.id.desc_valor_item);
        Locale locale = new Locale("pt","BR");
        campovalor.setLocale(locale);
        line_cash=findViewById(R.id.cash_ingresso);
        grupo = findViewById(R.id.radio_group_evento);
        this.simpleDateFormat = new SimpleDateFormat("EEEE d/MM/yy HH:mm", Locale.getDefault());
        data_evento_inicio=findViewById(R.id.data_selecionada_evento_inicio);
        data_evento_fim=findViewById(R.id.data_selecionada_evento_fim);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        campotitulo = findViewById(R.id.nome_evento);
        campodesc = findViewById(R.id.desc_evento);
        campoendereco = findViewById(R.id.desc_endereco_evento);
        botao_data=findViewById(R.id.botao_data_evento);

        //verifica se o gps ta ativo
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
        //inicia a pesquisa do endereco
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyB5sEdUE6YUrPxTPS4tI3EPdfcqpw8WN9E");
        }

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_evento);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG));
        autocompleteFragment.setHint("Ex: Rua Sevilhana n400");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("sdsdsd74", "Place: " + place.getName() + ", endereco " + place.getAddress() + "detalhes " + place.getAddressComponents() + ","
                        + place.getId() + " latitude" + place.getLatLng());
                campoendereco.setVisibility(View.VISIBLE);
                campoendereco.setText(place.getAddress());
                //
                //Guardando a localização
                geoLocation = new GeoLocation(place.getLatLng().latitude, place.getLatLng().longitude);
                point = new GeoPoint(geoLocation.latitude, geoLocation.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("sdsdsd74", "An error occurred: " + status);
            }
        });
        botao_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DoubleDateAndTimePickerDialog.Builder(Cadastrar_Novo_Evento.this)
                        //.bottomSheet()
                        //.curved()
                        //.minutesStep(15)
                        .backgroundColor(Color.WHITE)
                        .mainColor(getResources().getColor(R.color.md_light_blue_700))
                        .titleTextColor(Color.WHITE)
                        .title("Selecione data/Hora")
                        .tab0Text("Inicio")
                        .tab1Text("FIM")
                        .mustBeOnFuture()
                        .listener(new DoubleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(List<Date> dates) {
                                final StringBuilder stringBuilder = new StringBuilder();
                                data_evento_inicio.setText(simpleDateFormat.format(dates.get(0)));
                                date_inicio=dates.get(0);
                                date_fim=dates.get(1);
                                data_evento_fim.setText(simpleDateFormat.format(dates.get(1)));
                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                String date_Fim_Formatted = simpleDateFormat.format(dates.get(1));
                                String date_Inicio_Formatted = simpleDateFormat.format(dates.get(0));
                                try {
                                    Date mDate_FIM = simpleDateFormat.parse(date_Fim_Formatted);
                                    Date mDate_inicio = simpleDateFormat.parse(date_Inicio_Formatted);
                                     time_Fim_InMilliseconds = mDate_FIM.getTime();
                                    time_inicio_InMilliseconds = mDate_inicio.getTime();
                                    Log.i("oskdoskdko32", String.valueOf(time_Fim_InMilliseconds));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                for (Date date : dates) {

                                    stringBuilder.append(simpleDateFormat.format(date)).append("\n");
                                }

                            }
                        }).display();
            }
        });
        imagem1 = findViewById(R.id.imageEventoCadastro1_evento);
        imagem1.setOnClickListener(this);


        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        botaosalvar = findViewById(R.id.botaosalvarevento);
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

        //Verificando Radio Button
        VerificaRadioButton();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    private void buildAlertMessageNoGps() {
        final MediaPlayer dialog_music = MediaPlayer.create(Cadastrar_Novo_Evento.this,R.raw.navi_ei);
        dialog_music.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_Evento.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText("Para publicar um novo evento \n o GPS precisa tá ativado, \n deseja ativar agora?");
        builder.setView(view);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog_music.stop();
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
    public void VerificaRadioButton(){
        //radio group
        grupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId== R.id.radio_evento_Gratuita){
                    isChecked="evento_free";
                    line_cash.setVisibility(View.GONE);
                }else if(checkedId==R.id.radiogrupo_Paga){
                    isChecked="evento_pago";
                    line_cash.setVisibility(View.VISIBLE);
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
                            .child("evento")
                            .child(identificadorUsuario)
                            .child(nomeImagem);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_Evento.this);
                    dialog_music_publicar = MediaPlayer.create(Cadastrar_Novo_Evento.this,R.raw.som_novo_item);
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
                    dialog_music_publicar.start();
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    //caso de errado
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dialog.dismiss();
                                    dialog_music_publicar.stop();
                                    urlimg = uri.toString();

                                    Picasso.get().load(uri)
                                            .into(imagem1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    dialog.dismiss();
                                    dialog_music_publicar.stop();
                                    Toast.makeText(Cadastrar_Novo_Evento.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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

    private Evento configurarEvento() {

        String nome = dados_usuario.getString("nome", "");
        String token = dados_usuario.getString("token", "");
        String titulo = campotitulo.getText().toString();
        String descricao = campodesc.getText().toString();
        String endereco = campoendereco.getText().toString();
        evento.setCapaevento(urlimg);
        evento.setNomeauthor(nome);
        evento.setToken_author(token);
        evento.setPreco_ingresso(campovalor.getText().toString());
        evento.setGratis_pago(isChecked);
        evento.setIdauthor(identificadorUsuario);
        evento.setTitulo(titulo);
        evento.setAtivado_desativado("ativado");
        evento.setMensagem(descricao);
        evento.setEndereco(endereco);
        evento.setData_fim_min(time_Fim_InMilliseconds);
        evento.setData_fim(date_fim);
        evento.setData_inicio(date_inicio);

        return evento;
    }


    public void validarDados() {
        evento = configurarEvento();
       if(time_inicio_InMilliseconds<time_Fim_InMilliseconds) {

           if (evento.getData_inicio() != null) {
               if (evento.getCapaevento() != null) {
                   if (TextUtils.isEmpty(evento.getTitulo())) {
                       campotitulo.setError(padrao);
                       return;
                   }
                   if (TextUtils.isEmpty(evento.getMensagem())) {
                       campodesc.setError(padrao);
                       return;
                   }

                   if (TextUtils.isEmpty(evento.getEndereco())) {
                       campoendereco.setError(padrao);
                       Toast.makeText(Cadastrar_Novo_Evento.this, "Cadastre o endereço do evento", Toast.LENGTH_SHORT).show();
                       return;
                   }

                   evento.salvar(point);
                   sendNotifiaction_admin();
                   Dialog();

               } else {
                   Toast.makeText(this, "Selecione uma Foto. ", Toast.LENGTH_SHORT).show();
               }
           } else {
               Toast.makeText(this, "Cadastre uma data. ", Toast.LENGTH_SHORT).show();
           }
       }else{
           Toast.makeText(this, "A Data do inicio do evento não está correta." , Toast.LENGTH_LONG).show();
       }
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
                                //  Toast.makeText(Detalhe_topico.this, "Failed!", Toast.LENGTH_SHORT).show();
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
            case R.id.imageEventoCadastro1_evento:
                EscolherImagem(1);
                break;


        }
    }

    public void EscolherImagem(int requestCode) {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(Cadastrar_Novo_Evento.this);
        startActivityForResult(intent, SELECAO_GALERIA);
    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(Cadastrar_Novo_Evento.this, Evento_Lista.class);
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
        Intent it = new Intent(Cadastrar_Novo_Evento.this, Evento_Lista.class);
        startActivity(it);
        finish();
    }


    private void  Dialog(){
        final MediaPlayer dialog_music = MediaPlayer.create(Cadastrar_Novo_Evento.this, R.raw.navi_veja);
        AlertDialog.Builder builder = new AlertDialog.Builder(Cadastrar_Novo_Evento.this);
        LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_Evento.this);
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
                Intent it = new Intent(Cadastrar_Novo_Evento.this, Evento_Lista.class);
                startActivity(it);
                finish();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog_music.start();
    }
}





