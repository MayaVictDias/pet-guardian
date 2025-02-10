package com.dias.mayara.petguardian.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.EditarPerfilActivity;
import com.dias.mayara.petguardian.activity.FiltroActivity;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewPetsDesaparecidos;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar;
    private RecyclerView recyclerViewPetsDesaparecidos, recyclerViewPetsParaAdocao;

    private FirebaseUser usuarioPerfil;

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioLogadoRef;
    private String idUsuarioLogado;

    private List<Pet> petListAdocao = new ArrayList<>();
    private List<Pet> petListDesaparecidos = new ArrayList<>(); // Lista para pets desaparecidos
    private PetsAdapter petsAdapterAdocao;
    private PetsAdapter petsAdapterDesaparecidos; // Adaptador para pets desaparecidos

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
        petListDesaparecidos = new ArrayList<>();
        petsAdapterAdocao = new PetsAdapter(petListAdocao);
        petsAdapterDesaparecidos = new PetsAdapter(petListDesaparecidos);

        // Configura o adapter e layout manager para pets para adoção
        recyclerViewPetsParaAdocao.setAdapter(petsAdapterAdocao);
        LinearLayoutManager layoutManagerAdocao = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPetsParaAdocao.setLayoutManager(layoutManagerAdocao);

        // Configura o adapter e layout manager para pets desaparecidos
        recyclerViewPetsDesaparecidos.setAdapter(petsAdapterDesaparecidos);
        LinearLayoutManager layoutManagerDesaparecidos = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPetsDesaparecidos.setLayoutManager(layoutManagerDesaparecidos);

        getPetsAdocao();
        getPetsDesaparecidos(); // Carregar pets desaparecidos

        buttonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FiltroActivity.class));
            }
        });

        return view;
    }

    private void getPetsAdocao() {
        // Referência para a coleção pets do usuário logado e a subcoleção adocao
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets").document(idUsuarioLogado).collection("adocao");

        // Consultando os documentos
        petsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                petListAdocao.clear(); // Limpa a lista antes de adicionar novos dados
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

                    petListAdocao.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
                }
                // Notifica o adapter sobre as mudanças na lista
                petsAdapterAdocao.notifyDataSetChanged();
            } else {
                // Lida com erros, se necessário
                Log.w("Firestore", "Erro ao obter os dados", task.getException());
            }
        });
    }

    private void getPetsDesaparecidos() {
        // Referência para a coleção pets do usuário logado e a subcoleção desaparecido
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference petsRef = db.collection("pets").document(idUsuarioLogado).collection("desaparecido");

        // Consultando os documentos
        petsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                petListDesaparecidos.clear(); // Limpa a lista antes de adicionar novos dados
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

                    petListDesaparecidos.add(new Pet(idPet, nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
                }
                // Notifica o adapter sobre as mudanças na lista
                petsAdapterDesaparecidos.notifyDataSetChanged();
            } else {
                // Lida com erros, se necessário
                Log.w("Firestore", "Erro ao obter os dados", task.getException());
            }
        });
    }

    private void recuperarDadosUsuarioLogado() {
        // Referência para o documento do usuário logado
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

    private void inicializarComponentes(View view) {
        textViewNomeUsuario = view.findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = view.findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = view.findViewById(R.id.escolherImagemPet);
        textViewPetsDesaparecidos = view.findViewById(R.id.textViewPetsDesaparecidos);
        recyclerViewPetsDesaparecidos = view.findViewById(R.id.recyclerViewPetsDesaparecidos);
        recyclerViewPetsParaAdocao = view.findViewById(R.id.recyclerViewPetsParaAdocao);
        buttonFiltrar = view.findViewById(R.id.buttonFiltrar);
    }
}