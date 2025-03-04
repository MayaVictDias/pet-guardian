package com.dias.mayara.petguardian.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dias.mayara.petguardian.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

// Classe que faz as tratativas de um usuário especifico dentro do app
public class UsuarioFirebase {

    // Metodo que recupera o usuário atualmente logado no app
    public static FirebaseUser getUsuarioAtual() {

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();

        return usuario.getCurrentUser();
    }

    // Metodo para atualizar o nome do usuário
    public static void atualizarNomeUsuario(String nome) {

        try {

            // Recuperar usuario que está logado no app
            FirebaseUser user = getUsuarioAtual();

            // Configuração do objeto para alteração de perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.
                    Builder().
                    setDisplayName( nome ) .
                    build();

            DocumentReference usuarioRef = ConfiguracaoFirebase.getFirebase().collection("usuarios").
                    document(getUsuarioAtual().getUid());

            usuarioRef.update("nomeUsuario", nome);

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar nome do usuário");
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado() {

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmailUsuario(firebaseUser.getEmail());
        usuario.setNomeUsuario(firebaseUser.getDisplayName());
        usuario.setIdUsuario(firebaseUser.getUid());

        DocumentReference usuarioRef = ConfiguracaoFirebase.getFirebase().collection("usuarios")
                .document(usuario.getIdUsuario());

        Log.d("Usuário firebase getIdUsuario", usuario.getIdUsuario());

        if (firebaseUser.getPhotoUrl() == null) {
            usuario.setCaminhoFotoUsuario("");
        } else {
            usuario.setCaminhoFotoUsuario(firebaseUser.getPhotoUrl().toString());
        }

        // Recupera os dados adicionais
            usuarioRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String celularUsuario = document.getString("celularUsuario");
                    String estadoUsuario = document.getString("estadoUsuario");
                    String cidadeUsuario = document.getString("cidadeUsuario");
                    String caminhoFoto = document.getString("caminhoFoto");

                    usuario.setCelularUsuario(celularUsuario);
                    usuario.setEstadoUsuario(estadoUsuario);
                    usuario.setCidadeUsuario(cidadeUsuario);
                    usuario.setCaminhoFotoUsuario(caminhoFoto);

                } else {
                    Log.d("Firestore", "Documento não encontrado!");
                }
            } else {
                Log.d("Firestore", "Erro ao recuperar dados do usuário: ", task.getException());
            }
        });

        return usuario;
    }

    public static void atualizarFotoUsuario(Uri url){

        try {

            //Usuario logado no App
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri( url )
                    .build();
            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Perfil","Erro ao atualizar a foto de perfil." );
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}