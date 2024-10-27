package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class StatusPetFragment extends Fragment {

    private List<View> adocaoComponents;
    private List<View> desaparecidoComponents;
    private List<View> procurandoDonoComponents;

    private SharedViewModel sharedViewModel;
    private Pet pet;

    private Button buttonVoltar, buttonAvancar;
    private FragmentInteractionListener listener;
    private RadioGroup radioGroupOptions;
    private RadioButton radioButtonAdocao, radioButtonDesaparecido, radioButtonProcurandoDono;
    private TextView textViewVistoUltimaVez, textViewCep, textViewEstado, textViewCidade,
            textViewBairro, textViewRuaAvenida, textViewSomenteNomeRuaAvenida,
            textViewNumeroEndereco, textViewComplementoEndereco, textViewSelecioneMapa,
            textViewSelecioneMapaInstrucoes;
    private TextInputEditText editTextCep, editTextEstado, editTextCidade, editTextBairro,
            editTextRuaAvenida, editTextNumeroEndereco, editTextComplementoEndereco;

    private Endereco endereco;

    // Variáveis para armazenar os dados dos campos
    private String cep, estado, cidade, bairro, ruaAvenida, numeroEndereco, complementoEndereco;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_pet, container, false);

        endereco = new Endereco();
        inicializarComponentes(view);
        inicializarListaDesaparecidoComponentes(view);
        inicializarListaAdocaoComponentes(view);
        inicializarListaProcurandoDonoComponents(view);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Carrega os dados salvos no ViewModel
        carregarDadosSalvos();

        // Configura o listener do RadioGroup
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupOptions);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                toggleOptions(checkedId);
                // Salva a opção selecionada no ViewModel
                sharedViewModel.setOpcaoSelecionada(checkedId);
            }
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarDados();
                listener.replaceFragment(ConferirInformacoesNovoPetFragment.class);
            }
        });

        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    salvarDados();
                    getActivity().getSupportFragmentManager().popBackStack();
                } catch (Exception e) {
                    e.printStackTrace(); // Imprime o erro no log
                }
            }
        });

        // Chama a função inicialmente para definir a visibilidade correta
        toggleOptions(radioGroup.getCheckedRadioButtonId());

        return view;
    }

    private void carregarDadosSalvos() {
        // Observa e atualiza os campos de acordo com o ViewModel
        sharedViewModel.getEndereco().observe(getViewLifecycleOwner(), new Observer<Endereco>() {
            @Override
            public void onChanged(Endereco endereco) {
                if (endereco != null) {
                    editTextCep.setText(endereco.getCep());
                    editTextEstado.setText(endereco.getEstado());
                    editTextCidade.setText(endereco.getCidade());
                    editTextBairro.setText(endereco.getBairro());
                    editTextRuaAvenida.setText(endereco.getRuaAvenida());
                    editTextNumeroEndereco.setText(endereco.getNumero());
                    editTextComplementoEndereco.setText(endereco.getComplemento());
                }
            }
        });

        sharedViewModel.getOpcaoSelecionada().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer opcaoSelecionada) {
                if (opcaoSelecionada != null) {
                    radioGroupOptions.check(opcaoSelecionada);
                }
            }
        });
    }

    private void salvarDados() {
        // Atualiza os dados do endereço no ViewModel
        Endereco endereco = new Endereco(
                editTextCep.getText().toString(),
                editTextEstado.getText().toString(),
                editTextCidade.getText().toString(),
                editTextBairro.getText().toString(),
                editTextRuaAvenida.getText().toString(),
                editTextNumeroEndereco.getText().toString(),
                editTextComplementoEndereco.getText().toString()
        );
        sharedViewModel.setEndereco(endereco);
    }

    private void toggleOptions(int checkedId) {
        toggleViewsVisibility(adocaoComponents, View.GONE);
        toggleViewsVisibility(desaparecidoComponents, View.GONE);
        toggleViewsVisibility(procurandoDonoComponents, View.GONE);

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
        editTextRuaAvenida = view.findViewById(R.id.editTextRuaAvenida);
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
        desaparecidoComponents.add(textViewRuaAvenida);
        desaparecidoComponents.add(textViewSomenteNomeRuaAvenida);
        desaparecidoComponents.add(editTextRuaAvenida);
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
        adocaoComponents.add(buttonAvancar);
        adocaoComponents.add(buttonVoltar);
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
        procurandoDonoComponents.add(textViewRuaAvenida);
        procurandoDonoComponents.add(textViewSomenteNomeRuaAvenida);
        procurandoDonoComponents.add(editTextRuaAvenida);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " deve implementar FragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
