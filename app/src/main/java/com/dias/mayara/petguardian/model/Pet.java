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
    private String racaPet;
    private String corPredominante;
    private String corDosOlhos;
    private String porte;
    private String nomeLowerCase;
    private String imagemUrl; // Armazenará a URL da imagem
    private String statusVacinacao;
    private String vacinasTomadas;
    private String vermifugado;
    private Timestamp dataVermifugacao;
    private String necessidadesEspeciais;
    private String doencasTratamentos;
    private String statusCastracao;
    private String nivelEnergia;
    private String sociabilidade;
    private boolean isAdestrado;
    private Timestamp dataCadastro;

    private DocumentReference firebaseRef;
    private String idUsuarioLogado;

    public Pet() {
        // Construtor vazio necessário para o Firestore
    }

    public Pet(String idTutor, String nomePet, String idadePet, String generoPet, String especiePet,
               String racaPet, String corPredominante, String corDosOlhos, String porte, String imagemUrl,
               String statusVacinacao, String vacinasTomadas, String vermifugado, Timestamp dataVermifugacao,
               String necessidadesEspeciais, String doencasTratamentos, String statusCastracao,
               String nivelEnergia, String sociabilidade, boolean isAdestrado, Timestamp dataCadastro) {

        this.idTutor = idTutor;
        this.nomePet = nomePet;
        this.idadePet = idadePet;
        this.generoPet = generoPet;
        this.especiePet = especiePet;
        this.racaPet = racaPet;
        this.corPredominante = corPredominante;
        this.corDosOlhos = corDosOlhos;
        this.porte = porte;
        this.nomeLowerCase = nomePet.toLowerCase();
        this.imagemUrl = imagemUrl;
        this.statusVacinacao = statusVacinacao;
        this.vacinasTomadas = vacinasTomadas;
        this.vermifugado = vermifugado;
        this.dataVermifugacao = dataVermifugacao;
        this.necessidadesEspeciais = necessidadesEspeciais;
        this.doencasTratamentos = doencasTratamentos;
        this.statusCastracao = statusCastracao;
        this.nivelEnergia = nivelEnergia;
        this.sociabilidade = sociabilidade;
        this.isAdestrado = isAdestrado;
        this.dataCadastro = dataCadastro;

        // Gera o ID único de 6 caracteres
        this.idPet = generateUniqueId();
        this.idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        this.idTutor = idUsuarioLogado;
    }


    public Pet(String idPet, String idTutor, String nomePet, String idadePet, String generoPet, String especiePet,
               String racaPet, String corPredominante, String corDosOlhos, String porte, String imagemUrl,
               String statusVacinacao, String vacinasTomadas, String vermifugado, Timestamp dataVermifugacao,
               String necessidadesEspeciais, String doencasTratamentos, String statusCastracao,
               String nivelEnergia, String sociabilidade, boolean isAdestrado, Timestamp dataCadastro) {
    // Construtor com idPet
        this.idPet = idPet;
        this.idTutor = idTutor;
        this.nomePet = nomePet;
        this.idadePet = idadePet;
        this.generoPet = generoPet;
        this.especiePet = especiePet;
        this.racaPet = racaPet;
        this.corPredominante = corPredominante;
        this.corDosOlhos = corDosOlhos;
        this.porte = porte;
        this.nomeLowerCase = nomePet.toLowerCase();
        this.imagemUrl = imagemUrl;
        this.statusVacinacao = statusVacinacao;
        this.vacinasTomadas = vacinasTomadas;
        this.vermifugado = vermifugado;
        this.dataVermifugacao = dataVermifugacao;
        this.necessidadesEspeciais = necessidadesEspeciais;
        this.doencasTratamentos = doencasTratamentos;
        this.statusCastracao = statusCastracao;
        this.nivelEnergia = nivelEnergia;
        this.sociabilidade = sociabilidade;
        this.isAdestrado = isAdestrado;
        this.dataCadastro = dataCadastro;
    }

    public Pet(String nomePet, String idadePet, String generoPet, String especiePet,
               String racaPet, String corPredominante, String corDosOlhos, String porte, String imagemUrl,
               String statusVacinacao, String vacinasTomadas, String vermifugado, Timestamp dataVermifugacao,
               String necessidadesEspeciais, String doencasTratamentos, String statusCastracao,
               String nivelEnergia, String sociabilidade, boolean isAdestrado) {
    // Construtor sem data de cadastro

        this.nomePet = nomePet;
        this.idadePet = idadePet;
        this.generoPet = generoPet;
        this.especiePet = especiePet;
        this.racaPet = racaPet;
        this.corPredominante = corPredominante;
        this.corDosOlhos = corDosOlhos;
        this.porte = porte;
        this.nomeLowerCase = nomePet.toLowerCase();
        this.imagemUrl = imagemUrl;
        this.statusVacinacao = statusVacinacao;
        this.vacinasTomadas = vacinasTomadas;
        this.vermifugado = vermifugado;
        this.dataVermifugacao = dataVermifugacao;
        this.necessidadesEspeciais = necessidadesEspeciais;
        this.doencasTratamentos = doencasTratamentos;
        this.statusCastracao = statusCastracao;
        this.nivelEnergia = nivelEnergia;
        this.sociabilidade = sociabilidade;
        this.isAdestrado = isAdestrado;

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
        petData.put("idPet", getIdPet());
        petData.put("idTutor", getIdTutor());
        petData.put("nomePet", getNomePet());
        petData.put("nomeLowerCasePet", getNomeLowerCase());
        petData.put("idadePet", getIdadePet());
        petData.put("generoPet", getGeneroPet());
        petData.put("portePet", getPorte());
        petData.put("corDosOlhosPet", getCorDosOlhos());
        petData.put("corPredominantePet", getCorPredominante());
        petData.put("especiePet", getEspeciePet());
        petData.put("racaPet", getRacaPet());
        petData.put("statusCastracao", getStatusCastracao());
        petData.put("imagemUrl", getImagemUrl());
        petData.put("dataCadastro", getDataCadastro());
        petData.put("statusVacinacao", getStatusVacinacao());
        petData.put("vacinasTomadas", getVacinasTomadas());
        petData.put("vermifugado", getVermifugado());
        petData.put("dataVermifugacao", getDataVermifugacao());
        petData.put("necessidadesEspeciais", getNecessidadesEspeciais());
        petData.put("doencasTratamentos", getDoencasTratamentos());
        petData.put("nivelEnergia", getNivelEnergia());
        petData.put("sociabilidade", getSociabilidade());
        petData.put("isAdestrado", isAdestrado());

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

    public String getStatusVacinacao() {
        return statusVacinacao;
    }

    public void setStatusVacinacao(String statusVacinacao) {
        this.statusVacinacao = statusVacinacao;
    }

    public String getVacinasTomadas() {
        return vacinasTomadas;
    }

    public void setVacinasTomadas(String vacinasTomadas) {
        this.vacinasTomadas = vacinasTomadas;
    }

    public String getVermifugado() {
        return vermifugado;
    }

    public void setVermifugado(String vermifugado) {
        this.vermifugado = vermifugado;
    }

    public Timestamp getDataVermifugacao() {
        return dataVermifugacao;
    }

    public void setDataVermifugacao(Timestamp dataVermifugacao) {
        this.dataVermifugacao = dataVermifugacao;
    }

    public String getNecessidadesEspeciais() {
        return necessidadesEspeciais;
    }

    public void setNecessidadesEspeciais(String necessidadesEspeciais) {
        this.necessidadesEspeciais = necessidadesEspeciais;
    }

    public String getNivelEnergia() {
        return nivelEnergia;
    }

    public void setNivelEnergia(String nivelEnergia) {
        this.nivelEnergia = nivelEnergia;
    }

    public String getSociabilidade() {
        return sociabilidade;
    }

    public void setSociabilidade(String sociabilidade) {
        this.sociabilidade = sociabilidade;
    }

    public boolean isAdestrado() {
        return isAdestrado;
    }

    public String getStatusCastracao() {
        return statusCastracao;
    }

    public void setStatusCastracao(String statusCastracao) {
        this.statusCastracao = statusCastracao;
    }

    public void setAdestrado(boolean adestrado) {
        isAdestrado = adestrado;
    }

    public String getDoencasTratamentos() {
        return doencasTratamentos;
    }

    public void setDoencasTratamentos(String doencasTratamentos) {
        this.doencasTratamentos = doencasTratamentos;
    }

    public String getNomeLowerCase() {
        return nomePet != null ? nomePet.toLowerCase() : null;
    }

    public void setNomeLowerCase() {
        this.nomeLowerCase = nomePet.toLowerCase();
    }

    public void setNomeLowerCase(String nomeLowerCase) {
        this.nomeLowerCase = nomeLowerCase;
    }

    public String getCorDosOlhos() {
        return corDosOlhos;
    }

    public void setCorDosOlhos(String corDosOlhos) {
        this.corDosOlhos = corDosOlhos;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getCorPredominante() {
        return corPredominante;
    }

    public void setCorPredominante(String corPredominante) {
        this.corPredominante = corPredominante;
    }

    public String getRacaPet() {
        return racaPet;
    }

    public void setRacaPet(String racaPet) {
        this.racaPet = racaPet;
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
                ", imagemUrl='" + imagemUrl + '\'' +
                ", statusVacinacao='" + statusVacinacao + '\'' +
                ", vacinasTomadas='" + vacinasTomadas + '\'' +
                ", vermifugado='" + vermifugado + '\'' +
                ", dataVermifugacao='" + dataVermifugacao + '\'' +
                ", necessidadesEspeciais='" + necessidadesEspeciais + '\'' +
                ", doencasTratamentos='" + doencasTratamentos + '\'' +
                ", nivelEnergia='" + nivelEnergia + '\'' +
                ", sociabilidade='" + sociabilidade + '\'' +
                ", statusCastracao='" + statusCastracao + '\'' +
                ", isAdestrado=" + isAdestrado +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}
