package com.dias.mayara.petguardian.model;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Pet implements Serializable {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

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
    private Timestamp dataCadastro; // Usando Timestamp do Firestore

    private DocumentReference firebaseRef;
    private String idUsuarioLogado;

    public Pet() {
        // Construtor vazio necessário para o Firestore
    }

    public Pet(String nomePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor, Timestamp dataCadastro) { // Construtor com data de cadastro
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

        // Gera o ID único de 6 caracteres
        this.idPet = generateUniqueId();
        this.idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        this.idTutor = idUsuarioLogado;
    }

    public Pet(String idPet, String nomePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor, Timestamp dataCadastro) { // Construtor com ID do pet, quando ele for puxado direto do banco de dados
        this.idPet = idPet;
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

        // Gera o ID único de 6 caracteres
        this.idPet = generateUniqueId();
        this.idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        this.idTutor = idUsuarioLogado;
    }

    // Gerador de ID único de 6 caracteres
    private String generateUniqueId() {
        SecureRandom random = new SecureRandom();
        StringBuilder id = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            id.append(CHARACTERS.charAt(index));
        }
        return id.toString();
    }

    public void salvar() {
        FirebaseFirestore firebaseRef = ConfiguracaoFirebase.getFirebase();
        DocumentReference petsRef = firebaseRef.collection("pets").document(idPet);

        // Salva os dados do pet
        petsRef.set(criarMapaPet()).addOnSuccessListener(aVoid -> {
            // Atualiza a quantidade de pets cadastrados no documento do tutor
            DocumentReference tutorRef = firebaseRef.collection("usuarios").document(idTutor);
            tutorRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("quantidadePetsCadastrados")) {
                    int quantidadeAtual = documentSnapshot.getLong("quantidadePetsCadastrados").intValue();
                    tutorRef.update("quantidadePetsCadastrados", quantidadeAtual + 1);
                }
            });
        });
    }


    // Método para criar o mapa de dados do pet
    private Map<String, Object> criarMapaPet() {
        Map<String, Object> petData = new HashMap<>();
        petData.put("idPet", idPet);
        petData.put("idTutor", idTutor);
        petData.put("nomePet", nomePet);
        petData.put("idadePet", idadePet);
        petData.put("generoPet", generoPet);
        petData.put("especiePet", especiePet);
        petData.put("sobreOPet", sobreOPet);
        petData.put("statusPet", statusPet);
        petData.put("imagemUrl", imagemUrl);
        petData.put("dataCadastro", dataCadastro); // Usando Timestamp diretamente

        if (idEndereco != null) {
            petData.put("idEndereco", idEndereco);
        }
        return petData;
    }

    // Getters e Setters
    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
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

    public void setGeneroPet(String generoPet) {
        this.generoPet = generoPet;
    }

    public String getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(String idTutor) {
        this.idTutor = idTutor;
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

    public void setIdEndereco(String idEndereco) {
        this.idEndereco = idEndereco;
    }
}