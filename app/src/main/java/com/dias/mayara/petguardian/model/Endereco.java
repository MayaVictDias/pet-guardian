package com.dias.mayara.petguardian.model;

import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Endereco {

    private String idEndereco;
    private String idPet;
    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private String ruaAvenida;
    private String numero;
    private String complemento;
    private String pais;
    private String pontoReferencia;

    private FirebaseFirestore firebaseRef;

    public Endereco() {
    }

    public Endereco(String cep, String estado, String cidade, String bairro, String ruaAvenida, String numero, String complemento, String pais, String pontoReferencia) {

        // Configuração de um ID único para o endereço
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        this.idEndereco = firebaseRef.collection("enderecos").document().getId();  // Gera um ID único

        this.complemento = complemento;
        this.numero = numero;
        this.ruaAvenida = ruaAvenida;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.pais = pais;
        this.pontoReferencia = pontoReferencia;
    }

    public void salvar() {
        // Verifica se o idEndereco foi configurado
        if (idEndereco != null && !idEndereco.isEmpty()) {
            // Inicializa a referência ao documento de "enderecos"
            DocumentReference enderecoRef = firebaseRef.collection("enderecos").document(idEndereco);

            // Salva o objeto Endereco na referência criada
            enderecoRef.set(this);
        } else {
            throw new IllegalStateException("ID do Endereço não foi configurado.");
        }
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

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "idEndereco='" + idEndereco + '\'' +
                ", idPet='" + idPet + '\'' +
                ", cep='" + cep + '\'' +
                ", estado='" + estado + '\'' +
                ", cidade='" + cidade + '\'' +
                ", bairro='" + bairro + '\'' +
                ", ruaAvenida='" + ruaAvenida + '\'' +
                ", numero='" + numero + '\'' +
                ", complemento='" + complemento + '\'' +
                ", pais='" + pais + '\'' +
                '}';
    }
}
