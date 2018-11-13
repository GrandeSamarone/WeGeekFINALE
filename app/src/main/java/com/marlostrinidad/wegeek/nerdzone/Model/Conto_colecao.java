package com.marlostrinidad.wegeek.nerdzone.Model;

import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

import java.util.HashMap;

public class Conto_colecao {
    private Conto conto;
    private Usuario usuario;
    private Boolean adicionado;
    private int qtdadd=0;

    public Conto_colecao(){

    }
    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        HashMap<String,Object> dadosusuario = new HashMap<>();
        dadosusuario.put("id",usuario.getId());

        DatabaseReference pLikeRef=firebaseRef
                .child("conto-colecao")
                .child(conto.getUid())
                .child(usuario.getId());
        pLikeRef.setValue(dadosusuario);

        //Atualizar quantidade de like
        atualizarQtd(1);


    }


    private void atualizarQtd(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("conto-colecao")
                .child(conto.getUid())
                .child("qtdadd");

        setQtdadd(getQtdadd()+valor);
        pLikeRef.setValue(getQtdadd());
      //  atualizarQtd_Colecao();
        atualizarQtd_MeusColecoes();

    }

    private void atualizarQtd_Colecao(){
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("conto")
                .child(conto.getUid())
                .child("quantcolecao");
        pLikeQuantRef.setValue(getQtdadd());
    }
    private void atualizarQtd_MeusColecoes(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("meusconto")
                .child(conto.getIdauthor())
                .child(conto.getUid())
                .child("quantcolecao");

        pLikeQuantRef.setValue(getQtdadd());
    }


    public   void removercolecao(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("conto-colecao")
                .child(conto.getUid())
                .child(usuario.getId());
        pLikeRef.removeValue();
        //Atualizar quantidade de like
        atualizarQtd(-1);
        remover_adicionei_colecao();
        atualizarQtd_MeusColecoes();
        atualizarQtd_MeusColecoes();
    }
    public   void remover_adicionei_colecao(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("adicionei-conto")
                .child(identificadorUsuario)
                .child(conto.getUid());
        pLikeRef.removeValue();

    }




    public int getQtdadd() {
        return qtdadd;
    }

    public void setQtdadd(int qtdadd) {
        this.qtdadd = qtdadd;
    }

    public Conto getConto() {
        return conto;
    }

    public void setConto(Conto conto) {
        this.conto = conto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getAdicionado() {
        return adicionado;
    }

    public void setAdicionado(Boolean adicionado) {
        this.adicionado = adicionado;
    }
}
