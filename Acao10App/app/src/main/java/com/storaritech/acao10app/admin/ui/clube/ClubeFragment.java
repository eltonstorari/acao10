package com.storaritech.acao10app.admin.ui.clube;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.storaritech.acao10app.Interface.RecyclerViwerInterface_Clubes;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.adaptador.ClubesAdapter;

import com.storaritech.acao10app.entidades.Clube;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubeFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener, RecyclerViwerInterface_Clubes {
    RecyclerView recyclerClubes;
    ArrayList<Clube> listaClubes;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;


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
        carregarWEBServiceListaClubes();

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
