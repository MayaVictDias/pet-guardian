package com.dias.mayara.petguardian.activity.CadastrarNovoUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class InserirEmailTelefoneActivity extends AppCompatActivity {

    private Button buttonCadastrarUsuarioAvancar;
    private Toolbar toolbar;
    private EditText editTextEmailNovoUsuario, editTextTelefoneNovoUsuario, editTextCidadeNovoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_novo_usuario_inserir_email);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Cadastrar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Recuperar informação de email do usuário passado na activity anterior
        Intent intent = getIntent();
        String nomeUsuario = intent.getStringExtra("nome_usuario");

        buttonCadastrarUsuarioAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editTextEmailNovoUsuario.getText().toString().trim().isEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmailNovoUsuario.getText().toString()).matches()) {
                        Toast.makeText(InserirEmailTelefoneActivity.this,
                                "Digite um e-mail válido",
                                Toast.LENGTH_SHORT).show();

                    } else if (!editTextTelefoneNovoUsuario.getText().toString().trim().isEmpty()) {

                        Intent intent = new Intent(getApplicationContext(), InserirSenhaActivity.class);
                        intent.putExtra("nome_usuario", nomeUsuario);
                        intent.putExtra("telefone_usuario", editTextTelefoneNovoUsuario.getText().toString());
                        intent.putExtra("cidade_usuario", editTextCidadeNovoUsuario.getText().toString());
                        intent.putExtra("email_usuario", editTextEmailNovoUsuario.getText().toString());
                        startActivity(intent);

                    } else {
                        Toast.makeText(InserirEmailTelefoneActivity.this,
                                "Por favor, digite o seu telefone",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InserirEmailTelefoneActivity.this,
                            "Por favor, digite o seu email",
                            Toast.LENGTH_SHORT).show();
                }
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
        buttonCadastrarUsuarioAvancar = findViewById(R.id.buttonCadastrarUsuarioAvancar);
        toolbar = findViewById(R.id.toolbarPrincipal);
        editTextEmailNovoUsuario = findViewById(R.id.editTextNomeNovoUsuario);
        editTextTelefoneNovoUsuario = findViewById(R.id.editTextTelefoneNovoUsuario);
        editTextCidadeNovoUsuario = findViewById(R.id.editTextCidadeNovoUsuario);
    }
}
