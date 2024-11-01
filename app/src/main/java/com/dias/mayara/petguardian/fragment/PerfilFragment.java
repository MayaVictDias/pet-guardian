package com.dias.mayara.petguardian.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.activity.MainActivity;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class PerfilFragment extends Fragment {

    private Usuario usuarioLogado;

    private TextView textViewNomeUsuario, textViewPerfilCidadeUsuario;
    private CircleImageView imagemPerfilUsuario;

    private FirebaseUser usuarioPerfil;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;

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

        return view;
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
        imagemPerfilUsuario = view.findViewById(R.id.imagemPerfilUsuario);
    }
}