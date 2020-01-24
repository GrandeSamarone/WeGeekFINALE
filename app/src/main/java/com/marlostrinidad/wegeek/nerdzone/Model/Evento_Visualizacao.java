package com.marlostrinidad.wegeek.nerdzone.Model;

import com.google.firebase.database.DatabaseReference;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

public class Evento_Visualizacao {
    public Evento evento;
    public int qtdvisualizacao=0;
    String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
    public Evento_Visualizacao() {
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public int getQtdvisualizacao() {
        return qtdvisualizacao;
    }

    public void setQtdvisualizacao(int qtdvisualizacao) {
        this.qtdvisualizacao = qtdvisualizacao;
    }

    public void Salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference VisuRef=firebaseRef
                .child("evento-visualizacao");
                VisuRef.child(evento.getId());


        //Atualizar quantidade de like
        atualizarQtd(1);
        atualizarQtdEvento();
        atualizarQtdMeusEvento();

    }

    private void atualizarQtd(int valor) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef=firebaseRef
                .child("evento-visualizacao")
                .child(evento.getId())
                .child("qtdvisualizacao");

        setQtdvisualizacao(getQtdvisualizacao()+valor);
        VisuRef.setValue(getQtdvisualizacao());
    }

    private void atualizarQtdEvento() {
        DatabaseReference firebaseRef_evento = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef_evento=firebaseRef_evento
                .child("evento")
                .child(evento.getEstado())
                .child(evento.getId())
                .child("quantVisualizacao");

        VisuRef_evento.setValue(getQtdvisualizacao());
    }

    private void atualizarQtdMeusEvento() {
        DatabaseReference firebaseRef_evento = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference VisuRef_evento=firebaseRef_evento
                .child("meusevento")
                .child(evento.getIdauthor())
                .child(evento.getId())
                .child("quantVisualizacao");

        VisuRef_evento.setValue(getQtdvisualizacao());
    }
}
