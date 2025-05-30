package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonConta, buttonPoliticaPrivacidade, buttonTermosUso, buttonCentralAjuda, buttonSair;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        inicializarComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditarPerfilActivity.class));
            }
        });

        buttonCentralAjuda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://forms.gle/BUpf17vrwBnVpwxQ7";
                // Criar uma Intent para abrir o navegador
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                // Iniciar a Intent
                startActivity(intent);
            }
        });

        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Desloga o usuário
                autenticacao.signOut();

                // Limpa o estado "manter logado" das SharedPreferences
                SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("manterLogado", false); // Limpa a preferência
                editor.apply(); // Aplica as mudanças

                // Redireciona o usuário para a tela de login
                Intent intent = new Intent(getApplicationContext(), InitialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }

    private void inicializarComponentes() {

        toolbar = findViewById(R.id.toolbar);
        buttonConta = findViewById(R.id.buttonConta);
        buttonCentralAjuda = findViewById(R.id.buttonCentralAjuda);
        buttonSair = findViewById(R.id.buttonSair);
    }
}