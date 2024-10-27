package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;

public class ConferirInformacoesNovoPetFragment extends Fragment {

    private Button buttonVoltar;
    private FragmentInteractionListener listener;
    private TextView textViewNomePet, textViewIdadePet, textViewGeneroPet, textViewEspecie,
            textViewSobreOPet, textViewStatusPet, textViewCep, textViewEstado, textViewCidade,
            textViewRuaAvenida, textViewNumero, textViewComplemento;

    private Endereco endereco;
    private Pet pet;

    private CadastroPetViewModel sharedViewModel;

    public ConferirInformacoesNovoPetFragment() {
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
        View view = inflater.inflate(R.layout.fragment_conferir_informacoes_novo_pet, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

        inicializarComponentes(view);

        endereco = new Endereco();
        pet = new Pet();

        carregarDados();

        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().getSupportFragmentManager().popBackStack();
                } catch (Exception e) {
                    e.printStackTrace(); // Imprime o erro no log
                }
            }
        });

        return view;
    }

    private void carregarDados() {

        sharedViewModel.getEndereco().observe(getViewLifecycleOwner(), new Observer<Endereco>() {
            @Override
            public void onChanged(Endereco endereco) {
                if (endereco != null) {

                    textViewCep.setText(endereco.getCep());
                    textViewEstado.setText(endereco.getEstado());
                    textViewCidade.setText(endereco.getCidade());
                    textViewRuaAvenida.setText(endereco.getRuaAvenida());
                    textViewNumero.setText(endereco.getNumero());
                    textViewComplemento.setText(endereco.getComplemento());

                }
            }
        });

        // Observa o objeto Pet e atualiza os TextViews quando ele muda
        sharedViewModel.getPet().observe(getViewLifecycleOwner(), new Observer<Pet>() {
            @Override
            public void onChanged(Pet pet) {
                if (pet != null) {
                    textViewNomePet.setText(pet.getNomePet());
                    textViewIdadePet.setText(pet.getIdadePet());
                    textViewGeneroPet.setText(pet.getGeneroPet());
                    textViewEspecie.setText(pet.getEspeciePet());
                    textViewSobreOPet.setText(pet.getSobreOPet());
                    textViewStatusPet.setText(pet.getStatusPet());
                }
            }
        });
    }

    private void inicializarComponentes(View view) {

        buttonVoltar = view.findViewById(R.id.buttonVoltar);
        textViewNomePet = view.findViewById(R.id.textViewNomePet);
        textViewIdadePet = view.findViewById(R.id.textViewIdadePet);
        textViewGeneroPet = view.findViewById(R.id.textViewGeneroPet);
        textViewEspecie = view.findViewById(R.id.textViewEspecie);
        textViewSobreOPet = view.findViewById(R.id.textViewSobreOPet);
        textViewStatusPet = view.findViewById(R.id.textViewStatusPet);
        textViewCep = view.findViewById(R.id.textViewCep);
        textViewEstado = view.findViewById(R.id.textViewEstado);
        textViewCidade = view.findViewById(R.id.textViewCidade);
        textViewRuaAvenida = view.findViewById(R.id.textViewRuaAvenida);
        textViewNumero = view.findViewById(R.id.textViewNumero);
        textViewComplemento = view.findViewById(R.id.textViewComplemento);
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