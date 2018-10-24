package com.marlostrinidad.wegeek.nerdzone.Model;

import java.io.Serializable;

/**
 * Created by fulanoeciclano on 30/06/2018.
 */

public class Foto implements Serializable {

    String nome;
    String url;

    public  Foto(){
    }
    public Foto(String nome, String url){
        this.nome=nome;
        this.url=url;
    }
    public  Foto(String url){
        this.url=url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
