package com.dias.mayara.petguardian.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.MaisInformacoesSobrePetActivity;
import com.dias.mayara.petguardian.activity.PerfilAmigoActivity;
import com.dias.mayara.petguardian.adapter.PesquisaUsuarioAdapter;
import com.dias.mayara.petguardian.adapter.PetsPesquisaAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.RecyclerItemClickListener;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisaPessoas, recyclerViewPesquisaPet;
    private List<Usuario> listaUsuarios;
    private List<Pet> listaPets;
    private DatabaseReference usuariosRef, petsRef;
    private PesquisaUsuarioAdapter pesquisaUsuarioAdapter;
    private PetsPesquisaAdapter petsPesquisaAdapter;
    private String idUsuarioLogado;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idUsuarioLogado = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerViewPesquisaPessoas = view.findViewById(R.id.recyclerViewPesquisaPessoas);
        recyclerViewPesquisaPet = view.findViewById(R.id.recyclerViewPesquisaPet);
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);

        listaUsuarios = new ArrayList<>();
        listaPets = new ArrayList<>();

        petsRef = ConfiguracaoFirebase.getFirebase().child("todosPets");
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        // Configuração do adapter de pesquisa
        pesquisaUsuarioAdapter = new PesquisaUsuarioAdapter(listaUsuarios);
        petsPesquisaAdapter = new PetsPesquisaAdapter(listaPets);

        // Configuração do RecyclerView pessoas
        recyclerViewPesquisaPessoas.setHasFixedSize(true);
        recyclerViewPesquisaPessoas.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPesquisaPessoas.setAdapter(pesquisaUsuarioAdapter);

        // Configuração do RecyclerView pets
        recyclerViewPesquisaPet.setHasFixedSize(true);
        recyclerViewPesquisaPet.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerViewPesquisaPet.setAdapter(petsPesquisaAdapter);

        // Configuração do evento de clique
        recyclerViewPesquisaPessoas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisaPessoas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);

                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        // COnfiguração do evento de clique
        recyclerViewPesquisaPet.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisaPet,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Pet petSelecionado = listaPets.get(position);
                        Intent i = new Intent(getActivity(), MaisInformacoesSobrePetActivity.class);

                        i.putExtra("petSelecionado", petSelecionado);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        // Configuração da searchView
        searchViewPesquisa.setQueryHint("Buscar");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String textoDigitado) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String textoDigitado) {

                pesquisarUsuarios(textoDigitado);
                pesquisarPets(textoDigitado);

                return true;
            }
        });

        return view;
    }

    private void pesquisarUsuarios(String textoDigitado) {

        // Limpa a lista
        listaUsuarios.clear();

        // Confere se tem algum texto pra ser pesquisado
        if (textoDigitado.length() > 0) {

            String textoUppercase = textoDigitado.toUpperCase();

            Query query = usuariosRef.orderByChild("nomeUppercaseUsuario")
                    .startAt(textoUppercase)
                    .endAt(textoUppercase + "\uf8ff");
            // OrderByChild: Ordenar por nome que começa com textoDigitado ou termina com ele

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Limpa a lista
                    listaUsuarios.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Usuario usuario = ds.getValue(Usuario.class);

                        // Verifica se é o usuário logado e remove da lista
                        if (idUsuarioLogado.equals(usuario.getIdUsuario())) {
                            continue;
                        }

                        listaUsuarios.add(usuario);


                    }

                    int totalUsuariosRetornados = listaUsuarios.size();

                    // Avisar o adapter que houve uma atualização nos itens retornados
                    pesquisaUsuarioAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void pesquisarPets(String textoDigitado) {

        // Limpa a lista
        listaPets.clear();

        // Confere se tem algum texto pra ser pesquisado
        if (textoDigitado.length() > 0) {
            // Converte o texto digitado para uppercase para a busca
            String textoUppercase = textoDigitado.toUpperCase();

            Query query = petsRef.orderByChild("nomeUppercasePet")
                    .startAt(textoUppercase)
                    .endAt(textoUppercase + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Limpa a lista
                    listaPets.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        // Obtem o objeto Pet e adiciona à lista
                        Pet pet = ds.getValue(Pet.class);

                        if (pet != null) {
                            listaPets.add(pet);
                        }
                    }

                    // Avisar o adapter que houve uma atualização nos itens retornados
                    petsPesquisaAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Trate possíveis erros aqui
                }
            });
        }
    }

}