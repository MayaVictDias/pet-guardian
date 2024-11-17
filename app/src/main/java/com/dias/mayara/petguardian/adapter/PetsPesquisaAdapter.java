package com.dias.mayara.petguardian.adapter;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
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
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

            String nomeBancoDadosStatus = "";

            if(pet.getStatusPet().equals("Adoção")) {
                nomeBancoDadosStatus = "adocao";
            } else if (pet.getStatusPet().equals("Desaparecido")) {
                nomeBancoDadosStatus = "desaparecido";
            } else if (pet.getStatusPet().equals("Procurando dono")) {
                nomeBancoDadosStatus = "procurandoDono";
            }

            DatabaseReference petRef = ConfiguracaoFirebase.getFirebase().child("pets")
                    .child(pet.getIdTutor())
                    .child(nomeBancoDadosStatus)
                    .child(pet.getIdPet());

            DatabaseReference enderecoRef = ConfiguracaoFirebase.getFirebase().child("enderecos")
                    .child(pet.getIdEndereco());

            holder.textViewNomePet.setText(pet.getNomePet());
            holder.textViewStatusPet.setText(pet.getStatusPet().toUpperCase());
            holder.textViewIdadeGenero.setText(pet.getIdadePet() + " • " + pet.getGeneroPet());

            if (holder.textViewStatusPet.getText().equals("ADOÇÃO")) {
                holder.textViewStatusPet.setBackgroundColor(Color.parseColor("#00FF47"));
            } else if (holder.textViewStatusPet.getText().equals("DESAPARECIDO")) {
                holder.textViewStatusPet.setBackgroundColor(Color.parseColor("#FF0000"));
            } else if (holder.textViewStatusPet.getText().equals("PROCURANDO DONO")) {
                holder.textViewStatusPet.setBackgroundColor(Color.parseColor("#0047FF"));
            }

            Glide.with(holder.imageViewFotoPet.getContext())
                    .load(pet.getImagemUrl()) // Aqui você insere a URL da imagem
                    .placeholder(R.drawable.imagem_carregamento) // Imagem padrão enquanto carrega
                    .error(R.drawable.no_image_found) // Imagem em caso de erro
                    .into(holder.imageViewFotoPet);

            enderecoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Endereco endereco = snapshot.getValue(Endereco.class);
                    if (endereco != null && !pet.getStatusPet().equals("Adoção")) {
                        holder.textViewCidadePet.setText(endereco.getCidade() + " - " + endereco.getEstado());
                    } else {
                        holder.textViewCidadePet.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    petRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            Pet petSelecionado = snapshot.getValue(Pet.class);

                            if (petSelecionado != null) {
                                Intent i = new Intent(holder.itemView.getContext(), MaisInformacoesSobrePetActivity.class);
                                i.putExtra("petSelecionado", petSelecionado);
                                holder.itemView.getContext().startActivity(i);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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

        private TextView textViewStatusPet, textViewNomePet, textViewIdadeGenero, textViewDesaparecidoHaTempo,
                textViewCidadePet;
        private ImageView imageViewFotoPet;
        private CardView cardView;

        private Handler handler = new Handler();
        private Runnable updateRunnable;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFotoPet = itemView.findViewById(R.id.imageViewFotoPet);
            textViewStatusPet = itemView.findViewById(R.id.textViewStatusPet);
            textViewNomePet = itemView.findViewById(R.id.textViewNomePet);
            textViewIdadeGenero = itemView.findViewById(R.id.textViewIdadeGenero);
            textViewDesaparecidoHaTempo = itemView.findViewById(R.id.textViewDesaparecidoHaTempo);
            textViewCidadePet = itemView.findViewById(R.id.textViewCidadePet);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void updateTimeSincePost(long postTimestamp) {
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

        public String calculateTimeSincePost(long postTimestamp) {
            long currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - postTimestamp;

            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference);
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
