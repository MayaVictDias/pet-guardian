package com.dias.mayara.petguardian.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;
import com.shuhart.stepview.StepView;

public class EditarPerfilActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonAlterarFoto, buttonSalvarAlteracoes;
    private EditText editTextNomeUsuario, editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    public void inicializarComponentes() {

        toolbar = findViewById(R.id.toolbarPrincipal);

        buttonAlterarFoto = findViewById(R.id.buttonAlterarFoto);
        editTextNomeUsuario = findViewById(R.id.editTextNomeUsuario);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);

    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}