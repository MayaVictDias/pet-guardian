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
import android.widget.Toast;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ConferirInformacoesNovoPetFragment extends Fragment {

    private List<View> adocaoComponents;
    private List<View> desaparecidoComponents;
    private List<View> procurandoDonoComponents;

    private Button buttonVoltar, buttonPublicar;
    private FragmentInteractionListener listener;
    private TextView textViewNomePet, textViewIdadePet, textViewBairro, textViewGeneroPet, textViewEspecie,
            textViewSobreOPet, textViewStatusPet, textViewCep, textViewEstado, textViewCidade,
            textViewRuaAvenida, textViewNumero, textViewComplemento, textViewVistoPelaUltimaVez;

    private CadastroPetViewModel sharedViewModel;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private String idUsuarioLogado;

    private Pet pet;
    private Endereco endereco;

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

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        inicializarComponentes(view);

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

        buttonPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Cria o objeto Endereco
                    endereco = new Endereco(
                            textViewCep.getText().toString(),
                            textViewEstado.getText().toString(),
                            textViewCidade.getText().toString(),
                            textViewBairro.getText().toString(),
                            textViewRuaAvenida.getText().toString(),
                            textViewNumero.getText().toString(),
                            textViewComplemento.getText().toString()
                    );

                    // Salva o endereço no Firebase
                    endereco.salvar();

                    // Cria o objeto Pet, passando idUsuarioLogado como idTutor
                    pet = new Pet(
                            textViewNomePet.getText().toString(),
                            textViewIdadePet.getText().toString(),
                            textViewGeneroPet.getText().toString(),
                            textViewEspecie.getText().toString(),
                            textViewSobreOPet.getText().toString(),
                            textViewStatusPet.getText().toString(),
                            endereco.getIdEndereco()
                    );

                    // Salva o pet no Firebase
                    pet.salvar();

                    // Exibe uma mensagem de sucesso
                    Toast.makeText(getView().getContext(), "Pet cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                    requireActivity().finish();
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
                    textViewBairro.setText(endereco.getBairro());
                    textViewRuaAvenida.setText(endereco.getRuaAvenida());
                    textViewNumero.setText(endereco.getNumero());
                    textViewComplemento.setText(endereco.getComplemento());
                }
            }
        });

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

                    // Verifica o status e chama toggleOptions com o ID apropriado
                    if ("Procurando dono".equals(pet.getStatusPet())) {
                        toggleOptions(R.id.radioButtonProcurandoDono);
                    } else if ("Desaparecido".equals(pet.getStatusPet())) {
                        toggleOptions(R.id.radioButtonDesaparecido);
                    } else if ("Adoção".equals(pet.getStatusPet())) {
                        toggleOptions(R.id.radioButtonAdocao);
                    }
                }
            }
        });
    }

    private void salvarDadosFirebase() {

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
        textViewBairro = view.findViewById(R.id.textViewBairro);
        textViewRuaAvenida = view.findViewById(R.id.textViewRuaAvenida);
        textViewNumero = view.findViewById(R.id.textViewNumero);
        textViewComplemento = view.findViewById(R.id.textViewComplemento);
        textViewVistoPelaUltimaVez = view.findViewById(R.id.textViewVistoPelaUltimaVez);
        buttonPublicar = view.findViewById(R.id.buttonPublicar);

        // Inicializa as listas de componentes
        inicializarListaDesaparecidoComponentes(view);
        inicializarListaAdocaoComponentes(view);
        inicializarListaProcurandoDonoComponents(view);
    }

    private void inicializarListaDesaparecidoComponentes(View view) {
        desaparecidoComponents = new ArrayList<>();
        desaparecidoComponents.add(textViewCep);
        desaparecidoComponents.add(textViewVistoPelaUltimaVez);
        desaparecidoComponents.add(textViewEstado);
        desaparecidoComponents.add(textViewCidade);
        desaparecidoComponents.add(textViewBairro);
        desaparecidoComponents.add(textViewRuaAvenida);
        desaparecidoComponents.add(textViewNumero);
        desaparecidoComponents.add(textViewComplemento);
    }

    private void inicializarListaAdocaoComponentes(View view) {
        adocaoComponents = new ArrayList<>();
    }

    private void inicializarListaProcurandoDonoComponents(View view) {
        procurandoDonoComponents = new ArrayList<>();
        procurandoDonoComponents.add(textViewCep);
        procurandoDonoComponents.add(textViewEstado);
        procurandoDonoComponents.add(textViewCidade);
        procurandoDonoComponents.add(textViewBairro);
        procurandoDonoComponents.add(textViewVistoPelaUltimaVez);
        procurandoDonoComponents.add(textViewRuaAvenida);
        procurandoDonoComponents.add(textViewNumero);
        procurandoDonoComponents.add(textViewComplemento);
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