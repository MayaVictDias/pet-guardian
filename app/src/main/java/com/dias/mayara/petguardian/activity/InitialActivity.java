package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class InitialActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean manterLogado = preferences.getBoolean("manterLogado", false);

        // Verifica se deve manter o usuário logado
        if (manterLogado) {
            // Se for para manter logado, redireciona direto para a MainActivity
            Intent intent = new Intent(InitialActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Verifica se o usuário está logado no Firebase
        verificaSeUsuarioEstaLogado();


        verificaSeUsuarioEstaLogado();

        Button loginButton = findViewById(R.id.loginButton);
        Button cadastrarButton = findViewById(R.id.cadastrarButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar um Intent para navegar para LoginActivity
                Intent intent = new Intent(InitialActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar um Intent para navegar para CadastrarActivity
                Intent intent = new Intent(InitialActivity.this, CadastrarNovoUsuarioInserirNomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public void verificaSeUsuarioEstaLogado() {

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        if (autenticacao.getCurrentUser() != null) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }
}