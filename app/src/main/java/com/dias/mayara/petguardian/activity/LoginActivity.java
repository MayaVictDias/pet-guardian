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
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();

        ToolbarHelper.setupToolbar(this, toolbar, "Login");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }

    private void inicializarComponentes() {

        toolbar = findViewById(R.id.toolbarPrincipal);
    }
}