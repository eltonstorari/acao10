package com.storaritech.acao10app.admin.ui.parceiro;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.storaritech.acao10app.R;


public class ParceiroFragment extends Fragment {
    public ParceiroFragment() {

}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_parceiro, container, false);



        return vista;
    }
}