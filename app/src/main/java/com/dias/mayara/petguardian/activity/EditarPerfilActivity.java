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
    private EditText editTextNomeUsuario, editTextEmail, editTextCelular;
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
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioRef = ConfiguracaoFirebase.getFirebase().collection("usuarios").
                document(usuarioLogado.getIdUsuario());

        // Verifica se usuarioLogado não é nulo e preenche os EditTexts
        if (usuarioLogado != null) {
            editTextNomeUsuario.setText(usuarioLogado.getNomeUsuario());
            editTextEmail.setText(usuarioLogado.getEmailUsuario());
            // No método onCreate ou onde for apropriado, após a inicialização do usuarioRef
            usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Verificar se o campo celularUsuario existe no documento
                        String celular = documentSnapshot.getString("celularUsuario");

                        if (celular != null) {
                            // Defina o valor do celular no EditText
                            editTextCelular.setText(celular);
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

        } else {
            Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
        }

        Uri url = Uri.parse(usuarioLogado.getCaminhoFotoUsuario());
        Log.d("URL da foto: ", url.toString());

        if (url != null && !url.toString().isEmpty()) {
            Glide.with(EditarPerfilActivity.this).load(url).into(imageViewFotoPerfilUsuario);
        } else {
            imageViewFotoPerfilUsuario.setImageResource(R.drawable.profile_image);
        }

        // Alterar foto do usuário
        buttonAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verificarPermissoes()) {
                    abrirGaleria();
                } else {
                    solicitarPermissoes();
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

    private boolean verificarPermissoes() {
        for (String permissao : permissoesNecessarias) {
            if (ContextCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {
                return false; // Permissão não concedida
            }
        }
        return true; // Todas as permissões concedidas
    }

    private void solicitarPermissoes() {
        ActivityCompat.requestPermissions(this, permissoesNecessarias, REQUEST_CODE_PERMISSIONS);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SELECAO_GALERIA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (verificarPermissoes()) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permissões necessárias não concedidas.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECAO_GALERIA) {
                Uri imagemSelecionada = data.getData(); // Obtém o URI da imagem selecionada
                if (imagemSelecionada != null) {
                    try {
                        // Exibe a imagem selecionada no ImageView
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        imageViewFotoPerfilUsuario.setImageBitmap(bitmap); // Exibe no ImageView

                        // Converte a imagem para bytes e faz o upload
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imagemBytes = baos.toByteArray();

                        // Faz o upload da imagem para o Firebase Storage
                        fazerUploadImagem(imagemBytes);

                    } catch (IOException e) {
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
        String celularAtualizado = editTextCelular.getText().toString(); // Captura o valor do celular

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
                                usuarioLogado.setCelularUsuario(celularAtualizado); // Atualiza o celular
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