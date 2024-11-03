package com.dias.mayara.petguardian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.model.Pet;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetViewHolder> {

    private List<Pet> petList;

    public PetsAdapter(List<Pet> petList) {
        this.petList = petList; // Inicializa a lista petList
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        // Defina os dados do pet aqui
        holder.petName.setText(pet.getNomePet());
        // Carregue a imagem do pet, se necessário
    }

    @Override
    public int getItemCount() {
        return petList.size(); // Aqui petList já deve estar inicializada
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView petName; // Exemplo de campo; adicione outros campos conforme necessário

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.petName); // Id do TextView no layout do item
            // Inicialize outros campos aqui
        }
    }
}
