package com.dias.mayara.petguardian.model;

public class Endereco {

    private String idEndereco;
    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String ruaAvenida;
    private String numero;
    private String complemento;

    public Endereco() {
    }

    public Endereco(String cep, String estado, String cidade, String bairro, String ruaAvenida, String numero, String complemento) {
        this.complemento = complemento;
        this.numero = numero;
        this.ruaAvenida = ruaAvenida;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
    }

    public String getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(String idEndereco) {
        this.idEndereco = idEndereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRuaAvenida() {
        return ruaAvenida;
    }

    public void setRuaAvenida(String ruaAvenida) {
        this.ruaAvenida = ruaAvenida;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
