package com.dias.mayara.petguardian.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.fragment.CameraIAFragment;
import com.dias.mayara.petguardian.fragment.HomeFragment;
import com.dias.mayara.petguardian.fragment.PerfilFragment;
import com.dias.mayara.petguardian.fragment.SearchFragment;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private FirebaseUser usuarioPerfil;
    private ValueEventListener valueEventListenerPerfil;
    private Usuario usuarioLogado;

    private ImageView userProfileImage;
    private TextView userName;
    private TextView userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se o usuário estiver vindo da tela de cadastro de novo usuário, irá visualizar a modal de boas vindas
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("exibir_modal_boas_vindas", false)) {
            exibirModalBoasVindas();
        }

        // Inicializa a instância do Firebase e referências do banco de dados
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        // Obtém os dados do usuário logado e configura a referência para o nó do usuário específico
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioLogadoRef = usuariosRef.child(usuarioLogado.getIdUsuario());

        // Recuperar dados do usuário
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        // Configuração da toolbar
        toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Pet Guardian");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Configurar o botão do menu
        ImageButton menuButton = toolbar.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // Personalizar o cabeçalho
        View headerView = navigationView.getHeaderView(0);
        userProfileImage = headerView.findViewById(R.id.user_profile_image);
        userName = headerView.findViewById(R.id.user_name);
        userLocation = headerView.findViewById(R.id.user_location);

        // Exibir foto do usuario, caso ele tenha setado uma
        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null) {
            Glide.with(MainActivity.this).load(url).into(userProfileImage);
        } else {
            userProfileImage.setImageResource(R.drawable.profile_image);
        }

        recuperarDadosUsuarioLogado();

        // Lidar com cliques no menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_notificacoes) {
                    // Handle Notificações click
                } else if (id == R.id.nav_meus_pets) {
                    // Handle Meus pets click
                } else if (id == R.id.nav_configuracoes) {
                    startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Definindo o comportamento de seleção de itens
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment selectedFragment = null;

                int id = menuItem.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (id == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (id == R.id.nav_add_pet) {
                    startActivity(new Intent(getApplicationContext(), CadastrarPetActivity.class));
                    return true;
                } else if (id == R.id.nav_camera_ia) {
                    selectedFragment = new CameraIAFragment();
                } else if (id == R.id.nav_perfil) {
                    selectedFragment = new PerfilFragment();
                }

                // Substituir o FrameLayout pelo fragmento selecionado
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();

                return true;
            }
        });

        // Definir o fragmento inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        }
    }


    private void recuperarDadosUsuarioLogado() {
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);

                if (usuario != null) {

                    userName.setText(usuario.getNomeUsuario());
                    userLocation.setText(usuario.getCidadeUsuario());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PerfilFragment", "Erro ao recuperar dados do usuário", error.toException());
            }
        });
    }

    private void exibirModalBoasVindas() {
        // Inflar o layout personalizado
        View modalView = getLayoutInflater().inflate(R.layout.modal_boas_vindas, null);

        // Criar o AlertDialog com o layout personalizado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(modalView);  // Define o layout customizado no diálogo

        AlertDialog dialog = builder.create();

        // Encontrar e configurar os componentes da modal
        TextView titulo = modalView.findViewById(R.id.titulo);
        TextView mensagem = modalView.findViewById(R.id.mensagem);
        Button botaoFechar = modalView.findViewById(R.id.botao_fechar);

        // Definir comportamento do botão para fechar a modal
        botaoFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();  // Fecha a modal
            }
        });

        // Exibir a modal
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Captura o clique no botão do Toggle
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}