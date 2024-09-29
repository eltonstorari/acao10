package com.storaritech.acao10app.admin.ui.qrcode;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.adaptador.ClubesAdapter;
import com.storaritech.acao10app.entidades.Clube;
import com.storaritech.acao10app.entidades.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class qrcodeFragment extends Fragment {

    Spinner spinner;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    ArrayList<Clube> listaClubes;
    EditText editAutenticacao;
    ImageView imageViewQRCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_qrcode, container, false);

        // Referência ao Spinner
        spinner = vista.findViewById(R.id.spinner_qrcodeAdmin_clubes);
        request = Volley.newRequestQueue(getContext());
        listaClubes=new ArrayList<>();
        editAutenticacao = vista.findViewById(R.id.edit_qrcodeAdmin_codigo);
        imageViewQRCode = vista.findViewById(R.id.imageViewQRCode);

        carregarWEBServiceListaClubes();

        editAutenticacao.addTextChangedListener(new TextWatcher() {
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



        return vista;
    }

    private void generateQRCode(String text) {
            if (!text.isEmpty()) {
                MultiFormatWriter writer = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
                    BarcodeEncoder encoder = new BarcodeEncoder();
                    Bitmap bitmap = encoder.createBitmap(bitMatrix);
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


}