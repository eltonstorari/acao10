package com.storaritech.acao10app.admin.ui.parceiro;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.adaptador.ParceirosAdapter;
import com.storaritech.acao10app.entidades.MySingleton;
import com.storaritech.acao10app.entidades.Parceiro;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ParceiroFragment extends Fragment {
    RecyclerView recyclerParceiros;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    ArrayList<Parceiro> listaParceiros;
    EditText editNome, editCNPJ, editWhatsapp, editInstagram, editContato, editSite;
    ImageView imgFoto;
    TextView txt_parceiroAdmin_propaganda,  txt_parceiroAdmin_propagandaAtivas, txt_parceiroAdmin_cadastro;

    public ParceiroFragment() {

}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_parceiro, container, false);

        listaParceiros = new ArrayList<>();
        recyclerParceiros = (RecyclerView) vista.findViewById(R.id.idRecyclerParceiros);
        recyclerParceiros.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerParceiros.setHasFixedSize(true);

        editCNPJ = vista.findViewById(R.id.edit_parceiroAdmin_cnpj);
        editNome = vista.findViewById(R.id.edit_parceiroAdmin_nome);
        editWhatsapp = vista.findViewById(R.id.edit_parceiroAdmin_whatsapp);
        editInstagram = vista.findViewById(R.id.edit_parceiroAdmin_instagram);
        editContato = vista.findViewById(R.id.edit_parceiroAdmin_contato);
        editSite = vista.findViewById(R.id.edit_parceiroAdmin_site);

        imgFoto = vista.findViewById(R.id.img_parceriosAdmin_Foto);

        txt_parceiroAdmin_propaganda = vista.findViewById(R.id.txt_parceiroAdmin_propaganda);
        txt_parceiroAdmin_propagandaAtivas = vista.findViewById(R.id.txt_parceiroAdmin_propagandaAtivas);
        txt_parceiroAdmin_cadastro = vista.findViewById(R.id.txt_parceiroAdmin_cadastro);

        carregarWEBServiceListaParceiros();


        return vista;
    }

    private void carregarWEBServiceListaParceiros() {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/parceiros/consultarLista.php";
        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Parceiro parceiro = null;
                JSONArray json = response.optJSONArray("parceiro");



                try {
                    for (int i = 0; i < json.length(); i++){
                        parceiro = new Parceiro();
                        JSONObject jsonObject = null;
                        jsonObject = json.getJSONObject(i);
                        parceiro.setCnpj(jsonObject.getString("cnpj"));
                        parceiro.setNome(jsonObject.getString("nome"));

                        //Cadastro String to Date
                        String cadastro = jsonObject.getString("cadastro");
                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(cadastro);
                        parceiro.setCadastro(date);

                        parceiro.setPropaganda(jsonObject.getString("propagandas"));
                        parceiro.setPropaganda_ativa(jsonObject.getString("propagandas_ativas"));
                        parceiro.setWhatsapp(jsonObject.getString("whatsapp"));
                        parceiro.setInstagram(jsonObject.getString("instagram"));
                        parceiro.setFacebook(jsonObject.getString("facebook"));
                        parceiro.setEmail(jsonObject.getString("email"));
                        parceiro.setContato(jsonObject.getString("contato"));
                        parceiro.setSite(jsonObject.getString("site"));
                        parceiro.setUrl_logo(jsonObject.getString("url_logo"));
                        listaParceiros.add(parceiro);


                    }

                    ParceirosAdapter adapter = new ParceirosAdapter(listaParceiros,  getContext());
                    recyclerParceiros.setAdapter(adapter);

                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("VolleyResponse", "ClientError: " + e.getMessage() );
                    Toast.makeText(getContext(), "Não foi possivel listar os Parceiros!!! --->" + response, Toast.LENGTH_LONG).show();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ClientError) {
                    Log.e("VolleyError", "ClientError: " + error.getMessage() + " - Status Code: " + error.networkResponse.statusCode);
                    // ... handle error
                }
                Toast.makeText(getContext(), "Erro na API --->" + error, Toast.LENGTH_LONG).show();

            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectReq);
    }
}