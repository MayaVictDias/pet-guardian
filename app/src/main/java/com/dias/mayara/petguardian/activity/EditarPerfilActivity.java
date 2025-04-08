package com.dias.mayara.petguardian.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditarPerfilActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button buttonAlterarFoto, buttonSalvarAlteracoes;
    private EditText editTextNomeUsuario, editTextEmail, editTextCelular, editTextCidade, editTextEstado;
    private ImageView imageViewFotoPerfilUsuario;
    private Usuario usuarioLogado;

    private String identificadorUsuario;
    private DocumentReference usuarioRef;

    private static final int SELECAO_GALERIA = 200;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private StorageReference storageRef;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inicializa os componentes
        inicializarComponentes();

        // Recupera o ID do usuário logado
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        // Configura a toolbar
        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Referência do Firestore
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioRef = ConfiguracaoFirebase.getFirebase().collection("usuarios")
                .document(usuarioLogado.getIdUsuario());

        // Preenche os campos com os dados do usuário
        if (usuarioLogado != null) {
            editTextNomeUsuario.setText(usuarioLogado.getNomeUsuario());
            editTextEmail.setText(usuarioLogado.getEmailUsuario());

            // Recupera os dados do Firestore
            usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Preenche os campos de celular, cidade e estado
                        String celular = documentSnapshot.getString("celularUsuario");
                        String cidade = documentSnapshot.getString("cidadeUsuario");
                        String estado = documentSnapshot.getString("estadoUsuario");

                        if (celular != null) {
                            editTextCelular.setText(celular);
                        }
                        if (cidade != null) {
                            editTextCidade.setText(cidade);
                        }
                        if (estado != null) {
                            editTextEstado.setText(estado);
                        }
                    } else {
                        Log.d("EditarPerfil", "Documento não encontrado.");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("EditarPerfil", "Erro ao obter dados: ", e);
                }
            });

            // Carrega a foto do perfil
            Uri url = Uri.parse(usuarioLogado.getCaminhoFotoUsuario());
            if (url != null && !url.toString().isEmpty()) {
                Glide.with(EditarPerfilActivity.this).load(url).into(imageViewFotoPerfilUsuario);
            } else {
                imageViewFotoPerfilUsuario.setImageResource(R.drawable.profile_image);
            }
        } else {
            Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
        }

        // Configura o botão de alterar foto - usando a mesma lógica do InformacoesGeraisPetFragment
        buttonAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        // Configura o clique na imagem para também abrir a seleção de imagem
        imageViewFotoPerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        // Configura o botão de salvar alterações
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogConfirmacaoSenha();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECAO_GALERIA) {
                Uri imagemSelecionadaUri = data.getData();
                if (imagemSelecionadaUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionadaUri);
                        imageViewFotoPerfilUsuario.setImageBitmap(bitmap);

                        // Converte a imagem para bytes e faz o upload
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imagemBytes = baos.toByteArray();

                        // Faz o upload da imagem para o Firebase Storage
                        fazerUploadImagem(imagemBytes);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(EditarPerfilActivity.this, "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void fazerUploadImagem(byte[] imagemBytes) {
        StorageReference imagemRef = storageRef.child("imagens/perfil/" + identificadorUsuario + ".jpeg");
        UploadTask uploadTask = imagemRef.putBytes(imagemBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Obtém a URL da imagem após o upload
                imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Atualiza a foto do usuário no Firebase
                        atualizarFotoUsuario(uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditarPerfilActivity.this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarFotoUsuario(Uri url) {
        // Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        // Atualizar foto no firebase
        usuarioLogado.setCaminhoFotoUsuario(url.toString());
        usuarioLogado.atualizar();
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
        String celularAtualizado = editTextCelular.getText().toString();
        String cidadeAtualizada = editTextCidade.getText().toString();
        String estadoAtualizado = editTextEstado.getText().toString();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null) {
            // Atualizar e-mail no Firebase Authentication
            usuarioAtual.updateEmail(emailAtualizado)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Atualizar nome no Firestore
                            UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                            // Atualizar informações no modelo local
                            if (usuarioLogado != null) {
                                usuarioLogado.setNomeUsuario(nomeAtualizado);
                                usuarioLogado.setEmailUsuario(emailAtualizado);
                                usuarioLogado.setCelularUsuario(celularAtualizado);
                                usuarioLogado.setCidadeUsuario(cidadeAtualizada);
                                usuarioLogado.setEstadoUsuario(estadoAtualizado);

                                // Garantir que o campo caminhoFotoUsuario não seja nulo
                                if (usuarioLogado.getCaminhoFotoUsuario() == null || usuarioLogado.getCaminhoFotoUsuario().isEmpty()) {
                                    usuarioLogado.setCaminhoFotoUsuario(usuarioAtual.getPhotoUrl() != null ? usuarioAtual.getPhotoUrl().toString() : "");
                                }

                                usuarioLogado.atualizar(); // Salva no Firestore
                            }

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
        editTextCidade = findViewById(R.id.editTextCidade);
        editTextEstado = findViewById(R.id.editTextEstado);
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