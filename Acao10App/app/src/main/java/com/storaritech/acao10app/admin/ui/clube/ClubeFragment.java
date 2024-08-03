package com.storaritech.acao10app.admin.ui.clube;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.storaritech.acao10app.Interface.RecyclerViwerInterface_Clubes;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.adaptador.ClubesAdapter;

import com.storaritech.acao10app.entidades.Clube;
import com.storaritech.acao10app.entidades.MySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClubeFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, RecyclerViwerInterface_Clubes {
    RecyclerView recyclerClubes;
    ArrayList<Clube> listaClubes;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    FloatingActionButton btnCadastrar, btnEditar, btnRemover;
    EditText edit_clubeAdmin_id, edit_clubeAdmin_nome, edit_clubeAdmin_descricao, edit_clubeAdmin_telefone, edit_clubeAdmin_whatsapp, edit_clubeAdmin_cep, edit_clubeAdmin_cidade, edit_clubeAdmin_bairro, edit_clubeAdmin_rua, edit_clubeAdmin_numero, edit_clubeAdmin_email;
    StringRequest stringRequest;
    ImageView imgFoto;


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
                    Toast.makeText(getContext(), "Erro ao Registrar", Toast.LENGTH_SHORT).show();
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

                    return parametros;
                }
            };
            //resquest.add(stringRequest);
            MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        });

        return vista;

    }

    private void carregarWEBServiceListaClubes() {

        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/clubes/consultarLista.php";
        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectReq);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(getContext(), "Erro ao cadastrar no servidor!!! --->" + volleyError.toString(), Toast.LENGTH_LONG).show();

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
            Toast.makeText(getContext(), "Não foi possivel listar os usuarios!!! --->" + response, Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void onitemClick(String IdPosition) {

    }
}
