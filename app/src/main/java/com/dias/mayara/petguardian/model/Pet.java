package com.dias.mayara.petguardian.model;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

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
    private long dataCadastro;

    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;


    public Pet() {

    }

    public Pet(String nomePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor, long dataCadastro) { // Construtor com data de cadastro
        this.nomePet = nomePet;
        this.idadePet = idadePet;
        this.generoPet = generoPet;
        this.especiePet = especiePet;
        this.sobreOPet = sobreOPet;
        this.statusPet = statusPet;
        this.imagemUrl = imagemUrl;
        this.idEndereco = idEndereco;
        this.idTutor = idTutor;
        this.dataCadastro = dataCadastro;

        // Configuração de um ID único para o pet
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        this.idPet = firebaseRef.child("pets").push().getKey();
        this.idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        this.idTutor = idUsuarioLogado; // Aqui você define o idTutor
    }

    public Pet(String nomePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor) { // Construtor sem data de cadastro
        this.nomePet = nomePet;
        this.idadePet = idadePet;
        this.generoPet = generoPet;
        this.especiePet = especiePet;
        this.sobreOPet = sobreOPet;
        this.statusPet = statusPet;
        this.imagemUrl = imagemUrl;
        this.idEndereco = idEndereco;
        this.idTutor = idTutor;

        // Configuração de um ID único para o pet
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        this.idPet = firebaseRef.child("pets").push().getKey();
        this.idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        this.idTutor = idUsuarioLogado; // Aqui você define o idTutor
    }

    public void salvarAdocao() {
        // Inclui o idUsuarioLogado no caminho para salvar o pet no nó específico do usuário
        DatabaseReference petsRef = ConfiguracaoFirebase.getFirebase()
                .child("pets")
                .child(idUsuarioLogado)
                .child("adocao")
                .child(getIdPet());

        DatabaseReference feedPetsRef = ConfiguracaoFirebase.getFirebase()
                .child("feedPets")
                .child("adocao")
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
        petData.put("dataCadastro", ServerValue.TIMESTAMP);

        if (idEndereco != null) {
            petData.put("idEndereco", getIdEndereco());
        }

        petsRef.setValue(petData);
        feedPetsRef.setValue(petData);
    }

    public void salvarDesaparecido() {
        // Inclui o idUsuarioLogado no caminho para salvar o pet no nó específico do usuário
        DatabaseReference petsRef = ConfiguracaoFirebase.getFirebase()
                .child("pets")
                .child(idUsuarioLogado)
                .child("desaparecido")
                .child(getIdPet());

        DatabaseReference feedPetsRef = ConfiguracaoFirebase.getFirebase()
                .child("feedPets")
                .child("desaparecido")
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
        petData.put("dataCadastro", ServerValue.TIMESTAMP);

        if (idEndereco != null) {
            petData.put("idEndereco", getIdEndereco());
        }

        petsRef.setValue(petData);
        feedPetsRef.setValue(petData);
    }

    public void salvarProcurandoDono() {
        // Inclui o idUsuarioLogado no caminho para salvar o pet no nó específico do usuário
        DatabaseReference petsRef = ConfiguracaoFirebase.getFirebase()
                .child("pets")
                .child(idUsuarioLogado)
                .child("procurandoDono")
                .child(getIdPet());

        DatabaseReference feedPetsRef = ConfiguracaoFirebase.getFirebase()
                .child("feedPets")
                .child("desaparecido")
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
        petData.put("dataCadastro", ServerValue.TIMESTAMP);

        if (idEndereco != null) {
            petData.put("idEndereco", getIdEndereco());
        }

        petsRef.setValue(petData);
        feedPetsRef.setValue(petData);
    }

    public long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(long dataCadastro) {
        this.dataCadastro = dataCadastro;
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