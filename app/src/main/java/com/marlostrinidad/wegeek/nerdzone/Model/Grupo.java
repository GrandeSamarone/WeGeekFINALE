package com.marlostrinidad.wegeek.nerdzone.Model;


import android.util.Log;

import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fulanoeciclano on 14/05/2018.
 */

public class Grupo implements Serializable {
    private String id;
    private String nome;
    private String foto;

    private List<Usuario> membros;

    public Grupo() {
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference gruporef = database.child("grupos");

        String idGrupoFirebase =gruporef.push().getKey();
        setId(idGrupoFirebase);
    }
    public void salvar(){
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference gruporef = database.child("grupos");

        gruporef.child(getId()).setValue(this);

        //Salvar conversa para membros do grupo
        for(Usuario membro:getMembros()){

            String idRemetente= Base64Custom.codificarBase64(membro.getTipoconta());
            Log.i("contaRemetente",idRemetente);
            String idDestinatario =getId();

           Conversa conversa = new Conversa();
           conversa.setIdRemetente(idRemetente);
           conversa.setIdDestinatario(idDestinatario);
           conversa.setUltimaMensagem("");
           conversa.setIsGroup("true");
           conversa.setGrupo(this);

           conversa.salvar();
        }
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }


}
