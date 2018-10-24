package com.marlostrinidad.wegeek.nerdzone.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * Created by fulanoeciclano on 19/05/2018.
 */

public class Gibi  implements Serializable {

    private String id;
    private String nome;
    private String url;
    private String descricao;
    private String saga;
    private String iconegibi;
   @JsonIgnore
    private String key;

    public Gibi() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIconegibi() {
        return iconegibi;
    }

    public void setIconegibi(String iconegibi) {
        this.iconegibi = iconegibi;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSaga() {
        return saga;
    }

    public void setSaga(String saga) {
        this.saga = saga;
    }

    public void setValues(Gibi gibi) {
        nome=gibi.nome;
        iconegibi=gibi.iconegibi;
    }
}
