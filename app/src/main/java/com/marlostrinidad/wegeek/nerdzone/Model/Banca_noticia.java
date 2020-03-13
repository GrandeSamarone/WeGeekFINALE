package com.marlostrinidad.wegeek.nerdzone.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Banca_noticia {

    private String id;
    private String url_img_status;
    private String nome_banca;
    private String desc_banca;
    private String icone_banca;
    private String id_autor;
    private String nome_autor;
    private String token_autor;
    private String foto_autor;
    private Boolean analizado;
    private @ServerTimestamp
    Date data;
    private @ServerTimestamp
    Date ultima_atualizacao,ultima_foto_data;


    public Banca_noticia(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String idBefore = db.collection("Banca_Noticia").document().getId();
        setId(idBefore);
    }

    public void salvar(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newBanca = new HashMap<>();
        newBanca.put("id",getId());
        newBanca.put("nome_banca", getNome_banca());
        newBanca.put("ultima_foto_data", FieldValue.serverTimestamp());
        newBanca.put("url_img_status", "");
        newBanca.put("analizado", false);
        newBanca.put("desc_banca", getDesc_banca());
        newBanca.put("icone_banca", getIcone_banca());
        newBanca.put("id_autor", getId_autor());
        newBanca.put("nome_autor", getNome_autor());
        newBanca.put("token_autor",getToken_autor());
        newBanca.put("data", FieldValue.serverTimestamp());
        newBanca.put("ultima_atualizacao", FieldValue.serverTimestamp());
        //  newComercio.put("categoria_itens",FieldValue.arrayUnion(getIdauthor()));
// Add a new document with a generated ID
        db.collection("Banca_Noticia")
                .add(newBanca)
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
    public void Atualizar(String id_doc){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> Atualizar_banca = new HashMap<>();
        Atualizar_banca.put("nome_banca", getNome_banca());
        Atualizar_banca.put("analizado", false);
        Atualizar_banca.put("desc_banca", getDesc_banca());
        Atualizar_banca.put("icone_banca", getIcone_banca());
        Atualizar_banca.put("data", FieldValue.serverTimestamp());
        Atualizar_banca.put("ultima_atualizacao", FieldValue.serverTimestamp());
        //  newComercio.put("categoria_itens",FieldValue.arrayUnion(getIdauthor()));
// Add a new document with a generated ID
        db.collection("Banca_Noticia")
                .document(id_doc).update(Atualizar_banca);
    }

    public Boolean getAnalizado() {
        return analizado;
    }

    public void setAnalizado(Boolean analizado) {
        this.analizado = analizado;
    }

    public Date getUltima_foto_data() {
        return ultima_foto_data;
    }

    public void setUltima_foto_data(Date ultima_foto_data) {
        this.ultima_foto_data = ultima_foto_data;
    }

    public String getUrl_img_status() {
        return url_img_status;
    }

    public void setUrl_img_status(String url_img_status) {
        this.url_img_status = url_img_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome_banca() {
        return nome_banca;
    }

    public void setNome_banca(String nome_banca) {
        this.nome_banca = nome_banca;
    }

    public String getDesc_banca() {
        return desc_banca;
    }

    public void setDesc_banca(String desc_banca) {
        this.desc_banca = desc_banca;
    }

    public String getIcone_banca() {
        return icone_banca;
    }

    public void setIcone_banca(String icone_banca) {
        this.icone_banca = icone_banca;
    }

    public String getId_autor() {
        return id_autor;
    }

    public void setId_autor(String id_autor) {
        this.id_autor = id_autor;
    }

    public String getNome_autor() {
        return nome_autor;
    }

    public void setNome_autor(String nome_autor) {
        this.nome_autor = nome_autor;
    }

    public String getToken_autor() {
        return token_autor;
    }

    public void setToken_autor(String token_autor) {
        this.token_autor = token_autor;
    }

    public String getFoto_autor() {
        return foto_autor;
    }

    public void setFoto_autor(String foto_autor) {
        this.foto_autor = foto_autor;
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
}

