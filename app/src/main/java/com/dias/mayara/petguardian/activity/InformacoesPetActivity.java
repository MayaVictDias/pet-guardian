package com.dias.mayara.petguardian.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class InformacoesPetActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageViewFotoPet;
    private TextView textViewDesaparecidoHaTempo, textViewNomeTitulo, textViewIdadeGenero, textViewId,
            textViewVistoPorUltimoTitulo, textViewEnderecoTitulo, textViewEnderecoCompletoDado,
            textViewPontoReferenciaTitulo, textViewPontoReferenciaDado, textViewRaca,
            textViewInformacoesGeraisTituloDado, textViewEspecie, textViewCorOlhos, textViewPorte,
            textViewCorPredominante;
    private Pet petSelecionado;

    private DatabaseReference enderecoRef;

    private Handler handler = new Handler();
    private Runnable updateRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacoes_pet_activity);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recuperar pet selecionado
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            petSelecionado = (Pet) bundle.getSerializable("petSelecionado");
        }

        enderecoRef = ConfiguracaoFirebase.getFirebase().child("enderecos").child(petSelecionado.getIdEndereco());

        Log.d("ID endereço", enderecoRef.toString());

        configurarCampos();

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
        textViewCorOlhos = findViewById(R.id.textViewCorOlhos);
        textViewPorte = findViewById(R.id.textViewPorte);
        textViewCorPredominante = findViewById(R.id.textViewCorPredominante);
    }

    // Método para lidar com o clique no botão de voltar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  // Volta para a tela anterior
        return true;
    }
}