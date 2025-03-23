package com.dias.mayara.petguardian.activity.CadastrarNovoUsuario;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dias.mayara.petguardian.activity.MainActivity;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class InserirSenhaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextNovoUsuarioInserirSenha, editTextNovoUsuarioConfirmarSenha;
    private ProgressBar progressBar;
    ;
    private Button buttonCadastrarNovoUsuario;

    private Usuario usuario;

    private AlertDialog dialog;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_novo_usuario_inserir_senha);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        inicializarComponentes();

        // Recuperar informação de email do usuário passado na activity anterior
        Intent intent = getIntent();
        String nomeUsuario = intent.getStringExtra("nome_usuario");
        String emailUsuario = intent.getStringExtra("email_usuario");
        String celularUsuario = intent.getStringExtra("celular_usuario");
        String cidadeUsuario = intent.getStringExtra("cidade_usuario");
        String estadoUsuario = intent.getStringExtra("estado_usuario");

        // Configurações da toolbar
        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Cadastrar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        progressBar.setVisibility(View.GONE);

        buttonCadastrarNovoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textoInserirSenha = editTextNovoUsuarioInserirSenha.getText().toString();
                String textoConfirmarSenha = editTextNovoUsuarioConfirmarSenha.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if (!textoInserirSenha.isEmpty()) {
                    if (!textoConfirmarSenha.isEmpty()) {

                        usuario = new Usuario();

                        usuario.setEmailUsuario(emailUsuario);
                        usuario.setNomeUsuario(nomeUsuario);
                        usuario.setCelularUsuario(celularUsuario);
                        usuario.setCidadeUsuario(cidadeUsuario);
                        usuario.setEstadoUsuario(estadoUsuario);
                        usuario.setSenhaUsuario(textoConfirmarSenha);

                        abrirDialogCarregamento("Cadastrando usuário");

                        cadastrar(usuario);

                    } else {
                        Toast.makeText(InserirSenhaActivity.this,
                                "Preencha o campo Confirme a sua Senha!",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(InserirSenhaActivity.this,
                            "Preencha o campo Digite a sua Senha!",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();

    }

    // Metodo responsável por cadastrar um novo usuário no firebase
    private void cadastrar(Usuario usuario) {

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmailUsuario(),
                usuario.getSenhaUsuario()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String erroExcecao = null;
                        if (task.isSuccessful()) {

                            try {

                                progressBar.setVisibility(View.GONE);

                                // Salvar dados do usuário no firebase
                                String idUsuario = task.getResult().getUser().getUid(); // Recupera ID do usuario criado pelo firebase
                                usuario.setIdUsuario(idUsuario);
                                usuario.salvar();

                                // Salvar nome do usuário
                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNomeUsuario());

                                Toast.makeText(InserirSenhaActivity.this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish(); // Método para encerrar a activity atual de cadastrar usuário

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        } else {

                            progressBar.setVisibility(View.GONE);

                            // Tratamento do erro
                            erroExcecao = "";
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma senha mais forte!";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "Por favor, digite um email válido";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erroExcecao = "Esta conta já foi cadastrada";
                            } catch (Exception e) {
                                erroExcecao = "ao cadastrar usuario " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(InserirSenhaActivity.this,
                                    "Erro: " + erroExcecao,
                                    Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        }

                    }
                }
        );

    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }

    private void inicializarComponentes() {

        toolbar = findViewById(R.id.toolbarPrincipal);
        editTextNovoUsuarioInserirSenha = findViewById(R.id.editTextNovoUsuarioInserirSenha);
        editTextNovoUsuarioConfirmarSenha = findViewById(R.id.editTextNovoUsuarioConfirmarSenha);
        progressBar = findViewById(R.id.progressBar2);
        buttonCadastrarNovoUsuario = findViewById(R.id.buttonCadastrarNovoUsuario);
    }
}