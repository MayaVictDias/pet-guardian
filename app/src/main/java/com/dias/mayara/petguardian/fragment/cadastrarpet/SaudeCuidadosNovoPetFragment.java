package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class SaudeCuidadosNovoPetFragment extends Fragment {


    private TextInputEditText editTextDataVermifugacao;
    private Spinner spinnerVacinacao;
    private Spinner spinnerVermifugacao;
    private Spinner spinnerCastracao;
    private Button buttonAvancar;

    private CadastroPetViewModel cadastroPetViewModel;

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

        // Configuração dos Spinners
        setupSpinners(view);

        // Configuração do campo de data
        setupDateInput(view);

        cadastroPetViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
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
        spinnerCastracao = view.findViewById(R.id.spinnerCastracao);
        buttonAvancar = view.findViewById(R.id.buttonAvancar);

    }
}