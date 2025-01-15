package com.dias.mayara.petguardian.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MaisInformacoesSobrePetActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageViewFotoPet;
    private TextView textViewDesaparecidoHaTempo, textViewNomeTitulo, textViewIdadeGenero, textViewId,
            textViewVistoPorUltimoTitulo, textViewEnderecoTitulo, textViewEnderecoCompletoDado,
            textViewPontoReferenciaTitulo, textViewPontoReferenciaDado, textViewRaca,
            textViewInformacoesGeraisTituloDado, textViewEspecie, textViewCorOlhos, textViewPorte,
            textViewCorPredominante, textViewStatusPet;
    private Pet petSelecionado;
    private Button buttonEntrarEmContato;
    private ImageButton buttonMenu;

    private String idUsuarioLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference enderecoRef;
    private DatabaseReference todosPetsRef;
    private DatabaseReference usuarioRef;
    private DatabaseReference petsRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference feedPetsRef;
    private Usuario usuario;

    private AlertDialog dialog;

    private Handler handler = new Handler();
    private Runnable updateRunnable;

    private List<View> adocaoComponents;
    private List<View> desaparecidoComponents;
    private List<View> procurandoDonoComponents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_pet_activity);

        // Recuperar pet selecionado
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            petSelecionado = (Pet) bundle.getSerializable("petSelecionado");
        }

        inicializarComponentes();

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        usuarioRef = usuariosRef.child(petSelecionado.getIdTutor());
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        enderecoRef = ConfiguracaoFirebase.getFirebase().child("enderecos").child(petSelecionado.getIdEndereco());

        configurarCampos();

        if (petSelecionado.getStatusPet().equals("Adoção")) {
            toggleViewsVisibility(adocaoComponents, View.VISIBLE);
        } else if (petSelecionado.getStatusPet().equals("Desaparecido")) {
            toggleViewsVisibility(desaparecidoComponents, View.VISIBLE);
        } else if (petSelecionado.getStatusPet().equals("Procurando dono")) {
            toggleViewsVisibility(procurandoDonoComponents, View.VISIBLE);
        }

        buttonEntrarEmContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MaisInformacoesSobrePetActivity.this);
                View modalView = getLayoutInflater().inflate(R.layout.modal_entrar_em_contato_pet, null);
                bottomSheetDialog.setContentView(modalView);

                TextView textViewNomeResponsavel = modalView.findViewById(R.id.textViewNomeResponsavel);
                TextView textViewEmailResponsavel = modalView.findViewById(R.id.textViewEmailResponsavel);
                TextView textViewCelularResponsavel = modalView.findViewById(R.id.textViewCelularResponsavel);

                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nomeUsuario = snapshot.child("nomeUsuario").getValue(String.class);
                        String emailUsuario = snapshot.child("emailUsuario").getValue(String.class);
                        String telefoneUsuario = snapshot.child("telefoneUsuario").getValue(String.class);

                        textViewNomeResponsavel.setText(nomeUsuario);
                        textViewEmailResponsavel.setText(emailUsuario);
                        textViewCelularResponsavel.setText(telefoneUsuario);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //textViewNomeResponsavel.setText(petSelecionado.getNomeResponsavel());

                Button closeButton = modalView.findViewById(R.id.modalButton);
                closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

                bottomSheetDialog.show();
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

    }

    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_pet, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {

                    String status = "";

                    if(petSelecionado.getStatusPet().equals("Desaparecido")) {
                        status = "desaparecido";
                    } else if (petSelecionado.getStatusPet().equals("Adoção")) {
                        status = "adocao";
                    } else if (petSelecionado.getStatusPet().equals("Procurando dono")) {
                        status = "procurandoDono";
                    }

                    abrirDialogCarregamento("Deletando evento");

                    todosPetsRef = ConfiguracaoFirebase.getFirebase().child("todosPets")
                            .child(petSelecionado.getIdPet());

                    Log.d("TodosPetsRef", todosPetsRef.toString());

                    petsRef = ConfiguracaoFirebase.getFirebase().child("pets")
                            .child(idUsuarioLogado)
                            .child(status)
                            .child(petSelecionado.getIdPet());

                    Log.d("PetsRef", petsRef.toString());

                    feedPetsRef = ConfiguracaoFirebase.getFirebase().child("feedPets")
                            .child(status)
                            .child(petSelecionado.getIdPet());

                    Log.d("FeedPetsRef", feedPetsRef.toString());

                    // Código para excluir o item
                    todosPetsRef.removeValue();
                    petsRef.removeValue();
                    feedPetsRef.removeValue();

                    usuario.setQuantidadePetsCadastrados(usuario.getQuantidadePetsCadastrados() - 1);

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

    private void configurarCampos() {

        Glide.with(imageViewFotoPet.getContext())
                .load(petSelecionado.getImagemUrl()) // Aqui você insere a URL da imagem
                .placeholder(R.drawable.imagem_carregamento) // Imagem padrão enquanto carrega
                .error(R.drawable.no_image_found) // Imagem em caso de erro
                .into(imageViewFotoPet);

        updateTimeSincePost(petSelecionado.getDataCadastro());

        textViewNomeTitulo.setText(petSelecionado.getNomePet());
        textViewIdadeGenero.setText(petSelecionado.getIdadePet() + " • " + petSelecionado.getGeneroPet());
        textViewId.setText(petSelecionado.getIdPet());
        textViewEspecie.setText(petSelecionado.getEspeciePet());

        if(petSelecionado.getStatusPet().equals("Adoção")) {
            textViewStatusPet.setBackgroundColor(Color.parseColor("#00FF47"));
            textViewStatusPet.setText("ADOÇÃO");
        } else if (petSelecionado.getStatusPet().equals("Desaparecido")) {
            textViewStatusPet.setBackgroundColor(Color.parseColor("#FF0000"));
            textViewStatusPet.setText("DESAPARECIDO");
        } else if (petSelecionado.getStatusPet().equals("Procurando dono")) {
            textViewStatusPet.setBackgroundColor(Color.parseColor("#0047FF"));
            textViewStatusPet.setText("PROCURANDO DONO");
        }

        // Configurar campos de endereço
        enderecoRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Endereco endereco = snapshot.getValue(Endereco.class);

                    Log.d("Endereço to string", endereco.toString());

                    if (endereco != null) {

                        String enderecoCompleto = endereco.getRuaAvenida() + ", " + endereco.getNumero()
                                + endereco.getCidade() + ", " + endereco.getEstado() + ", " + endereco.getCep()
                                + ", " + endereco.getPais();

                        textViewEnderecoCompletoDado.setText(enderecoCompleto);
                        textViewPontoReferenciaDado.setText(endereco.getCidade() + ", " + endereco.getEstado() + ", " + endereco.getCep());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateTimeSincePost(long postTimestamp) {
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

    public String calculateTimeSincePost(long postTimestamp) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - postTimestamp;

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
        textViewVistoPorUltimoTitulo = findViewById(R.id.textViewVistoPorUltimoTitulo);
        textViewEnderecoTitulo = findViewById(R.id.textViewEnderecoTitulo);
        textViewEnderecoCompletoDado = findViewById(R.id.textViewEnderecoCompletoDado);
        textViewPontoReferenciaTitulo = findViewById(R.id.textViewPontoReferenciaTitulo);
        textViewPontoReferenciaDado = findViewById(R.id.textViewPontoReferenciaDado);
        textViewInformacoesGeraisTituloDado = findViewById(R.id.textViewInformacoesGeraisTituloDado);
        textViewEspecie = findViewById(R.id.textViewEspecie);
        textViewRaca = findViewById(R.id.textViewRaca);
        textViewStatusPet = findViewById(R.id.textViewStatusPet);
        textViewCorOlhos = findViewById(R.id.textViewCorOlhos);
        textViewPorte = findViewById(R.id.textViewPorte);
        textViewCorPredominante = findViewById(R.id.textViewCorPredominante);
        buttonEntrarEmContato = findViewById(R.id.buttonEntrarEmContato);
        buttonMenu = findViewById(R.id.buttonMenu);

        // Inicializa as listas de componentes
        inicializarListaDesaparecidoComponentes();
        inicializarListaAdocaoComponentes();
        inicializarListaProcurandoDonoComponents();
    }

    private void inicializarListaDesaparecidoComponentes() {
        desaparecidoComponents = new ArrayList<>();
        desaparecidoComponents.add(imageViewFotoPet);
        desaparecidoComponents.add(textViewStatusPet);
        desaparecidoComponents.add(textViewDesaparecidoHaTempo);
        desaparecidoComponents.add(textViewNomeTitulo);
        desaparecidoComponents.add(textViewIdadeGenero);
        desaparecidoComponents.add(textViewId);

        desaparecidoComponents.add(textViewVistoPorUltimoTitulo);
        desaparecidoComponents.add(textViewEnderecoTitulo);
        desaparecidoComponents.add(textViewEnderecoCompletoDado);
        desaparecidoComponents.add(textViewPontoReferenciaTitulo);
        desaparecidoComponents.add(textViewPontoReferenciaDado);

        desaparecidoComponents.add(textViewInformacoesGeraisTituloDado);
        desaparecidoComponents.add(textViewEspecie);
        desaparecidoComponents.add(textViewRaca);
        desaparecidoComponents.add(textViewCorOlhos);
        desaparecidoComponents.add(textViewPorte);
        desaparecidoComponents.add(textViewCorPredominante);
        desaparecidoComponents.add(buttonEntrarEmContato);
    }

    private void inicializarListaAdocaoComponentes() {
        adocaoComponents = new ArrayList<>();

        adocaoComponents = new ArrayList<>();
        adocaoComponents.add(imageViewFotoPet);
        adocaoComponents.add(textViewStatusPet);
        adocaoComponents.add(textViewDesaparecidoHaTempo);
        adocaoComponents.add(textViewNomeTitulo);
        adocaoComponents.add(textViewIdadeGenero);
        adocaoComponents.add(textViewId);

        adocaoComponents.add(textViewInformacoesGeraisTituloDado);
        adocaoComponents.add(textViewEspecie);
        adocaoComponents.add(textViewRaca);
        adocaoComponents.add(textViewCorOlhos);
        adocaoComponents.add(textViewPorte);
        adocaoComponents.add(textViewCorPredominante);
        adocaoComponents.add(buttonEntrarEmContato);
    }

    private void inicializarListaProcurandoDonoComponents() {
        procurandoDonoComponents = new ArrayList<>();

        procurandoDonoComponents.add(imageViewFotoPet);
        procurandoDonoComponents.add(textViewStatusPet);
        procurandoDonoComponents.add(textViewDesaparecidoHaTempo);
        procurandoDonoComponents.add(textViewNomeTitulo);
        procurandoDonoComponents.add(textViewIdadeGenero);
        procurandoDonoComponents.add(textViewId);

        procurandoDonoComponents.add(textViewVistoPorUltimoTitulo);
        procurandoDonoComponents.add(textViewEnderecoTitulo);
        procurandoDonoComponents.add(textViewEnderecoCompletoDado);
        procurandoDonoComponents.add(textViewPontoReferenciaTitulo);
        procurandoDonoComponents.add(textViewPontoReferenciaDado);

        procurandoDonoComponents.add(textViewInformacoesGeraisTituloDado);
        procurandoDonoComponents.add(textViewEspecie);
        procurandoDonoComponents.add(textViewRaca);
        procurandoDonoComponents.add(textViewCorOlhos);
        procurandoDonoComponents.add(textViewPorte);
        procurandoDonoComponents.add(textViewCorPredominante);
    }

    private void toggleViewsVisibility(List<View> views, int visibility) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}