package com.marlostrinidad.wegeek.nerdzone.Model;


import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;

import java.util.HashMap;

public class RatingBar_Comercio {
    private Comercio comercio;
    private Usuario usuario;
    private Float rating;
    private int quantrating=0;

    public RatingBar_Comercio() {
    }

    public Comercio getComercio() {
        return comercio;
    }

    public void setComercio(Comercio comercio) {
        this.comercio = comercio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public int getQuantrating() {
        return quantrating;
    }

    public void setQuantrating(int quantrating) {
        this.quantrating = quantrating;
    }

    public void SalvarRating(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        HashMap<String,Object> dadosusuario = new HashMap<>();
        dadosusuario.put("nomeUsuario",usuario.getNome());
        dadosusuario.put("foto",usuario.getFoto());

        DatabaseReference pLikeRef=firebaseRef
                .child("ratingbar-comercio")
                .child(comercio.getIdMercado())
                .child(usuario.getId());
        pLikeRef.setValue(dadosusuario);

        //Atualizar quantidade de rating


    }


}
