package com.dias.mayara.petguardian.fragment;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewPetsDesaparecidos;
    private CircleImageView imagemPerfilUsuario;
    private RecyclerView recyclerViewPetsDesaparecidos, recyclerViewPetsParaAdocao;

    private FirebaseUser usuarioPerfil;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
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
        usuariosRef = firebaseRef.child("usuarios");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        // Obtém os dados do usuário logado e configura a referência para o nó do usuário específico
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getIdUsuario());

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

        return view;
    }

    private void getPetsAdocao() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("pets").child(idUsuarioLogado).child("adocao");

        // Usando addValueEventListener para atualizações em tempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petListAdocao.clear(); // Limpa a lista antes de adicionar novos dados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);
                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String imagemUrl = snapshot.child("imagemUrl").getValue(String.class);
                    String idadePet = snapshot.child("idadePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);
                    long dataCadastro = snapshot.child("dataCadastro").getValue(Long.class);

                    petListAdocao.add(new Pet(nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
                }
                // Notifica o adapter sobre as mudanças na lista
                petsAdapterAdocao.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Lida com erros, se necessário
            }
        });
    }

    private void getPetsDesaparecidos() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("pets").child(idUsuarioLogado).child("desaparecidos");

        // Usando addValueEventListener para atualizações em tempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petListDesaparecidos.clear(); // Limpa a lista antes de adicionar novos dados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);
                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String imagemUrl = snapshot.child("imagemUrl").getValue(String.class);
                    String idadePet = snapshot.child("idadePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);
                    long dataCadastro = snapshot.child("dataCadastro").getValue(Long.class);

                    petListDesaparecidos.add(new Pet(nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, imagemUrl, idEndereco, idTutor, dataCadastro));
                }
                // Notifica o adapter sobre as mudanças na lista
                petsAdapterDesaparecidos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Lida com erros, se necessário
            }
        });
    }

    private void recuperarDadosUsuarioLogado() {
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                if (usuario != null) {
                    textViewNomeUsuario.setText(usuario.getNomeUsuario());
                    textViewPerfilCidadeUsuario.setText(usuario.getCidadeUsuario());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PerfilFragment", "Erro ao recuperar dados do usuário", error.toException());
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
    }
}