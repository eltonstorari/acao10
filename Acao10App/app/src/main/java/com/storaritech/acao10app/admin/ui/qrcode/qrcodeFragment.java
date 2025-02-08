package com.storaritech.acao10app.admin.ui.qrcode;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.adaptador.ClubesAdapter;
import com.storaritech.acao10app.adaptador.QRCodeAdapter;
import com.storaritech.acao10app.adaptador.UsuariosAdapter;
import com.storaritech.acao10app.entidades.Clube;
import com.storaritech.acao10app.entidades.MySingleton;
import com.storaritech.acao10app.entidades.QRCode;
import com.storaritech.acao10app.entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class qrcodeFragment extends Fragment {
    RecyclerView recyclerQRCdes;;
    ArrayList<QRCode> listaQRCodes;
    RequestQueue requestQRCode;
    JsonObjectRequest jsonObjectReqQRCode;


    Spinner spinner;
    RequestQueue requestClube;
    JsonObjectRequest jsonObjectReq;
    ArrayList<Clube> listaClubes;
    EditText editCodigo, editCaixa, editDescricao;
    ImageView imageViewQRCode;

    FloatingActionButton btnCadastrar, btnEditar, btnRemover;
    StringRequest stringRequest;


    private static final int COD_SELECIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int COD_PERMISSAO = 100;

    private static final String PASTA_PRINCIPAL = "minhasImagensApp/";
    private static final String PASTA_IMAGEM = "imagens";
    private static final String DIRETORIO_IMAGEM = PASTA_PRINCIPAL + PASTA_IMAGEM;

    private String path;
    File fileImagem;

    Bitmap bitmap;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_qrcode, container, false);

        // Referência ao Spinner
        spinner = vista.findViewById(R.id.spinner_qrcodeAdmin_clubes);
        requestClube = Volley.newRequestQueue(getContext());
        listaClubes=new ArrayList<>();

        listaQRCodes=new ArrayList<>();
        recyclerQRCdes = vista.findViewById(R.id.idRecyclerQRCode);
        recyclerQRCdes.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerQRCdes.setHasFixedSize(true);
        requestQRCode = Volley.newRequestQueue(getContext());


        // Recycler view lista de QRCode
        editCodigo = vista.findViewById(R.id.edit_qrcodeAdmin_codigo);
        editCaixa = vista.findViewById(R.id.edit_qrcodeAdmin_caixa);
        editDescricao = vista.findViewById(R.id.edit_qrcodeAdmin_descricao);
        imageViewQRCode = vista.findViewById(R.id.imageViewQRCode);

        btnCadastrar = vista.findViewById(R.id.btn_qrcodeAdmin_Cadastro);

        carregarWEBServiceListaClubes();
        carregarWEBServiceListaQRCode();
        solicitarPermissoesVersoesSuperiores();

        editCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                generateQRCode(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCadastrar.setOnClickListener(v -> {

            if (bitmap == null) {
                Toast.makeText(getContext(), "Nenhuma imagem foi gerada ou selecionada", Toast.LENGTH_SHORT).show();
                return;
            } else {

                String ip = getString(R.string.ip);
                String url = ip + "/acao10/api/qrcodes/registroimg.php";
                stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equalsIgnoreCase("registra")) {
                            editCodigo.setText("");
                            editCaixa.setText("");
                            editDescricao.setText("");

                            imageViewQRCode.setImageResource(R.drawable.sem_foto);
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

                        String codigo = editCodigo.getText().toString();
                        String clube = spinner.getSelectedItem().toString().substring(0,5);
                        String caixa = editCaixa.getText().toString();
                        String descricao = editDescricao.getText().toString();
                        String imagem = converterImgString(bitmap);


                        Map<String, String> parametros = new HashMap<>();
                        parametros.put("codigo", codigo);
                        parametros.put("clube", clube);
                        parametros.put("caixa", caixa);
                        parametros.put("descricao", descricao);
                        parametros.put("imagem", imagem);

                        return parametros;
                    }
                };
                //resquest.add(stringRequest);
                MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
            }
        });





        return vista;
    }

    private void carregarWEBServiceListaQRCode() {

        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/qrcodes/consultarLista.php";



        jsonObjectReqQRCode = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                QRCode qrCode = null;
                JSONArray json = response.optJSONArray("qrcode");
                //JSONObject jsonObject = null;

                try {
                    for(int i=0; i<json.length(); i++){
                        qrCode = new QRCode();
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);
                        qrCode.setCodigo(jsonObject.optString("codigo"));
                        qrCode.setClube(jsonObject.optString("clube"));
                        qrCode.setCaixa(jsonObject.optString("caixa"));
                        qrCode.setDescricao(jsonObject.optString("descricao"));
                        qrCode.setUrl_logo(jsonObject.optString("url_logo"));
                        listaQRCodes.add(qrCode);


                    }

                    QRCodeAdapter adapter = new QRCodeAdapter(listaQRCodes, getContext());
                    recyclerQRCdes.setAdapter(adapter);

                }catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Não foi possivel listar os QRCode!!! --->" + response, Toast.LENGTH_LONG).show();



                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                 Toast.makeText(getContext(), "Erro ao cadastrar no servidor!!! --->" + volleyError.toString(), Toast.LENGTH_LONG).show();

            }
        });
        requestQRCode.add(jsonObjectReqQRCode);

    }

    private void generateQRCode(String text) {
        if (!text.isEmpty()) {
            MultiFormatWriter writer = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
                BarcodeEncoder encoder = new BarcodeEncoder();
                bitmap = encoder.createBitmap(bitMatrix); // Atribuir ao bitmap global
                imageViewQRCode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }


    private void carregarWEBServiceListaClubes() {

        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/clubes/consultarLista.php";
        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Clube clube = null;
                JSONArray json = response.optJSONArray("clube");
                JSONObject jsonObject = null;

                // Criar uma lista de itens
                List<String> itemList = new ArrayList<>();

                try {
                    for (int i = 0; i < json.length(); i++) {
                        clube = new Clube();

                        jsonObject = json.getJSONObject(i);
                        clube.setId(jsonObject.optInt("id"));
                        clube.setNome(jsonObject.optString("nome"));
                        itemList.add(String.format("%05d", jsonObject.optInt("id")) + " - " + jsonObject.optString("nome"));
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
                    // Criar um ArrayAdapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, itemList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);

                    // Adicionar um listener
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedItem = parent.getItemAtPosition(position).toString();
                            //Toast.makeText(getContext(), "Selecionado: " + selectedItem, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Ação quando nada é selecionado
                        }
                    });
                    //ClubesAdapter adapter = new ClubesAdapter(listaClubes,  this, getContext());
                    //recyclerClubes.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Não foi possivel listar os clubes!!! --->" + response, Toast.LENGTH_LONG).show();


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        //request.add(jsonObjectReq);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectReq);
    }

    private String converterImgString(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(getContext(), "Nenhuma imagem disponível", Toast.LENGTH_SHORT).show();
            return "";
        }
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagemByte = array.toByteArray();
        return android.util.Base64.encodeToString(imagemByte, android.util.Base64.DEFAULT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(getContext(), "Ação cancelada ou falhou", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case COD_SELECIONA:
                Uri tabQRCode = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), tabQRCode);
                    imageViewQRCode.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case COD_FOTO:
                bitmap = BitmapFactory.decodeFile(path);
                if (bitmap == null) {
                    Toast.makeText(getContext(), "Erro ao carregar a imagem", Toast.LENGTH_SHORT).show();
                } else {
                    bitmap = redimensionarImagem(bitmap, 600, 600);
                    imageViewQRCode.setImageBitmap(bitmap);
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==COD_PERMISSAO){
            if(grantResults.length==2 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//REPRESENTA DUAS PERMISSOES
                Toast.makeText(getContext(),"Permissıes Aceitas",Toast.LENGTH_SHORT);
                imageViewQRCode.setEnabled(true);
            }
        }else{
            solicitarPermissoesManual();
        }
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
}