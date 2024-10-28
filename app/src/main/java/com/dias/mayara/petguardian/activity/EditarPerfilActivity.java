package com.dias.mayara.petguardian.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Usuario;
import com.shuhart.stepview.StepView;

public class EditarPerfilActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonAlterarFoto, buttonSalvarAlteracoes;
    private EditText editTextNomeUsuario, editTextEmail;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Inicializa o usuário logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado(); // Método hipotético para obter o usuário logado

        // Salvar alterações no nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeAtualizado = editTextNomeUsuario.getText().toString();

                // Atualizar nome no firebase
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                // Verifica se usuarioLogado não é nulo antes de atualizar
                if (usuarioLogado != null) {
                    usuarioLogado.setNomeUsuario(nomeAtualizado);
                    usuarioLogado.atualizar();
                } else {
                    Toast.makeText(EditarPerfilActivity.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(EditarPerfilActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
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