package com.storaritech.acao10app.admin.ui.clube;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.storaritech.acao10app.Interface.RecyclerViwerInterface_Clubes;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.adaptador.ClubesAdapter;

import com.storaritech.acao10app.entidades.Clube;
import com.storaritech.acao10app.entidades.MySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class clubeFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, RecyclerViwerInterface_Clubes {
    RecyclerView recyclerClubes;
    ArrayList<Clube> listaClubes;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    FloatingActionButton btnCadastrar, btnEditar, btnRemover;
    EditText edit_clubeAdmin_id, edit_clubeAdmin_nome, edit_clubeAdmin_descricao, edit_clubeAdmin_telefone, edit_clubeAdmin_whatsapp, edit_clubeAdmin_cep, edit_clubeAdmin_cidade, edit_clubeAdmin_bairro, edit_clubeAdmin_rua, edit_clubeAdmin_numero, edit_clubeAdmin_email;
    StringRequest stringRequest;
    ImageView imgFoto;
    Button btnFoto;

    private static final int COD_SELECIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int COD_PERMISSAO = 100;


    private static final String PASTA_PRINCIPAL = "minhasImagensApp/";
    private static final String PASTA_IMAGEM = "imagens";
    private static final String DIRETORIO_IMAGEM = PASTA_PRINCIPAL + PASTA_IMAGEM;

    Bitmap bitmap;

    private String path;
    File fileImagem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_clube, container, false);

        listaClubes=new ArrayList<>();
        recyclerClubes = (RecyclerView)  vista.findViewById(R.id.idRecyclerClube);
        recyclerClubes.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerClubes.setHasFixedSize(true);
        request = Volley.newRequestQueue(getContext());
        btnCadastrar = vista.findViewById(R.id.btn_clubeAdmin_Cadastro);
        btnEditar = vista.findViewById(R.id.btn_clubeAdmin_Editar);
        btnRemover = vista.findViewById(R.id.btn_clubeAdmin_Deletar);

        edit_clubeAdmin_id = vista.findViewById(R.id.edit_clubeAdmin_id);
        edit_clubeAdmin_nome = vista.findViewById(R.id.edit_clubeAdmin_nome);
        edit_clubeAdmin_descricao = vista.findViewById(R.id.edit_clubeAdmin_descricao);
        edit_clubeAdmin_telefone = vista.findViewById(R.id.edit_clubeAdmin_telefone);
        edit_clubeAdmin_whatsapp = vista.findViewById(R.id.edit_clubeAdmin_whatsapp);
        edit_clubeAdmin_cep = vista.findViewById(R.id.edit_clubeAdmin_cep);
        edit_clubeAdmin_cidade = vista.findViewById(R.id.edit_clubeAdmin_cidade);
        edit_clubeAdmin_bairro = vista.findViewById(R.id.edit_clubeAdmin_bairro);
        edit_clubeAdmin_rua = vista.findViewById(R.id.edit_clubeAdmin_rua);
        edit_clubeAdmin_numero = vista.findViewById(R.id.edit_clubeAdmin_numero);
        edit_clubeAdmin_email = vista.findViewById(R.id.edit_clubeAdmin_email);
        imgFoto = vista.findViewById(R.id.img_clubeAdmin_Foto);

        btnFoto = vista.findViewById(R.id.btn_clubeAdmin_Img);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = edit_clubeAdmin_id.getText().toString();

                if(id.isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, "Selecione primeiramente o clube!", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{


                    String ip = getString(R.string.ip);
                    String url = ip + "/acao10/api/clubes/update.php?";
                    stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().equalsIgnoreCase("registra")) {
                                edit_clubeAdmin_id.setText("");
                                edit_clubeAdmin_nome.setText("");
                                edit_clubeAdmin_descricao.setText("");
                                edit_clubeAdmin_telefone.setText("");
                                edit_clubeAdmin_whatsapp.setText("");
                                edit_clubeAdmin_cep.setText("");
                                edit_clubeAdmin_cidade.setText("");
                                edit_clubeAdmin_bairro.setText("");
                                edit_clubeAdmin_rua.setText("");
                                edit_clubeAdmin_numero.setText("");
                                edit_clubeAdmin_email.setText("");
                                imgFoto.setImageResource(R.drawable.sem_foto);
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
                            String id = edit_clubeAdmin_id.getText().toString();
                            String nome = edit_clubeAdmin_nome.getText().toString();
                            String descricao = edit_clubeAdmin_descricao.getText().toString();
                            String telefone = edit_clubeAdmin_telefone.getText().toString();
                            String whatsapp = edit_clubeAdmin_whatsapp.getText().toString();
                            String cep = edit_clubeAdmin_cep.getText().toString();
                            String cidade = edit_clubeAdmin_cidade.getText().toString();
                            String bairro = edit_clubeAdmin_bairro.getText().toString();
                            String rua = edit_clubeAdmin_rua.getText().toString();
                            String numero = edit_clubeAdmin_numero.getText().toString();
                            String email = edit_clubeAdmin_email.getText().toString();
                            String imagem = converterImgString(bitmap);


                            Map<String, String> parametros = new HashMap<>();
                            parametros.put("id", id);
                            parametros.put("nome", nome);
                            parametros.put("descricao", descricao);
                            parametros.put("telefone", telefone);
                            parametros.put("whatsapp", whatsapp);
                            parametros.put("cep", cep);
                            parametros.put("cidade", cidade);
                            parametros.put("bairro", bairro);
                            parametros.put("rua", rua);
                            parametros.put("numero", numero);
                            parametros.put("email", email);
                            parametros.put("imagem", imagem);

                            return parametros;
                        }

                    };

                    MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);


                }

            }
        });


        carregarWEBServiceListaClubes();

        btnCadastrar.setOnClickListener(v -> {

            String ip = getString(R.string.ip);
            String url = ip + "/acao10/api/clubes/registroimg.php";
            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.trim().equalsIgnoreCase("registra")) {
                        edit_clubeAdmin_id.setText("");
                        edit_clubeAdmin_nome.setText("");
                        edit_clubeAdmin_descricao.setText("");
                        edit_clubeAdmin_telefone.setText("");
                        edit_clubeAdmin_whatsapp.setText("");
                        edit_clubeAdmin_cep.setText("");
                        edit_clubeAdmin_cidade.setText("");
                        edit_clubeAdmin_bairro.setText("");
                        edit_clubeAdmin_rua.setText("");
                        edit_clubeAdmin_numero.setText("");
                        edit_clubeAdmin_email.setText("");


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
                    Toast.makeText(getContext(), "Erro ao Registrar-> " + error, Toast.LENGTH_SHORT).show();
                    Log.i("RESPOSTA: ", "" + error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    String nome = edit_clubeAdmin_nome.getText().toString();
                    String descricao = edit_clubeAdmin_descricao.getText().toString();
                    String telefone = edit_clubeAdmin_telefone.getText().toString();
                    String whatsapp = edit_clubeAdmin_whatsapp.getText().toString();
                    String cep = edit_clubeAdmin_cep.getText().toString();
                    String cidade = edit_clubeAdmin_cidade.getText().toString();
                    String bairro = edit_clubeAdmin_bairro.getText().toString();
                    String rua = edit_clubeAdmin_rua.getText().toString();
                    String numero = edit_clubeAdmin_numero.getText().toString();
                    String email = edit_clubeAdmin_email.getText().toString();
                    String imagem = converterImgString(bitmap);


                    Map<String, String> parametros = new HashMap<>();
                    parametros.put("nome", nome);
                    parametros.put("descricao", descricao);
                    parametros.put("telefone", telefone);
                    parametros.put("whatsapp", whatsapp);
                    parametros.put("cep", cep);
                    parametros.put("cidade", cidade);
                    parametros.put("bairro", bairro);
                    parametros.put("rua", rua);
                    parametros.put("numero", numero);
                    parametros.put("email", email);
                    parametros.put("imagem", imagem);

                    return parametros;
                }
            };
            //resquest.add(stringRequest);
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        });

        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = edit_clubeAdmin_id.getText().toString();

                if(id.isEmpty()){
                    Toast.makeText(getContext(), "Selecione primeiramente o usuário!", Toast.LENGTH_SHORT).show();

                }else{


                    String ip = getString(R.string.ip);
                    String url = ip + "/acao10/api/clubes/remover.php?id=" + id;

                    stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            if (response.trim().equalsIgnoreCase("removido")) {
                                edit_clubeAdmin_id.setText("");
                                edit_clubeAdmin_nome.setText("");
                                edit_clubeAdmin_descricao.setText("");
                                edit_clubeAdmin_telefone.setText("");
                                edit_clubeAdmin_whatsapp.setText("");
                                edit_clubeAdmin_cep.setText("");
                                edit_clubeAdmin_cidade.setText("");
                                edit_clubeAdmin_bairro.setText("");
                                edit_clubeAdmin_rua.setText("");
                                edit_clubeAdmin_numero.setText("");
                                edit_clubeAdmin_email.setText("");
                                imgFoto.setImageResource(R.drawable.sem_foto);


                                Toast.makeText(getContext(), "Excluído com sucesso", Toast.LENGTH_SHORT).show();
                            } else {
                                if (response.trim().equalsIgnoreCase("naoExiste")) {
                                    Toast.makeText(getContext(), "Clube não Encontrado ->  " + response, Toast.LENGTH_SHORT).show();
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
        });

        btnFoto.setOnClickListener(v -> {

            carregarDialog();

        });

        if(solicitarPermissoesVersoesSuperiores()){
            btnFoto.setEnabled(true);

        }else{
            btnFoto.setEnabled(false);
        }

        return vista;

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

    private void carregarWEBServiceListaClubes() {

        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/clubes/consultarLista.php";
        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectReq);
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

    @Override
    public void onResponse(JSONObject response) {

        Clube clube = null;
        JSONArray json = response.optJSONArray("clube");
        //JSONObject jsonObject = null;

        try {
            for (int i = 0; i < json.length(); i++) {
                clube = new Clube();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);
                clube.setId(jsonObject.optInt("id"));
                clube.setNome(jsonObject.optString("nome"));
                clube.setDescricao(jsonObject.optString("descricao"));
                clube.setTelefone(jsonObject.optString("telefone"));
                clube.setWhatsapp(jsonObject.optString("whatsapp"));
                clube.setCep(jsonObject.optString("cep"));
                clube.setCidade(jsonObject.optString("cidade"));
                clube.setBairro(jsonObject.optString("bairro"));
                clube.setRua(jsonObject.optString("rua"));
                clube.setNumero(jsonObject.optString("numero"));
                clube.setEmail(jsonObject.optString("email"));
                clube.setUrl_logo(jsonObject.optString("url_logo"));
                listaClubes.add(clube);
            }

            ClubesAdapter adapter = new ClubesAdapter(listaClubes,  this, getContext());
            recyclerClubes.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Não foi possivel listar os clubes!!! --->" + response, Toast.LENGTH_LONG).show();


        }
    }
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getContext(), "Erro ao cadastrar no servidor!!! --->" + volleyError.toString(), Toast.LENGTH_LONG).show();

    }



    @Override
    public void onitemClick(String IdPosition) {

        String ip = getString(R.string.ip);

        String url = ip + "/acao10/api/clubes/consultarClubeUrl.php?id=" + IdPosition;
        url = url.replace(" ", "%20");
        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Clube tabClube = new Clube();
                JSONArray json = response.optJSONArray("clube");
                JSONObject jsonObject  = null;
                try {
                    jsonObject = json.getJSONObject(0);
                    tabClube.setId(jsonObject.optInt("id"));
                    tabClube.setNome(jsonObject.optString("nome"));
                    tabClube.setDescricao(jsonObject.optString("descricao"));
                    tabClube.setTelefone(jsonObject.optString("telefone"));
                    tabClube.setWhatsapp(jsonObject.optString("whatsapp"));
                    Log.d("RESPOSTA", tabClube.getWhatsapp());
                    tabClube.setCep(jsonObject.optString("cep"));
                    tabClube.setCidade(jsonObject.optString("cidade"));
                    Log.d("RESPOSTA", tabClube.getWhatsapp());
                    tabClube.setBairro(jsonObject.optString("bairro"));
                    tabClube.setRua(jsonObject.optString("rua"));
                    tabClube.setNumero(jsonObject.optString("numero"));
                    tabClube.setEmail(jsonObject.optString("email"));
                    tabClube.setUrl_logo(jsonObject.optString("url_logo"));
                }catch (JSONException e){
                    e.printStackTrace();
                }

                edit_clubeAdmin_id.setText(Integer.toString(tabClube.getId()));
                edit_clubeAdmin_nome.setText(tabClube.getNome());
                edit_clubeAdmin_descricao.setText(tabClube.getDescricao());
                edit_clubeAdmin_telefone.setText(tabClube.getTelefone());
                edit_clubeAdmin_whatsapp.setText(tabClube.getWhatsapp());
                Log.d("RESPOSTA", tabClube.getWhatsapp());
                edit_clubeAdmin_cep.setText(tabClube.getCep());
                edit_clubeAdmin_cidade.setText(tabClube.getCidade());
                edit_clubeAdmin_bairro.setText(tabClube.getBairro());
                edit_clubeAdmin_rua.setText(tabClube.getRua());
                edit_clubeAdmin_numero.setText(tabClube.getNumero() );
                edit_clubeAdmin_email.setText(tabClube.getEmail());


                String ip = getString(R.string.ip);
                String urlImagem = ip+"/acao10/api/clubes/" + tabClube.getUrl_logo();
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
}
