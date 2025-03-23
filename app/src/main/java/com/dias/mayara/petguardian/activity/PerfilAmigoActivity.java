package com.dias.mayara.petguardian.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.adapter.FiltroAdapter;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.Timestamp;
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

public class PerfilAmigoActivity extends AppCompatActivity implements FiltroAdapter.OnFiltroRemovedListener {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewQuantidadePetsCadastrados;
    private Toolbar toolbar;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar, buttonCompartilharPerfil;
    private RecyclerView recyclerViewPetsParaAdocao, recyclerViewFiltros;
    private LinearLayout layoutSemPets, layoutComPets;

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioAmigoRef;
    private ListenerRegistration listenerRegistrationPerfilAmigo;
    private ListenerRegistration listenerRegistrationPetsAdocao;

    private String idUsuarioLogado;
    private String usuarioID;

    private List<Pet> petListAdocao = new ArrayList<>();
    private PetsAdapter petsAdapterAdocao;
    private FiltroAdapter filtroAdapter;

    // Variáveis para armazenar os filtros atuais
    private String status = "Selecione";
    private String idade = "Selecione";
    private String genero = "Selecione";
    private String especie = "Selecione";
    private String corOlhos = "Selecione";
    private String corPredominante = "Selecione";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Inicializa a instância do Firebase e referências do banco de dados
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.collection("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Obtém os dados do usuário logado e configura a referência para o nó do usuário específico
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // Inicializa os componentes de interface
        inicializarComponentes();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recupera os dados do usuário logado
        recuperarDadosUsuarioLogado();

        // Verifica se a atividade foi iniciada por um deeplink
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            // Extrai o usuarioID da URL do deeplink
            usuarioID = data.getQueryParameter("usuarioID");
            Log.d("PerfilAmigoActivity", "ID usuário via deeplink: " + usuarioID);
        } else {
            // Caso contrário, tenta recuperar o usuarioID do Bundle
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                usuarioID = bundle.getString("usuarioID");
                Log.d("PerfilAmigoActivity", "ID usuário via bundle: " + usuarioID);
            }
        }

        // Verifica se o usuarioID foi recuperado corretamente
        if (usuarioID != null) {
            usuarioAmigoRef = usuariosRef.document(usuarioID);
            recuperarDadosPerfilAmigo();
        } else {
            Log.e("PerfilAmigoActivity", "usuarioID não foi passado corretamente.");
            Toast.makeText(this, "Erro ao carregar perfil. Tente novamente.", Toast.LENGTH_SHORT).show();
            finish(); // Fecha a atividade se o usuarioID for nulo
        }

        // Inicializando as listas e os adapters antes de carregar os dados do Firebase
        petListAdocao = new ArrayList<>();
        petsAdapterAdocao = new PetsAdapter(petListAdocao);

        // Configura o adapter e layout manager para pets para adoção
        recyclerViewPetsParaAdocao.setAdapter(petsAdapterAdocao);
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

        // Configura o RecyclerView de filtros
        configurarRecyclerViewFiltros();

        buttonCompartilharPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gera o link dinâmico
                String deepLink = "https://petguardian.com/perfil?usuarioID=" + usuarioSelecionado.getIdUsuario();

                // Copia o link para a área de transferência
                ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Link do Perfil", deepLink);
                clipboard.setPrimaryClip(clip);

                // Exibe uma mensagem de confirmação
                Toast.makeText(getApplicationContext(), "Link do perfil copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
                Log.d("ID usuario amigo", " " + usuarioSelecionado.getIdUsuario());
                Log.d("ID usuario logado", " " + idUsuarioLogado);
            }
        });

        buttonFiltrar.setOnClickListener(v -> {
            // Abrir a FiltroActivity com startActivityForResult
            Intent filtroIntent = new Intent(getApplicationContext(), FiltroActivity.class);
            startActivityForResult(filtroIntent, 1);
        });
    }

    private void inicializarComponentes() {
        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = findViewById(R.id.imagemUsuario);
        recyclerViewPetsParaAdocao = findViewById(R.id.recyclerViewPetsParaAdocao);
        recyclerViewFiltros = findViewById(R.id.recyclerViewFiltros);
        buttonFiltrar = findViewById(R.id.buttonFiltrar);
        buttonCompartilharPerfil = findViewById(R.id.buttonCompartilharPerfil);
        toolbar = findViewById(R.id.toolbar);

        // Inicializa os layouts de "Sem Pets" e "Com Pets"
        layoutSemPets = findViewById(R.id.layoutSemPets);
        layoutComPets = findViewById(R.id.layoutComPets);
    }

    private void configurarRecyclerViewFiltros() {
        // Crie uma lista de filtros (substitua por sua lógica)
        List<String> filtros = new ArrayList<>();

        // Configura o adapter
        filtroAdapter = new FiltroAdapter(filtros, this);
        recyclerViewFiltros.setAdapter(filtroAdapter);

        // Configura o LayoutManager (horizontal)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewFiltros.setLayoutManager(layoutManager);

        // Define a visibilidade do RecyclerView
        if (filtros.isEmpty()) {
            recyclerViewFiltros.setVisibility(View.GONE); // Oculta se não houver filtros
        } else {
            recyclerViewFiltros.setVisibility(View.VISIBLE); // Exibe se houver filtros
        }
    }

    @Override
    public void onFiltroRemoved(String filtro) {
        // Lógica para remover um filtro específico
        Log.d("PerfilAmigoActivity", "Filtro removido: " + filtro);
    }

    @Override
    public void onTodosFiltrosRemovidos() {
        // Lógica para remover todos os filtros
        Log.d("PerfilAmigoActivity", "Todos os filtros foram removidos");

        // Oculta o RecyclerView de filtros
        recyclerViewFiltros.setVisibility(View.GONE);

        // Recarregar a lista de pets sem filtros
        getPetsAdocao();
    }

    private void getPetsAdocao() {
        if (usuarioSelecionado == null) return;

        // Referência à coleção "pets" filtrando apenas os disponíveis para adoção
        CollectionReference databaseReference = FirebaseFirestore.getInstance()
                .collection("pets");

        Query query = databaseReference
                .whereEqualTo("idTutor", usuarioID);

        listenerRegistrationPetsAdocao = query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("Firestore", "Erro ao carregar os pets para adoção", e);
                return;
            }

            petListAdocao.clear();
            for (QueryDocumentSnapshot snapshot : snapshots) {
                Pet pet = snapshot.toObject(Pet.class);
                petListAdocao.add(pet);
            }

            // Verifica se há pets na lista
            if (petListAdocao.isEmpty()) {
                layoutSemPets.setVisibility(View.VISIBLE); // Exibe o layout "Sem Pets"
                layoutComPets.setVisibility(View.GONE); // Oculta o layout "Com Pets"
            } else {
                layoutSemPets.setVisibility(View.GONE); // Oculta o layout "Sem Pets"
                layoutComPets.setVisibility(View.VISIBLE); // Exibe o layout "Com Pets"
            }

            petsAdapterAdocao.notifyDataSetChanged();
        });
    }

    private void recuperarDadosPerfilAmigo() {
        if (usuarioAmigoRef == null) return;

        listenerRegistrationPerfilAmigo = usuarioAmigoRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Erro ao recuperar dados do perfil do amigo", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                usuarioSelecionado = snapshot.toObject(Usuario.class);
                if (usuarioSelecionado != null) {
                    String nomeUsuario = usuarioSelecionado.getNomeUsuario();
                    textViewNomeUsuario.setText(nomeUsuario);
                    textViewPerfilCidadeUsuario.setText(usuarioSelecionado.getCidadeUsuario() + " - " + usuarioSelecionado.getEstadoUsuario());

                    // Recuperar foto do usuário
                    String caminhoFoto = usuarioSelecionado.getCaminhoFotoUsuario();
                    if (caminhoFoto != null && !caminhoFoto.isEmpty()) {
                        Uri url = Uri.parse(caminhoFoto);
                        Glide.with(PerfilAmigoActivity.this)
                                .load(url)
                                .into(imagemPerfilUsuario);
                    }

                    // Carregar os pets do usuário selecionado
                    getPetsAdocao();
                }
            } else {
                Log.d("Firestore", "Documento não encontrado!");
            }
        });
    }

    private void recuperarDadosUsuarioLogado() {
        DocumentReference usuarioLogadoRef = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(idUsuarioLogado);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
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
            if (!corPredominante.equals("Selecione"))
                filtrosAtivos.add("Cor Predominante: " + corPredominante);

            // Atualizar o RecyclerView de filtros
            if (filtroAdapter != null) {
                filtroAdapter.setFiltros(filtrosAtivos);
                filtroAdapter.notifyDataSetChanged();

                // Verifica se há filtros para definir a visibilidade do recyclerViewFiltros
                if (filtrosAtivos.isEmpty()) {
                    recyclerViewFiltros.setVisibility(View.GONE); // Oculta o RecyclerView
                } else {
                    recyclerViewFiltros.setVisibility(View.VISIBLE); // Exibe o RecyclerView
                }
            }

            // Aplicar os filtros na consulta ao Firestore
            aplicarFiltros(status, idade, genero, especie, corOlhos, corPredominante);
        }
    }

    private void aplicarFiltros(String status, String idade, String genero, String especie, String corOlhos, String corPredominante) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Inicia a consulta com o filtro básico (pets do usuário logado)
        Query query = petsRef.whereEqualTo("idTutor", usuarioID);

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
                Log.e("PerfilAmigoActivity", "Erro ao aplicar filtros", task.getException());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (listenerRegistrationPerfilAmigo != null) {
            listenerRegistrationPerfilAmigo.remove();
        }

        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioLogado();
        petListAdocao.clear();
        getPetsAdocao();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cancela os listeners para evitar vazamentos de memória
        if (listenerRegistrationPerfilAmigo != null) {
            listenerRegistrationPerfilAmigo.remove();
        }
        if (listenerRegistrationPetsAdocao != null) {
            listenerRegistrationPetsAdocao.remove();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}