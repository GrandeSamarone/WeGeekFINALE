package com.marlostrinidad.wegeek.nerdzone.Model;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.storage.StorageReference;
import com.marlostrinidad.wegeek.nerdzone.Helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Evento implements Serializable {

    private String id;
    private String nomeauthor;
    private String titulo;
    private String subtitulo;
    private String idauthor;
    private String capaevento;
    private String mensagem;
    private String entrada;
    private String ultima_foto_data;
    private String preco_ingresso;
    private String token_author;
    private Boolean analizado;
    private @ServerTimestamp
    Date data_inicio;
    private @ServerTimestamp
    Date data_fim;
    private String ativado_desativado;
    private String gratis_pago;
    private GeoPoint geoPoint;
    private String endereco;
    private String estado;
    private int curtirCount = 0;
    private int quantVisualizacao=0;
    private String url_img_status;
    private long data_fim_min;
    private long data_inicio_min;



    public Evento() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String idBefore = db.collection("Evento").document().getId();
        setId(idBefore);
    }


    public void salvar(GeoPoint geoPoint){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        Map<String, Object> newEvento = new HashMap<>();
        newEvento.put("id",getId());
        newEvento.put("analizado",false);
        newEvento.put("titulo", getTitulo());
        newEvento.put("gratis_pago", getGratis_pago());
        newEvento.put("mensagem", getMensagem());
        newEvento.put("entrada",getEntrada());
        newEvento.put("preco_ingresso",getPreco_ingresso());
        newEvento.put("endereco", getEndereco());
        newEvento.put("token_author", getToken_author());
        newEvento.put("estado", "AM");
        newEvento.put("geoPoint",geoPoint);
        newEvento.put("ativado_desativado",getAtivado_desativado());
        newEvento.put("capaevento",getCapaevento());
        newEvento.put("nomeauthor", getNomeauthor());
        newEvento.put("idauthor", getIdauthor());
        newEvento.put("data_inicio", getData_inicio());
        newEvento.put("data_fim", getData_fim());
        newEvento.put("data_fim_min", getData_fim_min());
        newEvento.put("url_img_status", "");
        newEvento.put("ultima_atualizacao", FieldValue.serverTimestamp());
        //  newComercio.put("categoria_itens",FieldValue.arrayUnion(getIdauthor()));
// Add a new document with a generated ID
        db.collection("Evento")
                .add( newEvento)
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

        Map<String, Object> atualizar_Evento = new HashMap<>();
        atualizar_Evento.put("titulo", getTitulo());
        atualizar_Evento.put("gratis_pago", getGratis_pago());
        atualizar_Evento.put("mensagem", getMensagem());
        atualizar_Evento.put("entrada",getEntrada());
        atualizar_Evento.put("analizado",false);
        atualizar_Evento.put("preco_ingresso",getPreco_ingresso());
        atualizar_Evento.put("endereco", getEndereco());
        atualizar_Evento.put("token_author", getToken_author());
        atualizar_Evento.put("estado", "AM");
        atualizar_Evento.put("geoPoint",geoPoint);
        atualizar_Evento.put("ativado_desativado",getAtivado_desativado());
        atualizar_Evento.put("capaevento",getCapaevento());
        atualizar_Evento.put("nomeauthor", getNomeauthor());
        atualizar_Evento.put("idauthor", getIdauthor());
        atualizar_Evento.put("data_inicio", getData_inicio());
        atualizar_Evento.put("data_fim", getData_fim());
        atualizar_Evento.put("data_fim_min", getData_fim_min());
        atualizar_Evento.put("url_img_status", "");
        atualizar_Evento.put("ultima_atualizacao", FieldValue.serverTimestamp());
        //  newComercio.put("categoria_itens",FieldValue.arrayUnion(getIdauthor()));
// Add a new document with a generated ID
        db.collection("Evento")
               .document(id_doc).update(atualizar_Evento);

    }

    public Boolean getAnalizado() {
        return analizado;
    }

    public void setAnalizado(Boolean analizado) {
        this.analizado = analizado;
    }

    public String getUrl_img_status() {
        return url_img_status;
    }

    public void setUrl_img_status(String url_img_status) {
        this.url_img_status = url_img_status;
    }

    public String getEntrada() {
        return entrada;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public String getPreco_ingresso() {
        return preco_ingresso;
    }

    public String getGratis_pago() {
        return gratis_pago;
    }

    public void setGratis_pago(String gratis_pago) {
        this.gratis_pago = gratis_pago;
    }

    public void setPreco_ingresso(String preco_ingresso) {
        this.preco_ingresso = preco_ingresso;
    }

    public String getToken_author() {
        return token_author;
    }

    public void setToken_author(String token_author) {
        this.token_author = token_author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeauthor() {
        return nomeauthor;
    }

    public void setNomeauthor(String nomeauthor) {
        this.nomeauthor = nomeauthor;
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

    public String getIdauthor() {
        return idauthor;
    }

    public void setIdauthor(String idauthor) {
        this.idauthor = idauthor;
    }

    public String getCapaevento() {
        return capaevento;
    }

    public void setCapaevento(String capaevento) {
        this.capaevento = capaevento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }


    public Date getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(Date data_inicio) {
        this.data_inicio = data_inicio;
    }

    public Date getData_fim() {
        return data_fim;
    }

    public void setData_fim(Date data_fim) {
        this.data_fim = data_fim;
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

    public long getData_fim_min() {
        return data_fim_min;
    }

    public void setData_fim_min(long data_fim_min) {
        this.data_fim_min = data_fim_min;
    }

    public long getData_inicio_min() {
        return data_inicio_min;
    }

    public void setData_inicio_min(long data_inicio_min) {
        this.data_inicio_min = data_inicio_min;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getAtivado_desativado() {
        return ativado_desativado;
    }

    public void setAtivado_desativado(String ativado_desativado) {
        this.ativado_desativado = ativado_desativado;
    }
}

