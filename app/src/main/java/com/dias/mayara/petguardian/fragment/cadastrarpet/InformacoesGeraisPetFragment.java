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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
    private TextInputEditText editTextNomePet, editTextIdadePet;
    private RadioGroup radioGroupGenero, radioGroupEspecie, radioGroupIdade;
    private RadioButton radioButtonMacho, radioButtonFemea, radioButtonCachorro, radioButtonGato,
            radioButtonFilhote, radioButtonAdulto;
    private TextView textViewExplicacaoRaca, textViewRaca, textViewEscolherImagem;
    private CheckBox checkBoxNaoSeiNomePet;
    private ImageView escolherImagemPet;

    private Spinner spinnerCorDosOlhos, spinnerPorte, spinnerCorPredominante, spinnerRaca;

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

        configurarSpinners();

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

        // Configurar a visibilidade dos componentes de raça com base na espécie selecionada
        radioGroupEspecie.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonCachorro) {
                // Se a espécie for cachorro
                spinnerRaca.setVisibility(View.VISIBLE);
                textViewExplicacaoRaca.setVisibility(View.VISIBLE);
                textViewRaca.setVisibility(View.VISIBLE);

                // Configurar o Spinner com o array de raças de cachorro
                ArrayAdapter<CharSequence> adapterRacaCachorro = ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.racas_cachorro_array,
                        android.R.layout.simple_spinner_item
                );
                adapterRacaCachorro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRaca.setAdapter(adapterRacaCachorro);

                Toast.makeText(getContext(), "Lista de raças configurada para exibir cachorros", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioButtonGato) {
                // Se a espécie for gato
                spinnerRaca.setVisibility(View.VISIBLE);
                textViewExplicacaoRaca.setVisibility(View.VISIBLE);
                textViewRaca.setVisibility(View.VISIBLE);

                // Configurar o Spinner com o array de raças de gato
                ArrayAdapter<CharSequence> adapterRacaGato = ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.racas_gato_array,
                        android.R.layout.simple_spinner_item
                );
                adapterRacaGato.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerRaca.setAdapter(adapterRacaGato);Toast.makeText(getContext(), "Lista de raças configurada para exibir gatos", Toast.LENGTH_SHORT).show();
            } else {
                // Se nenhuma espécie for selecionada, ocultar os componentes de raça
                spinnerRaca.setVisibility(View.GONE);
                textViewExplicacaoRaca.setVisibility(View.GONE);
                textViewRaca.setVisibility(View.GONE);
            }

            // Resetar a seleção do Spinner para "Selecione"
            spinnerRaca.setSelection(0);
        });

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar se todos os campos estão preenchidos corretamente
                if (!validarCampos()) {
                    return; // Interrompe o processo se algum campo estiver inválido
                }

                // Obter o texto dos campos EditText e salvar no objeto pet
                if (checkBoxNaoSeiNomePet.isChecked()) {
                    pet.setNomePet("Nome desconhecido");
                } else {
                    pet.setNomePet(editTextNomePet.getText().toString());
                }

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

                // Obter a seleção dos Spinners
                pet.setCorDosOlhosPet(spinnerCorDosOlhos.getSelectedItem().toString());
                pet.setPortePet(spinnerPorte.getSelectedItem().toString());
                pet.setCorPredominantePet(spinnerCorPredominante.getSelectedItem().toString());

                // Salvar a raça selecionada (se visível)
                if (spinnerRaca.getVisibility() == View.VISIBLE) {
                    pet.setRacaPet(spinnerRaca.getSelectedItem().toString());
                }

                // Salvar o objeto Pet no SharedViewModel
                cadastroPetViewModel.setPet(pet);

                // Substituir o fragmento atual pelo próximo
                listener.replaceFragment(SaudeCuidadosNovoPetFragment.class);
            }
        });

        return view;
    }

    private boolean validarCampos() {
        // Verificar se o campo "Nome do pet" está preenchido
        if (editTextNomePet.getText() == null && !checkBoxNaoSeiNomePet.isChecked()
                || editTextNomePet.getText().toString().trim().isEmpty() && !checkBoxNaoSeiNomePet.isChecked()) {
            Toast.makeText(getContext(), "Preencha o campo 'Nome do pet'", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se a idade do pet foi selecionada
        if (radioGroupIdade.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Preencha o campo 'Idade do pet'", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se o gênero do pet foi selecionado
        if (radioGroupGenero.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Selecione o gênero do pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se a espécie do pet foi selecionada
        if (radioGroupEspecie.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Selecione a espécie do pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se uma imagem foi selecionada
        if (escolherImagemPet.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecione uma imagem", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se a cor dos olhos foi selecionada
        if (spinnerCorDosOlhos.getSelectedItem().toString().equals("Selecione")) {
            Toast.makeText(getContext(), "Selecione a cor dos olhos do pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se o porte foi selecionado
        if (spinnerPorte.getSelectedItem().toString().equals("Selecione")) {
            Toast.makeText(getContext(), "Selecione o porte do pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se a cor predominante foi selecionada
        if (spinnerCorPredominante.getSelectedItem().toString().equals("Selecione")) {
            Toast.makeText(getContext(), "Selecione a cor predominante do pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Verificar se a raça foi selecionada (se o spinner estiver visível)
        if (spinnerRaca.getVisibility() == View.VISIBLE && spinnerRaca.getSelectedItem().toString().equals("Selecione")) {
            Toast.makeText(getContext(), "Selecione a raça do pet", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Se todas as verificações passarem, retornar true
        return true;
    }

    private void configurarSpinners() {

        // Configuração do Spinner para cor dos olhos
        ArrayAdapter<CharSequence> adapterCorDosOlhos = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.cores_dos_olhos_array,
                android.R.layout.simple_spinner_item
        );
        adapterCorDosOlhos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorDosOlhos.setAdapter(adapterCorDosOlhos);

        // Configuração do Spinner para porte
        ArrayAdapter<CharSequence> adapterPorte = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.porte_array,
                android.R.layout.simple_spinner_item
        );
        adapterPorte.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPorte.setAdapter(adapterPorte);

        // Configuração do Spinner para cor predominante
        ArrayAdapter<CharSequence> adapterCorPredominante = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.cor_predominante_array,
                android.R.layout.simple_spinner_item
        );
        adapterCorPredominante.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorPredominante.setAdapter(adapterCorPredominante);

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
        radioButtonMacho = view.findViewById(R.id.radioButtonMacho);
        radioButtonFemea = view.findViewById(R.id.radioButtonFemea);
        escolherImagemPet = view.findViewById(R.id.imagemUsuario);
        textViewEscolherImagem = view.findViewById(R.id.textViewEscolherImagem);
        radioButtonCachorro = view.findViewById(R.id.radioButtonCachorro);
        radioButtonGato = view.findViewById(R.id.radioButtonGato);
        textViewRaca = view.findViewById(R.id.textViewRaca);
        checkBoxNaoSeiNomePet = view.findViewById(R.id.checkBoxNaoSeiNomePet);
        spinnerCorDosOlhos = view.findViewById(R.id.spinnerCorDosOlhos);
        spinnerPorte = view.findViewById(R.id.spinnerPorte);
        spinnerCorPredominante = view.findViewById(R.id.spinnerCorPredominante);
        spinnerRaca = view.findViewById(R.id.spinnerRaca);
        textViewExplicacaoRaca = view.findViewById(R.id.textViewExplicacaoRaca);

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
