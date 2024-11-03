package com.dias.mayara.petguardian.model;

import android.util.Log;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Pet {

    private String idPet;
    private String idTutor;
    private String nomePet;
    private String idadePet;
    private String generoPet;
    private String especiePet;
    private String sobreOPet;
    private String statusPet;
    private String idEndereco;
    private String imagemUrl; // Armazenará a URL da imagem

    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    public Pet() {
        // Construtor vazio
    }

    public Pet(String nomePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String idEndereco) {
        this.nomePet = nomePet;
        this.idadePet = idadePet;
        this.generoPet = generoPet;
        this.especiePet = especiePet;
        this.sobreOPet = sobreOPet;
        this.statusPet = statusPet;
        this.idEndereco = idEndereco;

        // Configuração de um ID único para o pet
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        this.idPet = firebaseRef.child("pets").push().getKey();
        this.idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        this.idTutor = idUsuarioLogado; // Aqui você define o idTutor
    }

    public void salvar() {
        // Inclui o idUsuarioLogado no caminho para salvar o pet no nó específico do usuário
        DatabaseReference petsRef = ConfiguracaoFirebase.getFirebase()
                .child("pets")
                .child(idUsuarioLogado)
                .child(getIdPet());

        Map<String, Object> petData = new HashMap<>();
        petData.put("idPet", getIdPet());
        petData.put("idTutor", getIdTutor());
        petData.put("nomePet", getNomePet());
        petData.put("idadePet", getIdadePet());
        petData.put("generoPet", getGeneroPet());
        petData.put("especiePet", getEspeciePet());
        petData.put("sobreOPet", getSobreOPet());
        petData.put("statusPet", getStatusPet());
        petData.put("imagemUrl", getImagemUrl());

        if (idEndereco != null) {
            petData.put("idEndereco", getIdEndereco());
        }

        petsRef.setValue(petData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Salvar Pet", "Pet salvo com sucesso.");
            } else {
                Log.e("Salvar Pet", "Erro ao salvar o pet: " + task.getException().getMessage());
            }
        });
    }



    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getIdPet() {
        return idPet;
    }

    public void setIdPet(String idPet) {
        this.idPet = idPet;
    }

    public String getNomePet() {
        return nomePet;
    }

    public void setNomePet(String nomePet) {
        this.nomePet = nomePet;
    }

    public String getIdadePet() {
        return idadePet;
    }

    public void setIdadePet(String idadePet) {
        this.idadePet = idadePet;
    }

    public String getGeneroPet() {
        return generoPet;
    }

    public String getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(String idTutor) {
        this.idTutor = idTutor;
    }

    public void setGeneroPet(String generoPet) {
        this.generoPet = generoPet;
    }

    public String getEspeciePet() {
        return especiePet;
    }

    public void setEspeciePet(String especiePet) {
        this.especiePet = especiePet;
    }

    public String getSobreOPet() {
        return sobreOPet;
    }

    public void setSobreOPet(String sobreOPet) {
        this.sobreOPet = sobreOPet;
    }

    public String getStatusPet() {
        return statusPet;
    }

    public void setStatusPet(String statusPet) {
        this.statusPet = statusPet;
    }

    public String getIdEndereco() {
        return idEndereco;
    }

    public void setEndereco(String id) {
        this.idEndereco = id;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "idPet='" + idPet + '\'' +
                ", nomePet='" + nomePet + '\'' +
                ", idadePet='" + idadePet + '\'' +
                ", generoPet='" + generoPet + '\'' +
                ", especiePet='" + especiePet + '\'' +
                ", sobreOPet='" + sobreOPet + '\'' +
                ", statusPet='" + statusPet + '\'' +
                ", id=" + idEndereco +
                '}';
    }
}