package com.marlostrinidad.wegeek.nerdzone.Model;


import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

import java.util.HashMap;

public class EventoLike {

    private Evento evento;
    private Usuario usuario;
    private int qtdlikes = 0;
    String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
    public EventoLike() {

    }

    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        HashMap<String,Object> dadosusuario = new HashMap<>();
        dadosusuario.put("nomeUsuario",usuario.getNome());
        dadosusuario.put("foto",usuario.getFoto());

        DatabaseReference pLikeRef=firebaseRef
                .child("evento-likes")
                .child(evento.getUid())
                .child(usuario.getId());
        pLikeRef.setValue(dadosusuario);

        //Atualizar quantidade de like
        atualizarQtd(1);

    }

    private void atualizarQtd(int valor){
          DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
          DatabaseReference pLikeRef=firebaseRef
                  .child("evento-likes")
                  .child(evento.getUid())
                  .child("qtdlikes");

          setQtdlikes(getQtdlikes()+valor);
          pLikeRef.setValue(getQtdlikes());
        atualizarQtd_Evento();
        atualizarQtd_MeusEvento();

    }
    public   void removerlike(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeRef=firebaseRef
                .child("evento-likes")
                .child(evento.getUid())
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
                .child("evento")
                .child(evento.getEstado())
                .child(evento.getUid())
                .child("curtirCount");

        pLikeQuantRef.setValue(getQtdlikes());
    }
    private void atualizarQtd_MeusEvento(){
        DatabaseReference firebaseRefs = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pLikeQuantRef=firebaseRefs
                .child("meusevento")
                .child(evento.getIdUsuario())
                .child(evento.getUid())
                .child("curtirCount");

        pLikeQuantRef.setValue(getQtdlikes());
    }

    public int getQtdlikes() {
        return qtdlikes;
    }

    public void setQtdlikes(int qtdlikes) {
        this.qtdlikes = qtdlikes;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
