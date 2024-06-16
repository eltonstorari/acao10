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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.storaritech.acao10app.FormCadastro;
import com.storaritech.acao10app.RecyclerViwerInterface;
import com.storaritech.acao10app.adaptador.UsuariosAdapter;
import com.storaritech.acao10app.admin.FormMenu_Admin;
import com.storaritech.acao10app.entidades.MySingleton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CadUsuarioFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, RecyclerViwerInterface {
    RecyclerView recyclerUsuarios;
    ArrayList<Usuario> listaUsuarios;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;


    EditText editId, editNome, editEmail, editSenha;
    RadioGroup radioGroupNivel;
    ImageView imgFoto;
    FloatingActionButton btnCadastrar, btnEditar, btnRemover;
    StringRequest stringRequest;
    Bitmap bitmap;
    private String usuarioID;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_cad_usuario, container, false);

        listaUsuarios=new ArrayList<>();
        recyclerUsuarios = (RecyclerView)  vista.findViewById(R.id.idRecyclerUsuarios);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerUsuarios.setHasFixedSize(true);
        request = Volley.newRequestQueue(getContext());

        carregarWEBServiceListaUsuarios();

        editId = vista.findViewById(R.id.edit_CadUsu_id);
        editNome = vista.findViewById(R.id.edit_CadUsu_nome);
        editEmail = vista.findViewById(R.id.edit_CadUsu_email);
        editSenha = vista.findViewById(R.id.edit_CadUsu_Senha);
        radioGroupNivel = vista.findViewById(R.id.group_CadUsu_Usuario);
        imgFoto = vista.findViewById(R.id.img_CadUsu_Foto);
        btnCadastrar = vista.findViewById(R.id.btn_CadUsu_Cadastro);
        btnEditar = vista.findViewById(R.id.btn_CadUsu_Editar);
        btnRemover = vista.findViewById(R.id.btn_CadUsu_Deletar);



        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Esconde Teclado
                //InputMethodManager imm = (InputMethodManager) getSystemService(FormMenu_Admin.INPUT_METHOD_SERVICE);
                //if(imm.isActive())
                   // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                String nome = editNome.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

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

        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = editId.getText().toString();

                if(id.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, "Selecione primeiramente o usuário!", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{


                    carregarWEBServiceRemover(id);


                }

            }
        });

        return vista;
    }





    private void carregarWEBServiceListaUsuarios() {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/usuarios/consultarLista.php";

        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectReq);

    }


    private void CadastrarUsuario(View v){
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

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
        String nome = editNome.getText().toString();
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        int selectedId = radioGroupNivel.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroupNivel.findViewById(selectedId);
        String nivel =  radioButton.getText().toString().toLowerCase();

        if (nivel.equals("usuário")){
            nivel = "usuario";
        }
        else if(nivel.equals("administrador")){
                nivel = "admin";
            }


        //carregarWebService
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/login/registro.php?id="+usuarioID+"&nome="+nome+"&email="+email+"&senha="+senha+"&nivel="+nivel+"&url_imagem=imagens/sem_foto.jpg";
        url = url.replace(" ", "%20");

        //edit_nome.setText(url);

        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                editId.setText("");
                editNome.setText("");
                editEmail.setText("");
                editSenha.setText("");
                imgFoto.setImageResource(R.drawable.sem_foto);
                carregarWEBServiceListaUsuarios();
                Toast.makeText(getContext(), "Cadastro realizado com sucesso!!!", Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Não foi possivel efetuar a consulta " + error.toString(), Toast.LENGTH_LONG).show();
                Log.i("ERRO", error.toString());


                FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
                usuarioAtual.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Foi deletado no firebase", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });

        //resquest.add(jsonObjectReq);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectReq);


    }

    private void carregarWEBServiceConsultaUsuario(String IdClick) {

        String ip = getString(R.string.ip);

        String url = ip + "/acao10/api/usuarios/consultarUsuarioUrl.php?id=" + IdClick;
        url = url.replace(" ", "%20");


        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                Usuario tabUsuario = new Usuario();

                JSONArray json = response.optJSONArray("usuario");
                JSONObject jsonObject  = null;

                try {
                    jsonObject = json.getJSONObject(0);
                    tabUsuario.setId(jsonObject.optString("id"));
                    tabUsuario.setNome(jsonObject.optString("nome"));
                    tabUsuario.setEmail(jsonObject.optString("email"));
                    tabUsuario.setSenha(jsonObject.optString("senha"));
                    tabUsuario.setNivel(jsonObject.optString("nivel"));
                    tabUsuario.setUrl_imagem(jsonObject.optString("url_imagem"));
                }catch (JSONException e){
                    e.printStackTrace();
                }

                editId.setText(tabUsuario.getId());
                editNome.setText(tabUsuario.getNome());
                editEmail.setText(tabUsuario.getEmail());
                editSenha.setText(tabUsuario.getSenha());

                /**
                 *  RadioGroup aqui!!!!
                 */


                String ip = getString(R.string.ip);


                String urlImagem = ip+"/acao10/api/usuarios/" + tabUsuario.getUrl_imagem();
                //Toast.makeText(getContext(), "Url " + urlImagem, Toast.LENGTH_LONG).show();


                carregarWEBServiceImg(urlImagem);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Não foi possivel efetuar a consulta " + error.toString(), Toast.LENGTH_LONG).show();
                Log.i("ERRO", error.toString());

            }
        });

        //resquest.add(jsonObjectReq);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectReq);


    }

    private void carregarWEBServiceImg(String urlImagem) {
        urlImagem = urlImagem.replace(" ", "%20");
        ImageRequest imageReq  = new ImageRequest(urlImagem, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                bitmap = response;
                imgFoto.setImageBitmap(response);

            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(),"Erro ao carregar imagem...", Toast.LENGTH_LONG).show();
                Log.i("ERRO IMAGE", "Response -> "+ error);

            }
        });

        //resquest.add(imageReq);
        MySingleton.getInstance(getContext()).addToRequestQueue(imageReq);

    }


    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getContext(), "Erro ao cadastrar no servidor!!! --->" + volleyError.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        {

            Usuario usuario = null;
            JSONArray json = response.optJSONArray("usuario");
            //JSONObject jsonObject = null;

            try {
                for(int i=0; i<json.length(); i++){
                    usuario = new Usuario();
                    JSONObject jsonObject = null;
                    jsonObject = json.getJSONObject(i);
                    usuario.setId(jsonObject.optString("id"));
                    usuario.setNome(jsonObject.optString("nome"));
                    usuario.setEmail(jsonObject.optString("email"));
                    usuario.setSenha(jsonObject.optString("senha"));
                    usuario.setNivel(jsonObject.optString("nivel"));
                    listaUsuarios.add(usuario);


                }

                UsuariosAdapter adapter = new UsuariosAdapter(listaUsuarios, this);
                recyclerUsuarios.setAdapter(adapter);

            }catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Não foi possivel listar os usuarios!!! --->" + response, Toast.LENGTH_LONG).show();



            }

        }
    }




    @Override
    public void onitemClick(String IdPosition) {
        carregarWEBServiceConsultaUsuario(IdPosition);

    }

    @Override
    public void onClick(String CRUD, String id) {
        if (CRUD == "editar"){
            // Acao de editar cadastro
        }else
        if (CRUD == "remover"){
            // Acao de remover cadastro

        }


        Toast.makeText(getContext(), CRUD, Toast.LENGTH_LONG).show();

    }

    private void carregarWEBServiceRemover(String id) {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/usuarios/remover.php?id=" + id;

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.trim().equalsIgnoreCase("removido")) {
                    editId.setText("");
                    editNome.setText("");
                    editEmail.setText("");
                    editSenha.setText("");
                    imgFoto.setImageResource(R.drawable.sem_foto);
                    carregarWEBServiceListaUsuarios();
                    Toast.makeText(getContext(), "Excluído com sucesso", Toast.LENGTH_SHORT).show();
                } else {
                    if (response.trim().equalsIgnoreCase("naoExiste")) {
                        Toast.makeText(getContext(), "Usuário não Encontrado ->  " + response, Toast.LENGTH_SHORT).show();
                        Log.i("RESPOSTA: ", "" + response);

                    }else{

                        Toast.makeText(getContext(), "Erro na Deleção ->  " + response, Toast.LENGTH_SHORT).show();
                        Log.i("RESPOSTA: ", "" + response);

                    }
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Erro ao Excluir", Toast.LENGTH_SHORT).show();

            }
        });


        //Toast.makeText(getContext(), "Deletada com sucesso!", Toast.LENGTH_SHORT).show();


        //resquest.add(stringRequest);
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

    }



}
