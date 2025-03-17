package com.dias.mayara.petguardian.model;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String idUsuario;
    private String nomeUsuario;
    private String nomeLowercaseUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String celularUsuario;
    private String caminhoFotoUsuario;
    private String cidadeUsuario;
    private String estadoUsuario;
    private int quantidadePetsCadastrados;

    public Usuario() {
    }

    // Referencia ao banco de dados Usuario dentro do firebase
    public void salvar() {
        FirebaseFirestore firebaseRef = ConfiguracaoFirebase.getFirebase();
        DocumentReference usuariosRef = firebaseRef.collection("usuarios").document(getIdUsuario()); // Referência ao documento do usuário
        usuariosRef.set(converterParaMap());
    }

    // Metodo que atualiza os valores no firebase
    public void atualizar() {

        FirebaseFirestore firebaseRef = ConfiguracaoFirebase.getFirebase(); // Instância do Firestore
        DocumentReference usuariosRef = firebaseRef.collection("usuarios").document(getIdUsuario()); // Referência ao documento do usuário

        Map<String, Object> valoresUsuario = converterParaMap(); // Dados a serem atualizados

        usuariosRef.update(valoresUsuario);

    }

    /*
     * Recupera os valores do firebase que estão nos formatos que são do tipo chave String e valor
     * Object
     */
    public Map<String, Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("emailUsuario", getEmailUsuario());
        usuarioMap.put("nomeLowercaseUsuario", getNomeLowercaseUsuario());
        usuarioMap.put("nomeUsuario", getNomeUsuario());
        usuarioMap.put("celularUsuario", getCelularUsuario());
        usuarioMap.put("cidadeUsuario", getCidadeUsuario());
        usuarioMap.put("estadoUsuario", getEstadoUsuario());
        usuarioMap.put("idUsuario", getIdUsuario());
        usuarioMap.put("caminhoFoto", getCaminhoFotoUsuario());
        usuarioMap.put("dataCadastro", Timestamp.now());
        usuarioMap.put("quantidadePetsCadastrados", getQuantidadePetsCadastrados());

        return usuarioMap;
    }

    public int getQuantidadePetsCadastrados() {
        return quantidadePetsCadastrados;
    }

    public void setQuantidadePetsCadastrados(int quantidadePetsCadastrados) {
        this.quantidadePetsCadastrados = quantidadePetsCadastrados;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public String getCelularUsuario() {
        return celularUsuario;
    }

    public void setCelularUsuario(String celularUsuario) {
        this.celularUsuario = celularUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public String getCaminhoFotoUsuario() {
        return caminhoFotoUsuario;
    }

    public void setCaminhoFotoUsuario(String caminhoFotoUsuario) {
        this.caminhoFotoUsuario = caminhoFotoUsuario;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(String estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getCidadeUsuario() {
        return cidadeUsuario;
    }

    public void setCidadeUsuario(String cidadeUsuario) {
        this.cidadeUsuario = cidadeUsuario;
    }

    public String getNomeLowercaseUsuario() {
        return nomeUsuario != null ? nomeUsuario.toLowerCase() : null;
    }

    public void setNomeLowercaseUsuario(String nomeLowercaseUsuario) {
        this.nomeLowercaseUsuario = nomeUsuario.toLowerCase();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", nomeLowercaseUsuario='" + getNomeLowercaseUsuario() + '\'' +
                ", emailUsuario='" + emailUsuario + '\'' +
                ", celularUsuario='" + celularUsuario + '\'' +
                ", caminhoFotoUsuario='" + caminhoFotoUsuario + '\'' +
                ", cidadeUsuario='" + cidadeUsuario + '\'' +
                ", estadoUsuario='" + estadoUsuario + '\'' +
                ", quantidadePetsCadastrados=" + quantidadePetsCadastrados +
                '}';
    }
}
