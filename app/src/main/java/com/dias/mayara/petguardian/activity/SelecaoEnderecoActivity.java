package com.dias.mayara.petguardian.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.pm.PackageManager;

import com.dias.mayara.petguardian.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;


public class SelecaoEnderecoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selecao_endereco);

        // Inicialize o mapa
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configure padding para insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        // Habilite o botão de localização
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Adicione listener ao botão de localização
            googleMap.setOnMyLocationButtonClickListener(() -> {
                // Opção para implementar ações extras ao clicar no botão
                return false; // Retorne false para manter o comportamento padrão
            });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        // Listener para clique no mapa
        googleMap.setOnMapClickListener(latLng -> {
            Geocoder geocoder = new Geocoder(this, new Locale("pt", "BR"));
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    Intent resultIntent = new Intent();

                    // Recuperando os dados corretamente
                    resultIntent.putExtra("ENDERECO_COMPLETO", address.getAddressLine(0));
                    resultIntent.putExtra("PAIS", address.getCountryName());

                    // A cidade sempre será preenchida com o valor correto
                    resultIntent.putExtra("CIDADE", address.getLocality());

                    // O bairro ficará vazio se não for encontrado
                    resultIntent.putExtra("BAIRRO", address.getSubLocality() != null ? address.getSubLocality() : "");

                    // O estado
                    resultIntent.putExtra("ESTADO", address.getAdminArea());

                    // O CEP
                    resultIntent.putExtra("CEP", address.getPostalCode());

                    // A rua
                    resultIntent.putExtra("RUA", address.getThoroughfare());

                    // O número
                    resultIntent.putExtra("NUMERO", address.getSubThoroughfare());

                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }

}
