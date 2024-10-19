package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class ConfiguracoesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonConta, buttonPoliticaPrivacidade, buttonTermosUso, buttonCentralAjuda, buttonSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditarPerfilActivity.class));
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
        buttonPoliticaPrivacidade = findViewById(R.id.buttonPoliticaPrivacidade);
        buttonTermosUso = findViewById(R.id.buttonTermosUso);
        buttonCentralAjuda = findViewById(R.id.buttonCentralAjuda);
        buttonSair = findViewById(R.id.buttonSair);
    }
}