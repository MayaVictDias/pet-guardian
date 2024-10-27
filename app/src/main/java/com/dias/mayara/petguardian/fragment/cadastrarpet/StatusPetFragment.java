package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;
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

                endereco.setCep(editTextCep.getText().toString());
                endereco.setEstado(editTextEstado.getText().toString());
                endereco.setCidade(editTextCidade.getText().toString());
                endereco.setBairro(editTextBairro.getText().toString());
                endereco.setRuaAvenida(editTextRuaAvenida.getText().toString());
                endereco.setNumero(editTextNumeroEndereco.getText().toString());
                endereco.setComplemento(editTextComplementoEndereco.getText().toString());

                // Armazene o endereço no ViewModel
                sharedViewModel.setEndereco(endereco);

                listener.replaceFragment(ConferirInformacoesNovoPetFragment.class);

            }
        });

        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Chama a função inicialmente para definir a visibilidade correta
        toggleOptions(radioGroup.getCheckedRadioButtonId());

        return view;
    }


/*
    private void setupEditTexts() {
        editTextCep.liste(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setCep(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextEstado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setEstado(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextCidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setCidade(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextCidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setCidade(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextBairro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setBairro(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextRuaAvenida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setRuaAvenida(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextNumeroEndereco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setNumero(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTextComplementoEndereco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                endereco.setComplemento(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonAdocao) {
                pet.setStatusPet("Adoção");
            } else if (checkedId == R.id.radioButtonDesaparecido) {
                pet.setStatusPet("Desaparecido");
            } else if (checkedId == R.id.radioButtonProcurandoDono) {
                pet.setStatusPet("Procurando dono");
            }
        });
    }


 */
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
        desaparecidoComponents.add(buttonAvancar);
        desaparecidoComponents.add(buttonVoltar);

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
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }

}