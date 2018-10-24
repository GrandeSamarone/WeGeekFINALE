package com.marlostrinidad.wegeek.nerdzone.Autenticacao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.Politica_Privacidade.Politica_PrivacidadeActivity;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private TextView textoprivacidade;
    private com.shobhitpuri.custombuttons.GoogleSignInButton botaologin;
    private Usuario usuario;
    private AlertDialog dialog;
    private SharedPreferences sPreferences = null;
    private String identificadorUsuario;
    DatabaseReference usuarioLogadoRef,database_perfil;
    Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         //fundo
        TrocarFundos_status_bar();

        //Configuracoes Originais
        textoprivacidade = findViewById(R.id.textView);
        textoprivacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, Politica_PrivacidadeActivity.class);
                startActivity(it);
            }
        });
        auth = FirebaseAuth.getInstance();
        user =auth.getCurrentUser();
        database_perfil = ConfiguracaoFirebase.getDatabase().getReference().child("usuarios");
        // Check if user is signed in (non-null) and update UI accordingly.

        botaologin = findViewById(R.id.sign_in_button);

        botaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // SharedPreferences
        sPreferences = getSharedPreferences("firstRun", MODE_PRIVATE);



        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser==null){
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        }else{
            Intent it = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
        final View view  = layoutInflater.inflate(R.layout.dialog_carregando_gif_comscroop,null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.gif_briguinha)
                .into(imageViewgif);
        builder.setView(view);
        dialog = builder.create();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


        dialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialog.dismiss();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            dialog.dismiss();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Erro, Verifique sua Conexão com a internet e tente Novamente.", Toast.LENGTH_LONG).show();
                Log.i("sdoaskaok", String.valueOf(e));


                // ...
            }
        }
    }
    private void TrocarFundos_status_bar(){
        //mudando a cor do statusbar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_login);
        }
        if (Build.VERSION.SDK_INT <= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setStatusBarTintEnabled(true);
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_login);
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
            systemBarTintManager.setStatusBarTintResource(R.drawable.gradiente_login);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
      //  Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
        final View view  = layoutInflater.inflate(R.layout.dialog_carregando_gif_comscroop,null);
        ImageView imageViewgif = view.findViewById(R.id.gifimage);

        Glide.with(getApplicationContext())
                .asGif()
                .load(R.drawable.gif_briguinha)
                .into(imageViewgif);
        builder.setView(view);

        dialog = builder.create();

        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            //Veirica se a conta existe no Database
                            VerificandoCadastro();

                        } else {
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    "Tente Novamente mais tarde", Toast.LENGTH_LONG).show();
                            // If sign in fails, display a message to the user.
                          /* Toast.makeText(GoogleSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           */
                        }

                        // ...
                    }
                });
    }

    public void VerificandoCadastro(){
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        final FirebaseUser user = auth.getCurrentUser();
        database_perfil.child(identificadorUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            dialog.dismiss();
                            Intent its = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(its);
                            finish();

                        }else{
                            dialog.dismiss();
                            usuario = new Usuario();
                            usuario.setId(identificadorUsuario);
                            usuario.setNome(user.getDisplayName());
                            usuario.setFoto(String.valueOf(user.getPhotoUrl()));
                            usuario.setCapa("");
                            usuario.setSeguidores(usuario.getSeguidores());
                            usuario.setSeguindo(usuario.getSeguindo());
                            usuario.setContos(usuario.getContos());
                            usuario.setTopicos(usuario.getTopicos());
                            usuario.setArts(usuario.getArts());
                            usuario.setComercio(usuario.getComercio());
                            usuario.setEvento(usuario.getEvento());
                            usuario.setTipoconta(user.getEmail());
                            usuario.setTiposuario("usuario");
                            //   String  identificadorUsuario = Base64Custom.codificarBase64(usuario.getNome());
                            usuario.setId(identificadorUsuario);
                            usuario.salvar();

                            Intent it = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(it);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void Verificar(){

        if (sPreferences.getBoolean("firstRun", true)) {
            sPreferences.edit().putBoolean("firstRun", false).apply();
            /*Intent it = new Intent(LoginActivity.this,Cadastrar_icon_nome_Activity.class);
            startActivity(it);*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
            }else{

            }
        } else {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
            }else{
                finish();
            }

        }
    }


}