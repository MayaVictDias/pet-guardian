package com.dias.mayara.petguardian.model;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String telefoneUsuario;
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
        usuarioMap.put("nomeUsuario", getNomeUsuario());
        usuarioMap.put("telefoneUsuario", getTelefoneUsuario());
        usuarioMap.put("cidadeUsuario", getCidadeUsuario());
        usuarioMap.put("estadoUsuario", getCidadeUsuario());
        usuarioMap.put("idUsuario", getIdUsuario());
        usuarioMap.put("caminhoFoto", getCaminhoFotoUsuario());
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

    public String getTelefoneUsuario() {
        return telefoneUsuario;
    }

    public void setTelefoneUsuario(String telefoneUsuario) {
        this.telefoneUsuario = telefoneUsuario;
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
}
