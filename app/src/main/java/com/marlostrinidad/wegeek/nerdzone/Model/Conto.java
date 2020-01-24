package com.marlostrinidad.wegeek.nerdzone.Model;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Conto   implements Serializable {

    private String id;
    private String idauthor;
    private String nomeauthor;
    private String titulo;
    private String mensagem;
    private String data;
    private int likecount = 0;
    private int quantcolecao=0;


    public Conto() {

    }


    public  void SalvarConto(){
           // String IdConto= Base64Custom.codificarBase64("danlelis");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newConto = new HashMap<>();
        newConto.put("id",getId());
        newConto.put("titulo", getTitulo());
        newConto.put("mensagem", getMensagem());
        newConto.put("idauthor", getIdauthor());
        newConto.put("nomeauthor", getNomeauthor());
        newConto.put("data",getData());

// Add a new document with a generated ID
            db.collection("Conto")
                    //.document(getIdauthor())
                    //.collection("Historias")
                    //.document("sdsdsdsdsdsdwsd")
                    .add(newConto)
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

    public void salvarContoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto");
        anuncioref.child(getId()).setValue(this);
    }
    public  void AdicioneiConto(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("adicionei-conto");
        anuncioref.child(identificadorUsuario)
                .child(getId()).setValue(this);

        salvarContoPublico();
    }

    public void remover(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meusconto")
                .child(identificadorUsuario)
                .child(getId());

        anuncioref.removeValue();
     removerContocolecao();
     removerContoLike();
        removerContoPublico();
    }

    public void removerContoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto")
                .child(getId());

        anuncioref.removeValue();

    }
    public void removerContoLike(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto-likes")
                .child(getId());

        anuncioref.removeValue();

    }
    public void removerContocolecao(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto-colecao")
                .child(getId());

        anuncioref.removeValue();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdauthor() {
        return idauthor;
    }

    public void setIdauthor(String idauthor) {
        this.idauthor = idauthor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public int getQuantcolecao() {
        return quantcolecao;
    }

    public void setQuantcolecao(int quantcolecao) {
        this.quantcolecao = quantcolecao;
    }

    public String getNomeauthor() {
        return nomeauthor;
    }

    public void setNomeauthor(String nomeauthor) {
        this.nomeauthor = nomeauthor;
    }
}
