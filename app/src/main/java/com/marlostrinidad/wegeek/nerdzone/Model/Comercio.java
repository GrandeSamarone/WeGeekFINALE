package com.marlostrinidad.wegeek.nerdzone.Model;


import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fulanoeciclano on 14/08/2018.
 */

public class Comercio implements Serializable {

    private String idMercado;
    private String idAutor;
    private int numRatings;
    private double totalrating;
    private int quantVisualizacao=0;
    private String autor;
    private String categoria;
    private String estado;
    private String titulo;
    private String endereco;
    private String valor;
    private String descricao;
    private String fraserapida;
    private String data;
    private List<String> fotos;
    private String icone;
    @JsonIgnore
    private String key;
    public Comercio() {
        DatabaseReference mercadoref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comercio");
        setIdMercado(mercadoref.push().getKey());
    }



    public void salvar(DataSnapshot seguidoressnapshot){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase();
        Map objeto = new HashMap();
        String combinacaoId="/"+ identificadorUsuario+"/"+getIdMercado();

        objeto.put("/meuscomercio"+combinacaoId,this);
        for(DataSnapshot Seguidores:seguidoressnapshot.getChildren()){
            String idSeguidores=Seguidores.getKey();
            HashMap<String,Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("idmercado",getIdMercado());
            String IdAtualizacao="/"+ idSeguidores+"/"+getIdMercado();

            objeto.put("/feed-comercio"+IdAtualizacao,dadosSeguidor);
        }

        anuncioref.updateChildren(objeto);
       salvarMercadoPublico();
    }
    public void salvarMercadoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comercio");
        anuncioref.child(getEstado())
                .child(getCategoria())
                .child(getIdMercado()).setValue(this);
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIdMercado() {
        return idMercado;
    }

    public void setIdMercado(String idMercado) {
        this.idMercado = idMercado;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFraserapida() {
        return fraserapida;
    }

    public void setFraserapida(String fraserapida) {
        this.fraserapida = fraserapida;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValues(Comercio comercio) {
        titulo= comercio.titulo;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getTotalrating() {
        return totalrating;
    }

    public void setTotalrating(double totalrating) {
        this.totalrating = totalrating;
    }

    public String getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(String idAutor) {
        this.idAutor = idAutor;
    }

    public int getQuantVisualizacao() {
        return quantVisualizacao;
    }

    public void setQuantVisualizacao(int quantVisualizacao) {
        this.quantVisualizacao = quantVisualizacao;
    }
}
