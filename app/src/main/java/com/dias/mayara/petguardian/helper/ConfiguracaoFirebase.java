package com.dias.mayara.petguardian.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;

    // Retorna a referencia do database
    public static DatabaseReference getFirebase() {
        if(referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    // Retorna a instancia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao() {
        if ( referenciaAutenticacao == null) {
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
         return referenciaAutenticacao;
    }
}
