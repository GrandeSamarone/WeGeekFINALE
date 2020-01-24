package com.marlostrinidad.wegeek.nerdzone.Autenticacao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.marlostrinidad.wegeek.nerdzone.Activits.MainActivity;
import com.marlostrinidad.wegeek.nerdzone.Helper.Base64Custom;
import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.Politica_Privacidade.Politica_PrivacidadeActivity;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;

import static android.view.View.GONE;

public class LoginActivity extends TrocarFundo {

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
    Usuario usuarioLogado;
    private  MediaPlayer dialog_music;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private CollectionReference citiesRef;
    private FirebaseAuth.AuthStateListener authStateListener;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //Configuracoes Originais
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        citiesRef=db.collection("Usuarios");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textoprivacidade = findViewById(R.id.textView);
        textoprivacidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, Politica_PrivacidadeActivity.class);
                startActivity(it);
            }
        });


        botaologin = findViewById(R.id.sign_in_button);
        botaologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delaysplash();
            }
        });


            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




    }
    public void delaysplash(){
        dialog.show();
        dialog_music.start();
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                signIn();
            }
        }, 10800);
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
        Conferir(currentUser);

        Dialog();
          }

    private void Conferir(FirebaseUser currentUser) {

        if (currentUser != null) {
            //Toast.makeText(this, currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
               Intent it = new Intent(LoginActivity.this, MainActivity.class);
               startActivity(it);
               finish();
    }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        dialog.show();
        dialog_music.start();
      //  dialog.show();
        //dialog_music.start();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Erro, Verifique sua Conexão com a internet e tente Novamente.", Toast.LENGTH_LONG).show();
                Log.i("sdoaskaok", String.valueOf(e));

                dialog.dismiss();
                dialog_music.stop();


            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //  Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            //Veirica se a conta existe no Database
                            FirebaseUser user = auth.getCurrentUser();
                            VerificandoCadastro(user.getEmail(),user.getUid(),user.getDisplayName(), String.valueOf(user.getPhotoUrl()));
                            dialog.dismiss();
                            dialog_music.stop();
                        } else {
                            dialog.dismiss();
                            dialog_music.stop();
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

    public void VerificandoCadastro (final String email, final String id, final String nome, final String foto) {
        //identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Log.i("sodksokd33",email);
        identificadorUsuario= Base64Custom.codificarBase64(email);
        final DocumentReference docRef = db.collection("Usuarios").document(identificadorUsuario);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //   Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    dialog.dismiss();
                    dialog_music.stop();
                    //Usuario usuario = snapshot.toObject(Usuario.class);
                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);

                } else {
                    dialog.dismiss();
                    dialog_music.stop();
                    usuario = new Usuario();
                    usuario.setToken(FirebaseInstanceId.getInstance().getToken());
                    usuario.setId(id);
                    usuario.setNome(nome);
                    usuario.setFoto(foto);
                    usuario.setCapa("");
                    usuario.setTipoconta(email);
                    usuario.setTipousuario("usuario");
                    //   String  identificadorUsuario = Base64Custom.codificarBase64(usuario.getNome());
                    usuario.salvar();

                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TASK |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                }
            }
        });


    }


    private void Dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
       dialog_music = MediaPlayer.create(LoginActivity.this,R.raw.intro_briga);
        final View view = layoutInflater.inflate(R.layout.dialog_carregando_gif_carr_imagem, null);
        SimpleDraweeView imageViewgif = view.findViewById(R.id.gifimage_C);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri("asset:///gif_briguinha.gif")
                .setAutoPlayAnimations(true)
                .build();
        imageViewgif.setController(controller);
        builder.setView(view);

        dialog = builder.create();
    }



}