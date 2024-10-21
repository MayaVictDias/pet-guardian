package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.fragment.cadastrarpet.InformacoesGeraisPetFragment;
import com.dias.mayara.petguardian.fragment.cadastrarpet.StatusPetFragment;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.helper.ToolbarHelper;

public class CadastrarPetActivity extends AppCompatActivity implements FragmentInteractionListener {

    private Toolbar toolbar;
    private ImageButton imageButtonClose;

    // CadastrarPetActivity.java
    public interface FragmentInteractionListener {
        void onAvancarButtonClicked();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_pet);

        inicializarComponentes();

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        if (savedInstanceState == null) {
            // Inicia o fragmento DadosFragment no frame_layout
            Fragment fragment = new InformacoesGeraisPetFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
        }
    }

    public void inicializarComponentes() {

        toolbar = findViewById(R.id.toolbar);
        imageButtonClose = findViewById(R.id.imageButtonClose);

    }

    public void onAvancarButtonClicked() {
        Fragment statusPetFragment = new StatusPetFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, statusPetFragment)
                .addToBackStack(null) // Adiciona à pilha de retorno
                .commit();
    }

    @Override
    public void replaceFragment(Class<? extends Fragment> fragmentClass) {
        try {
            Fragment fragment = fragmentClass.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null) // Adiciona à pilha de retorno
                    .commit();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}