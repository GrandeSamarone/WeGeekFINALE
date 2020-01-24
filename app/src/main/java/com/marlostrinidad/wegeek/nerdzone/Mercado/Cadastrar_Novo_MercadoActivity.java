package com.marlostrinidad.wegeek.nerdzone.Mercado;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.GeoLocation;
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Localizacao;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Cadastrar_Novo_MercadoActivity extends TrocarFundo implements View.OnClickListener {

    private static final String padrao = "Obrigatório";
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private AppCompatEditText campotitulo, campodesc, campoendereco;
    private CircleImageView imagem1;
    private StorageReference storageReference;
    private Button botaosalvar;
    private String identificadorUsuario, estadostring, lojastring,nomeImagem;
    private Comercio comercio = new Comercio();
    private Toolbar toolbar;
    private AlertDialog dialog;
    private CircleImageView icone;
    private TextView textToolbar;
    private LatLng lat_long;
    private GeoPoint point;
    private GeoLocation geoLocation;
    private String urlimg;
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
        setContentView(R.layout.activity_cadastrar__novo__mercado);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle("");
        textToolbar = findViewById(R.id.app_toolbar_title_secundario);
        textToolbar.setText("Novo Comércio");
        setSupportActionBar(toolbar);

        //Configuração Basica
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        campotitulo = findViewById(R.id.nome_mercado);
        campodesc = findViewById(R.id.desc_mercado);

        campoloja = findViewById(R.id.spinnerloja);
        campoendereco = findViewById(R.id.desc_endereco);


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
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment_mercado);

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


        imagem1 = findViewById(R.id.imageLojaCadastro1);
        imagem1.setOnClickListener(this);


        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        botaosalvar = findViewById(R.id.botaosalvarmercado);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validarDados();
            }
        });

        /// /validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);

        //carregar SPINNER
        CarregarDadosSpinner();

        // Preferences pega o nome do usuario;
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    private void sendNotifiaction( ) {
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
    private void buildAlertMessageNoGps() {
            final MediaPlayer dialog_music = MediaPlayer.create(Cadastrar_Novo_MercadoActivity.this,R.raw.navi_ei);
            dialog_music.start();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_MercadoActivity.this);
        final View view = layoutInflater.inflate(R.layout.dialog_com_sim_nao, null);
        TextView mensagem=view.findViewById(R.id.texto_dialog_sim_nao);
        mensagem.setText("Para criar um novo comércio \n o GPS precisa tá ativado, \n deseja ativar agora?");
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
                    dialog_music_add= MediaPlayer.create(Cadastrar_Novo_MercadoActivity.this,R.raw.som_novo_item);
                    LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_MercadoActivity.this);
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
                                    Toast.makeText(Cadastrar_Novo_MercadoActivity.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();

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

    private Comercio configurarMercado() {

        String nome = dados_usuario.getString("nome", "");
        String token = dados_usuario.getString("token", "");
        String titulo = campotitulo.getText().toString();
        String descricao = campodesc.getText().toString();
        String endereco = campoendereco.getText().toString();
        String loja = campoloja.getSelectedItem().toString();
        comercio.setIcone(urlimg);
        comercio.setEstado("AM");
        comercio.setNome_img(nomeImagem);
        comercio.setNomeauthor(nome);
        comercio.setToken_author(token);
        comercio.setIdauthor(identificadorUsuario);
        comercio.setTitulo(titulo);
        comercio.setCategoria(loja);
        comercio.setDescricao(descricao);
        comercio.setEndereco(endereco);

        return comercio;
    }

    boolean validateSpinner(Spinner spinner, String error) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            TextView selectedTextView = (TextView) selectedView;
            if (selectedTextView.getText().equals("Categoria")) {
                selectedTextView.setError(error);
                return false;
            }
        }
        return true;
    }

    public void validarDados() {
        comercio = configurarMercado();
        if ((!lojastring.equals("Categoria"))) {
            if (comercio.getIcone() != null) {
                if (TextUtils.isEmpty(comercio.getTitulo())) {
                    campotitulo.setError(padrao);
                    return;
                }
                if (TextUtils.isEmpty(comercio.getDescricao())) {
                    campodesc.setError(padrao);
                    return;
                }

                if (TextUtils.isEmpty(comercio.getEndereco())) {
                    campoendereco.setError(padrao);
                    Toast.makeText(Cadastrar_Novo_MercadoActivity.this, "Ative o GPS Para cadastrar um endereço.", Toast.LENGTH_SHORT).show();
                    return;
                }
                comercio.salvar(point);
              //avisar o adm
                sendNotifiaction();

                Dialog();
            } else {
                Toast.makeText(this, "Selecione uma Foto. ", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecione uma Categoria. ", Toast.LENGTH_SHORT).show();
        }
    }
    private void  Dialog(){
        final MediaPlayer dialog_music = MediaPlayer.create(Cadastrar_Novo_MercadoActivity.this, R.raw.navi_veja);
        AlertDialog.Builder builder = new AlertDialog.Builder(Cadastrar_Novo_MercadoActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_MercadoActivity.this);
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
                Intent it = new Intent(Cadastrar_Novo_MercadoActivity.this, Detalhe_Loja.class);
                it.putExtra("id", comercio.getId());
                it.putExtra("categoria", comercio.getCategoria());
                it.putExtra("icone_loja", comercio.getIcone());
                startActivity(it);
                finish();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog_music.start();
    }

    //click na imagem para cadastra comercio
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imageLojaCadastro1:
                EscolherImagem(1);
                break;


        }
    }

    public void EscolherImagem(int requestCode) {
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(Cadastrar_Novo_MercadoActivity.this);
        startActivityForResult(intent, SELECAO_GALERIA);
    }


    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(Cadastrar_Novo_MercadoActivity.this, MercadoActivity.class);
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


    //carregar spinner
    private void CarregarDadosSpinner() {

        //spinner categoria
        String[] loja = getResources().getStringArray(R.array.loja);
        ArrayAdapter<String> adaptercat = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loja);
        adaptercat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoloja.setAdapter(adaptercat);

        campoloja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lojastring = parent.getItemAtPosition(position).toString();
                validateSpinner(campoloja, "Erro");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it = new Intent(Cadastrar_Novo_MercadoActivity.this, MercadoActivity.class);
        startActivity(it);
        finish();
    }
}
