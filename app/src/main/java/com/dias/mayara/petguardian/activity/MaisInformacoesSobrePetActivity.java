package com.dias.mayara.petguardian.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MaisInformacoesSobrePetActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageViewFotoPet;
    private TextView textViewRaca, textViewCorOlhos, textViewPorte, textViewCorPredominante,
            textViewStatusVacinacao, textViewVacinasTomadas, textViewVermifugado,
            textViewDataUltimaVermifugacao, textViewPetCastrado,
            textViewHistoricoDoencasTratamentos, textViewNecessidadesEspeciais,
            textViewNivelEnergia, textViewSociabilidade, textViewPetAdestrado, textViewSaudeCuidados,
            textViewStatusVacinacaoTitulo, textViewVacinasTomadasTitulo, textViewVermifugadoTitulo,
            textViewDataUltimaVermifugacaoTitulo, textViewPetCastradoTitulo,
            textViewHistoricoDoencasTratamentosTitulo, textViewNecessidadesEspeciaisTitulo,
            textViewComportamentoPersonalidade, textViewNivelEnergiaTitulo,
            textViewSociabilidadeTitulo, textViewPetAdestradoTitulo,
            textViewDesaparecidoHaTempo, textViewNomeTitulo, textViewIdadeGenero,
            textViewId, textViewEspecie;
    private Pet petSelecionado;
    private Button buttonEntrarEmContato;
    private FloatingActionButton buttonGerarLink;
    private ImageButton buttonCopiarId;
    private ImageButton buttonMenu;

    private String idUsuarioLogado;

    private FirebaseFirestore firebaseRef;
    private DocumentReference usuarioRef, petsRef;
    private CollectionReference usuariosRef;
    private Usuario usuario;
    private String petId;

    private AlertDialog dialog;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_pet_activity);

        // Inicializa os componentes
        inicializarComponentes();

        // Verifica se a atividade foi aberta por um Deep Link
        Uri data = getIntent().getData();
        if (data != null) {
            // Extrai o ID do pet do Deep Link
            petId = data.getQueryParameter("id");
        } else {
            // Recuperar pet selecionado via Intent normal
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                petId = getIntent().getStringExtra("petId");
            }
        }

        if (petId != null) {
            buscarDadosDoPet(petId); // Busca os dados do pet
        } else {
            // Tratar caso o ID do pet não seja recebido
            Toast.makeText(this, "ID do pet não encontrado.", Toast.LENGTH_SHORT).show();
            finish();
        }

        buttonGerarLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica se o petSelecionado foi carregado
                if (petSelecionado != null) {
                    // Cria o link dinâmico
                    String deepLink = "https://petguardian.com/pet?id=" + petSelecionado.getIdPet();

                    // Copia o link para a área de transferência
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Link do Pet", deepLink);
                    clipboard.setPrimaryClip(clip);

                    // Exibe uma mensagem de confirmação
                    Toast.makeText(getApplicationContext(), "Link do pet  copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Dados do pet não carregados.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void buscarDadosDoPet(String petId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference petRef = db.collection("pets").document(petId);

        petRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    petSelecionado = snapshot.toObject(Pet.class);

                    if (petSelecionado != null) {
                        // Configura a atividade com os dados do pet
                        configurarAtividadeComPet();
                    }
                } else {
                    // Tratar caso o pet não seja encontrado
                    finish();
                }
            } else {
                // Tratar erro na busca
                System.err.println("Erro ao buscar o pet: " + task.getException().getMessage());
                finish();
            }
        });
    }

    private void configurarAtividadeComPet() {
        // Mova todo o código que depende de petSelecionado para cá
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.collection("usuarios");
        usuarioRef = usuariosRef.document(petSelecionado.getIdTutor());
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        buttonEntrarEmContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MaisInformacoesSobrePetActivity.this);
                View modalView = getLayoutInflater().inflate(R.layout.modal_entrar_em_contato_pet, null);
                bottomSheetDialog.setContentView(modalView);

                TextView textViewNomeResponsavel = modalView.findViewById(R.id.textViewNomeResponsavel);
                TextView textViewEmailResponsavel = modalView.findViewById(R.id.textViewEmailResponsavel);
                TextView textViewCelularResponsavel = modalView.findViewById(R.id.textViewCelularResponsavel);

                usuarioRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String nomeUsuario = document.getString("nomeUsuario");
                                String emailUsuario = document.getString("emailUsuario");
                                String telefoneUsuario = document.getString("celularUsuario");

                                textViewNomeResponsavel.setText(nomeUsuario);
                                textViewEmailResponsavel.setText(emailUsuario);
                                textViewCelularResponsavel.setText(telefoneUsuario);
                            }
                        }
                    }
                });

                Button closeButton = modalView.findViewById(R.id.modalButton);
                closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

                bottomSheetDialog.show();
            }
        });

        buttonCopiarId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtém o texto do ID
                String id = textViewId.getText().toString();

                // Copia o ID para a área de transferência
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ID do Pet", id);
                clipboard.setPrimaryClip(clip);

                // Exibe uma mensagem de confirmação
                Toast.makeText(getApplicationContext(), "ID copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
            }
        });

        if (petSelecionado.getIdTutor().equals(idUsuarioLogado)) {
            buttonMenu.setVisibility(View.VISIBLE);

            buttonMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Lógica para abrir o menu
                    showPopupMenu(view);
                }
            });
        } else if (!petSelecionado.getIdTutor().equals(idUsuarioLogado)) {
            buttonMenu.setVisibility(View.GONE);
        }

        // Configura os campos do pet
        configurarCampos(petSelecionado);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_pet, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {

                    abrirDialogCarregamento("Deletando evento");

                    petsRef = ConfiguracaoFirebase.getFirebase().collection("pets")
                            .document(petSelecionado.getIdPet());

                    // Código para excluir o item
                    petsRef.delete();

                    usuarioRef.update("quantidadePetsCadastrados", usuario.getQuantidadePetsCadastrados() - 1);

                    dialog.cancel();

                    Toast.makeText(view.getContext(),
                            "Pet deletado com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    finish();

                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private void abrirDialogCarregamento(String titulo) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();
    }

    private void configurarCampos(Pet petSelecionado) {
        // Configura a imagem do pet
        Glide.with(imageViewFotoPet.getContext())
                .load(petSelecionado.getImagemUrl())
                .placeholder(R.drawable.imagem_carregamento)
                .error(R.drawable.no_image_found)
                .into(imageViewFotoPet);

        // Atualiza o tempo desde o post
        updateTimeSincePost(petSelecionado.getDataCadastro());

        // Configura os TextViews principais
        textViewNomeTitulo.setText(petSelecionado.getNomePet());
        textViewIdadeGenero.setText(petSelecionado.getIdadePet() + " • " + petSelecionado.getGeneroPet());
        textViewId.setText(petSelecionado.getIdPet());
        textViewEspecie.setText(petSelecionado.getEspeciePet());

        // Configura os TextViews de informações gerais
        textViewRaca.setText(petSelecionado.getRacaPet());
        textViewCorOlhos.setText(petSelecionado.getCorDosOlhosPet());
        textViewPorte.setText(petSelecionado.getPortePet());
        textViewCorPredominante.setText(petSelecionado.getCorPredominantePet());

        // Configura os TextViews de saúde e cuidados
        textViewStatusVacinacao.setText(petSelecionado.getStatusVacinacao());
        if (petSelecionado.getVacinasTomadas().equals("") || petSelecionado.getVacinasTomadas() == null) {
            textViewVacinasTomadas.setText("Não informado");
        } else {
            textViewVacinasTomadas.setText(petSelecionado.getVacinasTomadas());
        }

        // Corrigindo a lógica para vermifugado e castração
        textViewVermifugado.setText("Sim".equalsIgnoreCase(petSelecionado.getVermifugado()) ? "Sim" : "Não");
        textViewDataUltimaVermifugacao.setText(petSelecionado.getDataVermifugacao() != null ? petSelecionado.getDataVermifugacao().toDate().toString() : "Não informado");
        textViewPetCastrado.setText("Sim".equalsIgnoreCase(petSelecionado.getStatusCastracao()) ? "Sim" : "Não");

        // Configura os TextViews de histórico e necessidades especiais
        textViewHistoricoDoencasTratamentos.setText(petSelecionado.getDoencasTratamentos());
        textViewNecessidadesEspeciais.setText(petSelecionado.getNecessidadesEspeciais());

        // Configura os TextViews de comportamento e personalidade
        textViewNivelEnergia.setText(petSelecionado.getNivelEnergia());
        textViewSociabilidade.setText(petSelecionado.getSociabilidade());
        textViewPetAdestrado.setText(petSelecionado.isAdestrado() ? "Sim" : "Não");
    }

    public void updateTimeSincePost(Timestamp postTimestamp) {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                textViewDesaparecidoHaTempo.setText(calculateTimeSincePost(postTimestamp));
                handler.postDelayed(this, 60000); // Atualiza a cada 60 segundos
            }
        };
        handler.post(updateRunnable);
    }

    public String calculateTimeSincePost(Timestamp postTimestamp) {
        long currentTime = System.currentTimeMillis();
        long postTime = postTimestamp.toDate().getTime(); // Converte Timestamp para milissegundos
        long timeDifference = currentTime - postTime;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifference);

        if (minutes < 60) {
            return minutes + " minuto(s) atrás";
        } else if (hours < 24) {
            return hours + " hora(s) atrás";
        } else {
            return days + " dia(s) atrás";
        }
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbar);
        imageViewFotoPet = findViewById(R.id.imageViewFotoPet);
        textViewDesaparecidoHaTempo = findViewById(R.id.textViewPostadoHaTempo);
        textViewNomeTitulo = findViewById(R.id.textViewNomeTitulo);
        textViewIdadeGenero = findViewById(R.id.textViewIdadeGenero);
        textViewId = findViewById(R.id.textViewId);
        buttonCopiarId = findViewById(R.id.buttonCopiarId);
        textViewEspecie = findViewById(R.id.textViewEspecie);
        buttonEntrarEmContato = findViewById(R.id.buttonEntrarEmContato);
        buttonMenu = findViewById(R.id.buttonMenu);
        buttonGerarLink = findViewById(R.id.buttonGerarLink);

        // Inicializar os TextViews
        textViewRaca = findViewById(R.id.textViewRaca);
        textViewCorOlhos = findViewById(R.id.textViewCorOlhos);
        textViewPorte = findViewById(R.id.textViewPorte);
        textViewCorPredominante = findViewById(R.id.textViewCorPredominante);
        textViewStatusVacinacao = findViewById(R.id.textViewStatusVacinacao);
        textViewVacinasTomadas = findViewById(R.id.textViewVacinasTomadas);
        textViewVermifugado = findViewById(R.id.textViewVermifugado);
        textViewDataUltimaVermifugacao = findViewById(R.id.textViewDataUltimaVermifugacao);
        textViewPetCastrado = findViewById(R.id.textViewPetCastrado);
        textViewHistoricoDoencasTratamentos = findViewById(R.id.textViewHistoricoDoencasTratamentos);
        textViewNecessidadesEspeciais = findViewById(R.id.textViewNecessidadesEspeciais);
        textViewNivelEnergia = findViewById(R.id.textViewNivelEnergia);
        textViewSociabilidade = findViewById(R.id.textViewSociabilidade);
        textViewPetAdestrado = findViewById(R.id.textViewPetAdestrado);

        // Inicializar os TextViews faltantes (sem os que possuem números)
        textViewSaudeCuidados = findViewById(R.id.textViewSaudeCuidados);
        textViewStatusVacinacaoTitulo = findViewById(R.id.textViewStatusVacinacaoTitulo);
        textViewVacinasTomadasTitulo = findViewById(R.id.textViewVacinasTomadasTitulo);
        textViewVermifugadoTitulo = findViewById(R.id.textViewVermifugadoTitulo);
        textViewDataUltimaVermifugacaoTitulo = findViewById(R.id.textViewDataUltimaVermifugacaoTitulo);
        textViewPetCastradoTitulo = findViewById(R.id.textViewPetCastradoTitulo);
        textViewHistoricoDoencasTratamentosTitulo = findViewById(R.id.textViewHistoricoDoencasTratamentosTitulo);
        textViewNecessidadesEspeciaisTitulo = findViewById(R.id.textViewNecessidadesEspeciaisTitulo);
        textViewComportamentoPersonalidade = findViewById(R.id.textViewComportamentoPersonalidade);
        textViewNivelEnergiaTitulo = findViewById(R.id.textViewNivelEnergiaTitulo);
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }

    @Override
    public void onBackPressed() {
        // Verifica se há atividades na pilha
        if (isTaskRoot()) {
            // Se não houver atividades na pilha, abre a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Finaliza a atividade atual
        } else {
            // Se houver atividades na pilha, volta para a anterior
            super.onBackPressed();
        }
    }
}