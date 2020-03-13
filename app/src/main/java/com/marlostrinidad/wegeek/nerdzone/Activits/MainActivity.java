package com.marlostrinidad.wegeek.nerdzone.Activits;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.messaging.FirebaseMessaging;
import com.marlostrinidad.wegeek.nerdzone.Fragments.Chat_Fragment;
import com.marlostrinidad.wegeek.nerdzone.Fragments.MainActivityFragment;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.marlostrinidad.wegeek.nerdzone.MinhaConta.MinhaConta;
import com.marlostrinidad.wegeek.nerdzone.Model.Usuario;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.marlostrinidad.wegeek.nerdzone.secao_Adm.Main_adm_Activity;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sPreferences = null;
    private FirebaseFirestore db;
    private String identificadorUsuario;
    private DocumentReference docRef;
    private FloatingActionButton secao_adm;
    private static final String ARQUIVO_PREFERENCIA = "arquivoreferencia";
    private Toast toast = null;
    int counter=0;
    private SharedPreferences dados_usuario;
    BottomNavigationView bottom_navigation;
    Fragment selectedfragment = null;
    private ListenerRegistration registration_user;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //Configuraçoes Originais
        dados_usuario = getSharedPreferences(ARQUIVO_PREFERENCIA, MODE_PRIVATE);
        String tipo_user=dados_usuario.getString("tipo_usuario", "");
        // viewPager=findViewById(R.id.view_pager);
        db = FirebaseFirestore.getInstance();


        secao_adm=findViewById(R.id.buton_secao_adm);
        if(tipo_user.equals("admin")){
            secao_adm.setVisibility(View.VISIBLE);
            secao_adm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(MainActivity.this, Main_adm_Activity.class);
                    startActivity(it);
                }
            });

            FirebaseMessaging.getInstance().subscribeToTopic("WeGeeK_Admin")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //  Toast.makeText(MainActivity.this, "dey certi", Toast.LENGTH_SHORT).show();
                            if (!task.isSuccessful()) {
                            }

                        }
                    });

        }else{
            secao_adm.setVisibility(View.GONE);
        }

        //escondendo navegação
        //  CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
        //         navigation.getLayoutParams();
        // layoutParams.setBehavior(new BottomNavigationBehavior());




    }


    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(authStateListener);
        //atualiza as informacoes
        SharedPreference();
        //  ViewPage();
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameContaner_Principal,
                new MainActivityFragment()).commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            selectedfragment = new MainActivityFragment();
                            break;
                        case R.id.navigation_chat_pessoal:
                            selectedfragment = new Chat_Fragment();
                            break;
                        case R.id.navigation_perfil:
                            Intent it = new Intent(getApplicationContext(), MinhaConta.class);
                            startActivity(it);

                            // selectedfragment = new Minha_Conta_Fragment();
                            break;

                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameContaner_Principal,
                                selectedfragment).commit();
                    }

                    return true;
                }
            };
    //Pegar dados do usuario do firebase e guardar local
    private void SharedPreference() {
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        docRef = db.collection("Usuarios").document(identificadorUsuario);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    SharedPreferences sharedPreferences = getSharedPreferences(ARQUIVO_PREFERENCIA, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("nome", usuario.getNome());
                    editor.putString("foto_usuario", usuario.getFoto());
                    editor.putString("token", usuario.getToken());
                    editor.putString("tipo_usuario", usuario.getTipousuario());

                    //  Toast.makeText(MainActivity.this, usuario.getNome() + " pegou"+ usuario.getTipousuario(), Toast.LENGTH_SHORT).show();
                    editor.apply();
                }
            }
        });

    }


    @Override
    protected void onPause() {
        killToast();
        super.onPause();
    }

    //Fechar o app quando o usuario aperta 2 vezes o botao voltar
    @Override
    public void onBackPressed() {
        counter+=1;
        showToast("Press Novamente para sair");
        if(counter==2){
            this.finish();
        }
    }



    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }

    private void killToast() {
        if (this.toast != null) {
            this.toast.cancel();
        }
    }



}


