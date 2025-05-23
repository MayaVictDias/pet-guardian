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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ConferirInformacoesNovoPetFragment extends Fragment {

    private Button buttonVoltar, buttonPublicar;
    private FragmentInteractionListener listener;
    private TextView textViewNomePet, textViewIdadePet, textViewGeneroPet, textViewEspecie,
            textViewStatusVacinacao, textViewVacinasTomadas, textViewVermifugado, textViewDataUltimaVermifugacao,
            textViewPetCastrado, textViewHistoricoDoencasTratamentos, textViewCorDosOlhos, textViewCorPredominante,
            textViewPorte, textViewNecessidadesEspeciais, textViewNivelEnergia, textViewSociabilidade,
            textViewPetAdestrado, textViewRaca;

    // TextViews para os títulos
    private TextView textViewStatusVacinacaoTitulo, textViewVacinasTomadasTitulo,
            textViewVermifugadoTitulo, textViewDataVermifugacaoTitulo, textViewPetCastradoTitulo,
            textViewHistoricoDoencasTratamentosTitulo, textViewNecessidadesEspeciaisTitulo, textViewNivelEnergiaTitulo,
            textViewSociabilidadeTitulo, textViewPetAdestradoTitulo;

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

        inicializarComponentes(view);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(CadastroPetViewModel.class);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = ConfiguracaoFirebase.getFirebase().collection("usuarios");
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

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

            // Converte a data de vermifugação de String para Timestamp
            Timestamp dataVermifugacao = convertStringToTimestamp(textViewDataUltimaVermifugacao.getText().toString());

            // Cria o objeto Pet com os dados necessários, mas sem a URL da imagem ainda
            pet = new Pet(
                    idUsuarioLogado,
                    textViewNomePet.getText().toString(),
                    textViewIdadePet.getText().toString(),
                    textViewGeneroPet.getText().toString(),
                    textViewEspecie.getText().toString(),
                    textViewRaca.getText().toString(),
                    textViewCorPredominante.getText().toString(),
                    textViewCorDosOlhos.getText().toString(),
                    textViewPorte.getText().toString(),

                    urlImagemPet != null ? urlImagemPet.toString() : null, // Verifique se a URL da imagem é nula
                    textViewStatusVacinacao.getText().toString(),
                    textViewVacinasTomadas.getText().toString(),
                    textViewVermifugado.getText().toString(),
                    dataVermifugacao, // Passa o Timestamp convertido
                    textViewNecessidadesEspeciais.getText().toString(),
                    textViewHistoricoDoencasTratamentos.getText().toString(),
                    textViewPetCastrado.getText().toString(),
                    textViewNivelEnergia.getText().toString(),
                    textViewSociabilidade.getText().toString(),
                    isAdestrado,
                    Timestamp.now()
            );

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
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Erro ConferirInformacoesNovoPetFragment", "erro: " + e.toString());
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para converter uma String no formato dd/MM/yyyy para um Timestamp
    private Timestamp convertStringToTimestamp(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null; // Retorna null se a string for nula ou vazia
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = dateFormat.parse(dateString);
            return new Timestamp(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Retorna null se a conversão falhar
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
                    // Verifica e atribui os valores aos TextViews
                    textViewNomePet.setText(pet.getNomePet() != null ? pet.getNomePet() : "Não informado");
                    textViewIdadePet.setText(pet.getIdadePet() != null ? pet.getIdadePet() : "Não informado");
                    textViewGeneroPet.setText(pet.getGeneroPet() != null ? pet.getGeneroPet() : "Não informado");
                    textViewEspecie.setText(pet.getEspeciePet() != null ? pet.getEspeciePet() : "Não informado");
                    textViewCorDosOlhos.setText(pet.getCorDosOlhosPet() != null ? pet.getCorDosOlhosPet() : "Não informado");
                    textViewCorPredominante.setText(pet.getCorPredominantePet() != null ? pet.getCorPredominantePet() : "Não informado");
                    textViewPorte.setText(pet.getPortePet() != null ? pet.getPortePet() : "Não informado");
                    textViewRaca.setText(pet.getRacaPet() != null ? pet.getRacaPet() : "Não informado");

                    textViewStatusVacinacao.setText(pet.getStatusVacinacao() != null ? pet.getStatusVacinacao() : "Não informado");
                    textViewVacinasTomadas.setText(pet.getVacinasTomadas() != null ? pet.getVacinasTomadas() : "Não informado");
                    textViewVermifugado.setText(pet.getVermifugado() != null ? pet.getVermifugado() : "Não informado");
                    textViewDataUltimaVermifugacao.setText(pet.getDataVermifugacao() != null ? convertTimestampToString(pet.getDataVermifugacao()) : "Não informado");
                    textViewPetCastrado.setText(pet.getStatusCastracao() != null ? pet.getStatusCastracao() : "Não informado");
                    textViewHistoricoDoencasTratamentos.setText(pet.getDoencasTratamentos() != null ? pet.getDoencasTratamentos() : "Não informado");
                    textViewNecessidadesEspeciais.setText(pet.getNecessidadesEspeciais() != null ? pet.getNecessidadesEspeciais() : "Não informado");

                    textViewNivelEnergia.setText(pet.getNivelEnergia() != null ? pet.getNivelEnergia() : "Não informado");
                    textViewSociabilidade.setText(pet.getSociabilidade() != null ? pet.getSociabilidade() : "Não informado");

                    if (pet.isAdestrado()) {
                        textViewPetAdestrado.setText("Adestrado");
                    } else {
                        textViewPetAdestrado.setText("Não adestrado");
                    }
                }
            }
        });
    }

    // Método para converter um Timestamp em uma String no formato dd/MM/yyyy
    private String convertTimestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return ""; // Retorna uma string vazia se o Timestamp for nulo
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(timestamp.toDate());
    }

    private void inicializarComponentes(View view) {
        // Botões
        buttonVoltar = view.findViewById(R.id.buttonVoltar);
        buttonPublicar = view.findViewById(R.id.buttonPublicar);

        // TextViews (informações gerais)
        textViewNomePet = view.findViewById(R.id.textViewNomePet);
        textViewIdadePet = view.findViewById(R.id.textViewIdadePet);
        textViewGeneroPet = view.findViewById(R.id.textViewGeneroPet);
        textViewEspecie = view.findViewById(R.id.textViewEspecie);
        textViewRaca = view.findViewById(R.id.textViewRaca);

        textViewCorDosOlhos = view.findViewById(R.id.textViewCorDosOlhos);
        textViewCorPredominante = view.findViewById(R.id.textViewCorPredominante);
        textViewPorte = view.findViewById(R.id.textViewPorte);

                // ImageView (foto do pet)
        imageViewFotoPet = view.findViewById(R.id.imageViewFotoPet);

        // TextViews (vacinação)
        textViewStatusVacinacao = view.findViewById(R.id.textViewStatusVacinacao);
        textViewVacinasTomadas = view.findViewById(R.id.textViewVacinasTomadas);

        // TextViews (vermifugação)
        textViewVermifugado = view.findViewById(R.id.textViewVermifugado);
        textViewDataUltimaVermifugacao = view.findViewById(R.id.textViewDataUltimaVermifugacao);

        // TextViews (castração)
        textViewPetCastrado = view.findViewById(R.id.textViewPetCastrado);

        // TextViews (histórico de saúde)
        textViewHistoricoDoencasTratamentos = view.findViewById(R.id.textViewHistoricoDoencasTratamentos);
        textViewNecessidadesEspeciais = view.findViewById(R.id.textViewNecessidadesEspeciais);

        // TextViews (comportamento)
        textViewNivelEnergia = view.findViewById(R.id.textViewNivelEnergia);
        textViewSociabilidade = view.findViewById(R.id.textViewSociabilidade);
        textViewPetAdestrado = view.findViewById(R.id.textViewPetAdestrado);

        // TextViews (títulos)
        textViewStatusVacinacaoTitulo = view.findViewById(R.id.textViewStatusVacinacaoTitulo);
        textViewVacinasTomadasTitulo = view.findViewById(R.id.textViewVacinasTomadasTitulo);
        textViewVermifugadoTitulo = view.findViewById(R.id.textViewVermifugadoTitulo);
        textViewDataVermifugacaoTitulo = view.findViewById(R.id.textViewDataVermifugacaoTitulo);
        textViewPetCastradoTitulo = view.findViewById(R.id.textViewPetCastradoTitulo);
        textViewHistoricoDoencasTratamentosTitulo = view.findViewById(R.id.textViewHistoricoDoencasTratamentosTitulo);
        textViewNecessidadesEspeciaisTitulo = view.findViewById(R.id.textViewNecessidadesEspeciaisTitulo);
        textViewNivelEnergiaTitulo = view.findViewById(R.id.textViewNivelEnergiaTitulo);
        textViewSociabilidadeTitulo = view.findViewById(R.id.textViewSociabilidadeTitulo);
        textViewPetAdestradoTitulo = view.findViewById(R.id.textViewPetAdestradoTitulo);
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