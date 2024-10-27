package com.dias.mayara.petguardian.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Endereco> endereco = new MutableLiveData<>();
    private final MutableLiveData<Integer> opcaoSelecionada = new MutableLiveData<>();
    private final MutableLiveData<Pet> pet = new MutableLiveData<>();

    // Getter e Setter para Endereco
    public LiveData<Endereco> getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco.setValue(endereco);
    }

    // Getter e Setter para Opção Selecionada (RadioGroup)
    public LiveData<Integer> getOpcaoSelecionada() {
        return opcaoSelecionada;
    }

    public void setOpcaoSelecionada(int opcao) {
        opcaoSelecionada.setValue(opcao);
    }

    // Getter e Setter para Pet
    public LiveData<Pet> getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet.setValue(pet);
    }
}
