package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.fragment.cadastrarpet.InformacoesGeraisPetFragment;
import com.dias.mayara.petguardian.fragment.cadastrarpet.StatusPetFragment;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.helper.ToolbarHelper;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;

public class CadastrarPetActivity extends AppCompatActivity implements FragmentInteractionListener {

    private Toolbar toolbar;
    private ImageButton imageButtonClose;
    private CadastroPetViewModel cadastroPetViewModel;

    // CadastrarPetActivity.java
    public interface FragmentInteractionListener {
        void onAvancarButtonClicked();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_pet);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        inicializarComponentes();

        // Obter ViewModel
        cadastroPetViewModel = new ViewModelProvider(this).get(CadastroPetViewModel.class);

        setSupportActionBar(toolbar);
        ToolbarHelper.setupToolbar(this, toolbar, "Editar perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        imageButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    public CadastroPetViewModel getSharedViewModel() {
        return cadastroPetViewModel;
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
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Use o FragmentManager para substituir o fragmento e adicionar à pilha
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(null); // Adiciona à pilha de retrocesso
        transaction.commit();
    }

}