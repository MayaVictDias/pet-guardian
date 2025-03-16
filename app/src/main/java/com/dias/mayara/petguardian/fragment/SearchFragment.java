package com.dias.mayara.petguardian.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.FiltroActivity;
import com.dias.mayara.petguardian.activity.MaisInformacoesSobrePetActivity;
import com.dias.mayara.petguardian.activity.PerfilAmigoActivity;
import com.dias.mayara.petguardian.adapter.FiltroAdapter;
import com.dias.mayara.petguardian.adapter.PesquisaUsuarioAdapter;
import com.dias.mayara.petguardian.adapter.PetsPesquisaAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.RecyclerItemClickListener;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements FiltroAdapter.OnFiltroRemovedListener {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerViewPesquisaPessoas, recyclerViewPesquisaPet, recyclerViewChips;
    private List<Usuario> listaUsuarios;
    private List<Pet> listaPets;
    private CollectionReference usuariosRef;
    private CollectionReference petsRef;
    private ImageButton buttonFiltrar;
    private PesquisaUsuarioAdapter pesquisaUsuarioAdapter;
    private PetsPesquisaAdapter petsPesquisaAdapter;
    private FiltroAdapter filtroAdapter;
    private List<String> filtros = new ArrayList<>();
    private String idUsuarioLogado;

    private RecyclerView recyclerViewFiltros;
    private RecyclerView recyclerViewPetsParaAdocao;
    private LinearLayout layoutEmptyStateFiltros;
    private LinearLayout layoutEmptyStatePets;

    private static final int FILTRO_REQUEST_CODE = 1; // Código de requisição para a FiltroActivity

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtém o ID do usuário logado
        idUsuarioLogado = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Referências do Firestore
        petsRef = ConfiguracaoFirebase.getFirebase().collection("pets");
        usuariosRef = ConfiguracaoFirebase.getFirebase().collection("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        inicializarComponentes(view);

        // Configura os RecyclerViews e a SearchView
        configurarRecycleViews(view);

        // Configuração do evento de clique para a lista de usuários
        recyclerViewPesquisaPessoas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisaPessoas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                        i.putExtra("usuarioID", usuarioSelecionado.getIdUsuario());
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

        // Configuração do evento de clique para a lista de pets
        recyclerViewPesquisaPet.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewPesquisaPet,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Pet petSelecionado = listaPets.get(position);
                        String idPetSelecionado = petSelecionado.getIdPet();
                        Intent i = new Intent(getActivity(), MaisInformacoesSobrePetActivity.class);
                        i.putExtra("petId", idPetSelecionado);
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

        // Carrega os 3 últimos pets e usuários ao abrir a tela
        carregarUltimosPets();
        carregarUltimosUsuarios();

        // Configuração da SearchView
        searchViewPesquisa.setQueryHint("Buscar por nome ou ID");
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

        buttonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir a FiltroActivity com startActivityForResult
                Intent intent = new Intent(getContext(), FiltroActivity.class);
                startActivityForResult(intent, FILTRO_REQUEST_CODE);
            }
        });

        return view;
    }

    private void checkRecyclerViewEmpty(RecyclerView recyclerView, LinearLayout emptyStateLayout, String emptyMessage) {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            TextView textViewEmptyState = emptyStateLayout.findViewById(R.id.textViewEmptyState);
            textViewEmptyState.setText(emptyMessage);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void configurarRecycleViews(View view) {
        recyclerViewPesquisaPessoas = view.findViewById(R.id.recyclerViewPesquisaPessoas);
        recyclerViewPesquisaPet = view.findViewById(R.id.recyclerViewPesquisaPet);
        recyclerViewFiltros = view.findViewById(R.id.recyclerViewFiltros);
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);

        // Inicializa as listas
        listaUsuarios = new ArrayList<>();
        listaPets = new ArrayList<>();

        // Configuração dos adapters
        pesquisaUsuarioAdapter = new PesquisaUsuarioAdapter(listaUsuarios);
        petsPesquisaAdapter = new PetsPesquisaAdapter(listaPets);
        filtroAdapter = new FiltroAdapter(filtros, this);

        // Configuração do RecyclerView de pessoas
        recyclerViewPesquisaPessoas.setHasFixedSize(true);
        recyclerViewPesquisaPessoas.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPesquisaPessoas.setAdapter(pesquisaUsuarioAdapter);

        // Configuração do RecyclerView de pets
        recyclerViewPesquisaPet.setHasFixedSize(true);
        recyclerViewPesquisaPet.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewPesquisaPet.setAdapter(petsPesquisaAdapter);

        // Configuração do RecyclerView de chips (etiquetas)
        recyclerViewFiltros.setHasFixedSize(true);
        recyclerViewFiltros.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFiltros.setAdapter(filtroAdapter);
    }

    private void pesquisarUsuarios(String textoDigitado) {
        // Limpa a lista
        listaUsuarios.clear();

        // Confere se tem algum texto para ser pesquisado
        if (textoDigitado.length() > 0) {
            String textoLowercase = textoDigitado.toLowerCase();

            // Query para pesquisar usuários no Firestore
            Query query = usuariosRef
                    .whereGreaterThanOrEqualTo("nomeLowercaseUsuario", textoLowercase)
                    .whereLessThanOrEqualTo("nomeLowercaseUsuario", textoLowercase + "\uf8ff");

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listaUsuarios.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        Usuario usuario = document.toObject(Usuario.class);
                        if (usuario != null && !idUsuarioLogado.equals(usuario.getIdUsuario())) {
                            listaUsuarios.add(usuario);
                        }
                    }
                    pesquisaUsuarioAdapter.notifyDataSetChanged();
                    checkRecyclerViewEmpty(recyclerViewPesquisaPessoas, layoutEmptyStateFiltros, "Não há usuários encontrados");
                } else {
                    Log.e("SearchFragment", "Erro ao pesquisar usuários: " + task.getException().getMessage());
                }
            });
        }
    }

    private void pesquisarPets(String textoDigitado) {
        // Limpa a lista
        listaPets.clear();

        // Confere se tem algum texto para ser pesquisado
        if (textoDigitado.length() > 0) {
            String textoLowercase = textoDigitado.toLowerCase();

            // Query para pesquisar pets no Firestore pelo nome ou idPet
            Query queryNome = petsRef
                    .whereGreaterThanOrEqualTo("nomeLowerCasePet", textoLowercase)
                    .whereLessThanOrEqualTo("nomeLowerCasePet", textoLowercase + "\uf8ff");

            Query queryIdPet = petsRef
                    .whereEqualTo("idPet", textoDigitado); // Busca exata pelo idPet

            // Executa as consultas
            queryNome.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    listaPets.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        Pet pet = document.toObject(Pet.class);
                        if (pet != null && !listaPets.contains(pet)) { // Evita duplicatas
                            listaPets.add(pet);
                        }
                    }

                    // Agora, busca pelo idPet
                    queryIdPet.get().addOnCompleteListener(taskIdPet -> {
                        if (taskIdPet.isSuccessful()) {
                            for (DocumentSnapshot document : taskIdPet.getResult()) {
                                Pet pet = document.toObject(Pet.class);
                                if (pet != null && !listaPets.contains(pet)) { // Evita duplicatas
                                    listaPets.add(pet);
                                }
                            }

                            // Atualiza o adapter
                            petsPesquisaAdapter.notifyDataSetChanged();
                            checkRecyclerViewEmpty(recyclerViewPesquisaPet, layoutEmptyStatePets, "Não há pets encontrados");
                        } else {
                            Log.e("SearchFragment", "Erro ao pesquisar pets pelo idPet: " + taskIdPet.getException().getMessage());
                        }
                    });
                } else {
                    Log.e("SearchFragment", "Erro ao pesquisar pets pelo nome: " + task.getException().getMessage());
                }
            });
        } else {
            // Se o texto estiver vazio, recarrega os últimos pets
            carregarUltimosPets();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTRO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Receber os filtros do Intent
            String status = data.getStringExtra("status");
            String idade = data.getStringExtra("idade");
            String genero = data.getStringExtra("genero");
            String especie = data.getStringExtra("especie");
            String corOlhos = data.getStringExtra("corOlhos");
            String corPredominante = data.getStringExtra("corPredominante");

            // Atualizar a lista de filtros ativos
            List<String> filtrosAtivos = new ArrayList<>();
            if (!status.equals("Selecione")) filtrosAtivos.add("Status: " + status);
            if (!idade.equals("Selecione")) filtrosAtivos.add("Idade: " + idade);
            if (!genero.equals("Selecione")) filtrosAtivos.add("Gênero: " + genero);
            if (!especie.equals("Selecione")) filtrosAtivos.add("Espécie: " + especie);
            if (!corOlhos.equals("Selecione")) filtrosAtivos.add("Cor dos Olhos: " + corOlhos);
            if (!corPredominante.equals("Selecione")) filtrosAtivos.add("Cor Predominante: " + corPredominante);

            // Atualizar o RecyclerView de filtros
            filtroAdapter.setFiltros(filtrosAtivos);
            filtroAdapter.notifyDataSetChanged();

            // Verifica se o RecyclerView de filtros está vazio
            checkRecyclerViewFiltrosEmpty();

            // Aplicar os filtros na consulta ao Firestore
            aplicarFiltros(status, idade, genero, especie, corOlhos, corPredominante);
        }
    }

    private void aplicarFiltros(String status, String idade, String genero, String especie, String corOlhos, String corPredominante) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Inicia a consulta com o filtro básico (todos os pets)
        Query query = petsRef;

        // Aplica os filtros adicionais, se selecionados
        if (!status.equals("Selecione")) {
            query = query.whereEqualTo("statusPet", status);
        }
        if (!idade.equals("Selecione")) {
            query = query.whereEqualTo("idadePet", idade);
        }
        if (!genero.equals("Selecione")) {
            query = query.whereEqualTo("generoPet", genero);
        }
        if (!especie.equals("Selecione")) {
            query = query.whereEqualTo("especiePet", especie);
        }
        if (!corOlhos.equals("Selecione")) {
            query = query.whereEqualTo("corDosOlhosPet", corOlhos);
        }
        if (!corPredominante.equals("Selecione")) {
            query = query.whereEqualTo("corPredominantePet", corPredominante);
        }

        // Executa a consulta
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Limpa a lista atual de pets
                listaPets.clear();

                // Adiciona os pets filtrados à lista
                for (DocumentSnapshot document : task.getResult()) {
                    Pet pet = document.toObject(Pet.class);
                    if (pet != null) {
                        listaPets.add(pet);
                    }
                }

                // Atualiza a interface
                petsPesquisaAdapter.notifyDataSetChanged();
                checkRecyclerViewEmpty(recyclerViewPesquisaPet, layoutEmptyStatePets, "Não há pets encontrados");
            } else {
                Log.e("SearchFragment", "Erro ao aplicar filtros: " + task.getException().getMessage());
            }
        });
    }

    private void carregarUltimosPets() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Ordena por data de cadastro e limita a 3 resultados
        petsRef.orderBy("dataCadastro", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaPets.clear(); // Limpa a lista atual
                        for (DocumentSnapshot document : task.getResult()) {
                            Pet pet = document.toObject(Pet.class);
                            if (pet != null) {
                                listaPets.add(pet);
                            }
                        }
                        // Atualiza o adapter
                        petsPesquisaAdapter.notifyDataSetChanged();
                        checkRecyclerViewEmpty(recyclerViewPesquisaPet, layoutEmptyStatePets, "Não há pets cadastrados");
                    } else {
                        Log.e("SearchFragment", "Erro ao carregar últimos pets: " + task.getException().getMessage());
                    }
                });
    }

    private void carregarUltimosUsuarios() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usuariosRef = db.collection("usuarios");

        // Ordena por data de cadastro e limita a 3 resultados
        usuariosRef.orderBy("dataCadastro", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaUsuarios.clear(); // Limpa a lista atual
                        for (DocumentSnapshot document : task.getResult()) {
                            Usuario usuario = document.toObject(Usuario.class);
                            if (usuario != null && !idUsuarioLogado.equals(usuario.getIdUsuario())) {
                                listaUsuarios.add(usuario);
                            }
                        }
                        // Atualiza o adapter
                        pesquisaUsuarioAdapter.notifyDataSetChanged();
                        checkRecyclerViewEmpty(recyclerViewPesquisaPessoas, layoutEmptyStateFiltros, "Não há usuários cadastrados");
                    } else {
                        Log.e("SearchFragment", "Erro ao carregar últimos usuários: " + task.getException().getMessage());
                    }
                });
    }

    private void checkRecyclerViewFiltrosEmpty() {
        if (filtroAdapter == null || filtroAdapter.getItemCount() == 0) {
            recyclerViewFiltros.setVisibility(View.GONE); // Esconde o RecyclerView
        } else {
            recyclerViewFiltros.setVisibility(View.VISIBLE); // Mostra o RecyclerView
        }
    }

    private void inicializarComponentes(View view) {
        buttonFiltrar = view.findViewById(R.id.buttonFiltrar);

        recyclerViewFiltros = view.findViewById(R.id.recyclerViewFiltros);
        recyclerViewPetsParaAdocao = view.findViewById(R.id.recyclerViewPetsParaAdocao);
        layoutEmptyStateFiltros = view.findViewById(R.id.layoutEmptyStateFiltros);
        layoutEmptyStatePets = view.findViewById(R.id.layoutEmptyStatePets);

        // Verifica se o RecyclerView de filtros está vazio ao inicializar
        checkRecyclerViewFiltrosEmpty();
    }

    @Override
    public void onFiltroRemoved(String filtro) {
        // Lógica para quando um filtro é removido
        Log.d("SearchFragment", "Filtro removido: " + filtro);

        // Verifica se o RecyclerView de filtros está vazio
        checkRecyclerViewFiltrosEmpty();
    }

    @Override
    public void onTodosFiltrosRemovidos() {
        // Lógica para quando todos os filtros são removidos
        Log.d("SearchFragment", "Todos os filtros foram removidos");

        // Verifica se o RecyclerView de filtros está vazio
        checkRecyclerViewFiltrosEmpty();

        // Recarrega a lista de pets e usuários sem filtros
        pesquisarUsuarios("");
        pesquisarPets("");
    }
}