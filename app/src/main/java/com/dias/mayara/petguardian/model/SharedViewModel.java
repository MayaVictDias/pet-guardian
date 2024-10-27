package com.dias.mayara.petguardian.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<Pet> pet = new MutableLiveData<>();
    private MutableLiveData<Endereco> endereco = new MutableLiveData<>();

    public void setPet(Pet pet) {
        this.pet.setValue(pet);
    }

    public LiveData<Pet> getPet() {
        return pet;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco.setValue(endereco);
    }

    public LiveData<Endereco> getEndereco() {
        return endereco;
    }
}