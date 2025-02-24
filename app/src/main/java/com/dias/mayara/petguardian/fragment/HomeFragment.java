package com.dias.mayara.petguardian.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewCarrosselAdocao;
    private LinearLayout layoutSemPets, layoutComPets;
    private PetsAdapter petsAdapterAdocao;
    private List<Pet> listaPetsAdocao = new ArrayList<>(); // Inicializando a lista de pets

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
        recyclerViewCarrosselAdocao = view.findViewById(R.id.recyclerViewCarrosselAdocao);

        // Inicializando a lista e os adaptadores
        petsAdapterAdocao = new PetsAdapter(listaPetsAdocao);
        recyclerViewCarrosselAdocao.setAdapter(petsAdapterAdocao);

        getPetsAdocao(); // Carregar pets para adoção

        inicializarComponentes(view);

        // Configurando o layout manager para os RecyclerViews
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCarrosselAdocao.setLayoutManager(layoutManagerAdocao);

        return view;
    }

    private void getPetsAdocao() {
        // Referência para a coleção de feedPets e subcoleção adocao
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Usando o SnapshotListener para ouvir mudanças em tempo real
        petsRef.orderBy("dataCadastro", Query.Direction.DESCENDING).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w("Firestore", "Erro ao obter os dados", e);
                return;
            }

            if (queryDocumentSnapshots != null) {
                listaPetsAdocao.clear(); // Limpa a lista antes de atualizar

                // Preenche a lista com os dados mais recentes
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    // Obtendo os dados do documento
                    String idPet = snapshot.getString("idPet");
                    String idTutor = snapshot.getString("idTutor");
                    String nomePet = snapshot.getString("nomePet");
                    String idadePet = snapshot.getString("idadePet");
                    String generoPet = snapshot.getString("generoPet");
                    String especiePet = snapshot.getString("especiePet");
                    String raca = snapshot.getString("racaPet");
                    String corPredominantePet = snapshot.getString("corPredominantePet");
                    String corDosOlhosPet = snapshot.getString("corDosOlhosPet");
                    String portePet = snapshot.getString("portePet");
                    String imagemUrl = snapshot.getString("imagemUrl");
                    String statusVacinacao = snapshot.getString("statusVacinacao");
                    String vacinasTomadas = snapshot.getString("vacinasTomadas");
                    String vermifugado = snapshot.getString("vermifugado");

                    Timestamp dataVermifugacao = null;
                    if (snapshot.contains("dataVermifugacao") && snapshot.get("dataVermifugacao") != null) {
                        Object dataObj = snapshot.get("dataVermifugacao");
                        if (dataObj instanceof Timestamp) {
                            dataVermifugacao = (Timestamp) dataObj;
                        } else {
                            Log.w("Firestore", "O campo dataVermifugacao não é um Timestamp válido.");
                        }
                    }

                    String necessidadesEspeciais = snapshot.getString("necessidadesEspeciais");
                    String doencasTratamentos = snapshot.getString("doencasTratamentos");
                    String statusCastracao = snapshot.getString("statusCastracao");
                    String nivelEnergia = snapshot.getString("nivelEnergia");
                    String sociabilidade = snapshot.getString("sociabilidade");
                    boolean isAdestrado = snapshot.getBoolean("isAdestrado");
                    Timestamp dataCadastro = snapshot.getTimestamp("dataCadastro");

                    listaPetsAdocao.add(new Pet(idPet, idTutor, nomePet, idadePet, generoPet, especiePet,
                            raca, corPredominantePet, corDosOlhosPet, portePet, imagemUrl, statusVacinacao,
                            vacinasTomadas, vermifugado, dataVermifugacao, necessidadesEspeciais,
                            doencasTratamentos, statusCastracao, nivelEnergia, sociabilidade, isAdestrado,
                            dataCadastro));
                }

                // Notifica o adaptador para atualizar a lista
                if (petsAdapterAdocao != null) {
                    petsAdapterAdocao.notifyDataSetChanged();
                }

                // Atualiza a visibilidade dos elementos
                atualizarVisibilidade();
            } else {
                Log.d("Firestore", "Nenhum pet encontrado para adoção.");
            }
        });
    }


    private void atualizarVisibilidade() {
        if (listaPetsAdocao.isEmpty()) {
            layoutSemPets.setVisibility(View.VISIBLE);
            layoutComPets.setVisibility(View.GONE);
        } else {
            layoutSemPets.setVisibility(View.GONE);
            layoutComPets.setVisibility(View.VISIBLE);
        }
    }


    private void inicializarComponentes(View view) {
        layoutSemPets = view.findViewById(R.id.layoutSemPets);
        layoutComPets = view.findViewById(R.id.layoutComPets);

    }
}
