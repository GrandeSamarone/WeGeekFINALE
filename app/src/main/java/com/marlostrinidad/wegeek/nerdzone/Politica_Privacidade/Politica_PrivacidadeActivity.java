package com.marlostrinidad.wegeek.nerdzone.Politica_Privacidade;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.marlostrinidad.wegeek.nerdzone.Helper.TrocarFundo;
import com.marlostrinidad.wegeek.nerdzone.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class Politica_PrivacidadeActivity extends TrocarFundo {

    private  WebView webView;
    private Toolbar toolbar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica__privacidade);

        toolbar = findViewById(R.id.toolbarsecundario);
        toolbar.setTitle(R.string.politicaa_privacidade);
        setSupportActionBar(toolbar);


        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void init(){
        webView = findViewById(R.id.webview);

        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        if (netInfo != null) {
            if (netInfo.isConnected()) {
                webView.loadUrl("file:///android_asset/privacidade.html");

                // webView.loadUrl("https://www.iubenda.com/privacy-policy/50217024");
            }else {
                webView.loadUrl("file:///android_asset/privacidade.html");
            }
        } else {
            webView.loadUrl("file:///android_asset/privacidade.html");
        }




        webView.requestFocus();

        progressDialog = new ProgressDialog(Politica_PrivacidadeActivity.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        init();
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




}
