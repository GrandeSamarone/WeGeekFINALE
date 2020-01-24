package com.marlostrinidad.wegeek.nerdzone.Model;

import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;

import java.util.HashMap;

public class TopicoLike {

    private Forum forum;
    private Usuario usuario;
    private int qtdlikes =0;

    public TopicoLike(){

    }

    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        HashMap<String,Object> dadosusuario = new HashMap<>();
        dadosusuario.put("nomeUsuario",usuario.getNome());
        dadosusuario.put("foto",usuario.getFoto());

        DatabaseReference pLikeRef=firebaseRef
                .child("forum-likes")
                .child(forum.getId())
                .child(usuario.getId());
        pLikeRef.setValue(dadosusuario);

        //Atualizar quantidade de like
        atualizarQtd(1);

    }

    private void atualizarQtd(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("forum-likes")
                .child(forum.getId())
                .child("qtdlikes");

        setQtdlikes(getQtdlikes()+valor);
        pLikeRef.setValue(getQtdlikes());
        atualizarQtd_Evento();
        atualizarQtd_MeusEvento();

    }
    public   void removerlike(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("forum-likes")
                .child(forum.getId())
                .child(usuario.getId());
        pLikeRef.removeValue();
        //Atualizar quantidade de like
        atualizarQtd(-1);
        atualizarQtd_Evento();
        atualizarQtd_MeusEvento();


    }


    private void atualizarQtd_Evento(){
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("forum")
                .child(forum.getId())
                .child("likecount");

        pLikeQuantRef.setValue(getQtdlikes());
    }
    private void atualizarQtd_MeusEvento(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("meustopicos")
                .child(forum.getIdauthor())
                .child(forum.getId())
                .child("likecount");

        pLikeQuantRef.setValue(getQtdlikes());
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
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
