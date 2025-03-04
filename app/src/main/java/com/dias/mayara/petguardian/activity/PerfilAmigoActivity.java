package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
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

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewQuantidadePetsCadastrados;
    private Toolbar toolbar;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar;
    private RecyclerView recyclerViewPetsParaAdocao;
    private LinearLayout layoutSemPets, layoutComPets; // Adicionado

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioAmigoRef;
    private ListenerRegistration listenerRegistrationPerfilAmigo;
    private ListenerRegistration listenerRegistrationPetsAdocao;

    private String idUsuarioLogado;
    private String usuarioID;

    private List<Pet> petListAdocao = new ArrayList<>();
    private List<Pet> petListDesaparecidos = new ArrayList<>();
    private PetsAdapter petsAdapterAdocao;

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

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioID = bundle.getString("usuarioID");
            if (usuarioID != null) {
                usuarioAmigoRef = usuariosRef.document(usuarioID);
                recuperarDadosPerfilAmigo();
            }
        }

        // Inicializando as listas e os adapters antes de carregar os dados do Firebase
        petListAdocao = new ArrayList<>();
        petListDesaparecidos = new ArrayList<>();
        petsAdapterAdocao = new PetsAdapter(petListAdocao);

        // Configura o adapter e layout manager para pets para adoção
        recyclerViewPetsParaAdocao.setAdapter(petsAdapterAdocao);
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

        buttonFiltrar.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FiltroActivity.class)));
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
                Timestamp dataVermifugacao = snapshot.getTimestamp("dataVermifugacao");
                String necessidadesEspeciais = snapshot.getString("necessidadesEspeciais");
                String doencasTratamentos = snapshot.getString("doencasTratamentos");
                String statusCastracao = snapshot.getString("statusCastracao");
                String nivelEnergia = snapshot.getString("nivelEnergia");
                String sociabilidade = snapshot.getString("sociabilidade");
                String isAdestrado = snapshot.getString("sociabilidade");
                Timestamp dataCadastro = snapshot.getTimestamp("dataCadastro");

                boolean adestrado = "Sim".equalsIgnoreCase(isAdestrado);

                petListAdocao.add(new Pet(idPet, idTutor, nomePet, idadePet, generoPet, especiePet,
                        raca, corPredominantePet, corDosOlhosPet, portePet, imagemUrl, statusVacinacao,
                        vacinasTomadas, vermifugado, dataVermifugacao, necessidadesEspeciais,
                        doencasTratamentos, statusCastracao, nivelEnergia, sociabilidade, adestrado,
                        dataCadastro));
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

        usuarioLogadoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Usuario usuario = snapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        textViewNomeUsuario.setText(usuario.getNomeUsuario());
                        textViewPerfilCidadeUsuario.setText(usuario.getCidadeUsuario() +
                                " - " + usuario.getEstadoUsuario());
                    }
                } else {
                    Log.d("Firestore", "Documento do usuário não encontrado!");
                }
            } else {
                Log.e("Firestore", "Erro ao recuperar dados do usuário", task.getException());
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

    private void inicializarComponentes() {
        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = findViewById(R.id.imagemUsuario);
        recyclerViewPetsParaAdocao = findViewById(R.id.recyclerViewPetsParaAdocao);
        buttonFiltrar = findViewById(R.id.buttonFiltrar);
        toolbar = findViewById(R.id.toolbar);

        // Inicializa os layouts de "Sem Pets" e "Com Pets"
        layoutSemPets = findViewById(R.id.layoutSemPets);
        layoutComPets = findViewById(R.id.layoutComPets);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}