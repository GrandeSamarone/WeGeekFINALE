package com.marlostrinidad.wegeek.nerdzone.Abrir_Imagem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.marlostrinidad.wegeek.nerdzone.Adapter.MyViewPagerAdapter;
import com.marlostrinidad.wegeek.nerdzone.Model.Foto;
import com.marlostrinidad.wegeek.nerdzone.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AbrirImagemHQ extends AppCompatActivity {
    private ArrayList<Foto> FotoList;
    private MyViewPagerAdapter myViewPagerAdapter;
    private RequestQueue mRequestQueue;
    private ViewPager viewPager;
    private ProgressDialog pDialog;
    private AlertDialog alerta;
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

        FotoList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);



        pDialog = new ProgressDialog(this);
        tantasPaginas = findViewById(R.id.lbl_count);
        nomeGibi = findViewById(R.id.lbl_nome);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        mRequestQueue = Volley.newRequestQueue(this);
        parseJSON();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



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
                case R.id.navigation_finish:
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
        tantasPaginas.setText((position + 1) + " de " + FotoList.size());


        Foto image = FotoList.get(position);
        nomeGibi.setText(image.getNome());

    }

    private void parseJSON() {
        pDialog.setMessage("Carregando...");
        pDialog.show();
        String url = "http://149.56.182.39/MARVEL/hulk-ocaido/hulkocaido.json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("HQ");
                            pDialog.hide();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);


                                String imageNome = hit.getString("nome");
                                String imageUrl = hit.getString("url");


                                //Pega o nome
                              /*  toolbar.setTitle(imageNome);
                                toolbar.setSubtitle("Paginas:"+"01"+" de "+FotoList.size());*/

                                Log.i("textoposicao", String.valueOf(hit));

                                FotoList.add(new Foto(imageNome,imageUrl));
                            }

                            myViewPagerAdapter = new MyViewPagerAdapter(AbrirImagemHQ.this,FotoList);
                            viewPager.setAdapter(myViewPagerAdapter);
                            /// / mExampleAdapter = new ExampleAdapter(MainActivity.this, FotoList);
                            //mRecyclerView.setAdapter(mExampleAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.hide();
            }
        });

        mRequestQueue.add(request);
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
