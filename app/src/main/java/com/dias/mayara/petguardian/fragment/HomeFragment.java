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

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Time;
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

        getPetsAdocao(); // Carregar pets para adoção

        // Configurando o layout manager para os RecyclerViews
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCarrosselAdocao.setLayoutManager(layoutManagerAdocao);

        return view;
    }

    private void getPetsAdocao() {
        // Referência para a coleção de feedPets e subcoleção adocao
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Consultando documentos
        petsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaPetsAdocao.clear(); // Limpa a lista antes de adicionar novos dados
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Obtendo os dados do documento
                    String idPet = document.getString("idPet");
                    String idEndereco = document.getString("idEndereco");
                    String idTutor = document.getString("idTutor");
                    String especiePet = document.getString("especiePet");
                    String nomePet = document.getString("nomePet");
                    String generoPet = document.getString("generoPet");
                    String imagemUrl = document.getString("imagemUrl");
                    String idadePet = document.getString("idadePet");
                    String sobreOPet = document.getString("sobreOPet");
                    String statusPet = document.getString("statusPet");
                    Timestamp dataCadastro = document.getTimestamp("dataCadastro");

                    listaPetsAdocao.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
                }

                // Traz os eventos mais recentes como primeiros do feed
                Collections.reverse(listaPetsAdocao);

                // Notifica o adapter sobre as mudanças na lista
                petsAdapterAdocao.notifyDataSetChanged();
            } else {
                // Lida com erros, se necessário
                Log.w("Firestore", "Erro ao obter os dados", task.getException());
            }
        });
    }



    private void getPetsDesaparecidos() {
        // Referência para a coleção de feedPets e subcoleção desaparecido
        CollectionReference petsRef = ConfiguracaoFirebase.getFirebase().collection("pets").
                document().collection("pets");

        // Consultando documentos
        petsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaPetsDesaparecidos.clear(); // Limpa a lista antes de adicionar novos dados
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Obtendo os dados do documento
                    String idPet = document.getString("idPet");
                    String idEndereco = document.getString("idEndereco");
                    String idTutor = document.getString("idTutor");
                    String especiePet = document.getString("especiePet");
                    String nomePet = document.getString("nomePet");
                    String generoPet = document.getString("generoPet");
                    String imagemUrl = document.getString("imagemUrl");
                    String idadePet = document.getString("idadePet");
                    String sobreOPet = document.getString("sobreOPet");
                    String statusPet = document.getString("statusPet");
                    Timestamp dataCadastro = document.getTimestamp("dataCadastro");

                    listaPetsDesaparecidos.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
                }

                // Traz os eventos mais recentes como primeiros do feed
                Collections.reverse(listaPetsDesaparecidos);

                // Notifica o adaptador sobre as mudanças
                petsAdapterDesaparecidos.notifyDataSetChanged();
            } else {
                // Lida com erros, se necessário
                Log.w("Firestore", "Erro ao obter os dados", task.getException());
            }
        });
    }



    private void inicializarComponentes(View view) {
        buttonUsarMinhaLocalizacao = view.findViewById(R.id.buttonUsarMinhaLocalizacao);
    }
}
