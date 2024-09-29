package com.storaritech.acao10app.usuario.ui.doarAgora;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.storaritech.acao10app.R;
import com.storaritech.acao10app.databinding.FragmentDoarAgoraBinding;


public class DoarAgoraFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View vista = inflater.inflate(R.layout.fragment_doar_agora, container, false);


        return vista;
    }

}

