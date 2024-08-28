package com.storaritech.acao10app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

public class FormCadastro extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener  {

    private EditText edit_nome, edit_email, edit_senha;
    private Button bt_cadastrar;
    private String usuarioID;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();


        request = Volley.newRequestQueue(getApplicationContext());
        


        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Esconde Teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(FormCadastro.INPUT_METHOD_SERVICE);
                if(imm.isActive())
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

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



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        String url = ip + "/acao10/api/login/registro.php?id="+usuarioID+"&nome="+nome+"&email="+email+"&senha="+senha+"&nivel=usuario&url_imagem=imagens/sem_foto.jpg";
        url = url.replace(" ", "%20");

        //edit_nome.setText(url);

        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectReq);
    }


    private void IniciarComponentes(){
        edit_nome = findViewById(R.id.edit_nome_Cadastro);
        edit_email = findViewById(R.id.edit_email_Cadastro);
        edit_senha = findViewById(R.id.edit_senha_Cadastro);


        bt_cadastrar = findViewById(R.id.bt_cadastrar);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

        Toast.makeText(getApplicationContext(), "Erro ao cadastrar no servidor!!! --->" + volleyError.toString(), Toast.LENGTH_LONG).show();
        //edit_nome.setText(volleyError.toString());

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        usuarioAtual.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               TelaLogin();
            }
        });
    }

    private void TelaLogin() {
        Intent intent = new Intent(FormCadastro.this, FormLogin.class);
        startActivity(intent);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        Toast.makeText(getApplicationContext(), "Cadastro realizado com sucesso!!!", Toast.LENGTH_LONG).show();
        TelaLogin();


    }


}