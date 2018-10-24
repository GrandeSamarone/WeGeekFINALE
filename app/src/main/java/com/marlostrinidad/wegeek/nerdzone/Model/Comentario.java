package com.marlostrinidad.wegeek.nerdzone.Model;

import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

/**
 * Created by fulanoeciclano on 20/05/2018.
 */

public class Comentario implements Serializable {
    private String idcomentario;
    private String id_postagem;
    private String id_author;
    private String text;
    private int totalcomentarios =0;
    private int quantcomentario=0;
    String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
    @JsonIgnore
    private String key;

    public Comentario() {
    }
    public  boolean salvar(){

        DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comentario-evento")
                .child(getId_postagem());

        String chave = comentarioRef.push().getKey();
        setIdcomentario(chave);
        comentarioRef.child(getIdcomentario()).setValue(this);

        return true;
}

    public  boolean salvar_Topico(){

        DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comentario-topico")
                .child(getId_postagem());

        String chave = comentarioRef.push().getKey();
        setIdcomentario(chave);
        comentarioRef.child(getIdcomentario()).setValue(this);


        return true;
    }

    private void atualizarQtdComentarioTopico(int valor) {
        DatabaseReference firebaseRef_evento = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef_evento=firebaseRef_evento
                .child("comentario-topico")
                .child("quantcomentario")
                .child(getId_postagem());

        setQuantcomentario(getQuantcomentario()+valor);
        VisuRef_evento.setValue(getTotalcomentarios());
    }


    public String getIdcomentario() {
        return idcomentario;
    }

    public void setIdcomentario(String idcomentario) {
        this.idcomentario = idcomentario;
    }

    public String getId_postagem() {
        return id_postagem;
    }

    public void setId_postagem(String id_postagem) {
        this.id_postagem = id_postagem;
    }

    public String getId_author() {
        return id_author;
    }

    public void setId_author(String id_author) {
        this.id_author = id_author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTotalcomentarios() {
        return totalcomentarios;
    }

    public void setTotalcomentarios(int totalcomentarios) {
        this.totalcomentarios = totalcomentarios;
    }

    public int getQuantcomentario() {
        return quantcomentario;
    }

    public void setQuantcomentario(int quantcomentario) {
        this.quantcomentario = quantcomentario;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
