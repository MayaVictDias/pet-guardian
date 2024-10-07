package com.dias.mayara.petguardian.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dias.mayara.petguardian.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        setSupportActionBar(toolbar);

        // Remover o título padrão da ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Criar um TextView para o título centralizado
        TextView toolbarTitle = new TextView(this);
        toolbarTitle.setText("Fazer login");
        toolbarTitle.setTextSize(20); // Tamanho do texto
        toolbarTitle.setGravity(Gravity.CENTER); // Centraliza o texto

        // Definir LayoutParams para centralizar o TextView na Toolbar
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.WRAP_CONTENT,
                Toolbar.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.CENTER; // Centraliza no eixo horizontal

        // Adicionar o TextView à Toolbar
        toolbar.addView(toolbarTitle, layoutParams);

        // Exibe o ícone de voltar na toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Definir comportamento para quando o botão de voltar for clicado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Lógica para o botão de voltar
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}