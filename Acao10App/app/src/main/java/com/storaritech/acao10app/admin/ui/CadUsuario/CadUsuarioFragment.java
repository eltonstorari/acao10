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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
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
import android.provider.Settings;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



public class CadUsuarioFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, RecyclerViwerInterface {
    RecyclerView recyclerUsuarios;
    ArrayList<Usuario> listaUsuarios;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;


    EditText editId, editNome, editEmail, editSenha;
    RadioGroup radioGroupNivel;
    RadioButton radioUsuario, radioAdmin;
    ImageView imgFoto;
    FloatingActionButton btnCadastrar, btnEditar, btnRemover;
    StringRequest stringRequest;
    Bitmap bitmap;
    private String usuarioID;
    Button btnFoto;


    private static final int COD_SELECIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int COD_PERMISSAO = 100;

    private static final String PASTA_PRINCIPAL = "minhasImagensApp/";
    private static final String PASTA_IMAGEM = "imagens";
    private static final String DIRETORIO_IMAGEM = PASTA_PRINCIPAL + PASTA_IMAGEM;

    private String path;
    File fileImagem;



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
        editId = vista.findViewById(R.id.edit_CadUsu_id);
        editNome = vista.findViewById(R.id.edit_CadUsu_nome);
        editEmail = vista.findViewById(R.id.edit_CadUsu_email);
        editSenha = vista.findViewById(R.id.edit_CadUsu_Senha);
        radioGroupNivel = vista.findViewById(R.id.group_CadUsu_Usuario);
        imgFoto = vista.findViewById(R.id.img_CadUsu_Foto);
        btnCadastrar = vista.findViewById(R.id.btn_CadUsu_Cadastro);
        btnEditar = vista.findViewById(R.id.btn_CadUsu_Editar);
        btnRemover = vista.findViewById(R.id.btn_CadUsu_Deletar);
        btnFoto = vista.findViewById(R.id.btn_CadUsu_Img);
        radioUsuario = vista.findViewById(R.id.radio_CadUsu_Usuario);
        radioAdmin = vista.findViewById(R.id.radio_CadUsu_Admin);

        carregarWEBServiceListaUsuarios();

        btnFoto.setOnClickListener(v -> {

            carregarDialog();

        });

        btnCadastrar.setOnClickListener(v -> {

            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if(nome.isEmpty() || email.isEmpty() || senha.isEmpty()){
                Snackbar snackbar = Snackbar.make(v, "Preencha todos os campos", Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }else{

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            String ip = getString(R.string.ip);
                            String url = ip + "/acao10/api/usuarios/registroimg.php";

                            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {


                                    if (response.trim().equalsIgnoreCase("registra")) {
                                        editId.setText("");
                                        editNome.setText("");
                                        editEmail.setText("");
                                        editSenha.setText("");
                                        radioUsuario.setChecked(true);
                                        imgFoto.setImageResource(R.drawable.sem_foto);


                                        Toast.makeText(getContext(), "Registrado com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Registro não inserido", Toast.LENGTH_SHORT).show();
                                        Log.i("RESPOSTA: ", "" + response);

                                    }

                                }

                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getContext(), "Erro ao Registrar", Toast.LENGTH_SHORT).show();

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    int selectedId = radioGroupNivel.getCheckedRadioButtonId();
                                    RadioButton radioButton = radioGroupNivel.findViewById(selectedId);
                                    String nivel =  radioButton.getText().toString().toLowerCase();

                                    if (nivel.equals("usuário")){
                                        nivel = "usuario";
                                    }
                                    else if(nivel.equals("administrador")){
                                        nivel = "admin";
                                    }

                                    String id = usuarioID;
                                    String nome = editNome.getText().toString();
                                    String email = editEmail.getText().toString();
                                    String senha = editSenha.getText().toString();

                                    String imagem = converterImgString(bitmap);
                                    //String urlImagem = "/imagens/" + nome + "jpg";

                                    Map<String, String> parametros = new HashMap<>();
                                    parametros.put("id", id);
                                    parametros.put("nome", nome);
                                    parametros.put("email", email);
                                    parametros.put("senha", senha);
                                    parametros.put("nivel", nivel);
                                    parametros.put("imagem", imagem);
                                    //parametros.put("url_imagem", urlImagem);



                                    return parametros;
                                }

                            };

                            //resquest.add(stringRequest);

                            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);

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

        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = editId.getText().toString();

                if(id.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, "Selecione primeiramente o usuário!", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{


                    carregarWEBServiceAtualizar();


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

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carregarDialog();
            }
        });

        if(solicitarPermissoesVersoesSuperiores()){
            btnFoto.setEnabled(true);

        }else{
            btnFoto.setEnabled(false);
        }
        return vista;
    }


    //Solicitações de permissões
    //ver se as permissıes foram aceitas
    private boolean solicitarPermissoesVersoesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validar se estamos em vers„o de android menor que 6 para solicitar permissoes
            return true;
        }

        //ver se as permissıes foram aceitas
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&getContext().checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }


        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            carregarDialogoRecomendacao();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, COD_PERMISSAO);
        }

        return false;//processa o evento dependendo do que se defina aqui
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==COD_PERMISSAO){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//REPRESENTA DUAS PERMISSOES
                Toast.makeText(getContext(),"Permissıes Aceitas",Toast.LENGTH_SHORT);
                btnFoto.setEnabled(true);
            }
        }else{
            solicitarPermissoesManual();
        }
    }

    private void solicitarPermissoesManual() {
        final CharSequence[] opciones={"sim","não"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(getContext());
        alertOpciones.setTitle("Deseja configurar as permissões manualmente?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("sim")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getContext(),"Permissões Aceitas",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void carregarDialogoRecomendacao() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permissões Desativadas");
        dialogo.setMessage("Deve aceitar as permissıes para funcionamento completo do App");

        dialogo.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    private void carregarDialog() {

        final CharSequence[] opcoes = {"Tirar Foto", "Selecionar da Galeria", "Cancelar"};
        final AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setTitle("Escolha uma Opção");
        builder.setItems(opcoes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(opcoes[i].equals("Tirar Foto")){
                    abrirCamera();

                }else{
                    if (opcoes[i].equals("Selecionar da Galeria")){
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Selecione"),COD_SELECIONA);

                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        builder.show();




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode, data);

        switch (requestCode){
            case COD_SELECIONA:
                Uri tabUsuario=data.getData();
                imgFoto.setImageURI(tabUsuario);

                try {
                    bitmap=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),tabUsuario);
                    imgFoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                break;


            case COD_FOTO:

                Toast.makeText(getContext(), "Abriu a Camera", Toast.LENGTH_SHORT).show();


                MediaScannerConnection.scanFile(getContext(), new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("Path", "" + path);
                    }
                });
                bitmap = BitmapFactory.decodeFile(path);
                imgFoto.setImageBitmap(bitmap);
                break;
        }

        bitmap = redimensionarImagem(bitmap, 600, 600);
    }

    private Bitmap redimensionarImagem(Bitmap bitmap, float larguraNova, float alturaNova) {

        int largura=bitmap.getWidth();
        int altura=bitmap.getHeight();

        if(largura>larguraNova || altura>alturaNova){
            float escalaLargura=larguraNova/largura;
            float escalaAltura= alturaNova/altura;

            Matrix matrix=new Matrix();
            matrix.postScale(escalaLargura,escalaAltura);

            return Bitmap.createBitmap(bitmap,0,0,largura,altura,matrix,false);

        }else{
            return bitmap;
        }


    }

    private void abrirCamera() {
        File meuFile = new File(Environment.getExternalStorageDirectory(), DIRETORIO_IMAGEM);
        boolean estaCriada = meuFile.exists();

        if(estaCriada == false){
            estaCriada = meuFile.mkdirs();
        }

        if (estaCriada == true){
            Long consecultivo = System.currentTimeMillis()/1000;
            String nome =  consecultivo.toString() + ".jpg";

            path = Environment.getExternalStorageDirectory() + File.separator + DIRETORIO_IMAGEM + File.separator + nome;
            fileImagem = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagem));


            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                String authorities=getContext().getPackageName()+".provider";
                Uri imageUri= FileProvider.getUriForFile(getContext(),authorities,fileImagem);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagem));
            }

            startActivityForResult(intent, COD_FOTO);
        }

    }

    private String converterImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagemByte=array.toByteArray();
        String imagemString = android.util.Base64.encodeToString(imagemByte, android.util.Base64.DEFAULT);

        return imagemString;
    }

    private void carregarWEBServiceListaUsuarios() {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/usuarios/consultarLista.php";

        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectReq);

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

                if (tabUsuario.getNivel().equals("usuario")){
                    radioUsuario.setChecked(true);
                } else if (tabUsuario.getNivel().equals("admin")){
                    radioAdmin.setChecked(true);
                }



                String ip = getString(R.string.ip);


                String urlImagem = ip+"/acao10/api/usuarios/" + tabUsuario.getUrl_imagem();
                Toast.makeText(getContext(), "Url " + urlImagem, Toast.LENGTH_LONG).show();


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
                    //usuario.setSenha(jsonObject.optString("senha"));
                    usuario.setNivel(jsonObject.optString("nivel"));
                    usuario.setUrl_imagem(jsonObject.optString("url_imagem"));
                    listaUsuarios.add(usuario);


                }

                UsuariosAdapter adapter = new UsuariosAdapter(listaUsuarios, this, getContext());
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

    private void carregarWEBServiceAtualizar() {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/usuarios/update.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                if (response.trim().equalsIgnoreCase("registra")) {

                    Toast.makeText(getContext(), "Atualizando com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Registro não Atualizado", Toast.LENGTH_SHORT).show();
                    Log.i("RESPOSTA: ", "" + response);
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Erro ao Atualizar", Toast.LENGTH_SHORT).show();

            }
        }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                int selectedId = radioGroupNivel.getCheckedRadioButtonId();
                RadioButton radioButton = radioGroupNivel.findViewById(selectedId);
                String nivel =  radioButton.getText().toString().toLowerCase();

                if (nivel.equalsIgnoreCase("usuário")){
                    nivel = "usuario";
                }
                else if(nivel.equalsIgnoreCase("administrador")){
                    nivel = "admin";
                }

                String id = usuarioID;
                String nome = editNome.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();

                String imagem = converterImgString(bitmap);
                //String urlImagem = "/imagens/" + nome + "jpg";

                Map<String, String> parametros = new HashMap<>();
                parametros.put("id", id);
                parametros.put("nome", nome);
                parametros.put("email", email);
                parametros.put("senha", senha);
                parametros.put("nivel", nivel);
                parametros.put("imagem", imagem);
                //parametros.put("url_imagem", urlImagem);


                return parametros;
            }

        };

        //resquest.add(stringRequest);
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
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
