package com.storaritech.acao10app.admin.ui.CadUsuario;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.storaritech.acao10app.FormCadastro;
import com.storaritech.acao10app.admin.FormMenu_Admin;
import com.storaritech.acao10app.entidades.MySingleton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.storaritech.acao10app.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class CadUsuarioFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener  {
    private EditText edit_id, edit_nome, edit_email, edit_senha;
    Button bt_cadastrar, bt_Foto;
    RadioGroup radioGroup;
    RadioButton radionButton;
    ImageView imgFoto;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    String usuarioID;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_cad_usuario, container, false);
        //getSupportActionBar().hide();
        IniciarComponentes(vista);

        request = Volley.newRequestQueue(getContext());

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Esconde Teclado
                //InputMethodManager imm = (InputMethodManager) getSystemService(CadUsuarioFragment.INPUT_METHOD_SERVICE);
                //if(imm.isActive())
                    //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if(nome.isEmpty() || email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, "Preencha todos os campos", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{

                    CadastrarUsuario(v);
                }

            }
        });





        int itemRadioSelecionado = radioGroup.getCheckedRadioButtonId();
        radionButton = vista.findViewById(itemRadioSelecionado);


        return vista;
    }

    private void CadastrarUsuario(View v){
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){




                    SalvarDadosUsuario();


                    //Snackbar snackbar = Snackbar.make(v, "Cadastro realizado com sucesso!!!", Snackbar.LENGTH_LONG);
                    //snackbar.setBackgroundTint(Color.WHITE);
                    //snackbar.setTextColor(Color.BLACK);
                    //snackbar.show();

                }else{
                    String erro;
                    try{
                        throw task.getException();


                    }catch(FirebaseAuthWeakPasswordException e) {
                        erro = "Digite uma senha com no minimo 6 caracteres";
                    }catch (FirebaseAuthUserCollisionException e) {
                        erro = "Está conta já existe!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "Email inválido";
                    }catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });

    }

    private void SalvarDadosUsuario() {
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();
        //carregarWebService
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/usuarios/registro.php?id="+usuarioID+"&nome="+nome+"&email="+email+"&senha="+senha+"&nivel=usuario&url_imagem=imagem";
        url = url.replace(" ", "%20");

        //edit_nome.setText(url);

        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectReq);
    }


    private void IniciarComponentes(View v){
        edit_id = v.findViewById(R.id.edit_CadUsu_id);
        edit_nome = v.findViewById(R.id.edit_CadUsu_nome);
        edit_email = v.findViewById(R.id.edit_CadUsu_email);
        edit_senha = v.findViewById(R.id.edit_CadUsu_Senha);
        radioGroup = v.findViewById(R.id.group_CadUsu_Usuario);

        bt_cadastrar = v.findViewById(R.id.btn_CadUsu_Cadastro);
        imgFoto = v.findViewById(R.id.img_CadUsu_Foto);
        bt_Foto = v.findViewById(R.id.btn_CadUsu_Img);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getContext(), "Erro ao cadastrar no servidor!!! --->" + volleyError.toString(), Toast.LENGTH_LONG).show();
        //edit_nome.setText(volleyError.toString());

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioAtual.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //TelaLogin();
            }
        });
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        Toast.makeText(getContext(), "Cadastro realizado com sucesso!!!", Toast.LENGTH_LONG).show();
        //TelaLogin();
    }
}
