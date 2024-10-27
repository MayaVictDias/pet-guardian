package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.SharedViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class InformacoesGeraisPetFragment extends Fragment {

    private Button buttonAvancar;
    private FragmentInteractionListener listener;
    private TextInputEditText editTextNomePet, editTextIdadePet, editTextSobreOPet;
    private RadioGroup radioGroupGenero, radioGroupEspecie, radioGroupIdade;
    private RadioButton radioButtonMacho, radioButtonFemea, radioButtonCachorro, radioButtonGato,
            radioButtonFilhote, radioButtonAdulto;
    private CheckBox checkBoxNaoSeiNomePet;

    private SharedViewModel sharedViewModel;

    private Pet pet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            listener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_informacoes_gerais_pet, container, false);

        inicializarComponentes(view);

        pet = new Pet();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        checkBoxNaoSeiNomePet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // O CheckBox foi marcado
                editTextNomePet.setEnabled(false);
            } else {
                // O CheckBox foi desmarcado
                editTextNomePet.setEnabled(true);
            }
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editTextNomePet.getText() == null && !checkBoxNaoSeiNomePet.isChecked()
                        || editTextNomePet.getText().toString().trim().isEmpty() && !checkBoxNaoSeiNomePet.isChecked()) {
                    Toast.makeText(getContext(), "Preencha o campo 'Nome do pet'", Toast.LENGTH_SHORT).show();
                } else if(radioGroupIdade.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Preencha o campo 'Idade do pet'", Toast.LENGTH_SHORT).show();
                } else if(radioGroupGenero.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Selecione o gênero do pet", Toast.LENGTH_SHORT).show();
                } else if(radioGroupEspecie.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Selecione a espécie do pet", Toast.LENGTH_SHORT).show();
                } else if(editTextSobreOPet.getText() == null || editTextSobreOPet.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Preencha o campo 'Sobre o pet'", Toast.LENGTH_SHORT).show();
                } else {

                    // Obter o texto dos campos EditText e salvar no objeto pet
                    pet.setNomePet(editTextNomePet.getText().toString());
                    pet.setSobreOPet(editTextSobreOPet.getText().toString());

                    // Obter a seleção de Gênero
                    int selectedGeneroId = radioGroupGenero.getCheckedRadioButtonId();
                    if (selectedGeneroId == R.id.radioButtonMacho) {
                        pet.setGeneroPet("Macho");
                    } else if (selectedGeneroId == R.id.radioButtonFemea) {
                        pet.setGeneroPet("Fêmea");
                    }

                    // Obter a seleção de Espécie
                    int selectedEspecieId = radioGroupEspecie.getCheckedRadioButtonId();
                    if (selectedEspecieId == R.id.radioButtonCachorro) {
                        pet.setEspeciePet("Cachorro");
                    } else if (selectedEspecieId == R.id.radioButtonGato) {
                        pet.setEspeciePet("Gato");
                    }

                    // Obter a seleção de Idade
                    int selectedIdadeId = radioGroupEspecie.getCheckedRadioButtonId();
                    if (selectedIdadeId == R.id.radioButtonFilhote) {
                        pet.setEspeciePet("Filhote");
                    } else if (selectedIdadeId == R.id.radioButtonAdulto) {
                        pet.setEspeciePet("Adulto");
                    }

                    // Salvar o objeto Pet no SharedViewModel
                    sharedViewModel.setPet(pet);

                    // Substituir o fragmento atual pelo próximo
                    listener.replaceFragment(StatusPetFragment.class);

                }
            }
        });

        return view;
    }

    private void setupEditTexts() {
        // Watcher para o nome do pet
        editTextNomePet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setNomePet(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Watcher para a idade do pet
        editTextIdadePet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setIdadePet(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Watcher para a descrição do pet
        editTextSobreOPet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pet.setSobreOPet(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Listener para o RadioGroup de Gênero
        radioGroupGenero.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonMacho) {
                pet.setGeneroPet("Macho");
            } else if (checkedId == R.id.radioButtonFemea) {
                pet.setGeneroPet("Fêmea");
            }
        });

        // Listener para o RadioGroup de Espécie
        radioGroupEspecie.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonCachorro) {
                pet.setEspeciePet("Cachorro");
            } else if (checkedId == R.id.radioButtonGato) {
                pet.setEspeciePet("Gato");
            }
        });

        // Listener para o RadioGroup de Idade
        radioGroupIdade.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonFilhote) {
                pet.setIdadePet("Filhote");
            } else if (checkedId == R.id.radioButtonAdulto) {
                pet.setEspeciePet("Adulto");
            }
        });
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void onDetach() {
        super.onDetach();
        listener = null; // Limpa a referência
    }

    public void inicializarComponentes(View view) {
        buttonAvancar = view.findViewById(R.id.buttonAvancar);

        editTextNomePet = view.findViewById(R.id.editTextNomePet);
        radioGroupIdade = view.findViewById(R.id.radioGroupIdade);
        radioButtonFilhote = view.findViewById(R.id.radioButtonFilhote);
        radioButtonAdulto = view.findViewById(R.id.radioButtonAdulto);
        radioGroupGenero = view.findViewById(R.id.radioGroupGenero);
        radioGroupEspecie = view.findViewById(R.id.radioGroupEspecie);
        editTextSobreOPet = view.findViewById(R.id.editTextSobreOPet);
        radioButtonMacho = view.findViewById(R.id.radioButtonMacho);
        radioButtonFemea = view.findViewById(R.id.radioButtonFemea);
        radioButtonCachorro = view.findViewById(R.id.radioButtonCachorro);
        radioButtonGato = view.findViewById(R.id.radioButtonGato);
        checkBoxNaoSeiNomePet = view.findViewById(R.id.checkBoxNaoSeiNomePet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sharedViewModel.getPet().removeObservers(getViewLifecycleOwner());
    }

}
