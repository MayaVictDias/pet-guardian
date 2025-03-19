package com.dias.mayara.petguardian.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.FiltroActivity;
import com.dias.mayara.petguardian.adapter.FiltroAdapter;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment implements FiltroAdapter.OnFiltroRemovedListener {

    private Usuario usuarioLogado;
    private LinearLayout layoutSemPets, layoutComPets;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar, buttonCompartilharPerfil;
    private RecyclerView recyclerViewPetsParaAdocao, recyclerViewFiltros;
    private EditText editTextPesquisarPet;
    private SearchView searchViewPesquisa;

    private FirebaseUser usuarioPerfil;
    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioLogadoRef;
    private String idUsuarioLogado;

    // Variáveis para armazenar os filtros atuais
    private String status = "Selecione";
    private String idade = "Selecione";
    private String genero = "Selecione";
    private String especie = "Selecione";
    private String corOlhos = "Selecione";
    private String corPredominante = "Selecione";

    private List<Pet> petListAdocao = new ArrayList<>();
    private PetsAdapter petsAdapterAdocao;
    private ListenerRegistration petsListener;
    private static final int FILTRO_REQUEST_CODE = 1;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance() {
        return new PerfilFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializa a instância do Firebase e referências do banco de dados
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.collection("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Obtém os dados do usuário logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioLogadoRef = usuariosRef.document(usuarioLogado.getIdUsuario());
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        // Inicializa os componentes de interface
        inicializarComponentes(view);

        // Recupera os dados do usuário logado
        recuperarDadosUsuarioLogado();

        // Exibir foto do usuário, caso ele tenha setado uma
        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null) {
            Glide.with(PerfilFragment.this).load(url).into(imagemPerfilUsuario);
        } else {
            imagemPerfilUsuario.setImageResource(R.drawable.profile_image);
        }

        // Inicializando as listas e os adapters antes de carregar os dados do Firebase
        petListAdocao = new ArrayList<>();
        petsAdapterAdocao = new PetsAdapter(petListAdocao);

        // Configura o adapter e layout manager para pets para adoção
        recyclerViewPetsParaAdocao.setAdapter(petsAdapterAdocao);
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

        recyclerViewFiltros.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> filtros = new ArrayList<>();
        FiltroAdapter adapter = new FiltroAdapter(filtros, this);
        recyclerViewFiltros.setAdapter(adapter);

        // Torna o RecyclerView visível
        recyclerViewFiltros.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();

        // Inicia a escuta em tempo real para os pets
        getPetsAdocao();

        // No método onCreate ou em outro local apropriado
        buttonCompartilharPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Recupera o ID do usuário (substitua pelo método correto para obter o ID)
                String idUsuario = UsuarioFirebase.getIdentificadorUsuario();

                // Gera o link dinâmico
                String deepLink = "https://petguardian.com/perfil?id=" + idUsuario;

                // Copia o link para a área de transferência
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Link do Perfil", deepLink);
                clipboard.setPrimaryClip(clip);

                // Exibe uma mensagem de confirmação
                Toast.makeText(getContext(), "Link do perfil copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
            }
        });

        // Adicionar um TextWatcher ao editTextPesquisarPet
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pesquisarPets(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarPets(newText);
                return true;
            }
        });

        buttonFiltrar.setOnClickListener(v -> {
            // Abrir a FiltroActivity com startActivityForResult
            Intent intent = new Intent(getContext(), FiltroActivity.class);
            startActivityForResult(intent, FILTRO_REQUEST_CODE);
        });

        return view;
    }

    private void inicializarComponentes(View view) {
        textViewNomeUsuario = view.findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = view.findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = view.findViewById(R.id.imagemUsuario);
        recyclerViewPetsParaAdocao = view.findViewById(R.id.recyclerViewPetsParaAdocao);
        recyclerViewFiltros = view.findViewById(R.id.recyclerViewFiltros);
        buttonFiltrar = view.findViewById(R.id.buttonFiltrar);
        buttonCompartilharPerfil = view.findViewById(R.id.buttonCompartilharPerfil);
        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);

        // Inicializa os layouts de "Sem Pets" e "Com Pets"
        layoutSemPets = view.findViewById(R.id.layoutSemPets);
        layoutComPets = view.findViewById(R.id.layoutComPets);
    }

    @Override
    public void onFiltroRemoved(String filtro) {
        // Lógica para remover um filtro específico
        Log.d("PerfilFragment", "Filtro removido: " + filtro);
    }

    @Override
    public void onTodosFiltrosRemovidos() {
        // Lógica para remover todos os filtros
        Log.d("PerfilFragment", "Todos os filtros foram removidos");

        // Recarregar a lista de pets sem filtros
        getPetsAdocao();
    }

    private void getPetsAdocao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Verifica se há filtros aplicados
        FiltroAdapter adapter = (FiltroAdapter) recyclerViewFiltros.getAdapter();
        if (adapter != null && adapter.getItemCount() > 0) {
            // Aplica os filtros usando as variáveis de classe
            aplicarFiltros(status, idade, genero, especie, corOlhos, corPredominante);
        } else {
            // Carrega todos os pets sem filtros
            petsRef.whereEqualTo("idTutor", idUsuarioLogado)
                    .addSnapshotListener((snapshots, error) -> {
                        if (error != null) {
                            Log.w("Firestore", "Erro ao ouvir mudanças", error);
                            return;
                        }

                        if (snapshots != null && !snapshots.isEmpty()) {
                            petListAdocao.clear();
                            for (QueryDocumentSnapshot snapshot : snapshots) {
                                Pet pet = snapshot.toObject(Pet.class);
                                petListAdocao.add(pet);
                            }

                            // Atualiza a interface com base na lista de pets
                            if (petListAdocao.isEmpty()) {
                                layoutSemPets.setVisibility(View.VISIBLE);
                                layoutComPets.setVisibility(View.GONE);
                            } else {
                                layoutSemPets.setVisibility(View.GONE);
                                layoutComPets.setVisibility(View.VISIBLE);
                            }

                            petsAdapterAdocao.notifyDataSetChanged();
                        } else {
                            layoutSemPets.setVisibility(View.VISIBLE);
                            layoutComPets.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void aplicarFiltros(String status, String idade, String genero, String especie, String corOlhos, String corPredominante) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Inicia a consulta com o filtro básico (pets do usuário logado)
        com.google.firebase.firestore.Query query = petsRef.whereEqualTo("idTutor", idUsuarioLogado);

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
                petListAdocao.clear();

                // Adiciona os pets filtrados à lista
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    Pet pet = snapshot.toObject(Pet.class);
                    petListAdocao.add(pet);
                }

                // Atualiza a interface com base na lista de pets
                if (petListAdocao.isEmpty()) {
                    layoutSemPets.setVisibility(View.VISIBLE); // Exibe "Não há pets cadastrados"
                    layoutComPets.setVisibility(View.GONE); // Oculta a lista de pets
                } else {
                    layoutSemPets.setVisibility(View.GONE); // Oculta "Não há pets cadastrados"
                    layoutComPets.setVisibility(View.VISIBLE); // Exibe a lista de pets
                }

                // Notifica o adapter sobre as mudanças na lista
                petsAdapterAdocao.notifyDataSetChanged();
            } else {
                // Trata erros na consulta
                Log.e("PerfilFragment", "Erro ao aplicar filtros", task.getException());
            }
        });
    }

    private void pesquisarPets(String textoDigitado) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Limpa a lista de pets
        petListAdocao.clear();

        // Confere se há texto para pesquisar
        if (textoDigitado.length() > 0) {
            String textoLowercase = textoDigitado.toLowerCase();

            // Query para pesquisar pets no Firestore
            Query queryNome = petsRef
                    .whereEqualTo("idTutor", idUsuarioLogado) // Filtra apenas os pets do usuário logado
                    .whereGreaterThanOrEqualTo("nomeLowerCasePet", textoLowercase)
                    .whereLessThanOrEqualTo("nomeLowerCasePet", textoLowercase + "\uf8ff");

            Query queryIdPet = petsRef
                    .whereEqualTo("idTutor", idUsuarioLogado) // Filtra apenas os pets do usuário logado
                    .whereEqualTo("idPet", textoDigitado); // Busca exata pelo idPet

            // Executa as consultas
            queryNome.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    petListAdocao.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        Pet pet = document.toObject(Pet.class);
                        if (pet != null && !petListAdocao.contains(pet)) { // Evita duplicatas
                            petListAdocao.add(pet);
                        }
                    }

                    // Agora, busca pelo idPet
                    queryIdPet.get().addOnCompleteListener(taskIdPet -> {
                        if (taskIdPet.isSuccessful()) {
                            for (DocumentSnapshot document : taskIdPet.getResult()) {
                                Pet pet = document.toObject(Pet.class);
                                if (pet != null && !petListAdocao.contains(pet)) { // Evita duplicatas
                                    petListAdocao.add(pet);
                                }
                            }

                            // Atualiza o adapter
                            petsAdapterAdocao.notifyDataSetChanged();
                            Log.d("PerfilFragment lista de pets: ", petListAdocao.toString());
                            checkRecyclerViewEmpty();
                        } else {
                            Log.e("PerfilFragment", "Erro ao pesquisar pets pelo idPet: " + taskIdPet.getException().getMessage());
                        }
                    });
                } else {
                    Log.e("PerfilFragment", "Erro ao pesquisar pets pelo nome: " + task.getException().getMessage());
                }
            });
        } else {
            // Se o texto estiver vazio, recarrega todos os pets do usuário
            getPetsAdocao();
        }
    }

    private void checkRecyclerViewEmpty() {
        if (petListAdocao.isEmpty()) {
            layoutSemPets.setVisibility(View.VISIBLE); // Exibe "Não há pets cadastrados"
            layoutComPets.setVisibility(View.GONE); // Oculta a lista de pets
        } else {
            layoutSemPets.setVisibility(View.GONE); // Oculta "Não há pets cadastrados"
            layoutComPets.setVisibility(View.VISIBLE); // Exibe a lista de pets
        }
    }

    private void recuperarDadosUsuarioLogado() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(idUsuarioLogado);

        // Consultando os dados do usuário
        usuarioRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Criando o objeto Usuario a partir dos dados do Firestore
                    Usuario usuario = document.toObject(Usuario.class);

                    if (usuario != null) {
                        // Atualizando a interface com os dados do usuário
                        textViewNomeUsuario.setText(usuario.getNomeUsuario());
                        textViewPerfilCidadeUsuario.setText(usuario.getCidadeUsuario() + " - " + usuario.getEstadoUsuario());
                    }
                }
            } else {
                Log.e("PerfilFragment", "Erro ao recuperar dados do usuário", task.getException());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILTRO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Receber os filtros do Intent
            status = data.getStringExtra("status");
            idade = data.getStringExtra("idade");
            genero = data.getStringExtra("genero");
            especie = data.getStringExtra("especie");
            corOlhos = data.getStringExtra("corOlhos");
            corPredominante = data.getStringExtra("corPredominante");

            // Atualizar a lista de filtros ativos
            List<String> filtrosAtivos = new ArrayList<>();
            if (!status.equals("Selecione")) filtrosAtivos.add("Status: " + status);
            if (!idade.equals("Selecione")) filtrosAtivos.add("Idade: " + idade);
            if (!genero.equals("Selecione")) filtrosAtivos.add("Gênero: " + genero);
            if (!especie.equals("Selecione")) filtrosAtivos.add("Espécie: " + especie);
            if (!corOlhos.equals("Selecione")) filtrosAtivos.add("Cor dos Olhos: " + corOlhos);
            if (!corPredominante.equals("Selecione")) filtrosAtivos.add("Cor Predominante: " + corPredominante);

            // Atualizar o RecyclerView de filtros
            FiltroAdapter adapter = (FiltroAdapter) recyclerViewFiltros.getAdapter();
            if (adapter != null) {
                adapter.setFiltros(filtrosAtivos);
                adapter.notifyDataSetChanged();
            }

            // Aplicar os filtros na consulta ao Firestore
            aplicarFiltros(status, idade, genero, especie, corOlhos, corPredominante);
        }
    }
}