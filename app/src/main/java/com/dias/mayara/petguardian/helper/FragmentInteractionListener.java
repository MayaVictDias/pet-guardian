package com.dias.mayara.petguardian.helper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface FragmentInteractionListener {
    void onAvancarButtonClicked();
    void replaceFragment(Class<? extends Fragment> fragmentClass); // Novo método
}
