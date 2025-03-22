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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewQuantidadePetsCadastrados;
    private Toolbar toolbar;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar, buttonCompartilharPerfil;
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
                Log.d("IPerfilAmigoActivity", " " + usuarioID);
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
        petListDesaparecidos = new ArrayList<>();
        petsAdapterAdocao = new PetsAdapter(petListAdocao);

        // Configura o adapter e layout manager para pets para adoção
        recyclerViewPetsParaAdocao.setAdapter(petsAdapterAdocao);
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

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
                    textViewPerfilCidadeUsuario.setText(usuarioSelecionado.getCidadeUsuario());

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
        buttonCompartilharPerfil = findViewById(R.id.buttonCompartilharPerfil);
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