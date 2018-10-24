package com.marlostrinidad.wegeek.nerdzone.Model;

import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Conto   implements Serializable {

    private String uid;
    private String idauthor;
    private String titulo;
    private String mensagem;
    private String data;
    private int likecount = 0;
    private int quantcolecao=0;


    public Conto() {
        DatabaseReference eventoref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto");
        setUid(eventoref.push().getKey());  }

    public  void SalvarConto(DataSnapshot seguidoressnapshot){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        Map objeto = new HashMap();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase();
        String combinacaoId="/"+ getIdauthor()+"/"+getUid();

       objeto.put("/meusconto"+combinacaoId,this);

      for(DataSnapshot Seguidores:seguidoressnapshot.getChildren()){

          String idSeguidores=Seguidores.getKey();
          HashMap<String,Object> dadosSeguidor = new HashMap<>();
          dadosSeguidor.put("idconto",getUid());
          String IdAtualizacao="/"+ idSeguidores+"/"+getUid();

          objeto.put("/feed-conto"+IdAtualizacao,dadosSeguidor);
      }

         anuncioref.updateChildren(objeto);

         salvarContoPublico();


    }

    public void salvarContoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("conto");
        anuncioref.child(getUid()).setValue(this);
    }
    public  void AdicioneiConto(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("adicionei-conto");
        anuncioref.child(identificadorUsuario)
                .child(getUid()).setValue(this);

        salvarContoPublico();
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
