package com.marlostrinidad.wegeek.nerdzone.Model;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fulanoeciclano on 21/05/2018.
 */

public class Usuario implements Serializable {

    private String id;
    private String token;
    private String tipoconta;
   private String online;
    private String nome;
    private String frase;
    private String foto;
    private Boolean digitando;
    private String Capa;
    private List<String> conversar;
    private List<String> comercio;
    private String tipousuario;


    public Usuario() {
    }
    public void salvar(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newUsuario = new HashMap<>();
        newUsuario.put("id",identificadorUsuario);
        newUsuario.put("token",getToken());
        newUsuario.put("nome", getNome());
        newUsuario.put("frase", getFrase());
        newUsuario.put("foto", getFoto());
        newUsuario.put("online", "offline");
        newUsuario.put("conversas",getConversar());
        newUsuario.put("comercio",getComercio());
        newUsuario.put("tipoconta",getTipoconta());
        newUsuario.put("tipousuario","usuario");

// Add a new document with a generated ID
        db.collection("Usuarios")
                .document(identificadorUsuario)
                .set(newUsuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    public List<String> getComercio() {
        return comercio;
    }

    public void setComercio(List<String> comercio) {
        this.comercio = comercio;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public List<String> getConversar() {
        return conversar;
    }

    public void setConversar(List<String> conversar) {
        this.conversar = conversar;
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

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCapa() {
        return Capa;
    }

    public void setCapa(String capa) {
        Capa = capa;
    }

    public String getTipousuario() {
        return tipousuario;
    }

    public void setTipousuario(String tipousuario) {
        this.tipousuario = tipousuario;
    }

    public String getTipoconta() {
        return tipoconta;
    }

    public void setTipoconta(String tipoconta) {
        this.tipoconta = tipoconta;
    }




    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
