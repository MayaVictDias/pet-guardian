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
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetViewHolder> {

    private List<Pet> petList;
    private Handler handler = new Handler();

    public PetsAdapter(List<Pet> petList) {
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

            DocumentReference petRef = ConfiguracaoFirebase.getFirebase().collection("pets")
                    .document(pet.getIdTutor())
                    .collection("petsUsuario")
                    .document(pet.getIdPet());

            DocumentReference enderecoRef = ConfiguracaoFirebase.getFirebase().collection("enderecos") // Coleção de pets
                    .document(pet.getIdEndereco());

            holder.textViewNomePet.setText(pet.getNomePet());
            holder.textViewIdadeGenero.setText(pet.getIdadePet() + " • " + pet.getGeneroPet());
            holder.textViewStatusPet.setText(pet.getStatusPet().toUpperCase());

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

            enderecoRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Endereco endereco = snapshot.toObject(Endereco.class);
                        if (endereco != null && !pet.getStatusPet().equals("Adoção")) {
                            holder.textViewCidadePet.setText(endereco.getCidade() + " - " + endereco.getEstado());
                        } else {
                            holder.textViewCidadePet.setVisibility(View.GONE);
                        }
                    }
                } else {
                    // Tratar erro, se necessário
                    System.err.println("Erro ao recuperar o endereço: " + task.getException().getMessage());
                }
            });


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Referência ao documento do pet no Firestore
                    DocumentReference petRef = ConfiguracaoFirebase.getFirebase()
                            .collection("pets")
                            .document(pet.getIdPet());

                    // Recuperando os dados do pet
                    petRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                Pet petSelecionado = snapshot.toObject(Pet.class);

                                if (petSelecionado != null) {
                                    Intent i = new Intent(holder.itemView.getContext(), MaisInformacoesSobrePetActivity.class);
                                    i.putExtra("petSelecionado", petSelecionado);
                                    holder.itemView.getContext().startActivity(i);
                                }
                            }
                        } else {
                            // Tratar erro, se necessário
                            System.err.println("Erro ao recuperar o pet: " + task.getException().getMessage());
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
