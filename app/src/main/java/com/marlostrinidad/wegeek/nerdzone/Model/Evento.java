package com.marlostrinidad.wegeek.nerdzone.Model;


import com.marlostrinidad.wegeek.nerdzone.Config.ConfiguracaoFirebase;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Evento implements Serializable {

    private String uid;
    private String author;
    private String titulo;
    private String subtitulo;
    private String imgperfilusuario;
    private String idUsuario;
    private String capaevento;
    private List<String> fotoseventos;
    private String mensagem;
    private String datainicio;
    private String datafim;
    private String estado;
    private int curtirCount = 0;
    private int quantVisualizacao=0;


    String usuariologado = UsuarioFirebase.getIdentificadorUsuario();

    public Evento() {
        DatabaseReference eventoref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("evento");
        setUid(eventoref.push().getKey());
    }


    public void salvar(DataSnapshot seguidoressnapshot){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase();
        Map objeto = new HashMap();
        String combinacaoId="/"+ usuariologado+"/"+getUid();

        objeto.put("/meusevento"+combinacaoId,this);
        for(DataSnapshot Seguidores:seguidoressnapshot.getChildren()){
            String idSeguidores=Seguidores.getKey();
            HashMap<String,Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("idevento",getUid());
            String IdAtualizacao="/"+ idSeguidores+"/"+getUid();

            objeto.put("/feed-evento"+IdAtualizacao,dadosSeguidor);
        }

        anuncioref.updateChildren(objeto);
          salvarEventoPublico();
    }

    public void salvarEventoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("evento");
        anuncioref.child(getEstado())
                .child(getUid()).setValue(this);
    }

    public void remover(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("meusevento")
                .child(identificadorUsuario)
                .child(getUid());

        anuncioref.removeValue();

        removerEventoPublico();
        deletar_img_eventos();
    }

    public void removerEventoPublico(){
        DatabaseReference anuncioref = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("evento")
                .child(getEstado())
                .child(getUid());

        anuncioref.removeValue();

    }
    public void deletar_img_eventos(){
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage()
                .child("imagens")
                .child("evento")
                .child(identificadorUsuario)
                .child(getUid());

        storageReference.delete();
    }

    /*public Evento(String uid, String author, String imgperfilusuario,String fotoevento, String titulo,
                  String subtitulo,
                  String mensagem,String datafim,String datainicio,String estado) {
        this.uid = uid;
        this.author = author;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.mensagem = mensagem;
        this.imgperfilusuario= imgperfilusuario;
        this.datainicio = datainicio;
        this.datafim= datafim;
        this.estado= estado;
        this.fotoevento = fotoevento;
    }
*/
    // [START post_to_map]
    /*@Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("imgperfilusuario", imgperfilusuario);
        result.put("fotoevento", fotoevento);
        result.put("titulo", titulo);
        result.put("subtitulo",subtitulo);
        result.put("mensagem", mensagem);
        result.put("datainicio",datainicio);
        result.put("datafim", datafim);
        result.put("estado", estado);
        result.put("starCount", curtirCount);
        result.put("stars", curtida);

        return result;
    }
    */
    // [END post_to_map]


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getImgperfilusuario() {
        return imgperfilusuario;
    }

    public void setImgperfilusuario(String imgperfilusuario) {
        this.imgperfilusuario = imgperfilusuario;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCapaevento() {
        return capaevento;
    }

    public void setCapaevento(String capaevento) {
        this.capaevento = capaevento;
    }

    public List<String> getFotoseventos() {
        return fotoseventos;
    }

    public void setFotoseventos(List<String> fotoseventos) {
        this.fotoseventos = fotoseventos;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getDatainicio() {
        return datainicio;
    }

    public void setDatainicio(String datainicio) {
        this.datainicio = datainicio;
    }

    public String getDatafim() {
        return datafim;
    }

    public void setDatafim(String datafim) {
        this.datafim = datafim;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCurtirCount() {
        return curtirCount;
    }

    public void setCurtirCount(int curtirCount) {
        this.curtirCount = curtirCount;
    }

    public int getQuantVisualizacao() {
        return quantVisualizacao;
    }

    public void setQuantVisualizacao(int quantVisualizacao) {
        this.quantVisualizacao = quantVisualizacao;
    }
}

