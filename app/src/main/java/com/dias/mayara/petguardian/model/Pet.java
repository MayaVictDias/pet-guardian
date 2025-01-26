package com.dias.mayara.petguardian.model;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Pet implements Serializable {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

    private String idPet;
    private String nomeUppercasePet;
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

    private DocumentReference firebaseRef;
    private String idUsuarioLogado;


    public Pet() {

    }

    public Pet(String nomePet, String nomeUppercasePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor, long dataCadastro) { // Construtor com data de cadastro
        this.nomePet = nomePet;
        this.nomeUppercasePet = nomeUppercasePet;
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

    public Pet(String idPet, String nomePet, String nomeUppercasePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor, long dataCadastro) { // Construtor com ID do pet, quando ele for puxado direto do banco de dados
        this.idPet = idPet;
        this.nomePet = nomePet;
        this.nomeUppercasePet = nomeUppercasePet;
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

    public Pet(String nomePet, String nomeUppercasePet, String idadePet, String generoPet, String especiePet,
               String sobreOPet, String statusPet, String imagemUrl, String idEndereco,
               String idTutor) { // Construtor sem data de cadastro
        this.nomePet = nomePet;
        this.nomeUppercasePet = nomeUppercasePet;
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


    /// Método para salvar o pet para adoção
    public void salvarAdocao() {
        // Obtém a instância do Firestore
        FirebaseFirestore firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Referência para o documento de adoção do pet do usuário
        DocumentReference petsRef = firebaseRef.collection("pets")
                .document(idUsuarioLogado)
                .collection("adocao")
                .document(idPet);

        // Criando o mapa de dados do pet
        Map<String, Object> petData = criarMapaPet();

        // Salvando os dados do pet nos três lugares
        petsRef.set(petData);
    }



    // Método para salvar o pet como desaparecido
    public void salvarDesaparecido() {
        // Obtém a instância do Firestore
        FirebaseFirestore firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Referência para o documento de adoção do pet do usuário
        DocumentReference petsRef = firebaseRef.collection("pets")
                .document(idUsuarioLogado)
                .collection("desaparecido")
                .document(idPet);

        // Criando o mapa de dados do pet
        Map<String, Object> petData = criarMapaPet();

        // Salvando os dados do pet nos três lugares
        petsRef.set(petData);
    }

    // Método para salvar o pet procurando dono
    public void salvarProcurandoDono() {
        // Obtém a instância do Firestore
        FirebaseFirestore firebaseRef = ConfiguracaoFirebase.getFirebase();

        // Referência para o documento de adoção do pet do usuário
        DocumentReference petsRef = firebaseRef.collection("pets")
                .document(idUsuarioLogado)
                .collection("procurandoDono")
                .document(idPet);

        // Criando o mapa de dados do pet
        Map<String, Object> petData = criarMapaPet();

        // Salvando os dados do pet nos três lugares
        petsRef.set(petData);
    }



    // Método para criar o mapa de dados do pet
    private Map<String, Object> criarMapaPet() {
        Map<String, Object> petData = new HashMap<>();
        petData.put("idPet", idPet);
        petData.put("idTutor", idTutor);
        petData.put("nomePet", nomePet);
        petData.put("nomeUppercasePet", nomeUppercasePet);
        petData.put("idadePet", idadePet);
        petData.put("generoPet", generoPet);
        petData.put("especiePet", especiePet);
        petData.put("sobreOPet", sobreOPet);
        petData.put("statusPet", statusPet);
        petData.put("imagemUrl", imagemUrl);
        petData.put("dataCadastro", ServerValue.TIMESTAMP);

        if (idEndereco != null) {
            petData.put("idEndereco", idEndereco);
        }
        return petData;
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

    public String getNomeUppercasePet() {
        return nomeUppercasePet;
    }

    public void setNomeUppercasePet(String nomeUppercasePet) {
        this.nomeUppercasePet = nomeUppercasePet;
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
                ", idTutor='" + idTutor + '\'' +
                ", nomePet='" + nomePet + '\'' +
                ", idadePet='" + idadePet + '\'' +
                ", generoPet='" + generoPet + '\'' +
                ", especiePet='" + especiePet + '\'' +
                ", sobreOPet='" + sobreOPet + '\'' +
                ", statusPet='" + statusPet + '\'' +
                ", idEndereco='" + idEndereco + '\'' +
                ", imagemUrl='" + imagemUrl + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", firebaseRef=" + firebaseRef +
                ", idUsuarioLogado='" + idUsuarioLogado + '\'' +
                '}';
    }
}