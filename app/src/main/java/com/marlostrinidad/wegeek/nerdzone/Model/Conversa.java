package com.marlostrinidad.wegeek.nerdzone.Model;


import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by fulanoeciclano on 16/06/2018.
 */

public class Conversa {
    private String idRemetente;
    private String idDestinatario;
    private String ultimaMensagem;
    private Usuario usuarioExibicao;
    private String isGroup;
    private Grupo grupo;
    @JsonIgnore
    private String key;


    public Conversa(){
        this.setIsGroup("false");

    }
    public void salvar(){
        DatabaseReference databaseReference= ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference conversaRef= databaseReference.child("conversas");
        conversaRef.child(this.getIdRemetente())
                .child(this.getIdDestinatario())
                .setValue(this);

    }


    public void removerConversa(){

        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conversas")
                .child(getIdRemetente())
                .child(getIdDestinatario());

        anuncioref.removeValue();

        removerMensagens();

    }

    public void removerMensagens(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("mensagens")
                .child(getIdRemetente())
                .child(getIdDestinatario());

        anuncioref.removeValue();

    }
    public String getIdRemetente() {
        return idRemetente;
    }

    public void setIdRemetente(String idRemetente) {
        this.idRemetente = idRemetente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibicao() {
        return usuarioExibicao;
    }

    public void setUsuarioExibicao(Usuario usuarioExibicao) {
        this.usuarioExibicao = usuarioExibicao;
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
 /*
   public void setValues(Conversa conversa) {
        ultimaMensagem=conversa.ultimaMensagem;
        usuarioExibicao=conversa.usuarioExibicao;
    }
    */


}
