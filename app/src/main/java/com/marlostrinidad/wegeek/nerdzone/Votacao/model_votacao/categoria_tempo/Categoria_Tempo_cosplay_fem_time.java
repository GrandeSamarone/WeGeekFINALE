package com.marlostrinidad.wegeek.nerdzone.Votacao.model_votacao.categoria_tempo;

import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

public class Categoria_Tempo_cosplay_fem_time {

    private String id_categoria;
    private String id_usuario;
    private String tempodata;
    private Long tempo_milisigundos;

    public Categoria_Tempo_cosplay_fem_time(){

    }

    public void SalvarTempo(){
        String  identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("votacao")
                .child("categorias");
        anuncioref.child("cosplay_fem_TIME")
                .child(identificadorUsuario).setValue(this);
    }

    public String getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(String id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getTempodata() {
        return tempodata;
    }

    public void setTempodata(String tempodata) {
        this.tempodata = tempodata;
    }

    public Long getTempo_milisigundos() {
        return tempo_milisigundos;
    }

    public void setTempo_milisigundos(Long tempo_milisigundos) {
        this.tempo_milisigundos = tempo_milisigundos;
    }
}

