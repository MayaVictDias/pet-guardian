package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;

public class ComportamentoPersonalidadeFragment extends Fragment {


    private Spinner spinnerNivelEnergia, spinnerAdestrado;
    private EditText editTextSociabilidade;
    private Button buttonAvancar, buttonVoltar;

    private CadastroPetViewModel cadastroPetViewModel;
    private FragmentInteractionListener listener;

    public ComportamentoPersonalidadeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_comportamento_personalidade, container, false);

        inicializarComponentes(view);

        // Configuração dos Spinners
        setupSpinners(view);

        cadastroPetViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

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

        return view;
    }

    private void setupSpinners(View view) {

        ArrayAdapter<CharSequence> adapterNivelEnergia = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.nivel_energia_array,
                android.R.layout.simple_spinner_item
        );
        adapterNivelEnergia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelEnergia.setAdapter(adapterNivelEnergia);

        ArrayAdapter<CharSequence> adapterAdestrado = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sim_nao_array,
                android.R.layout.simple_spinner_item
        );
        adapterAdestrado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdestrado.setAdapter(adapterAdestrado);
    }

    private void salvarDados() {
        // Obtém o objeto Pet atual do ViewModel (se ele já existir)
        Pet petAtual = cadastroPetViewModel.getPet().getValue();

        if (petAtual == null) {
            // Se o pet atual for nulo, crie um novo objeto Pet
            petAtual = new Pet();
        }

        // Salva o objeto Pet atualizado no ViewModel
        cadastroPetViewModel.setPet(petAtual);
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

    private void inicializarComponentes(View view) {

        spinnerNivelEnergia = view.findViewById(R.id.spinnerNivelEnergia);
        spinnerAdestrado = view.findViewById(R.id.spinnerAdestrado);
        editTextSociabilidade = view.findViewById(R.id.editTextSociabilidade);
        buttonVoltar = view.findViewById(R.id.buttonVoltar);
        buttonAvancar = view.findViewById(R.id.buttonAvancar);
    }
}