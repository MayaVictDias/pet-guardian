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
import com.google.firebase.Timestamp;
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
    private TextView textViewNomePet, textViewIdadePet, textViewGeneroPet, textViewEspecie,
            textViewSobreOPet, textViewStatusPet, textViewStatusVacinacaoTitulo, textViewStatusVacinacao, textViewVacinasTomadasTitulo,
            textViewVacinasTomadas, textViewVermifugadoTitulo, textViewVermifugado, textViewDataUltimaVermifugacao,
            textViewPetCastrado, textViewHistoricoDoencasTratamentos, textViewNecessidadesEspeciais,
            textViewNivelEnergia, textViewSociabilidade, textViewPetAdestrado;
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

            boolean isAdestrado = "Adestrado".equalsIgnoreCase(textViewPetAdestrado.getText().toString().trim());

            // Cria o objeto Pet com os dados necessários, mas sem a URL da imagem ainda
            pet = new Pet(
                    usuarioLogadoRef.getId(),
                    textViewNomePet.getText().toString(),
                    textViewIdadePet.getText().toString(),
                    textViewGeneroPet.getText().toString(),
                    textViewEspecie.getText().toString(),
                    textViewSobreOPet.getText().toString(),
                    textViewStatusPet.getText().toString(),
                    textViewPetCastrado.getText().toString(),
                    urlImagemPet != null ? urlImagemPet.toString() : null, // Verifique se a URL da imagem é nula
                    textViewStatusVacinacao.getText().toString(),
                    textViewVacinasTomadas.getText().toString(),
                    textViewVermifugado.getText().toString(),
                    textViewDataUltimaVermifugacao.getText().toString(),
                    textViewNecessidadesEspeciais.getText().toString(),
                    textViewHistoricoDoencasTratamentos.getText().toString(),
                    textViewNivelEnergia.getText().toString(),
                    textViewSociabilidade.getText().toString(),
                    isAdestrado,
                    Timestamp.now()

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
                                Log.d("Upload imagem", "Erro ao fazer upload da imagem. Erro: " + e);
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
                                            pet.salvar();

                                            Log.d("Upload imagem", String.valueOf(urlImagemPet));

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
                    textViewStatusVacinacao.setText(pet.getStatusVacinacao());
                    textViewVermifugado.setText(pet.getVermifugado());
                    textViewDataUltimaVermifugacao.setText(pet.getDataVermifugacao());
                    textViewHistoricoDoencasTratamentos.setText(pet.getDoencasTratamentos());
                    textViewNecessidadesEspeciais.setText(pet.getNecessidadesEspeciais());
                    textViewNivelEnergia.setText(pet.getNivelEnergia());
                    textViewSociabilidade.setText(pet.getSociabilidade());

                    if(pet.isAdestrado()) {
                        textViewPetAdestrado.setText("Adestrado");
                    } else {
                        textViewPetAdestrado.setText("Não adestrado");
                    }

                    textViewVacinasTomadas.setText(pet.getVacinasTomadas());
                    textViewPetCastrado.setText(pet.getStatusCastracao());


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
        textViewSobreOPet = view.findViewById(R.id.textViewSobreOPet);
        textViewStatusPet = view.findViewById(R.id.textViewStatusPet);
        buttonPublicar = view.findViewById(R.id.buttonPublicar);
        textViewStatusVacinacao = view.findViewById(R.id.textViewStatusVacinacao);
        textViewVermifugado = view.findViewById(R.id.textViewVermifugado);
        textViewDataUltimaVermifugacao = view.findViewById(R.id.textViewDataUltimaVermifugacao);
        textViewHistoricoDoencasTratamentos = view.findViewById(R.id.textViewHistoricoDoencasTratamentos);
        textViewNecessidadesEspeciais = view.findViewById(R.id.textViewNecessidadesEspeciais);
        textViewNivelEnergia = view.findViewById(R.id.textViewNivelEnergia);
        textViewSociabilidade = view.findViewById(R.id.textViewSociabilidade);
        textViewPetAdestrado = view.findViewById(R.id.textViewPetAdestrado);
        textViewStatusVacinacaoTitulo = view.findViewById(R.id.textViewStatusVacinacaoTitulo);
        textViewStatusVacinacao = view.findViewById(R.id.textViewStatusVacinacao);
        textViewVacinasTomadasTitulo = view.findViewById(R.id.textViewVacinasTomadasTitulo);
        textViewVacinasTomadas = view.findViewById(R.id.textViewVacinasTomadas);
        textViewVermifugado = view.findViewById(R.id.textViewVermifugado);
        textViewDataUltimaVermifugacao = view.findViewById(R.id.textViewDataUltimaVermifugacao);
        textViewPetCastrado = view.findViewById(R.id.textViewPetCastrado);
        textViewHistoricoDoencasTratamentos = view.findViewById(R.id.textViewHistoricoDoencasTratamentos);
        textViewNecessidadesEspeciais = view.findViewById(R.id.textViewNecessidadesEspeciais);
        textViewNivelEnergia = view.findViewById(R.id.textViewNivelEnergia);
        textViewSociabilidade = view.findViewById(R.id.textViewSociabilidade);
        textViewPetAdestrado = view.findViewById(R.id.textViewPetAdestrado);

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