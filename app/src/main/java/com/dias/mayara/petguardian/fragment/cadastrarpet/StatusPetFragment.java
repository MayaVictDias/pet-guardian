package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class StatusPetFragment extends Fragment {

    // Listas para armazenar componentes de cada opção
    private List<View> adocaoComponents;
    private List<View> desaparecidoComponents;
    private List<View> procurandoDonoComponents;

    private Button buttonVoltar, buttonAvancar;
    private FragmentInteractionListener listener;
    private RadioGroup radioGroupOptions;
    private RadioButton radioButtonAdocao, radioButtonDesaparecido, radioButtonProcurandoDono;
    private TextView textViewVistoUltimaVez, textViewCep, textViewEstado, textViewCidade,
            textViewBairro, textViewRuaAvenida, textViewSomenteNomeRuaAvenida,
            textViewNumeroEndereco, textViewComplementoEndereco, textViewSelecioneMapa,
            textViewSelecioneMapaInstrucoes;
    private TextInputLayout editTextCep, editTextEstado, editTextCidade, editTextBairro,
            editTextRuaAvcnida, editTextNumeroEndereco, editTextComplementoEndereco;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_pet, container, false);

        inicializarComponentes(view);

        inicializarListaDesaparecidoComponentes(view);
        inicializarListaAdocaoComponentes(view);
        inicializarListaProcurandoDonoComponents(view);

        // Configura o listener do RadioGroup
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupOptions);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                toggleOptions(checkedId);
            }
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.replaceFragment(ConferirInformacoesNovoPetFragment.class);

            }
        });

        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volta ao fragmento anterior
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Chama a função inicialmente para definir a visibilidade correta
        toggleOptions(radioGroup.getCheckedRadioButtonId());

        return view;
    }

    private void toggleOptions(int checkedId) {
        // Oculta todos os componentes antes de exibir os necessários
        toggleViewsVisibility(adocaoComponents, View.GONE);
        toggleViewsVisibility(desaparecidoComponents, View.GONE);
        toggleViewsVisibility(procurandoDonoComponents, View.GONE);

        // Define a visibilidade dos componentes com base na seleção do RadioButton
        if (checkedId == R.id.radioButtonAdocao) {
            toggleViewsVisibility(adocaoComponents, View.VISIBLE);
        } else if (checkedId == R.id.radioButtonDesaparecido) {
            toggleViewsVisibility(desaparecidoComponents, View.VISIBLE);
        } else if (checkedId == R.id.radioButtonProcurandoDono) {
            toggleViewsVisibility(procurandoDonoComponents, View.VISIBLE);
        }
    }


    private void inicializarComponentes(View view) {

        buttonVoltar = view.findViewById(R.id.buttonVoltar);
        buttonAvancar = view.findViewById(R.id.buttonAvancar);

        radioGroupOptions = view.findViewById(R.id.radioGroupOptions);
        radioButtonAdocao = view.findViewById(R.id.radioButtonAdocao);
        radioButtonDesaparecido = view.findViewById(R.id.radioButtonDesaparecido);
        radioButtonProcurandoDono = view.findViewById(R.id.radioButtonProcurandoDono);

        textViewVistoUltimaVez = view.findViewById(R.id.textViewVistoUltimaVez);
        textViewCep = view.findViewById(R.id.textViewCep);
        editTextCep = view.findViewById(R.id.editTextCep);
        textViewEstado = view.findViewById(R.id.textViewEstado);
        editTextEstado = view.findViewById(R.id.editTextEstado);
        textViewCidade = view.findViewById(R.id.textViewCidade);
        editTextCidade = view.findViewById(R.id.editTextCidade);
        textViewBairro = view.findViewById(R.id.textViewBairro);
        editTextBairro = view.findViewById(R.id.editTextBairro);
        textViewRuaAvenida = view.findViewById(R.id.textViewRuaAvenida);
        textViewSomenteNomeRuaAvenida = view.findViewById(R.id.textViewSomenteNomeRuaAvenida);
        editTextRuaAvcnida = view.findViewById(R.id.editTextRuaAvcnida);
        textViewNumeroEndereco = view.findViewById(R.id.textViewNumeroEndereco);
        editTextNumeroEndereco = view.findViewById(R.id.editTextNumeroEndereco);
        textViewComplementoEndereco = view.findViewById(R.id.textViewComplementoEndereco);
        editTextComplementoEndereco = view.findViewById(R.id.editTextComplementoEndereco);
        textViewSelecioneMapa = view.findViewById(R.id.textViewSelecioneMapa);
        textViewSelecioneMapaInstrucoes = view.findViewById(R.id.textViewSelecioneMapaInstrucoes);
    }

    private void inicializarListaDesaparecidoComponentes(View view) {

        desaparecidoComponents = new ArrayList<>();
        desaparecidoComponents.add(textViewVistoUltimaVez);
        desaparecidoComponents.add(textViewCep);
        desaparecidoComponents.add(editTextCep);
        desaparecidoComponents.add(textViewEstado);
        desaparecidoComponents.add(editTextEstado);
        desaparecidoComponents.add(textViewCidade);
        desaparecidoComponents.add(editTextCidade);
        desaparecidoComponents.add(textViewBairro);
        desaparecidoComponents.add(editTextBairro);
        desaparecidoComponents.add(editTextBairro);
        desaparecidoComponents.add(textViewRuaAvenida);
        desaparecidoComponents.add(textViewSomenteNomeRuaAvenida);
        desaparecidoComponents.add(editTextRuaAvcnida);
        desaparecidoComponents.add(textViewNumeroEndereco);
        desaparecidoComponents.add(editTextNumeroEndereco);
        desaparecidoComponents.add(textViewComplementoEndereco);
        desaparecidoComponents.add(editTextComplementoEndereco);
        desaparecidoComponents.add(textViewSelecioneMapa);
        desaparecidoComponents.add(textViewSelecioneMapaInstrucoes);

        desaparecidoComponents.add(buttonAvancar);
        desaparecidoComponents.add(buttonVoltar);

    }

    private void inicializarListaAdocaoComponentes(View view) {

        adocaoComponents = new ArrayList<>();
        adocaoComponents.add(view.findViewById(R.id.textViewVistoUltimaVez));
    }

    private void inicializarListaProcurandoDonoComponents(View view) {

        procurandoDonoComponents = new ArrayList<>();
        procurandoDonoComponents.add(textViewVistoUltimaVez);
        procurandoDonoComponents.add(textViewCep);
        procurandoDonoComponents.add(editTextCep);
        procurandoDonoComponents.add(textViewEstado);
        procurandoDonoComponents.add(editTextEstado);
        procurandoDonoComponents.add(textViewCidade);
        procurandoDonoComponents.add(editTextCidade);
        procurandoDonoComponents.add(textViewBairro);
        procurandoDonoComponents.add(editTextBairro);
        procurandoDonoComponents.add(editTextBairro);
        procurandoDonoComponents.add(textViewRuaAvenida);
        procurandoDonoComponents.add(textViewSomenteNomeRuaAvenida);
        procurandoDonoComponents.add(editTextRuaAvcnida);
        procurandoDonoComponents.add(textViewNumeroEndereco);
        procurandoDonoComponents.add(editTextNumeroEndereco);
        procurandoDonoComponents.add(textViewComplementoEndereco);
        procurandoDonoComponents.add(editTextComplementoEndereco);
        procurandoDonoComponents.add(textViewSelecioneMapa);
        procurandoDonoComponents.add(textViewSelecioneMapaInstrucoes);

        procurandoDonoComponents.add(buttonAvancar);
        procurandoDonoComponents.add(buttonVoltar);
    }

    private void toggleViewsVisibility(List<View> views, int visibility) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
}
