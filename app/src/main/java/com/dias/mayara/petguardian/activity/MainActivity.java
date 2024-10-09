package com.dias.mayara.petguardian.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dias.mayara.petguardian.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("exibir_modal_boas_vindas", false)) {
            exibirModalBoasVindas();
        }


    }

    private void exibirModalBoasVindas() {
        new AlertDialog.Builder(this)
                .setTitle("Bem-vindo!")
                .setMessage("Seja bem-vindo ao Pet Guardian!")
                .setPositiveButton("Valeu!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)  // Modal não pode ser cancelada ao tocar fora dela
                .show();
    }


}