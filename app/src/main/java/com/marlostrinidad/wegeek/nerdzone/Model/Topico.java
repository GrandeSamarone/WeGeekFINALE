package com.marlostrinidad.wegeek.nerdzone.Model;



import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Topico  implements Serializable {

    public String uid;
    public String idauthor;
    public String titulo;
    public String foto;
    public String mensagem;
    public String data;
    public int likecount = 0;
    public  int quantcomentario=0;
    public Map<String, Boolean> stars = new HashMap<>();
    String usuariologado = UsuarioFirebase.getIdentificadorUsuario();
    public Topico() {
        DatabaseReference eventoref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("topico");
        setUid(eventoref.push().getKey());  }

  public  void SalvarTopico(DataSnapshot seguidoressnapshot){

      DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase();
      Map objeto = new HashMap();
      String combinacaoId="/"+ usuariologado+"/"+getUid();

      objeto.put("/meustopicos"+combinacaoId,this);
      for(DataSnapshot Seguidores:seguidoressnapshot.getChildren()){

          String idSeguidores=Seguidores.getKey();
          HashMap<String,Object> dadosSeguidor = new HashMap<>();
          dadosSeguidor.put("idtopico",getUid());
          String IdAtualizacao="/"+ idSeguidores+"/"+getUid();

          objeto.put("/feed-topico"+IdAtualizacao,dadosSeguidor);
      }

      anuncioref.updateChildren(objeto);
      salvarTopicoPublico();
  }

    public void salvarTopicoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("topico");
        anuncioref.child(getUid()).setValue(this);
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }


    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public int getQuantcomentario() {
        return quantcomentario;
    }

    public void setQuantcomentario(int quantcomentario) {
        this.quantcomentario = quantcomentario;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
// [END post_class]
