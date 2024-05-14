package com.storaritech.acao10app;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.storaritech.acao10app.admin.FormMenu_Admin;
import com.storaritech.acao10app.entidades.MySingleton;
import com.storaritech.acao10app.entidades.Usuario;
import com.storaritech.acao10app.usuario.FormMenu_Usuario;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FormLogin extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {


    //var
    private TextView text_tela_cadastro, txt_NivelLogin;
    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;

    RelativeLayout layoutRegistrar;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    StringRequest stringRequest;

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();






        if(usuarioAtual != null){
            carregarWEBService(FirebaseAuth.getInstance().getUid());
            //carregarWEBService(usuarioAtual.getUid());
            //TelaPrincipal();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_login);


        getSupportActionBar().hide();

        //chamar janela (Activity)
        iniciarComponentes();
        text_tela_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FormLogin.this, FormCadastro.class);
                startActivity(intent);
            }
        });

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarComponentes();
                String nivel = txt_NivelLogin.getText().toString();
                //Entra na tela do Usuário (Usuário/ Admin)
                if (nivel.equals("usuario")){
                    //Toast.makeText(getApplicationContext(), nivel, Toast.LENGTH_LONG).show();
                    TelaPrincipal_Usuario();
                } else if (nivel.equals("admin")){
                    TelaPrincipal_Admin();
                    //Toast.makeText(getApplicationContext(), nivel, Toast.LENGTH_LONG).show();
                }
                else if (nivel.equals("UsuarioId")){
                    //Esconde Teclado
                    InputMethodManager imm = (InputMethodManager) getSystemService(FormCadastro.INPUT_METHOD_SERVICE);
                    if(imm.isActive())
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                    String email = edit_email.getText().toString();
                    String senha = edit_senha.getText().toString();

                    if(email.isEmpty() || senha.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Preencha os Campos!",Toast.LENGTH_LONG).show();
                    }else{
                        AutenticarUsuario();

                        //Entra na tela do Usuário (Usuário/ Admin)
                        if (nivel.equals("usuario")){
                            //Toast.makeText(getApplicationContext(), nivel, Toast.LENGTH_LONG).show();
                            TelaPrincipal_Usuario();
                        } else if (nivel.equals("admin")){
                            TelaPrincipal_Admin();
                            //Toast.makeText(getApplicationContext(), nivel, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;



        });
    }

    private void AutenticarUsuario() {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bt_entrar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Usuário Localizado!", Toast.LENGTH_LONG).show();
                            carregarWEBService(FirebaseAuth.getInstance().getUid());

                        }
                    }, 3000);

                }else{
                    String erro;

                    try {
                        throw task.getException();
                    }catch (Exception e){

                        erro = "Login e senha não conferem!";



                    }

                    Toast.makeText(getApplicationContext(), erro,Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void TelaPrincipal_Usuario(){
        Intent intent = new Intent(FormLogin.this, FormMenu_Usuario.class);
        startActivity(intent);
        finish();
    }

    private void TelaPrincipal_Admin(){
        Intent intent = new Intent(FormLogin.this, FormMenu_Admin.class);
        startActivity(intent);
        finish();
    }



    private void iniciarComponentes() {
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        edit_email = findViewById(R.id.edit_email_Login);
        edit_senha = findViewById(R.id.edit_senha_Login);
        bt_entrar = findViewById(R.id.bt_Entrar);
        progressBar = findViewById(R.id.progressbar);
        txt_NivelLogin =  findViewById(R.id.text_NivelLogin);

    }

    private void carregarWEBService(String UserId) {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/usuarios/consultarUrl.php?id=" + UserId;
        url = url.replace(" ", "%20");


        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Usuario tabUsuarios = new Usuario();
                JSONArray json = response.optJSONArray("usuarios");
                JSONObject jsonObject  = null;
                try {
                    assert json != null;
                    jsonObject = json.getJSONObject(0);
                    tabUsuarios.setNome(jsonObject.optString("nome"));
                    tabUsuarios.setEmail(jsonObject.optString("email"));
                    tabUsuarios.setSenha(jsonObject.optString("senha"));
                    tabUsuarios.setNivel(jsonObject.optString("nivel"));
                    tabUsuarios.setUrlImagem(jsonObject.optString("url_imagem"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                    txt_NivelLogin.setText(tabUsuarios.getNivel());
                    bt_entrar.setText("Entrar Agora! ");
                    bt_entrar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    edit_email.setVisibility(View.INVISIBLE);
                    edit_senha.setVisibility(View.INVISIBLE);
                    text_tela_cadastro.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Não foi possivel efetuar a consulta " + error.toString(), Toast.LENGTH_LONG).show();
                Log.i("ERRO", error.toString());
                finishAffinity();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq);


    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

        Toast.makeText(getApplicationContext(), "Erro ao cadastrar no servidor!" + volleyError.toString(), Toast.LENGTH_LONG).show();
        //edit_nome.setText(volleyError.toString());
    }

    @Override
    public void onResponse(JSONObject jsonObject) {

        Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!!!", Toast.LENGTH_LONG).show();
        //Intent intent = new Intent(FormCadastro.this, FormLogin.class);
        //startActivity(intent);


    }


}