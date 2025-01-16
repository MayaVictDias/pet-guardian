package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario, textViewPetsDesaparecidos;
    private CircleImageView imagemPerfilUsuario;
    private ImageButton buttonFiltrar;
    private RecyclerView recyclerViewPetsDesaparecidos, recyclerViewPetsParaAdocao;

    private FirebaseUser usuarioPerfil;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuarioAmigoRef;
    private ValueEventListener valueEventListenerPerfil;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private String idUsuarioLogado;

    private List<Pet> petListAdocao = new ArrayList<>();
    private List<Pet> petListDesaparecidos = new ArrayList<>(); // Lista para pets desaparecidos
    private PetsAdapter petsAdapterAdocao;
    private PetsAdapter petsAdapterDesaparecidos; // Adaptador para pets desaparecidos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

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
        inicializarComponentes();

        // Recupera os dados do usuário logado
        recuperarDadosUsuarioLogado();

        // Recuperar usuário selecionado
        Bundle bundle = getIntent().getExtras();

        if( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFotoUsuario();
            if( caminhoFoto != null && !caminhoFoto.isEmpty() ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imagemPerfilUsuario );
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

        buttonFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FiltroActivity.class));
            }
        });
    }


    private void getPetsAdocao() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("pets").child(usuarioSelecionado.getIdUsuario()).child("adocao");

        // Usando addValueEventListener para atualizações em tempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petListAdocao.clear(); // Limpa a lista antes de adicionar novos dados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idPet = snapshot.child("idPet").getValue(String.class);
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);
                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String nomeUppercasePet = snapshot.child("nomeUppercasePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String imagemUrl = snapshot.child("imagemUrl").getValue(String.class);
                    String idadePet = snapshot.child("idadePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);
                    long dataCadastro = snapshot.child("dataCadastro").getValue(Long.class);

                    petListAdocao.add(new Pet(idPet, nomePet, nomeUppercasePet, idadePet, generoPet, especiePet, sobreOPet,
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
                .getReference("pets").child(usuarioSelecionado.getIdUsuario()).child("desaparecido");

        // Usando addValueEventListener para atualizações em tempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petListDesaparecidos.clear(); // Limpa a lista antes de adicionar novos dados
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idPet = snapshot.child("idPet").getValue(String.class);
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);
                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String nomeUppercasePet = snapshot.child("nomeUppercasePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String imagemUrl = snapshot.child("imagemUrl").getValue(String.class);
                    String idadePet = snapshot.child("idadePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);
                    long dataCadastro = snapshot.child("dataCadastro").getValue(Long.class);

                    petListDesaparecidos.add(new Pet(idPet, nomePet, nomeUppercasePet, idadePet, generoPet, especiePet, sobreOPet,
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
    // Metodo responsavel por recuperar os dados do usuario que está sendo pesquisado
    private void recuperarDadosPerfilAmigo() {

        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getIdUsuario());

        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Usuario usuario = snapshot.getValue(Usuario.class);

                        String nomeUsuario = usuario.getNomeUsuario();

                        // Configura valores recuperados
                        textViewNomeUsuario.setText(nomeUsuario);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    private void recuperarDadosUsuarioLogado() {
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                if (usuario != null) {
                    textViewNomeUsuario.setText(usuario.getNomeUsuario());
                    textViewPerfilCidadeUsuario.setText(usuario.getCidadeUsuario() +
                            " - " + usuario.getEstadoUsuario());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PerfilFragment", "Erro ao recuperar dados do usuário", error.toException());
            }
        });
    }


    // Metodo que é chamado logo após o CreateView
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

        // Remove o listener que está recuperando dados de um amigo especifico
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void inicializarComponentes() {
        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        textViewPerfilCidadeUsuario = findViewById(R.id.textViewPerfilCidadeUsuario);
        imagemPerfilUsuario = findViewById(R.id.escolherImagemPet);
        textViewPetsDesaparecidos = findViewById(R.id.textViewPetsDesaparecidos);
        recyclerViewPetsDesaparecidos = findViewById(R.id.recyclerViewPetsDesaparecidos);
        recyclerViewPetsParaAdocao = findViewById(R.id.recyclerViewPetsParaAdocao);
        buttonFiltrar = findViewById(R.id.buttonFiltrar);
    }
}