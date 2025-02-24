package com.dias.mayara.petguardian.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.FiltroActivity;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private Usuario usuarioLogado;
    private LinearLayout layoutSemPets, layoutComPets;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewQuantidadePetsCadastrados;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar;
    private RecyclerView recyclerViewPetsParaAdocao;

    private FirebaseUser usuarioPerfil;

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioLogadoRef;
    private String idUsuarioLogado;

    private List<Pet> petListAdocao = new ArrayList<>();
    private PetsAdapter petsAdapterAdocao;
    private ListenerRegistration petsListener; // Listener para atualizações em tempo real

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance() {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

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
        inicializarComponentes(view);

        // Recupera os dados do usuário logado
        recuperarDadosUsuarioLogado();

        // Exibir foto do usuario, caso ele tenha setado uma
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
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

        // Inicia a escuta em tempo real para os pets
        getPetsAdocao();

        buttonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FiltroActivity.class));
            }
        });

        return view;
    }

    private void getPetsAdocao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets");

        // Configura um listener em tempo real para a coleção de pets
        petsListener = petsRef.whereEqualTo("idTutor", idUsuarioLogado)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.w("Firestore", "Erro ao ouvir mudanças", error);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        petListAdocao.clear(); // Limpa a lista antes de adicionar novos dados
                        for (QueryDocumentSnapshot snapshot : snapshots) {
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

                        // Notifica o adapter sobre as mudanças na lista
                        petsAdapterAdocao.notifyDataSetChanged();
                    } else {
                        // Caso não haja documentos
                        layoutSemPets.setVisibility(View.VISIBLE);
                        layoutComPets.setVisibility(View.GONE);
                    }
                });
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
                        textViewQuantidadePetsCadastrados.setText(usuario.getQuantidadePetsCadastrados() +
                                " pet(s) cadastrado(s)");
                    }
                }
            } else {
                Log.e("PerfilFragment", "Erro ao recuperar dados do usuário", task.getException());
            }
        });
    }

    private void inicializarComponentes(View view) {
        textViewNomeUsuario = view.findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = view.findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = view.findViewById(R.id.escolherImagemPet);
        textViewQuantidadePetsCadastrados = view.findViewById(R.id.textViewQuantidadePetsCadastrados);
        recyclerViewPetsParaAdocao = view.findViewById(R.id.recyclerViewPetsParaAdocao);
        buttonFiltrar = view.findViewById(R.id.buttonFiltrar);

        // Inicializa os layouts de "Sem Pets" e "Com Pets"
        layoutSemPets = view.findViewById(R.id.layoutSemPets);
        layoutComPets = view.findViewById(R.id.layoutComPets);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove o listener para evitar vazamentos de memória
        if (petsListener != null) {
            petsListener.remove();
        }
    }
}