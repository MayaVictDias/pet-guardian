package com.dias.mayara.petguardian.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {

    private static FirebaseFirestore referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference storage;

    // Retorna a referencia do database
    public static FirebaseFirestore getFirebase() {
        if(referenciaFirebase == null) {
            referenciaFirebase = FirebaseFirestore.getInstance();
        }
        return referenciaFirebase;
    }

    // Retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAuth() {
        if ( referenciaAutenticacao == null) {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
         return referenciaAutenticacao;
    }

    public static StorageReference getFirebaseStorage(){
        if( storage == null ){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
