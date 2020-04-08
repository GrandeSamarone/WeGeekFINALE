package com.marlostrinidad.wegeek.nerdzone.Model;


import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fulanoeciclano on 14/08/2018.
 */

public class Comercio implements Serializable {

    private String id;
    private String idauthor;
    private int numRatings;
    private double totalrating=0;
    private int quantVisualizacao=0;
    private String url_img_status;
    private String nomeauthor;
    private String categoria;
    private String estado;
    private String titulo;
    private GeoPoint geoPoint;
    private String endereco;
    private String valor;
    private String nome_img;
    private String descricao;
    private String icone;
    private String token_author;
    private Boolean analizado;
    private @ServerTimestamp
    Date data;
    private @ServerTimestamp
    Date ultima_atualizacao;
    @JsonIgnore
    public ArrayList<String> status_ids;
    private Boolean url_status_boolean;
    public Comercio() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String idBefore = db.collection("Comercio").document().getId();
        setId(idBefore);
    }



    public void salvar(GeoPoint geoPoint){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Map<String, Object> newComercio = new HashMap<>();
        newComercio.put("id",getId());
        newComercio.put("titulo", getTitulo());
        newComercio.put("analizado",false);
        newComercio.put("url_status_boolean", false);
        newComercio.put("descricao", getDescricao());
        newComercio.put("endereco", getEndereco());
        newComercio.put("nome_img", getNome_img());
        newComercio.put("idauthor", getIdauthor());
        newComercio.put("geoPoint",geoPoint);
        newComercio.put("token_author", getToken_author());
        newComercio.put("icone",getIcone());
        newComercio.put("nomeauthor", getNomeauthor());
        newComercio.put("estado", getEstado());
        newComercio.put("categoria", getCategoria());
        newComercio.put("data", FieldValue.serverTimestamp());
        newComercio.put("url_img_status","");
        newComercio.put("ultima_atualizacao", FieldValue.serverTimestamp());
      //  newComercio.put("categoria_itens",FieldValue.arrayUnion(getIdauthor()));
// Add a new document with a generated ID
        db.collection("Comercio")
                .add(newComercio)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }
    public void Atualizar(GeoPoint geoPoint,String id_doc){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Map<String, Object> atualizar = new HashMap<>();
        atualizar.put("titulo", getTitulo());
        atualizar.put("descricao", getDescricao());
        atualizar.put("endereco", getEndereco());
        atualizar.put("idauthor", getIdauthor());
        atualizar.put("geoPoint",geoPoint);
        atualizar.put("token_author", getToken_author());
        atualizar.put("icone",getIcone());
        atualizar.put("analizado",false);
        atualizar.put("nomeauthor", getNomeauthor());
        atualizar.put("estado", getEstado());
        atualizar.put("categoria", getCategoria());
        atualizar.put("data", FieldValue.serverTimestamp());
        atualizar.put("ultima_atualizacao", FieldValue.serverTimestamp());
        //  newComercio.put("categoria_itens",FieldValue.arrayUnion(getIdauthor()));
// Add a new document with a generated ID
        db.collection("Comercio").document(id_doc).update(atualizar);


    }

    public ArrayList<String> getStatus_ids() {
        return status_ids;
    }

    public void setStatus_ids(ArrayList<String> status_ids) {
        this.status_ids = status_ids;
    }

    public Boolean getUrl_status_boolean() {
        return url_status_boolean;
    }

    public void setUrl_status_boolean(Boolean url_status_boolean) {
        this.url_status_boolean = url_status_boolean;
    }

    public Boolean getAnalizado() {
        return analizado;
    }

    public void setAnalizado(Boolean analizado) {
        this.analizado = analizado;
    }

    public String getNomeauthor() {
        return nomeauthor;
    }

    public String getNome_img() {
        return nome_img;
    }

    public void setNome_img(String nome_img) {
        this.nome_img = nome_img;
    }

    public void setNomeauthor(String nomeauthor) {
        this.nomeauthor = nomeauthor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getUltima_atualizacao() {
        return ultima_atualizacao;
    }

    public void setUltima_atualizacao(Date ultima_atualizacao) {
        this.ultima_atualizacao = ultima_atualizacao;
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
    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public String getUrl_img_status() {
        return url_img_status;
    }

    public void setUrl_img_status(String url_img_status) {
        this.url_img_status = url_img_status;
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

    public String getIdauthor() {
        return idauthor;
    }

    public void setIdauthor(String idauthor) {
        this.idauthor = idauthor;
    }

    public int getQuantVisualizacao() {
        return quantVisualizacao;
    }

    public void setQuantVisualizacao(int quantVisualizacao) {
        this.quantVisualizacao = quantVisualizacao;
    }

    public String getToken_author() {
        return token_author;
    }

    public void setToken_author(String token_author) {
        this.token_author = token_author;
    }


    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }
}
