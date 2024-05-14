package com.storaritech.acao10app.admin.ui.CadUsuario;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.storaritech.acao10app.entidades.MySingleton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.storaritech.acao10app.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class CadUsuarioFragment extends Fragment {
    EditText campoID, campoNome, campoEmail, campoSenha;
    Button botaoCadastrar, botaoFoto;
    RadioGroup radioGroup;
    RadioButton radionButton;
    ImageView imgFoto;
    ConstraintLayout layoutCadastrar;
    //RequestQueue resquest;
    JsonObjectRequest jsonObjectReq;
    StringRequest stringRequest;

    private static final int COD_SELECIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int COD_PERMISSAO = 100;

    private static final String PASTA_PRINCIPAL = "minhasImagensApp/"; //dir principal
    private static final String PASTA_IMAGEM = "imagens";
    private static final String DIRETORIO_IMAGEM = PASTA_PRINCIPAL + PASTA_IMAGEM;

    private String path;
    File fileImagem;

    Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_cad_usuario, container, false);
        campoID = vista.findViewById(R.id.edit_CadUsu_id);
        campoNome = vista.findViewById(R.id.edit_CadUsu_nome);
        campoEmail = vista.findViewById(R.id.edit_CadUsu_email);
        campoSenha = vista.findViewById(R.id.edit_CadUsu_Senha);
        radioGroup = vista.findViewById(R.id.group_CadUsu_Usuario);

        botaoCadastrar = vista.findViewById(R.id.btn_CadUsu_Cadastro);
        imgFoto = vista.findViewById(R.id.img_CadUsu_Foto);
        botaoFoto = vista.findViewById(R.id.btn_CadUsu_Img);

        int itemRadioSelecionado = radioGroup.getCheckedRadioButtonId();
        radionButton = vista.findViewById(itemRadioSelecionado);


        return vista;
    }
}
