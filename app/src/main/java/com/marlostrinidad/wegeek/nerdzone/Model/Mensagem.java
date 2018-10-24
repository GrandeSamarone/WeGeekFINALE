package com.marlostrinidad.wegeek.nerdzone.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by fulanoeciclano on 10/05/2018.
 */

public class Mensagem {
    private String idUsuario;
    private String nome;
    private String Img_Usuario;
    private String mensagem;
    private String tempo;
    private String imagem;
    @JsonIgnore
    private String key;

    public Mensagem() {

        this.setNome("");
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getImg_Usuario() {
        return Img_Usuario;
    }

    public void setImg_Usuario(String img_Usuario) {
        Img_Usuario = img_Usuario;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
}
