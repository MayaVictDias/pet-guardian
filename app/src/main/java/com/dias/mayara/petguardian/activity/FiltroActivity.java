package com.dias.mayara.petguardian.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class FiltroActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton imageButtonClose;

    private Spinner spinnerStatusPet, spinnerIdadePet, spinnerGeneroPet, spinnerEspeciePet,
            spinnerCorDosOlhos, spinnerCorPredominante;
    private Button buttonFiltrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        configurarAdapters();

    }

    private void configurarAdapters() {

        /********* Configuração do spinner de status *********/
        // Opções do menu suspenso
        String[] statusOptions = {"Selecione", "Para adoção", "Perdido", "Encontrado"};

        // Configurando o adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Vinculando o adaptador ao Spinner
        spinnerStatusPet.setAdapter(adapter);

        /********* Configuração do spinner de idade *********/
        // Opções do menu suspenso
        String[] idadeOptions = {"Selecione", "Filhote", "Adulto"};

        // Configurando o adaptador
        ArrayAdapter<String> adapterIdade = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                idadeOptions
        );
        adapterIdade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterIdade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Vinculando o adaptador ao Spinner
        spinnerIdadePet.setAdapter(adapterIdade);

        /********* Configuração do sppiner de gênero *********/
        // Opções do menu suspenso
        String[] generoOptions = {"Selecione", "Macho", "Fêmea"};

        // Configurando o adaptador
        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                generoOptions
        );
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Vinculando o adaptador ao Spinner
        spinnerGeneroPet.setAdapter(adapterGenero);

        /********* Configuração do spinner de espécie *********/
        // Opções do menu suspenso
        String[] especieOptions = {"Selecione", "Cachorro", "Gato"};

        // Configurando o adaptador
        ArrayAdapter<String> especieAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                especieOptions
        );
        especieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Vinculando o adaptador ao Spinner
        spinnerEspeciePet.setAdapter(especieAdapter);

        /********* Configuração do spinner de cor dos olhos *********/
        // Opções do menu suspenso
        String[] corOlhosOptions = {"Selecione", "Amarelo", "Âmbar", "Azul", "Castanho", "Cinza", "Cobre",
                "Heterocromia", "Preto", "Verde"};

        // Configurando o adaptador
        ArrayAdapter<String> corOlhosdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                corOlhosOptions
        );
        corOlhosdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        corOlhosdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Vinculando o adaptador ao Spinner
        spinnerCorDosOlhos.setAdapter(corOlhosdapter);

        /********* Configuração do spinner de cor predominante *********/
        // Opções do menu suspenso
        String[] corPredominanteOptions = {"Selecione", "Branco", "Caramelo", "Cinza",
                "Creme", "Dourado", "Marrom", "Preto", "Tigrado"};


        // Configurando o adaptador
        ArrayAdapter<String> corPredominantedapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                corPredominanteOptions
        );
        corPredominantedapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        corPredominantedapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Vinculando o adaptador ao Spinner
        spinnerCorPredominante.setAdapter(corPredominantedapter);

    }

    private void inicializarComponentes() {

        toolbar = findViewById(R.id.toolbar);
        imageButtonClose = findViewById(R.id.imageButtonClose);
        spinnerStatusPet = findViewById(R.id.spinnerStatusPet);

        spinnerIdadePet = findViewById(R.id.spinnerIdadePet);
        spinnerGeneroPet = findViewById(R.id.spinnerGeneroPet);
        spinnerEspeciePet = findViewById(R.id.spinnerEspeciePet);
        spinnerCorDosOlhos = findViewById(R.id.spinnerCorDosOlhos);
        spinnerCorPredominante = findViewById(R.id.spinnerCorPredominante);
        buttonFiltrar = findViewById(R.id.buttonFiltrar);
    }
}