package com.dias.mayara.petguardian.model;

public class Pet {

    private String idPet;
    private String nomePet;
    private String idadePet;
    private String generoPet;
    private String especiePet;
    private String sobreOPet;
    //private String caminhoFotoPet;
    private String statusPet;

    private Endereco id;

    public Pet() {

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

    public Endereco getEndereco() {
        return id;
    }

    public void setEndereco(Endereco id) {
        this.id = id;
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
                ", id=" + id +
                '}';
    }
}
