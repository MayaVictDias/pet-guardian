package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.ToolbarHelper;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextLoginEmail, editTextLoginSenha;
    private Button buttonLogin;
    private ProgressBar progressBar;
    private CheckBox checkBoxLembrarUsuario;

    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();

        ToolbarHelper.setupToolbar(this, toolbar, "Login");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoEmail = editTextLoginEmail.getText().toString();
                String textoSenha = editTextLoginSenha.getText().toString();

                if (!textoEmail.isEmpty() && !textoSenha.isEmpty()) {
                    usuario = new Usuario();
                    usuario.setEmailUsuario(textoEmail);
                    usuario.setSenhaUsuario(textoSenha);

                    validarLogin(usuario);

                } else {
                    Toast.makeText(LoginActivity.this, "Preencha os campos de Email e Senha!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validarLogin(Usuario usuario) {
        progressBar.setVisibility(View.VISIBLE);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmailUsuario(),
                usuario.getSenhaUsuario()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Verificar o estado da checkbox e salvar nas SharedPreferences
                    SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    if (checkBoxLembrarUsuario.isChecked()) {
                        // Se checkbox marcada, salva a preferência para manter o login ativo
                        editor.putBoolean("manterLogado", true);
                    } else {
                        // Se checkbox não marcada, remove a preferência
                        editor.putBoolean("manterLogado", false);
                        // Aqui você pode fazer o logout imediato, se desejar não manter a sessão
                        autenticacao.signOut();
                    }

                    editor.apply(); // Aplica as mudanças nas SharedPreferences
                    progressBar.setVisibility(View.GONE);

                    // Redireciona para a MainActivity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbarPrincipal);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginSenha = findViewById(R.id.editTextLoginSenha);
        buttonLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progressBar);
        checkBoxLembrarUsuario = findViewById(R.id.checkBoxLembrarUsuario);

        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean lembrarUsuario = preferences.getBoolean("manterLogado", false);
        checkBoxLembrarUsuario.setChecked(lembrarUsuario);
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}
