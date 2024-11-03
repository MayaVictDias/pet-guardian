package com.dias.mayara.petguardian.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dias.mayara.petguardian.R;
import com.dias.mayara.petguardian.adapter.PetsAdapter;
import com.dias.mayara.petguardian.model.Pet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Button buttonUsarMinhaLocalizacao;

    private ViewPager2 viewPagerPets;
    private PetsAdapter petsCarouselAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPagerPets = view.findViewById(R.id.viewPagerPets);

        List<Pet> pets = new ArrayList<>(); // ou obtenha a lista de onde for necessário
        PetsAdapter petsAdapter = new PetsAdapter(pets);
        viewPagerPets.setAdapter(petsAdapter);

        return view;
    }


    private void inicializarComponentes(View view) {
        buttonUsarMinhaLocalizacao = view.findViewById(R.id.buttonUsarMinhaLocalizacao);

    }

    private List<Pet> getPetsFromDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pets");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Pet> petList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String idEndereco = snapshot.child("idEndereco").getValue(String.class);
                    String idTutor = snapshot.child("idTutor").getValue(String.class);

                    String especiePet = snapshot.child("especiePet").getValue(String.class);
                    String nomePet = snapshot.child("nomePet").getValue(String.class);
                    String generoPet = snapshot.child("generoPet").getValue(String.class);
                    String idadePet = snapshot.child("especiePet").getValue(String.class);
                    String sobreOPet = snapshot.child("sobreOPet").getValue(String.class);
                    String statusPet = snapshot.child("statusPet").getValue(String.class);

                    int imageResId = R.drawable.pet_guardian_logotipo; // Use uma imagem padrão ou faça o download

                    petList.add(new Pet(nomePet, idadePet, generoPet, especiePet, sobreOPet,
                            statusPet, idEndereco));
                }
                // Atualize o adapter com a nova lista
                petsCarouselAdapter = new PetsAdapter(petList);
                viewPagerPets.setAdapter(petsCarouselAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return null;
    }

}