package com.dias.mayara.petguardian.activity.CadastrarNovoUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class InserirEnderecoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextEstado, editTextCidade;
    private Button buttonCadastrarUsuarioAvancar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_endereco);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Endereço");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Recuperar informação de email do usuário passado na activity anterior
        Intent intent = getIntent();
        String nomeUsuario = intent.getStringExtra("nome_usuario");
        String email_usuario = intent.getStringExtra("email_usuario");
        String celular_usuario = intent.getStringExtra("celular_usuario");

        buttonCadastrarUsuarioAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editTextEstado.getText().toString().trim().isEmpty()) {

                    if (!editTextCidade.getText().toString().trim().isEmpty()) {

                        Intent intent = new Intent(getApplicationContext(), InserirSenhaActivity.class);
                        intent.putExtra("nome_usuario", nomeUsuario);
                        intent.putExtra("email_usuario", email_usuario);
                        intent.putExtra("celular_usuario", celular_usuario);
                        intent.putExtra("estado_usuario", editTextEstado.getText().toString());
                        intent.putExtra("cidade_usuario", editTextCidade.getText().toString());

                        startActivity(intent);
                    } else {
                        Toast.makeText(InserirEnderecoActivity.this, "Preencha o campo 'Cidade'", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InserirEnderecoActivity.this, "Preencha o campo 'Estado'", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbarPrincipal);
        editTextEstado = findViewById(R.id.editTextEstado);
        editTextCidade = findViewById(R.id.editTextCidade);
        buttonCadastrarUsuarioAvancar = findViewById(R.id.buttonCadastrarUsuarioAvancar);
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}