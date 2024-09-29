package com.storaritech.acao10app.usuario.ui.doarAgora;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.storaritech.acao10app.databinding.FragmentDoarAgoraBinding;

public class DoarAgoraFragment extends Fragment {

private FragmentDoarAgoraBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {


    binding = FragmentDoarAgoraBinding.inflate(inflater, container, false);
    View root = binding.getRoot();




        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}