package com.dias.mayara.petguardian.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.widget.EditText;

import com.dias.mayara.petguardian.activity.LoginActivity;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String telefoneUsuario;
    private String caminhoFotoUsuario;
    private String cidadeUsuario;
    private int quantidadePetsCadastrados;

    public Usuario() {
    }

    // Referencia ao banco de dados Usuario dentro do firebase
    public void salvar() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getIdUsuario());
        usuariosRef.setValue(converterParaMap());
    }

    // Metodo que atualiza os valores no firebase
    public void atualizar() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getIdUsuario());

        Map<String, Object> valoresUsuario = converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);

    }

    /*
     * Recupera os valores do firebase que estão nos formatos que são do tipo chave String e valor
     * Object
     */
    public Map<String, Object> converterParaMap() {
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("emailUsuario", getEmailUsuario());
        usuarioMap.put("nomeUsuario", getNomeUsuario());
        usuarioMap.put("telefoneUsuario", telefoneUsuario);
        usuarioMap.put("cidadeUsuario", getCidadeUsuario());
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

    public String getCidadeUsuario() {
        return cidadeUsuario;
    }

    public void setCidadeUsuario(String cidadeUsuario) {
        this.cidadeUsuario = cidadeUsuario;
    }
}
