package com.dias.mayara.petguardian.helper;

import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dias.mayara.petguardian.R;

import org.w3c.dom.Text;

public class ToolbarHelper {

    public static void setupToolbar(AppCompatActivity activity, Toolbar toolbar, String title) {
        activity.setSupportActionBar(toolbar);

        // Remover o título padrão da ActionBar
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (activity.getSupportActionBar() != null) {

            // Criar um TextView para o título centralizado
            TextView toolbarTitle = new TextView(activity);
            toolbarTitle.setText(title);
            toolbarTitle.setTextSize(20); // Tamanho do texto
            toolbarTitle.setGravity(Gravity.CENTER); // Centraliza o texto

            // Definir LayoutParams para centralizar o TextView na Toolbar
            Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                    Toolbar.LayoutParams.WRAP_CONTENT,
                    Toolbar.LayoutParams.MATCH_PARENT
            );
            layoutParams.gravity = Gravity.CENTER; // Centraliza no eixo horizontal

            // Adicionar o TextView à Toolbar
            toolbar.addView(toolbarTitle, layoutParams);
        }
    }
}