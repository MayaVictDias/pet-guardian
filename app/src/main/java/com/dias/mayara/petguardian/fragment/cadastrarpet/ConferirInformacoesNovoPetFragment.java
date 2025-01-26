package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.ConfiguracaoFirebase;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;
import com.dias.mayara.petguardian.helper.UsuarioFirebase;
import com.dias.mayara.petguardian.model.CadastroPetViewModel;
import com.dias.mayara.petguardian.model.Endereco;
import com.dias.mayara.petguardian.model.Pet;
import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConferirInformacoesNovoPetFragment extends Fragment {

    private List<View> adocaoComponents;
    private List<View> desaparecidoComponents;
    private List<View> procurandoDonoComponents;

    private Button buttonVoltar, buttonPublicar;
    private FragmentInteractionListener listener;
    private TextView textViewNomePet, textViewIdadePet, textViewBairro, textViewGeneroPet, textViewEspecie,
            textViewSobreOPet, textViewStatusPet, textViewCep, textViewEstado, textViewCidade,
            textViewRuaAvenida, textViewNumero, textViewComplemento, textViewVistoPelaUltimaVez, textViewPais,
            textViewCepTitulo, textViewBairroTitulo, textViewPaisTitulo, textViewEstadoTitulo, textViewCidadeTitulo,
            textViewRuaAvenidaTitulo, textViewNumeroTitulo, textViewComplementoTitulo, textViewPontoReferenciaTitulo,
            textViewPontoReferencia;
    private ImageView imageViewFotoPet;

    private CadastroPetViewModel sharedViewModel;

    private FirebaseFirestore firebaseRef;
    private CollectionReference usuariosRef;
    private DocumentReference usuarioLogadoRef;
    private String idUsuarioLogado;
    private String idPet;
    private AlertDialog dialog;
    private Usuario usuario;

    private Pet pet;
    private Endereco endereco;

    private Uri urlImagemPet;

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
        usuariosRef = ConfiguracaoFirebase.getFirebase().collection("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        inicializarComponentes(view);

        carregarDados();

        // Configurar a imagem do pet
        configurarImagemPet();

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

                abrirDialogCarregamento("Salvando pet");

                salvarDados();
            }
        });


        return view;
    }

    private void abrirDialogCarregamento(String titulo) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(titulo);
        alert.setCancelable(false); // Impede que o usuário cancele a tela de carregamento
        alert.setView(R.layout.dialog_carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void salvarDados() {
        try {

            // Cria o objeto Endereco e salva no Firebase
            endereco = new Endereco(
                    textViewCep.getText().toString(),
                    textViewEstado.getText().toString(),
                    textViewCidade.getText().toString(),
                    textViewBairro.getText().toString(),
                    textViewRuaAvenida.getText().toString(),
                    textViewNumero.getText().toString(),
                    textViewComplemento.getText().toString(),
                    textViewPais.getText().toString(),
                    textViewPontoReferencia.getText().toString()
            );
            endereco.salvar();

            // Cria o objeto Pet com os dados necessários, mas sem a URL da imagem ainda
            pet = new Pet(
                    textViewNomePet.getText().toString(),
                    textViewNomePet.getText().toString().toUpperCase(),
                    textViewIdadePet.getText().toString(),
                    textViewGeneroPet.getText().toString(),
                    textViewEspecie.getText().toString(),
                    textViewSobreOPet.getText().toString(),
                    textViewStatusPet.getText().toString(),
                    urlImagemPet != null ? urlImagemPet.toString() : null, // Verifique se a URL da imagem é nula
                    endereco.getIdEndereco(),
                    idUsuarioLogado
            );

            usuario.setQuantidadePetsCadastrados(usuario.getQuantidadePetsCadastrados() + 1);
            usuario.atualizar();

            // Faça o upload da imagem e defina a URL da imagem no objeto Pet após o upload bem-sucedido
            sharedViewModel.getImagemPet().observe(getViewLifecycleOwner(), new Observer<byte[]>() {
                @Override
                public void onChanged(byte[] imagemBytes) {
                    if (imagemBytes != null) {
                        // Converte o array de bytes em um Bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);

                        // Cria referência para o Firebase Storage
                        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
                        StorageReference imagemRef = storageRef.child("imagens")
                                .child("pets")
                                .child(pet.getIdPet() + ".jpeg");
                        Log.d("Imagem ref", imagemRef.toString());

                        // Recuperar dados da imagem para salvar no Firebase
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] dadosImagem = baos.toByteArray();
                        Log.d("Dados imagem", Arrays.toString(dadosImagem));

                        // Faz o upload da imagem no Firebase Storage
                        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Erro ao fazer upload da imagem. Erro: " + e, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            urlImagemPet = task.getResult();

                                            pet.setImagemUrl(urlImagemPet.toString());

                                            Log.d("URL Imagem Pet", String.valueOf(urlImagemPet));

                                            if (textViewStatusPet.getText().equals("Adoção")) {
                                                // Após definir a URL da imagem, salva o objeto Pet no Firebase
                                                pet.salvarAdocao(); // Certifique-se que este método inclui o timestamp

                                            } else if (textViewStatusPet.getText().equals("Desaparecido")) {
                                                pet.salvarDesaparecido();

                                            } else if (textViewStatusPet.getText().equals("Procurando dono")) {

                                                pet.salvarProcurandoDono();
                                            }

                                            // Exibe uma mensagem de sucesso
                                            Toast.makeText(getView().getContext(), "Pet cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                            requireActivity().finish();
                                        } else {
                                            Toast.makeText(getContext(), "Erro ao obter URL da imagem", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adicione este método no seu fragment
    private void configurarImagemPet() {
        sharedViewModel.getImagemPet().observe(getViewLifecycleOwner(), new Observer<byte[]>() {
            @Override
            public void onChanged(byte[] imagemBytes) {
                if (imagemBytes != null) {
                    // Converte o array de bytes em um Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imagemBytes, 0, imagemBytes.length);
                    imageViewFotoPet.setImageBitmap(bitmap);
                }
            }
        });
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
                    textViewPais.setText(endereco.getPais());
                    textViewPontoReferencia.setText(endereco.getPontoReferencia());
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

    private void inicializarComponentes(View view) {

        buttonVoltar = view.findViewById(R.id.buttonVoltar);
        textViewNomePet = view.findViewById(R.id.textViewNomePet);
        textViewIdadePet = view.findViewById(R.id.textViewIdadePet);
        textViewGeneroPet = view.findViewById(R.id.textViewGeneroPet);
        textViewEspecie = view.findViewById(R.id.textViewEspecie);
        imageViewFotoPet = view.findViewById(R.id.imageViewFotoPet);
        textViewCepTitulo = view.findViewById(R.id.textViewCepTitulo);
        textViewBairroTitulo = view.findViewById(R.id.textViewBairroTitulo);
        textViewSobreOPet = view.findViewById(R.id.textViewSobreOPet);
        textViewEstadoTitulo = view.findViewById(R.id.textViewEstadoTitulo);
        textViewStatusPet = view.findViewById(R.id.textViewStatusPet);
        textViewCep = view.findViewById(R.id.textViewCep);
        textViewEstado = view.findViewById(R.id.textViewEstado);
        textViewRuaAvenidaTitulo = view.findViewById(R.id.textViewRuaAvenidaTitulo);
        textViewCidade = view.findViewById(R.id.textViewCidade);
        textViewPontoReferenciaTitulo = view.findViewById(R.id.textViewPontoReferenciaTitulo);
        textViewPontoReferencia = view.findViewById(R.id.textViewPontoReferencia);
        textViewCidadeTitulo = view.findViewById(R.id.textViewCidadeTitulo);
        textViewBairro = view.findViewById(R.id.textViewBairro);
        textViewRuaAvenida = view.findViewById(R.id.textViewRuaAvenida);
        textViewNumero = view.findViewById(R.id.textViewNumero);
        textViewPais = view.findViewById(R.id.textViewPais);
        textViewNumeroTitulo = view.findViewById(R.id.textViewNumeroTitulo);
        textViewComplementoTitulo = view.findViewById(R.id.textViewComplementoTitulo);
        textViewPaisTitulo = view.findViewById(R.id.textViewPaisTitulo);
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
        desaparecidoComponents.add(textViewPaisTitulo);
        desaparecidoComponents.add(textViewPais);
        desaparecidoComponents.add(textViewEstado);
        desaparecidoComponents.add(textViewCepTitulo);
        desaparecidoComponents.add(textViewEstadoTitulo);
        desaparecidoComponents.add(textViewCidadeTitulo);
        desaparecidoComponents.add(textViewBairroTitulo);
        desaparecidoComponents.add(textViewRuaAvenidaTitulo);
        desaparecidoComponents.add(textViewComplementoTitulo);
        desaparecidoComponents.add(textViewCidade);
        desaparecidoComponents.add(textViewPontoReferenciaTitulo);
        desaparecidoComponents.add(textViewPontoReferencia);
        desaparecidoComponents.add(textViewNumeroTitulo);
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
        procurandoDonoComponents.add(textViewPais);
        procurandoDonoComponents.add(textViewCidade);
        procurandoDonoComponents.add(textViewCepTitulo);
        procurandoDonoComponents.add(textViewEstadoTitulo);
        procurandoDonoComponents.add(textViewNumeroTitulo);
        procurandoDonoComponents.add(textViewBairro);
        procurandoDonoComponents.add(textViewVistoPelaUltimaVez);
        procurandoDonoComponents.add(textViewPontoReferenciaTitulo);
        procurandoDonoComponents.add(textViewPontoReferencia);
        procurandoDonoComponents.add(textViewPaisTitulo);
        procurandoDonoComponents.add(textViewCidadeTitulo);
        procurandoDonoComponents.add(textViewRuaAvenida);
        procurandoDonoComponents.add(textViewNumero);
        procurandoDonoComponents.add(textViewBairroTitulo);
        procurandoDonoComponents.add(textViewComplementoTitulo);
        procurandoDonoComponents.add(textViewRuaAvenidaTitulo);
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