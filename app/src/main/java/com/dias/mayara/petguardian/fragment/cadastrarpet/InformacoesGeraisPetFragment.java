package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.helper.Permissao;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;

public class InformacoesGeraisPetFragment extends Fragment {


    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private Button buttonAvancar;
    private FragmentInteractionListener listener;
    private TextInputEditText editTextNomePet, editTextIdadePet, editTextSobreOPet;
    private RadioGroup radioGroupGenero, radioGroupEspecie, radioGroupIdade;
    private RadioButton radioButtonMacho, radioButtonFemea, radioButtonCachorro, radioButtonGato,
            radioButtonFilhote, radioButtonAdulto;
    private CheckBox checkBoxNaoSeiNomePet;
    private ImageView escolherImagemPet;
    private TextView textViewEscolherImagem;

    private static final int SELECAO_CAMERA = 100;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private CadastroPetViewModel cadastroPetViewModel;

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

        // Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        inicializarComponentes(view);

        pet = new Pet();

        cadastroPetViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

        checkBoxNaoSeiNomePet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // O CheckBox foi marcado
                editTextNomePet.setEnabled(false);
            } else {
                // O CheckBox foi desmarcado
                editTextNomePet.setEnabled(true);
            }
        });

        // Observa a imagem armazenada no ViewModel e atualiza a ImageView quando houver alteração
        cadastroPetViewModel.getImagemPet().observe(getViewLifecycleOwner(), imagemBytes -> {
            if (imagemBytes != null) {
                escolherImagemPet.setBackground(null); // Remove o fundo do ImageView
                textViewEscolherImagem.setVisibility(View.GONE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);
                escolherImagemPet.setImageBitmap(bitmap);
            }
        });

        escolherImagemPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(i, SELECAO_CAMERA);

                }
            }
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextNomePet.getText() == null && !checkBoxNaoSeiNomePet.isChecked()
                        || editTextNomePet.getText().toString().trim().isEmpty() && !checkBoxNaoSeiNomePet.isChecked()) {
                    Toast.makeText(getContext(), "Preencha o campo 'Nome do pet'", Toast.LENGTH_SHORT).show();
                } else if (radioGroupIdade.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Preencha o campo 'Idade do pet'", Toast.LENGTH_SHORT).show();
                } else if (radioGroupGenero.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Selecione o gênero do pet", Toast.LENGTH_SHORT).show();
                } else if (radioGroupEspecie.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getContext(), "Selecione a espécie do pet", Toast.LENGTH_SHORT).show();
                } else if (editTextSobreOPet.getText() == null || editTextSobreOPet.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Preencha o campo 'Sobre o pet'", Toast.LENGTH_SHORT).show();
                } else if (escolherImagemPet.getDrawable() == null) {
                    Toast.makeText(getContext(), "Selecione uma imagem", Toast.LENGTH_SHORT).show();
                }else {

                    // Obter o texto dos campos EditText e salvar no objeto pet
                    if (checkBoxNaoSeiNomePet.isChecked()) {
                        pet.setNomePet("Nome desconhecido");
                    } else {
                        pet.setNomePet(editTextNomePet.getText().toString());
                    }

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
                    int selectedIdadeId = radioGroupIdade.getCheckedRadioButtonId();
                    if (selectedIdadeId == R.id.radioButtonFilhote) {
                        pet.setIdadePet("Filhote");
                    } else if (selectedIdadeId == R.id.radioButtonAdulto) {
                        pet.setIdadePet("Adulto");
                    }

                    Log.d("PetInfo", "Nome: " + pet.getNomePet());
                    Log.d("PetInfo", "Idade: " + pet.getIdadePet());
                    Log.d("PetInfo", "Genero: " + pet.getGeneroPet());
                    Log.d("PetInfo", "Especie: " + pet.getEspeciePet());
                    Log.d("PetInfo", "Sobre: " + pet.getSobreOPet());
                    Log.d("PetInfo", "Status: " + pet.getStatusPet());

                    // Salvar o objeto Pet no SharedViewModel
                    cadastroPetViewModel.setPet(pet);

                    // Substituir o fragmento atual pelo próximo
                    listener.replaceFragment(SaudeCuidadosNovoPetFragment.class);

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
        escolherImagemPet = view.findViewById(R.id.escolherImagemPet);
        textViewEscolherImagem = view.findViewById(R.id.textViewEscolherImagem);
        radioButtonCachorro = view.findViewById(R.id.radioButtonCachorro);
        radioButtonGato = view.findViewById(R.id.radioButtonGato);
        checkBoxNaoSeiNomePet = view.findViewById(R.id.checkBoxNaoSeiNomePet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cadastroPetViewModel.getPet().removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECAO_CAMERA && resultCode == getActivity().RESULT_OK) {
            Uri imagemSelecionadaUri = data.getData();
            if (imagemSelecionadaUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imagemSelecionadaUri);
                    escolherImagemPet.setBackground(null); // Remove o fundo do ImageView
                    textViewEscolherImagem.setVisibility(View.GONE);
                    escolherImagemPet.setImageBitmap(bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imagemBytes = baos.toByteArray();

                    // Armazena a imagem no ViewModel
                    cadastroPetViewModel.setImagemPet(imagemBytes);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
