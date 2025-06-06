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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioLogadoRef;
    private FirebaseUser usuarioPerfil;
    private ValueEventListener valueEventListenerPerfil;
    private Usuario usuarioLogado;

    private CircleImageView userProfileImage;
    private TextView userName;
    private TextView userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        // Inicializa a instância do Firebase e referências do banco de dados
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.collection("usuarios");

        // Obtém os dados do usuário logado e configura a referência para o nó do usuário específico
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioLogadoRef = usuariosRef.document(usuarioLogado.getIdUsuario());

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

        // Recuperar dados do usuário
        usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        // Exibir foto do usuario, caso ele tenha setado uma
        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null) {
            Glide.with(this).load(url).into(userProfileImage);
        } else {
            userProfileImage.setImageResource(R.drawable.profile_image);
        }

        userName = headerView.findViewById(R.id.user_name);
        userLocation = headerView.findViewById(R.id.user_location);

        recuperarDadosUsuarioLogado();

        // Lidar com cliques no menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_configuracoes) {
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(usuarioLogado.getIdUsuario());

        // Monitora alterações no Firestore
        usuarioRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MainActivity", "Erro ao monitorar alterações do usuário", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);

                    if (usuario != null) {
                        // Atualiza os dados do usuário
                        userName.setText(usuario.getNomeUsuario());
                        userLocation.setText(usuario.getCidadeUsuario() + " - " + usuario.getEstadoUsuario());

                        // Atualiza a imagem do perfil
                        atualizarImagemPerfil();
                    }
                }
            }
        });
    }

    private void atualizarImagemPerfil() {
        usuarioPerfil.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Uri url = usuarioPerfil.getPhotoUrl();
                    if (url != null) {
                        Glide.with(MainActivity.this).load(url).into(userProfileImage);
                    } else {
                        userProfileImage.setImageResource(R.drawable.profile_image);
                    }
                } else {
                    Log.e("MainActivity", "Erro ao recarregar perfil do usuário", task.getException());
                }
            }
        });
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