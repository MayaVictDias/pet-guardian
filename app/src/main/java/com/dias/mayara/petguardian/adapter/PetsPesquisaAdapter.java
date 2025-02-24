package com.dias.mayara.petguardian.adapter;

import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.MaisInformacoesSobrePetActivity;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PetsPesquisaAdapter extends RecyclerView.Adapter<PetsPesquisaAdapter.PetViewHolder> {

    private List<Pet> petList;
    private Handler handler = new Handler();

    public PetsPesquisaAdapter(List<Pet> petList) {
        this.petList = petList != null ? petList : new ArrayList<>(); // Evitar NullPointerException
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
        if (position >= 0 && position < petList.size()) {
            Pet pet = petList.get(position);

            // Configura os campos do pet
            holder.textViewNomePet.setText(pet.getNomePet());
            holder.textViewIdadeGenero.setText(pet.getIdadePet() + " • " + pet.getGeneroPet());

            // Carrega a imagem do pet usando Glide
            Glide.with(holder.imageViewFotoPet.getContext())
                    .load(pet.getImagemUrl()) // URL da imagem
                    .placeholder(R.drawable.imagem_carregamento) // Imagem padrão enquanto carrega
                    .error(R.drawable.no_image_found) // Imagem em caso de erro
                    .into(holder.imageViewFotoPet);

            // Configura o clique no cardView
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Passa apenas o ID do pet para a próxima atividade
                    Intent i = new Intent(holder.itemView.getContext(), MaisInformacoesSobrePetActivity.class);
                    i.putExtra("petId", pet.getIdPet()); // Passa o ID do pet
                    holder.itemView.getContext().startActivity(i);
                }
            });

            // Atualiza o texto do tempo desde a postagem
            holder.updateTimeSincePost(pet.getDataCadastro());
        }
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNomePet, textViewIdadeGenero, textViewDesaparecidoHaTempo,
                textViewCidadePet;
        private ImageView imageViewFotoPet;
        private CardView cardView;

        private Handler handler = new Handler();
        private Runnable updateRunnable;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFotoPet = itemView.findViewById(R.id.imageViewFotoPet);
            textViewNomePet = itemView.findViewById(R.id.textViewNomePet);
            textViewIdadeGenero = itemView.findViewById(R.id.textViewIdadeGenero);
            textViewDesaparecidoHaTempo = itemView.findViewById(R.id.textViewDesaparecidoHaTempo);
            textViewCidadePet = itemView.findViewById(R.id.textViewCidadePet);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void updateTimeSincePost(Timestamp postTimestamp) {
            if (updateRunnable != null) {
                handler.removeCallbacks(updateRunnable);
            }

            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    textViewDesaparecidoHaTempo.setText(calculateTimeSincePost(postTimestamp));
                    handler.postDelayed(this, 60000); // Atualiza a cada 60 segundos
                }
            };
            handler.post(updateRunnable);
        }

        public String calculateTimeSincePost(Timestamp postTimestamp) {
            long currentTime = System.currentTimeMillis();
            long postTime = postTimestamp.toDate().getTime(); // Converte Timestamp para milissegundos
            long timeDifference = currentTime - postTime;

            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
            long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
            long days = TimeUnit.MILLISECONDS.toDays(timeDifference);

            if (minutes < 60) {
                return minutes + " minuto(s) atrás";
            } else if (hours < 24) {
                return hours + " hora(s) atrás";
            } else {
                return days + " dia(s) atrás";
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            handler.removeCallbacks(updateRunnable); // Para evitar vazamentos de memória
        }
    }
}