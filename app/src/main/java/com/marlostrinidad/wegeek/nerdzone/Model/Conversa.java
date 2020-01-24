package com.marlostrinidad.wegeek.nerdzone.Model;


import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fulanoeciclano on 16/06/2018.
 */

public class Conversa {
    private String id;
    private String id_remetente;
    private String foto_loja;
    private String id_criador_loja;
    private String nome_loja;
    private @ServerTimestamp
    Date tempo;
    private String id_destinatario;
    private String ultimamensagem;
    private String nome_remetente;
    private String nome_destinatario;
    private String token_destinatario;
    private String icone_remetente;
    private String icone_destinatario;
    private Boolean vizu;

    public Conversa(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String idBefore = db.collection("Chat").document().getId();
        setId(idBefore);

    }
    public void salvar(){ }

    public Boolean getVizu() {
        return vizu;
    }

    public void setVizu(Boolean vizu) {
        this.vizu = vizu;
    }

    public String getId_remetente() {
        return id_remetente;
    }

    public void setId_remetente(String id_remetente) {
        this.id_remetente = id_remetente;
    }

    public String getId_destinatario() {
        return id_destinatario;
    }

    public void setId_destinatario(String id_destinatario) {
        this.id_destinatario = id_destinatario;
    }

    public String getUltimamensagem() {
        return ultimamensagem;
    }

    public void setUltimamensagem(String ultimamensagem) {
        this.ultimamensagem = ultimamensagem;
    }

    public String getIcone_remetente() {
        return icone_remetente;
    }

    public void setIcone_remetente(String icone_remetente) {
        this.icone_remetente = icone_remetente;
    }

    public String getIcone_destinatario() {
        return icone_destinatario;
    }

    public void setIcone_destinatario(String icone_destinatario) {
        this.icone_destinatario = icone_destinatario;
    }

    public String getNome_remetente() {
        return nome_remetente;
    }

    public void setNome_remetente(String nome_remetente) {
        this.nome_remetente = nome_remetente;
    }

    public String getNome_destinatario() {
        return nome_destinatario;
    }

    public void setNome_destinatario(String nome_destinatario) {
        this.nome_destinatario = nome_destinatario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoto_loja() {
        return foto_loja;
    }

    public void setFoto_loja(String foto_loja) {
        this.foto_loja = foto_loja;
    }

    public String getId_criador_loja() {
        return id_criador_loja;
    }

    public void setId_criador_loja(String id_criador_loja) {
        this.id_criador_loja = id_criador_loja;
    }

    public String getNome_loja() {
        return nome_loja;
    }

    public void setNome_loja(String nome_loja) {
        this.nome_loja = nome_loja;
    }

    public String getToken_destinatario() {
        return token_destinatario;
    }

    public void setToken_destinatario(String token_destinatario) {
        this.token_destinatario = token_destinatario;
    }

    public Date getTempo() {
        return tempo;
    }

    public void setTempo(Date tempo) {
        this.tempo = tempo;
    }


}
