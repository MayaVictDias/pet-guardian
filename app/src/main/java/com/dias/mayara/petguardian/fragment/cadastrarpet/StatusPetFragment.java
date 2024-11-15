package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.TextView;
import android.widget.Toast;

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

    private CadastroPetViewModel cadastroPetViewModel;
    private Pet pet;

    private Button buttonVoltar, buttonAvancar;
    private FragmentInteractionListener listener;
    private RadioGroup radioGroupOptions;
    private RadioButton radioButtonAdocao, radioButtonDesaparecido, radioButtonProcurandoDono;
    private TextView textViewVistoUltimaVez, textViewPais, textViewCep, textViewEstado, textViewCidade,
            textViewBairro, textViewRuaAvenida, textViewSomenteNomeRuaAvenida,
            textViewNumeroEndereco, textViewComplementoEndereco, textViewSelecioneMapa,
            textViewSelecioneMapaInstrucoes;
    private TextInputEditText editTextCep, editTextEstado, editTextCidade, editTextBairro,
            editTextRuaAvenida, editTextNumeroEndereco, editTextComplementoEndereco, editTextPais;
    private TextInputLayout textInputLayoutPais, textInputLayoutCidade, textInputLayoutComplementoEndereco,
            textInputLayoutCep, textInputLayoutBairro, textInputLayoutEndereco, textInputLayoutEstado,
            textInputLayoutRuaAvenida;

    private Endereco endereco;

    private CadastroPetViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_pet, container, false);

        endereco = new Endereco();
        pet = new Pet();
        inicializarComponentes(view);
        inicializarListaDesaparecidoComponentes(view);
        inicializarListaAdocaoComponentes(view);
        inicializarListaProcurandoDonoComponents(view);

        cadastroPetViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

        // Carrega os dados salvos no ViewModel
        carregarDadosSalvos();

        // Configura o listener do RadioGroup
        RadioGroup radioGroup = view.findViewById(R.id.radioGroupOptions);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                toggleOptions(checkedId);
                // Salva a opção selecionada no ViewModel
                cadastroPetViewModel.setOpcaoSelecionada(checkedId);
            }
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioGroupOptions.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Selecione o status do pet", Toast.LENGTH_SHORT).show();
                } else if(editTextCep.getVisibility() == View.VISIBLE && editTextCep.getText() == null ||
                        editTextCep.getVisibility() == View.VISIBLE && editTextCep.getText().toString().trim().isEmpty()) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'CEP'", Toast.LENGTH_SHORT).show();

                } else if(editTextEstado.getText() == null && editTextEstado.getVisibility() == View.VISIBLE ||
                        editTextEstado.getText().toString().trim().isEmpty() && editTextEstado.getVisibility() == View.VISIBLE) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'Estado'", Toast.LENGTH_SHORT).show();

                } else if(editTextCidade.getText() == null && editTextCidade.getVisibility() == View.VISIBLE ||
                        editTextCidade.getText().toString().trim().isEmpty() && editTextCidade.getVisibility() == View.VISIBLE ) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'Cidade'", Toast.LENGTH_SHORT).show();

                } else if(editTextBairro.getText() == null && editTextBairro.getVisibility() == View.VISIBLE ||
                        editTextBairro.getText().toString().trim().isEmpty() && editTextBairro.getVisibility() == View.VISIBLE) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'Bairro'", Toast.LENGTH_SHORT).show();

                } else if(editTextPais.getText() == null && editTextPais.getVisibility() == View.VISIBLE ||
                        editTextPais.getText().toString().trim().isEmpty() && editTextPais.getVisibility() == View.VISIBLE ) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'País'", Toast.LENGTH_SHORT).show();

                } else if(editTextRuaAvenida.getText() == null && editTextRuaAvenida.getVisibility() == View.VISIBLE ||
                        editTextRuaAvenida.getText().toString().trim().isEmpty() && editTextRuaAvenida.getVisibility() == View.VISIBLE ) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'Avenida'", Toast.LENGTH_SHORT).show();

                } else if(editTextNumeroEndereco.getText() == null && editTextNumeroEndereco.getVisibility() == View.VISIBLE ||
                        editTextNumeroEndereco.getText().toString().trim().isEmpty() && editTextNumeroEndereco.getVisibility() == View.VISIBLE) {

                    Toast.makeText(getView().getContext(), "Preencha o campo 'Número'", Toast.LENGTH_SHORT).show();

                } else {
                    salvarDados();
                    listener.replaceFragment(ConferirInformacoesNovoPetFragment.class);
                }
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
        cadastroPetViewModel.getEndereco().observe(getViewLifecycleOwner(), new Observer<Endereco>() {
            @Override
            public void onChanged(Endereco endereco) {
                if (endereco != null) {

                    Log.d("Endereço conferir informacoes", endereco.toString());

                    /*
                    editTextPais.setText(endereco.getPais());
                    editTextCep.setText(endereco.getCep());
                    editTextEstado.setText(endereco.getEstado());
                    editTextCidade.setText(endereco.getCidade());
                    editTextBairro.setText(endereco.getBairro());
                    editTextRuaAvenida.setText(endereco.getRuaAvenida());
                    editTextNumeroEndereco.setText(endereco.getNumero());
                    editTextComplementoEndereco.setText(endereco.getComplemento());

                     */
                }
            }
        });

        cadastroPetViewModel.getOpcaoSelecionada().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer opcaoSelecionada) {
                if (opcaoSelecionada != null) {
                    radioGroupOptions.check(opcaoSelecionada);
                }
            }
        });
    }

    private void salvarDados() {
        // Obtém o objeto Pet atual do ViewModel (se ele já existir)
        Pet petAtual = cadastroPetViewModel.getPet().getValue();

        if (petAtual == null) {
            // Se o pet atual for nulo, crie um novo objeto Pet
            petAtual = new Pet();
        }

        // Define o status com base na seleção do RadioGroup
        int selectedOptionId = radioGroupOptions.getCheckedRadioButtonId();
        String status = "";

        if (selectedOptionId == R.id.radioButtonAdocao) {
            status = "Adoção";
        } else if (selectedOptionId == R.id.radioButtonDesaparecido) {
            status = "Desaparecido";
        } else if (selectedOptionId == R.id.radioButtonProcurandoDono) {
            status = "Procurando dono";
        }

        // Atualiza apenas o campo de status no objeto Pet existente
        petAtual.setStatusPet(status);

        // Salva o objeto Pet atualizado no ViewModel
        cadastroPetViewModel.setPet(petAtual);

        // Salva os dados de endereço também
        Endereco endereco = new Endereco(
                editTextCep.getText().toString(),
                editTextEstado.getText().toString(),
                editTextCidade.getText().toString(),
                editTextBairro.getText().toString(),
                editTextRuaAvenida.getText().toString(),
                editTextNumeroEndereco.getText().toString(),
                editTextComplementoEndereco.getText().toString(),
                editTextPais.getText().toString()
        );
        cadastroPetViewModel.setEndereco(endereco);
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
        textViewPais = view.findViewById(R.id.textViewPais);
        editTextPais = view.findViewById(R.id.editTextPais);
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

        textInputLayoutPais = view.findViewById(R.id.textInputLayoutPais);
        textInputLayoutComplementoEndereco = view.findViewById(R.id.textInputLayoutComplementoEndereco);
        textInputLayoutBairro = view.findViewById(R.id.textInputLayoutBairro);
        textInputLayoutCidade = view.findViewById(R.id.textInputLayoutCidade);
        textInputLayoutRuaAvenida = view.findViewById(R.id.textInputLayoutRuaAvenida);
        textInputLayoutCep = view.findViewById(R.id.textInputLayoutCep);
        textInputLayoutEstado = view.findViewById(R.id.textInputLayoutEstado);
        textInputLayoutEndereco = view.findViewById(R.id.textInputLayoutEndereco);
    }

    private void inicializarListaDesaparecidoComponentes(View view) {
        desaparecidoComponents = new ArrayList<>();
        desaparecidoComponents.add(textViewVistoUltimaVez);
        desaparecidoComponents.add(textViewCep);
        desaparecidoComponents.add(editTextPais);
        desaparecidoComponents.add(textViewPais);
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

        desaparecidoComponents.add(textInputLayoutPais);
        desaparecidoComponents.add(textInputLayoutCidade);
        desaparecidoComponents.add(textInputLayoutComplementoEndereco);
        desaparecidoComponents.add(textInputLayoutCep);
        desaparecidoComponents.add(textInputLayoutBairro);
        desaparecidoComponents.add(textInputLayoutRuaAvenida);
        desaparecidoComponents.add(textInputLayoutEndereco);
        desaparecidoComponents.add(textInputLayoutEstado);
    }

    private void inicializarListaAdocaoComponentes(View view) {
        adocaoComponents = new ArrayList<>();
    }

    private void inicializarListaProcurandoDonoComponents(View view) {
        procurandoDonoComponents = new ArrayList<>();
        procurandoDonoComponents.add(textViewVistoUltimaVez);
        procurandoDonoComponents.add(editTextPais);
        procurandoDonoComponents.add(textViewPais);
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

        procurandoDonoComponents.add(textInputLayoutPais);
        procurandoDonoComponents.add(textInputLayoutCidade);
        procurandoDonoComponents.add(textInputLayoutComplementoEndereco);
        procurandoDonoComponents.add(textInputLayoutCep);
        procurandoDonoComponents.add(textInputLayoutBairro);
        procurandoDonoComponents.add(textInputLayoutEndereco);
        procurandoDonoComponents.add(textInputLayoutEstado);
        procurandoDonoComponents.add(textInputLayoutRuaAvenida);
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
