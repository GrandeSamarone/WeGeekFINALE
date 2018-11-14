package com.marlostrinidad.wegeek.nerdzone.Mercado;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.marlostrinidad.wegeek.nerdzone.Activits.Minhas_Publicacoes.setWindowFlag;

public class Cadastrar_Novo_MercadoActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String padrao = "Obrigatório";


    private AppCompatEditText campotitulo, campodesc, campoendereco,campofraserapida;
    private CurrencyEditText campovalor;
    private CircleImageView imagem1,imagem2,imagem3,imagem4,imagem5,imagem6;
    private StorageReference storageReference;
    private Spinner campoLocal, campoloja;
    private Button botaosalvar;
    private DatabaseReference database,databaseconta,SeguidoresRef;
    private DataSnapshot seguidoresSnapshot;
    private String identificadorUsuario,estadostring,lojastring,autorstring;
    private FirebaseAuth autenticacao;
    private Comercio comercio;
    private Usuario usuarioLogado;
    private FirebaseUser UsuarioAtual;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private CircleImageView icone;
    private String id_do_usuario;



    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE

    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private String[] pegandoUrl;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private ChildEventListener ChildEventListenerSeguidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar__novo__mercado);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle(R.string.novocomercio);
        setSupportActionBar(toolbar);

        //Configuração Basica
        campotitulo = findViewById(R.id.nome_mercado);
        campodesc = findViewById(R.id.desc_mercado);
        campovalor = findViewById(R.id.desc_valor);
        //configurar  localidade para pt
        Locale locale = new Locale("pt","BR");
        campovalor.setLocale(locale);
        campoendereco = findViewById(R.id.desc_endereco);
        campofraserapida= findViewById(R.id.fraserapida_mercado);
        campoloja = findViewById(R.id.spinnerloja);
        campoLocal = findViewById(R.id.spinneralocal);
        imagem1 = findViewById(R.id.imageLojaCadastro1);
        imagem1.setOnClickListener(this);
        imagem2 =findViewById(R.id.imageLojaCadastro2);
        imagem2.setOnClickListener(this);
        imagem3 = findViewById(R.id.imageLojaCadastro3);
        imagem3.setOnClickListener(this);
        imagem4 = findViewById(R.id.imageLojaCadastro4);
        imagem4.setOnClickListener(this);
        imagem5 = findViewById(R.id.imageLojaCadastro5);
        imagem5.setOnClickListener(this);
        imagem6 = findViewById(R.id.imageLojaCadastro6);
        imagem6.setOnClickListener(this);

        comercio = new Comercio();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        database = ConfiguracaoFirebase.getDatabase().getReference().child("comercio");
        SeguidoresRef =ConfiguracaoFirebase.getDatabase().getReference().child("seguidores");
        databaseconta = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
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



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TrocarFundos_status_bar();

        CarregarDados_do_Usuario();
        CarregarSeguidores();
    }


    public void  SalvarMercado(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(Cadastrar_Novo_MercadoActivity.this);
        final View view  = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando,null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_analizando)
                .into(imageViewgif);
        builder.setView(view);

        dialog = builder.create();
        dialog.show();
        /*salva no storage*/
        for(int i = 0; i< listaFotosRecuperadas.size();i++){
            String urlimagem = listaFotosRecuperadas.get(i);
            int tamanholista = listaFotosRecuperadas.size();

            SalvarFotoStorage(urlimagem,tamanholista,i);
        }
    }

    private void SalvarFotoStorage(String urlimagem, final int totalfotos, int contador) {
        //criar nó no storage
        StorageReference imagemMercado = storageReference
                .child("imagens")
                .child("comercio")
                .child(comercio.getIdMercado())
                .child("imagem"+contador);

        //fazer upload arquivo
        UploadTask uploadTask = imagemMercado.putFile(Uri.parse(urlimagem));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urlConvertida =uri.toString();

                        listaURLFotos.add(urlConvertida);

                        if(totalfotos==listaURLFotos.size()){
                            comercio.setFotos(listaURLFotos);
                            comercio.salvar(seguidoresSnapshot);
                            dialog.dismiss();
                            Intent it = new Intent(Cadastrar_Novo_MercadoActivity.this,MercadoActivity.class);
                           startActivity(it);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismiss();
                        Toast.makeText(Cadastrar_Novo_MercadoActivity.this, "Erro ao cadastrar", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }
    private void CarregarDados_do_Usuario(){
        usuario = UsuarioFirebase.getUsuarioAtual();
        String email = usuario.getEmail();
        ChildEventListenerperfil=databaseconta.orderByChild("tipoconta").equalTo(email).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Usuario perfil = dataSnapshot.getValue(Usuario.class );
                assert perfil != null;

                id_do_usuario = perfil.getId();
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
    private Comercio configurarMercado(){
        String estado = campoLocal.getSelectedItem().toString();
        String loja = campoloja.getSelectedItem().toString();
        String titulo = campotitulo.getText().toString();
        String valor = campovalor.getText().toString();
        String descricao = campodesc.getText().toString();
        String fraserapida = campofraserapida.getText().toString();
        String endereco = campoendereco.getText().toString();
        String autor = usuarioLogado.getNome();
        final Calendar calendartempo = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'-'MM'-'y",java.util.Locale.getDefault());// MM'/'dd'/'y;
        String data = simpleDateFormat.format(calendartempo.getTime());


        comercio.setEstado(estado);
        comercio.setAutor(autor);
        comercio.setIdAutor(id_do_usuario);
        comercio.setCategoria(loja);
        comercio.setTitulo(titulo);
        comercio.setValor(valor);
        comercio.setDescricao(descricao);
        comercio.setFraserapida(fraserapida);
        comercio.setEndereco(endereco);
        comercio.setData(data);

        return comercio;
    }

    public void validarDados() {
        comercio = configurarMercado();
        if (listaFotosRecuperadas.size() != 0) {
            if ( (!lojastring.equals("Categoria"))) {

           if ( (!estadostring.equals("Estado"))) {
               // verificando se  está vazio
               if (TextUtils.isEmpty(comercio.getTitulo())) {
                   campotitulo.setError(padrao);
                   return;
               }
               if (TextUtils.isEmpty(comercio.getFraserapida())) {
                   campofraserapida.setError(padrao);
                   return;
               }
               if (TextUtils.isEmpty(comercio.getDescricao())) {
                   campodesc.setError(padrao);
                   return;
               }

               if (TextUtils.isEmpty(comercio.getEndereco())) {
                   campoendereco.setError(padrao);
                   return;
               }
               if (TextUtils.isEmpty(comercio.getValor())) {
                   campovalor.setError(padrao);
                   return;
               }
               SalvarMercado();
           }else{
               Toast toast = Toast.makeText(this, "Selecione um Estado", Toast.LENGTH_SHORT);
               toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
               toast.show();
           }

           }else {
               Toast toast= Toast.makeText(this, "Selecione uma Categoria", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        } else {
                Toast toast=Toast.makeText(this, "Selecione ao menos uma foto", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            }


        }



    //click na imagem para cadastra comercio
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.imageLojaCadastro1:
                EscolherImagem(1);
                break;
            case  R.id.imageLojaCadastro2:
                EscolherImagem(2);
                break;
            case  R.id.imageLojaCadastro3:
                EscolherImagem(3);
                break;
            case  R.id.imageLojaCadastro4:
                EscolherImagem(4);
                break;
            case  R.id.imageLojaCadastro5:
                EscolherImagem(5);
                break;
            case  R.id.imageLojaCadastro6:
                EscolherImagem(6);
                break;


        }
    }

    public  void EscolherImagem(int requestCode){
        Intent intent = CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(this);
        startActivityForResult(intent,requestCode );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            CropImage.ActivityResult resultCAMERA = CropImage.getActivityResult(data);
            Uri imagemSelecionada = resultCAMERA.getUri();
            String caminhoImagem = imagemSelecionada.toString();
            // Configurar imagem  no ImageView

             if(requestCode == 1){
                imagem1.setImageURI(imagemSelecionada);
              imagem2.setVisibility(View.VISIBLE);
            } else if(requestCode == 2){
                imagem2.setImageURI(imagemSelecionada);
                imagem3.setVisibility(View.VISIBLE);
            }else if(requestCode == 3){
                imagem3.setImageURI(imagemSelecionada);
                imagem4.setVisibility(View.VISIBLE);
            }else if(requestCode == 4){
                imagem4.setImageURI(imagemSelecionada);
                imagem5.setVisibility(View.VISIBLE);
            }else if(requestCode == 5){
                imagem5.setImageURI(imagemSelecionada);
                imagem6.setVisibility(View.VISIBLE);
            }else if(requestCode==6){
                imagem6.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }



    //Botao Voltar
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent it = new Intent(Cadastrar_Novo_MercadoActivity.this, MercadoActivity.class);
                startActivity(it);

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

        for(int permissaoResultado:grantResults){
            if(permissaoResultado== PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
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
    private void IconeUsuario(String url) {
        //Imagem do icone do usuario
        icone = findViewById(R.id.icone_user_toolbar);
        icone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Cadastrar_Novo_MercadoActivity.this, MinhaConta.class);
                startActivity(it);
            }
        });

        Glide.with(Cadastrar_Novo_MercadoActivity.this)
                .load(url)
                .into(icone);
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

    //carregar spinner
    private void CarregarDadosSpinner() {
        //
        String[] artista = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, artista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoLocal.setAdapter(adapter);
        campoLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                estadostring = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner categoria
        String[] loja = getResources().getStringArray(R.array.loja);
        ArrayAdapter<String> adaptercat = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loja);
        adaptercat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoloja.setAdapter(adaptercat);

        campoloja.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lojastring = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
