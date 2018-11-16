package com.marlostrinidad.wegeek.nerdzone.Edit;

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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.marlostrinidad.wegeek.nerdzone.Helper.Permissoes;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Comercio;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.marlostrinidad.wegeek.nerdzone.Activits.Minhas_Publicacoes.setWindowFlag;

public class Edit_Loja_Activity extends AppCompatActivity implements View.OnClickListener{

    private static final String padrao = "Obrigatório";


    private AppCompatEditText campotitulo, campodesc, campovalor, campoendereco,campofraserapida;
    private CircleImageView imagem1_edit,imagem2_edit,imagem3_edit,imagem4_edit,imagem5_edit,imagem6_edit;
    private StorageReference storageReference;
    private TextView campoLocal, campoloja;
    private Button botaosalvar;
    private DatabaseReference database,databaseconta;
    private String identificadorUsuario,estadostring,lojastring,autorstring;
    private FirebaseAuth autenticacao;
    private Comercio comercios,comercio;
    private Usuario usuarioLogado;
    private FirebaseUser UsuarioAtual;
    private Toolbar toolbar;
    private AlertDialog dialog,dialogsalvar;
    private CircleImageView icone;
    private String id_do_usuario;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE

    };
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaFotosRecuperadas_edit = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private String[] pegandoUrl;
    private FirebaseUser usuario;
    private ChildEventListener ChildEventListenerperfil;
    private String ids;
    private String estado;
    private ChildEventListener ChildEventListenerevento;
    private DatabaseReference mDatabaseMercado,meuDatabaseMercado;
    private String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__loja_);

        toolbar = findViewById(R.id.toolbarsecundario_sem_foto);
        toolbar.setTitle(R.string.editar_comercio);
        setSupportActionBar(toolbar);

        //Configuração Basica
        campotitulo = findViewById(R.id.nome_mercado_edit);
        campodesc = findViewById(R.id.desc_mercado_edit);
        campovalor = findViewById(R.id.desc_valor_edit);
        campoendereco = findViewById(R.id.desc_endereco_edit);
        campofraserapida= findViewById(R.id.fraserapida_mercado_edit);
        campoLocal = findViewById(R.id.local_edit);
        campoloja = findViewById(R.id.loja__edit);
        imagem1_edit = findViewById(R.id.imageLojaCadastro1_edit);
        imagem1_edit.setOnClickListener(this);
        imagem2_edit =findViewById(R.id.imageLojaCadastro2_edit);
        imagem2_edit.setOnClickListener(this);
        imagem3_edit = findViewById(R.id.imageLojaCadastro3_edit);
        imagem3_edit.setOnClickListener(this);
        imagem4_edit = findViewById(R.id.imageLojaCadastro4_edit);
        imagem4_edit.setOnClickListener(this);
        imagem5_edit = findViewById(R.id.imageLojaCadastro5_edit);
        imagem5_edit.setOnClickListener(this);
        imagem6_edit = findViewById(R.id.imageLojaCadastro6_edit);
        imagem6_edit.setOnClickListener(this);

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        mDatabaseMercado = ConfiguracaoFirebase.getDatabase().getReference().child("comercio");
        meuDatabaseMercado = ConfiguracaoFirebase.getDatabase().getReference().child("meuscomercio");
        databaseconta = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        Log.i("sdsdsd",identificadorUsuario);
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        botaosalvar = findViewById(R.id.botaosalvarmercado_edit);
        botaosalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDados();
            }
        });

        /// /validar permissoes
        Permissoes.validarPermissoes(permissoes, this, 1);





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TrocarFundos_status_bar();
        CarregarDados_do_Mercado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDatabaseMercado.removeEventListener(ChildEventListenerevento);
    }

    private void CarregarDados_do_Mercado(){

        ids=getIntent().getStringExtra("id_do_mercado");
        Log.i("adsdasd",ids);
        estado= getIntent().getStringExtra("UF_do_mercado");
        categoria= getIntent().getStringExtra("CAT_do_mercado");
        ChildEventListenerevento=mDatabaseMercado.child(estado).child(categoria).orderByChild("idMercado")
                .equalTo(ids).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        comercios = dataSnapshot.getValue(Comercio.class );
                        assert comercios != null;
                        Log.i("adsdasd",comercios.getIdMercado());
                        campotitulo.setText(comercios.getTitulo());

                        campofraserapida.setText(comercios.getFraserapida());
                        campodesc.setText(comercios.getDescricao());
                        campoendereco.setText(comercios.getEndereco());
                        campoLocal.setText(comercios.getEstado());
                        campoloja.setText(comercios.getCategoria());
                        campovalor.setText(comercios.getValor());

                        listaFotosRecuperadas_edit = comercios.getFotos();
                        if (listaFotosRecuperadas_edit != null) {
                            String img1 = listaFotosRecuperadas_edit.get(0);

                            Glide.with(Edit_Loja_Activity.this)
                                    .load(img1)
                                    .into(imagem1_edit);

                           /* String img3 = urlFotos.get(2);
                            String img4 = urlFotos.get(3);
                            String img5 = urlFotos.get(4);
                            String img6 = urlFotos.get(5);

                            Glide.with(Edit_Loja_Activity.this)
                                    .load(img3)
                                    .into(imagem3);
                            Glide.with(Edit_Loja_Activity.this)
                                    .load(img3)
                                    .into(imagem3);
                            Glide.with(Edit_Loja_Activity.this)
                                    .load(img4)
                                    .into(imagem4);
                            Glide.with(Edit_Loja_Activity.this)
                                    .load(img5)
                                    .into(imagem5);
                            Glide.with(Edit_Loja_Activity.this)
                                    .load(img6)
                                    .into(imagem6);
*/
                        }
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



    public void  SalvarMercado(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(Edit_Loja_Activity.this);
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
                .child("mercado")
                .child(comercios.getIdMercado())
                .child("imagem"+contador);

        //fazer upload arquivo
        UploadTask uploadTask = imagemMercado.putFile(Uri.parse(urlimagem));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
            }
        });
    }

    public void validarDados() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(Edit_Loja_Activity.this);
        final View view  = layoutInflater.inflate(R.layout.dialog_carregando_gif_analisando,null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(this)
                .asGif()
                .load(R.drawable.gif_analizando)
                .into(imageViewgif);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
        Comercio c = new Comercio();
        c.setIdMercado(comercios.getIdMercado());
        c.setEstado(comercios.getEstado());
        c.setQuantVisualizacao(comercios.getQuantVisualizacao());
        c.setFotos(comercios.getFotos());
        c.setNumRatings(comercios.getNumRatings());
        c.setAutor(comercios.getAutor());
        c.setIdAutor(comercios.getIdAutor());
        c.setCategoria(comercios.getCategoria());
        c.setTitulo(campotitulo.getText().toString());
        c.setValor(campovalor.getText().toString());
        c.setDescricao(campodesc.getText().toString());
        c.setFraserapida(campofraserapida.getText().toString());
        c.setEndereco(campoendereco.getText().toString());
        c.setData(comercios.getData());
        mDatabaseMercado.child(estado).child(categoria).child(ids).setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        meuDatabaseMercado.child(identificadorUsuario).child(comercios.getIdMercado()).setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                Intent it = new Intent(Edit_Loja_Activity.this, Minhas_Publicacoes.class);
                startActivity(it);
                finish();
                Toast toast = Toast.makeText(Edit_Loja_Activity.this, "Atualizado com sucesso!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();   }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Intent it = new Intent(Edit_Loja_Activity.this, Minhas_Publicacoes.class);
                startActivity(it);
                finish();
                Toast toast = Toast.makeText(Edit_Loja_Activity.this, "Erro,tente novamente " +
                        "mais tarde", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });
    }



    //click na imagem para cadastra mercado
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case  R.id.imageLojaCadastro1_edit:
                Toast toast = Toast.makeText(this, "Imagens selecionadas nao podem ser " +
                        "alterada no momento.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                break;
            case  R.id.imageLojaCadastro2_edit:
                EscolherImagem(2);
                break;
            case  R.id.imageLojaCadastro3_edit:
                EscolherImagem(3);
                break;
            case  R.id.imageLojaCadastro4_edit:
                EscolherImagem(4);
                break;
            case  R.id.imageLojaCadastro5_edit:
                EscolherImagem(5);
                break;
            case  R.id.imageLojaCadastro6_edit:
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
                imagem1_edit.setImageURI(imagemSelecionada);
                imagem2_edit.setVisibility(View.VISIBLE);
            } else if(requestCode == 2){
                imagem2_edit.setImageURI(imagemSelecionada);
                imagem3_edit.setVisibility(View.VISIBLE);
            }else if(requestCode == 3){
                imagem3_edit.setImageURI(imagemSelecionada);
                imagem4_edit.setVisibility(View.VISIBLE);
            }else if(requestCode == 4){
                imagem4_edit.setImageURI(imagemSelecionada);
                imagem5_edit.setVisibility(View.VISIBLE);
            }else if(requestCode == 5){
                imagem5_edit.setImageURI(imagemSelecionada);
                imagem6_edit.setVisibility(View.VISIBLE);
            }else if(requestCode==6){
                imagem6_edit.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
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
                Intent it = new Intent(Edit_Loja_Activity.this, MinhaConta.class);
                startActivity(it);
            }
        });

        Glide.with(Edit_Loja_Activity.this)
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



}
