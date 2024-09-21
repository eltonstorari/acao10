package com.storaritech.acao10app.admin.ui.parceiro;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.entidades.Clube;
import com.storaritech.acao10app.entidades.Parceiro;

import java.util.ArrayList;


public class ParceiroFragment extends Fragment {

    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    ArrayList<Parceiro> listaParceiros;
    EditText editNome, editCNPJ, editWhatsapp, editInstagram, editContato, editSite;
    ImageView imgFoto;
    TextView txt_parceiroAdmin_propaganda,  txt_parceiroAdmin_propagandaAtivas, txt_parceiroAdmin_cadastro;

    public ParceiroFragment() {

}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_parceiro, container, false);

        editCNPJ = vista.findViewById(R.id.edit_parceiroAdmin_cnpj);
        editNome = vista.findViewById(R.id.edit_parceiroAdmin_nome);
        editWhatsapp = vista.findViewById(R.id.edit_parceiroAdmin_whatsapp);
        editInstagram = vista.findViewById(R.id.edit_parceiroAdmin_instagram);
        editContato = vista.findViewById(R.id.edit_parceiroAdmin_contato);
        editSite = vista.findViewById(R.id.edit_parceiroAdmin_site);

        imgFoto = vista.findViewById(R.id.img_parceriosAdmin_Foto);

        txt_parceiroAdmin_propaganda = vista.findViewById(R.id.txt_parceiroAdmin_propaganda);
        txt_parceiroAdmin_propagandaAtivas = vista.findViewById(R.id.txt_parceiroAdmin_propagandaAtivas);
        txt_parceiroAdmin_cadastro = vista.findViewById(R.id.txt_parceiroAdmin_cadastro);

        carregarWEBServiceListaParceiros();


        return vista;
    }

    private void carregarWEBServiceListaParceiros() {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/parceiros/consultarLista.php";
    }
}