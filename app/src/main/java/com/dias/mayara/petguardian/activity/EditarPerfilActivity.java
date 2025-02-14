package com.dias.mayara.petguardian.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.Permissao;
import com.dias.mayara.petguardian.helper.ToolbarHelper;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class EditarPerfilActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonAlterarFoto, buttonSalvarAlteracoes;
    private EditText editTextNomeUsuario, editTextEmail, editTextCelular;
    private ImageView imageViewFotoPerfilUsuario;
    private Usuario usuarioLogado;

    private String identificadorUsuario;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageRef;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        inicializarComponentes();

        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        storageRef = ConfiguracaoFirebase.getFirebaseStorage();

        // Inicializa o usuário logado
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado(); // Método hipotético para obter o usuário logado

        // Verifica se usuarioLogado não é nulo e preenche os EditTexts
        if (usuarioLogado != null) {
            editTextNomeUsuario.setText(usuarioLogado.getNomeUsuario());
            editTextEmail.setText(usuarioLogado.getEmailUsuario());
            editTextCelular.setText(usuarioLogado.getCelularUsuario());
        } else {
            Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
        }


        Uri url = Uri.parse(usuarioLogado.getCaminhoFotoUsuario());

        if(url != null && !url.equals("")) {

            Glide.with(EditarPerfilActivity.this).load(url).into(imageViewFotoPerfilUsuario);
        } else {

            imageViewFotoPerfilUsuario.setImageResource(R.drawable.profile_image);
        }

        // Alterar foto do usuário
        buttonAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Abre a galeria do celular

                if(i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        // Salvar alterações no nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Exibir o AlertDialog para confirmação da senha
                mostrarDialogConfirmacaoSenha();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            Bitmap imagem = null;

            try {
                // Selecao apenas da galeria de fotos
                if(requestCode == SELECAO_GALERIA) {
                    Uri localImagemSelecionada = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                }

                // Caso tenha sido escolhida uma imagem
                if(imagem != null) {

                    // Configura imagem na tela
                    imageViewFotoPerfilUsuario.setImageBitmap(imagem);

                    // Recuperar dados da imagem para salvar no firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Salvar imagem no firebase
                    final StorageReference imagemRef = storageRef.child("imagens").
                            child("perfil").child(identificadorUsuario + ".jpeg");
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this, "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizarFotoUsuario(url);
                                }
                            });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url) {

        // Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        // Atualizar foto no firebase
        usuarioLogado.setCaminhoFotoUsuario(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(this, "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();

    }

    private void mostrarDialogConfirmacaoSenha() {
        // Criando o AlertDialog
        final EditText inputSenha = new EditText(this);
        inputSenha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Confirmação de Senha")
                .setMessage("Digite sua senha para confirmar as alterações.")
                .setView(inputSenha)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    String senhaDigitada = inputSenha.getText().toString();
                    reautenticarUsuario(senhaDigitada); // Chama o método para reautenticar o usuário
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void reautenticarUsuario(String senhaDigitada) {
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null) {
            AuthCredential credenciais = EmailAuthProvider.getCredential(usuarioAtual.getEmail(), senhaDigitada);

            usuarioAtual.reauthenticate(credenciais)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Senha correta, salvar alterações
                            salvarAlteracoes();
                        } else {
                            // Senha incorreta
                            Toast.makeText(EditarPerfilActivity.this, "Senha incorreta!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditarPerfilActivity.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAlteracoes() {
        String nomeAtualizado = editTextNomeUsuario.getText().toString();
        String emailAtualizado = editTextEmail.getText().toString();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null) {
            // Atualizar e-mail no Firebase Authentication

            usuarioAtual.updateEmail(emailAtualizado)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Atualizar nome no Firebase Realtime Database ou Firestore
                            UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                            // Atualizar informações no modelo local
                            if (usuarioLogado != null) {
                                usuarioLogado.setNomeUsuario(nomeAtualizado);
                                usuarioLogado.setEmailUsuario(emailAtualizado);
                                usuarioLogado.atualizar(); // Método hipotético para salvar no banco de dados
                            }

                            Toast.makeText(EditarPerfilActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Tratar erro na atualização do e-mail
                            Toast.makeText(EditarPerfilActivity.this, "Erro ao atualizar e-mail: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditarPerfilActivity.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
        }
    }


    public void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbarPrincipal);
        buttonAlterarFoto = findViewById(R.id.buttonAlterarFoto);
        editTextNomeUsuario = findViewById(R.id.editTextNomeUsuario);
        editTextEmail = findViewById(R.id.editTextEmail);
        imageViewFotoPerfilUsuario = findViewById(R.id.ImageViewFotoPerfilUsuario);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAlteracoes);
        editTextCelular = findViewById(R.id.editTextCelular);
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}
