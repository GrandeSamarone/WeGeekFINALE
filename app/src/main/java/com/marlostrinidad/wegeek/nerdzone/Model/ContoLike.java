package com.marlostrinidad.wegeek.nerdzone.Model;

import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class ContoLike {

    private Conto conto;
    private Usuario usuario;
    private int qtdlikes =0;

    public ContoLike(){

    }


    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        HashMap<String,Object> dadosusuario = new HashMap<>();
        dadosusuario.put("nomeUsuario",usuario.getNome());
        dadosusuario.put("foto",usuario.getFoto());

        DatabaseReference pLikeRef=firebaseRef
                .child("conto-likes")
                .child(conto.getUid())
                .child(usuario.getId());
        pLikeRef.setValue(dadosusuario);

        //Atualizar quantidade de like
        atualizarQtd(1);

    }

    private void atualizarQtd(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("conto-likes")
                .child(conto.getUid())
                .child("qtdlikes");

        setQtdlikes(getQtdlikes()+valor);
        pLikeRef.setValue(getQtdlikes());
        atualizarQtd_Conto();
        atualizarQtd_MeusConto();

    }
    public   void removerlike(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("conto-likes")
                .child(conto.getUid())
                .child(usuario.getId());
        pLikeRef.removeValue();
        //Atualizar quantidade de like
        atualizarQtd(-1);
        atualizarQtd_Conto();
        atualizarQtd_MeusConto();


    }


    private void atualizarQtd_Conto(){
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("conto")
                .child(conto.getUid())
                .child("likecount");

        pLikeQuantRef.setValue(getQtdlikes());
    }
    private void atualizarQtd_MeusConto(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("meusconto")
                .child(identificadorUsuario)
                .child(conto.getUid())
                .child("likecount");

        pLikeQuantRef.setValue(getQtdlikes());
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

    public int getQtdlikes() {
        return qtdlikes;
    }

    public void setQtdlikes(int qtdlikes) {
        this.qtdlikes = qtdlikes;
    }
}
