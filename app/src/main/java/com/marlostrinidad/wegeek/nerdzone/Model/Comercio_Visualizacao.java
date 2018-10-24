package com.marlostrinidad.wegeek.nerdzone.Model;

import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Comercio_Visualizacao {
    public Comercio comercio;
    public int qtdvisualizacao=0;

    public Comercio_Visualizacao() {
    }
    public Comercio getComercio() {
        return comercio;
    }

    public void setComercio(Comercio comercio) {
        this.comercio = comercio;
    }

    public int getQtdvisualizacao() {
        return qtdvisualizacao;
    }

    public void setQtdvisualizacao(int qtdvisualizacao) {
        this.qtdvisualizacao = qtdvisualizacao;
    }
    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference VisuRef=firebaseRef
                .child("comercio-visualizacao");
        VisuRef.child(comercio.getIdMercado());


        //Atualizar quantidade de like
      atualizar_comercios_publicos(1);
        atualizar_meus_comercios(1);
        atualizarQtd(1);

    }

    private void atualizarQtd(int valor) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef=firebaseRef
                .child("comercio-visualizacao")
                .child(comercio.getIdMercado())
                .child("qtdvisualizacao");

        setQtdvisualizacao(getQtdvisualizacao()+valor);
        VisuRef.setValue(getQtdvisualizacao());
    }


    public void atualizar_meus_comercios(int valor){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference firebaseRef_comercio = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef_comercio=firebaseRef_comercio
                .child("meuscomercio")
                 .child(idUsuario)
                .child(comercio.getIdMercado())
                .child("quantVisualizacao");
        VisuRef_comercio.setValue(getQtdvisualizacao());

    }
    public void atualizar_comercios_publicos(int valor){
        DatabaseReference firebaseRef_comercio = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef_comercio=firebaseRef_comercio
                .child("comercio")
             .child(comercio.getEstado())
                .child(comercio.getCategoria())
                .child(comercio.getIdMercado())
                   .child("quantVisualizacao");
        VisuRef_comercio.setValue(getQtdvisualizacao());
    }




}
