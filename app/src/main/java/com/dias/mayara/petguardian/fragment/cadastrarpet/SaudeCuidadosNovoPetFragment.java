package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.google.android.material.textfield.TextInputEditText;

public class SaudeCuidadosNovoPetFragment extends Fragment {


    private TextInputEditText editTextDataVermifugacao;
    private EditText editTextVacinas, editTextNecessidadesEspeciais, editTextDoencasTratamentos;
    private Spinner spinnerVacinacao;
    private Spinner spinnerVermifugacao, spinnerCastracao;
    private Button buttonAvancar, buttonVoltar;
    private Pet pet;

    private CadastroPetViewModel sharedViewModel;
    private FragmentInteractionListener listener;

    public SaudeCuidadosNovoPetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saude_cuidados_novo_pet, container, false);

        inicializarComponentes(view);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

        pet = new Pet();

        configurarCampos();

        // Configuração dos Spinners
        setupSpinners(view);

        // Configuração do campo de data
        setupDateInput(view);

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinnerVacinacao.getSelectedItem().toString().equals("Selecione")) {
                    Toast.makeText(getContext(), "Selecione o campo 'Status da vacinação'", Toast.LENGTH_SHORT).show();
                } else if (spinnerVermifugacao.getSelectedItem().toString().equals("Selecione")) {
                    Toast.makeText(getContext(), "Selecione o campo 'Vermifugado?'", Toast.LENGTH_SHORT).show();
                } else if (spinnerCastracao.getSelectedItem().toString().equals("Selecione")) {
                    Toast.makeText(getContext(), "Selecione o campo 'Castrado?'", Toast.LENGTH_SHORT).show();
                } else if (editTextDoencasTratamentos.getText() == null || editTextDoencasTratamentos.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Selecione o campo 'Histórico de doenças e tratamentos'", Toast.LENGTH_SHORT).show();
                } else if (editTextNecessidadesEspeciais.getText() == null || editTextNecessidadesEspeciais.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Preencha o campo 'Necessidades especiais'", Toast.LENGTH_SHORT).show();
                } else {

                    salvarDados();
                    listener.replaceFragment(ComportamentoPersonalidadeFragment.class);

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

        return view;
    }

    private void configurarCampos() {

        sharedViewModel.getPet().observe(getViewLifecycleOwner(), new Observer<Pet>() {
            @Override
            public void onChanged(Pet petAtual) {
                if (petAtual != null) {
                    // Configura os valores nos campos
                    spinnerCastracao.setSelection(getSpinnerIndex(spinnerCastracao, petAtual.getStatusCastracao()));
                    spinnerVacinacao.setSelection(getSpinnerIndex(spinnerVacinacao, petAtual.getStatusVacinacao()));
                    spinnerVermifugacao.setSelection(getSpinnerIndex(spinnerVermifugacao, petAtual.getVermifugado()));

                    editTextDoencasTratamentos.setText(petAtual.getDoencasTratamentos());
                    editTextDataVermifugacao.setText(petAtual.getDataVermifugacao());
                    editTextNecessidadesEspeciais.setText(petAtual.getNecessidadesEspeciais());
                    editTextVacinas.setText(petAtual.getVacinasTomadas());
                }
            }
        });
    }

    // Método auxiliar para encontrar o índice correto no Spinner
    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0; // Retorna a primeira posição como padrão
    }



    private void salvarDados() {
        // Obtém o objeto Pet atual do ViewModel (se ele já existir)
        Pet petAtual = sharedViewModel.getPet().getValue();

        if (petAtual == null) {
            // Se o pet atual for nulo, crie um novo objeto Pet
            petAtual = new Pet();
        }

        petAtual.setStatusCastracao(spinnerCastracao.getSelectedItem().toString());
        petAtual.setVacinasTomadas(spinnerVacinacao.getSelectedItem().toString());
        petAtual.setDoencasTratamentos(editTextDoencasTratamentos.getText().toString());
        petAtual.setDataVermifugacao(editTextDataVermifugacao.getText().toString());
        petAtual.setVermifugado(spinnerVermifugacao.getSelectedItem().toString());
        petAtual.setNecessidadesEspeciais(editTextNecessidadesEspeciais.getText().toString());
        petAtual.setVacinasTomadas(editTextVacinas.getText().toString());

        // Salva o objeto Pet atualizado no ViewModel
        sharedViewModel.setPet(petAtual);
    }

    private void setupSpinners(View view) {
        // Spinner para Status da Vacinação
        ArrayAdapter<CharSequence> adapterVacinacao = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.vacinacao_array,
                android.R.layout.simple_spinner_item
        );
        adapterVacinacao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVacinacao.setAdapter(adapterVacinacao);

        // Spinner para Vermifugação
        ArrayAdapter<CharSequence> adapterVermifugacao = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.vermifugacao_array,
                android.R.layout.simple_spinner_item
        );
        adapterVermifugacao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVermifugacao.setAdapter(adapterVermifugacao);

        // Spinner para Castração
        ArrayAdapter<CharSequence> adapterCastracao = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.castracao_array,
                android.R.layout.simple_spinner_item
        );
        adapterCastracao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCastracao.setAdapter(adapterCastracao);
    }

    private void setupDateInput(View view) {

        // Configura o filtro para limitar o número de caracteres
        editTextDataVermifugacao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        // Adiciona um TextWatcher para formatar a data
        editTextDataVermifugacao.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", ""); // Remove caracteres não numéricos
                    String formatted = formatDate(clean); // Formata a data
                    current = formatted;
                    editTextDataVermifugacao.setText(formatted);
                    editTextDataVermifugacao.setSelection(formatted.length()); // Move o cursor para o final
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Método para formatar a data no padrão dd/MM/yyyy
    private String formatDate(String clean) {
        StringBuilder formatted = new StringBuilder();
        if (clean.length() >= 2) {
            formatted.append(clean.substring(0, 2)).append("/");
            if (clean.length() >= 4) {
                formatted.append(clean.substring(2, 4)).append("/");
                if (clean.length() >= 8) {
                    formatted.append(clean.substring(4, 8));
                } else {
                    formatted.append(clean.substring(4));
                }
            } else {
                formatted.append(clean.substring(2));
            }
        } else {
            formatted.append(clean);
        }
        return formatted.toString();
    }

    private void inicializarComponentes(View view) {

        editTextDataVermifugacao = view.findViewById(R.id.editTextDataVermifugacao);
        spinnerVacinacao = view.findViewById(R.id.spinnerVacinacao);
        spinnerVermifugacao = view.findViewById(R.id.spinnerVermifugacao);
        editTextVacinas = view.findViewById(R.id.editTextVacinas);
        editTextNecessidadesEspeciais = view.findViewById(R.id.editTextNecessidadesEspeciais);
        spinnerCastracao = view.findViewById(R.id.spinnerCastracao);
        editTextDoencasTratamentos = view.findViewById(R.id.editTextDoencasTratamentos);
        buttonVoltar = view.findViewById(R.id.buttonVoltar);
        buttonAvancar = view.findViewById(R.id.buttonAvancar);

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