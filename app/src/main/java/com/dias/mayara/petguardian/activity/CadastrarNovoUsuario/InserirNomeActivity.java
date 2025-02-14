package com.dias.mayara.petguardian.activity.CadastrarNovoUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class InserirNomeActivity extends AppCompatActivity {

    private Button buttonCadastrarUsuarioAvancar;
    private Toolbar toolbar;
    private EditText editTextNomeNovoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_novo_usuario_inserir_nome);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Cadastrar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        buttonCadastrarUsuarioAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( !editTextNomeNovoUsuario.getText().toString().trim().isEmpty() ){

                    Intent intent = new Intent(getApplicationContext(), InserirEmailTelefoneActivity.class);
                    intent.putExtra("nome_usuario", editTextNomeNovoUsuario.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(InserirNomeActivity.this,
                            "Por favor, digite o seu nome",
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
        editTextNomeNovoUsuario = findViewById(R.id.editTextNomeNovoUsuario);
    }

}