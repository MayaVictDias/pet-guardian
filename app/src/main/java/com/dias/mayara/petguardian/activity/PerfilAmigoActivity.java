package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;  // Importa ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewQuantidadePetsCadastrados;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar;
    private RecyclerView recyclerViewPetsDesaparecidos, recyclerViewPetsParaAdocao;

    private FirebaseUser usuarioPerfil;

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioLogadoRef;
    private DocumentReference usuarioAmigoRef;
    private ListenerRegistration listenerRegistrationPerfilAmigo;  // Listener para o perfil do amigo
    private ListenerRegistration listenerRegistrationPetsAdocao;    // Listener para pets para adoção
    private ListenerRegistration listenerRegistrationPetsDesaparecidos;  // Listener para pets desaparecidos

    private String idUsuarioLogado;

    private List<Pet> petListAdocao = new ArrayList<>();
    private List<Pet> petListDesaparecidos = new ArrayList<>();
    private PetsAdapter petsAdapterAdocao;
    private PetsAdapter petsAdapterDesaparecidos;

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
        usuarioLogadoRef = usuariosRef.document(usuarioLogado.getIdUsuario());

        // Recuperar dados do usuário
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        // Inicializa os componentes de interface
        inicializarComponentes();

        // Recupera os dados do usuário logado
        recuperarDadosUsuarioLogado();

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFotoUsuario();
            if (caminhoFoto != null && !caminhoFoto.isEmpty()) {
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this)
                        .load(url)
                        .into(imagemPerfilUsuario);
            }
        }

        // Inicializando as listas e os adapters antes de carregar os dados do Firebase
        petListAdocao = new ArrayList<>();
        petListDesaparecidos = new ArrayList<>();
        petsAdapterAdocao = new PetsAdapter(petListAdocao);
        petsAdapterDesaparecidos = new PetsAdapter(petListDesaparecidos);

        // Configura o adapter e layout manager para pets para adoção
        recyclerViewPetsParaAdocao.setAdapter(petsAdapterAdocao);
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

        // Configura o adapter e layout manager para pets desaparecidos
        recyclerViewPetsDesaparecidos.setAdapter(petsAdapterDesaparecidos);
        LinearLayoutManager layoutManagerDesaparecidos = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPetsDesaparecidos.setLayoutManager(layoutManagerDesaparecidos);

        buttonFiltrar.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FiltroActivity.class)));
    }

    private void getPetsAdocao() {
        // Referência à coleção "adocao" de um usuário específico
        CollectionReference databaseReference = FirebaseFirestore.getInstance()
                .collection("pets")
                .document(usuarioSelecionado.getIdUsuario())
                .collection("adocao");

        listenerRegistrationPetsAdocao = databaseReference.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("Firestore", "Erro ao carregar os pets para adoção", e);
                return;
            }
            petListAdocao.clear();
            for (QueryDocumentSnapshot snapshot : snapshots) {
                String idPet = snapshot.getString("idPet");
                String idEndereco = snapshot.getString("idEndereco");
                String idTutor = snapshot.getString("idTutor");
                String especiePet = snapshot.getString("especiePet");
                String nomePet = snapshot.getString("nomePet");
                String generoPet = snapshot.getString("generoPet");
                String imagemUrl = snapshot.getString("imagemUrl");
                String idadePet = snapshot.getString("idadePet");
                String sobreOPet = snapshot.getString("sobreOPet");
                String statusPet = snapshot.getString("statusPet");
                Timestamp dataCadastro = snapshot.getTimestamp("dataCadastro");

                petListAdocao.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                        statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
            }
            petsAdapterAdocao.notifyDataSetChanged();
        });
    }

    private void getPetsDesaparecidos() {
        CollectionReference databaseReference = FirebaseFirestore.getInstance()
                .collection("pets").document(usuarioSelecionado.getIdUsuario()).collection("desaparecido");

        listenerRegistrationPetsDesaparecidos = databaseReference.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("Firestore", "Erro ao carregar os pets desaparecidos", e);
                return;
            }
            petListDesaparecidos.clear();
            for (QueryDocumentSnapshot snapshot : snapshots) {
                String idPet = snapshot.getString("idPet");
                String idEndereco = snapshot.getString("idEndereco");
                String idTutor = snapshot.getString("idTutor");
                String especiePet = snapshot.getString("especiePet");
                String nomePet = snapshot.getString("nomePet");
                String generoPet = snapshot.getString("generoPet");
                String imagemUrl = snapshot.getString("imagemUrl");
                String idadePet = snapshot.getString("idadePet");
                String sobreOPet = snapshot.getString("sobreOPet");
                String statusPet = snapshot.getString("statusPet");
                Timestamp dataCadastro = snapshot.getTimestamp("dataCadastro");

                petListDesaparecidos.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                        statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
            }
            petsAdapterDesaparecidos.notifyDataSetChanged();
        });
    }

    private void recuperarDadosPerfilAmigo() {
        DocumentReference usuarioAmigoRef = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(usuarioSelecionado.getIdUsuario());

        listenerRegistrationPerfilAmigo = usuarioAmigoRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Erro ao recuperar dados do perfil do amigo", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Usuario usuario = snapshot.toObject(Usuario.class);
                if (usuario != null) {
                    String nomeUsuario = usuario.getNomeUsuario();
                    textViewNomeUsuario.setText(nomeUsuario);
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
        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioLogado();
        petListAdocao.clear();
        petListDesaparecidos.clear();
        getPetsAdocao();
        getPetsDesaparecidos();
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
        if (listenerRegistrationPetsDesaparecidos != null) {
            listenerRegistrationPetsDesaparecidos.remove();
        }
    }

    private void inicializarComponentes() {
        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = findViewById(R.id.escolherImagemPet);
        recyclerViewPetsDesaparecidos = findViewById(R.id.recyclerViewPetsDesaparecidos);
        recyclerViewPetsParaAdocao = findViewById(R.id.recyclerViewPetsParaAdocao);
        buttonFiltrar = findViewById(R.id.buttonFiltrar);
    }
}
