package com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem;

import android.app.ProgressDialog;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.marlostrinidad.wegeek.nerdzone.Adapter.Adapter_mostra_HQ;
import com.marlostrinidad.wegeek.nerdzone.Adapter.MyViewPagerAdapter;
import com.marlostrinidad.wegeek.nerdzone.Model.Foto;
import com.marlostrinidad.wegeek.nerdzone.Model.HQ;
import com.marlostrinidad.wegeek.nerdzone.R;


import java.util.ArrayList;

public class AbrirImagemHQ extends AppCompatActivity {


    private FirebaseFirestore db;
    private ArrayList<HQ> list_hq =new ArrayList<>() ;
    private Adapter_mostra_HQ adapter;
    private ListenerRegistration registration_hq,registration_hq_id;
    private ArrayList<Foto> FotoList;
    private MyViewPagerAdapter myViewPagerAdapter;
    private RequestQueue mRequestQueue;
    private ViewPager viewPager;
    private ProgressDialog pDialog;
    private AlertDialog alerta;
    private String id_HQ;
    private TextView tantasPaginas,nomeGibi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
/*
        if (android.os.Build.VERSION.SDK_INT > 11 && android.os.Build.VERSION.SDK_INT < 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        } else if (android.os.Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
*/
        setContentView(R.layout.activity_abrir_imagem_hq);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        db = FirebaseFirestore.getInstance();
        id_HQ = getIntent().getStringExtra("id_hq");
        if(!id_HQ.equals("")){
            RecuperarDados_HQ(id_HQ);
        }

        pDialog = new ProgressDialog(this);
        tantasPaginas = findViewById(R.id.lbl_count);
        nomeGibi = findViewById(R.id.lbl_nome);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (registration_hq!= null) {
            registration_hq.remove();
            registration_hq = null;
        }
        if (registration_hq_id!= null) {
            registration_hq_id.remove();
            registration_hq_id = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (registration_hq!= null) {
            registration_hq.remove();
            registration_hq = null;
        }
        if (registration_hq_id!= null) {
            registration_hq_id.remove();
            registration_hq_id = null;
        }
    }

    private int prox(int i) {
        return viewPager.getCurrentItem() + i;
    }
    private int voltar(int i) {
        return viewPager.getCurrentItem()-1;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    finish();
                    return true;
                case R.id.navigation_zoom:
                   Zoom();
                    return true;
                case R.id.navigation_voltar:
                    viewPager.setCurrentItem(voltar(-1),true);
                    return true;
                case R.id.navigation_prox:
                    viewPager.setCurrentItem(prox(+1),true);
                    return true;
            }
            return false;
        }
    };

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position)
        {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private void displayMetaInfo(int position) {
        tantasPaginas.setText((position + 1) + " de " + list_hq.size());


        HQ image = list_hq.get(position);
        nomeGibi.setText(image.getTitulo());

    }
    public void RecuperarDados_HQ(String id){

        Query query= db.collection("HQ").whereEqualTo("id",id);
        registration_hq_id=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    HQ HQ = change.getDocument().toObject( HQ.class);
                    switch (change.getType()) {
                        case ADDED:
                         String id_selecionado= change.getDocument().getId();
                            Recuperar_HQ(id_selecionado);
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
            }
        });

    }

    private void Recuperar_HQ(String id_HQ){
        list_hq.clear();
        Query query= db.collection("HQ")
                .document(id_HQ)
                .collection("Imagens")
                .orderBy("pos", Query.Direction.ASCENDING);
        registration_hq=query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("", "listen:error", e);
                    return;
                }

                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Log.i("sdsdsd",change.getDocument().getId());
                    HQ hq_model = change.getDocument().toObject(HQ.class);
                    //  Log.i("sdsdsd",change.getDocument().getId());
                    // Log.i("sdsdsd2",conto.getUid());
                    switch (change.getType()) {
                        case ADDED:
                           list_hq.add( hq_model);
                            myViewPagerAdapter = new MyViewPagerAdapter(AbrirImagemHQ.this,list_hq);
                            viewPager.setAdapter(myViewPagerAdapter);
                            if (list_hq.size() > 0) {

                            }
                           // adapter.notifyDataSetChanged();
                            Log.d("ad", "New city: " + change.getDocument().getData());
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }

            }
        });

    }
    //dialog de opcoes
    private void Zoom() {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout tela_opcao_foto.xml_foto.xml na view
        View view = li.inflate(R.layout.dialog_zoom, null);
        //definimos para o botão do layout um clickListener



        //Dialog de tela
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }
}