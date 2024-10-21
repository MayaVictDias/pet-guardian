package com.dias.mayara.petguardian.fragment.cadastrarpet;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.helper.FragmentInteractionListener;

public class InformacoesGeraisPetFragment extends Fragment {

    private Spinner spinner;
    private ImageView imageViewSeta;
    private Button buttonAvancar;
    private FragmentInteractionListener listener;

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

        inicializarComponentes(view);

        buttonAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.replaceFragment(StatusPetFragment.class);

            }
        });

        return view;
    }

    public void onDetach() {
        super.onDetach();
        listener = null; // Limpa a referência
    }

    public void inicializarComponentes(View view) {

        spinner = view.findViewById(R.id.mySpinner);
        imageViewSeta = view.findViewById(R.id.imageViewSeta);
        buttonAvancar = view.findViewById(R.id.buttonAvancar);

    }
}