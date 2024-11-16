package com.dias.mayara.petguardian.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button buttonUsarMinhaLocalizacao;
    private RecyclerView recyclerViewCarrosselDesaparecidos, recyclerViewCarrosselAdocao;
    private PetsAdapter petsAdapterAdocao;
    private PetsAdapter petsAdapterDesaparecidos;
    private List<Pet> listaPetsAdocao = new ArrayList<>(); // Inicializando a lista de pets
    private List<Pet> listaPetsDesaparecidos = new ArrayList<>(); // Inicializando a lista de pets

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewCarrosselDesaparecidos = view.findViewById(R.id.recyclerViewCarrosselDesaparecidos);
        recyclerViewCarrosselAdocao = view.findViewById(R.id.recyclerViewCarrosselAdocao);

        // Inicializando a lista e os adaptadores
        petsAdapterAdocao = new PetsAdapter(listaPetsAdocao);
        recyclerViewCarrosselAdocao.setAdapter(petsAdapterAdocao);
        petsAdapterDesaparecidos = new PetsAdapter(listaPetsDesaparecidos);
        recyclerViewCarrosselDesaparecidos.setAdapter(petsAdapterDesaparecidos);

        getPetsAdocao(); // Carregar pets para adoção
        getPetsDesaparecidos(); // Carregar pets desaparecidos

        // Configurando o layout manager para os RecyclerViews
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCarrosselAdocao.setLayoutManager(layoutManagerAdocao);

        LinearLayoutManager layoutManagerDesaparecidos = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCarrosselDesaparecidos.setLayoutManager(layoutManagerDesaparecidos);

        return view;
    }

    private void getPetsAdocao() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("feedPets").child("adocao");

        // Usando addValueEventListener para atualizações em tempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaPetsAdocao.clear(); // Limpa a lista antes de adicionar novos dados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idPet = snapshot.child("idPet").getValue(String.class);
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);
                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String imagemUrl = snapshot.child("imagemUrl").getValue(String.class);
                    String idadePet = snapshot.child("idadePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);
                    long dataCadastro = snapshot.child("dataCadastro").getValue(Long.class);

                    listaPetsAdocao.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));

                    Collections.reverse(listaPetsAdocao); // Traz os eventos mais recentes como primeiros do feed
                }
                // Notifica o adapter sobre as mudanças na lista
                petsAdapterAdocao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getPetsDesaparecidos() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("feedPets").child("desaparecido");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaPetsDesaparecidos.clear(); // Limpa a lista antes de adicionar novos dados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idPet = snapshot.child("idPet").getValue(String.class);
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);
                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String imagemUrl = snapshot.child("imagemUrl").getValue(String.class);
                    String idadePet = snapshot.child("idadePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);
                    long dataCadastro = snapshot.child("dataCadastro").getValue(Long.class);

                    listaPetsDesaparecidos.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));

                    Collections.reverse(listaPetsDesaparecidos); // Traz os eventos mais recentes como primeiros do feed
                }
                petsAdapterDesaparecidos.notifyDataSetChanged(); // Notifica o adaptador sobre as mudanças
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Lida com erros, se necessário
            }
        });
    }

    private void inicializarComponentes(View view) {
        buttonUsarMinhaLocalizacao = view.findViewById(R.id.buttonUsarMinhaLocalizacao);
    }
}
