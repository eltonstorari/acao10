package com.storaritech.acao10app.adaptador;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.storaritech.acao10app.Interface.RecyclerViwerInterface_Clubes;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.entidades.Clube;
import com.storaritech.acao10app.entidades.MySingleton;

import java.util.List;

public class ClubesAdapter extends RecyclerView.Adapter<ClubesAdapter.ClubesHolder> {
    private final RecyclerViwerInterface_Clubes recyclerViwerInterfaceClubes;
    List<Clube> listaClubes;
    RequestQueue request;
    Context context;

    public ClubesAdapter(List<Clube> listaClubes, RecyclerViwerInterface_Clubes recyclerViwerInterfaceClubes, Context context){
        this.listaClubes = listaClubes;
        this.recyclerViwerInterfaceClubes = recyclerViwerInterfaceClubes;
        this.context = context;
        request = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public ClubesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_clubes, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        vista.setLayoutParams(layoutParams);



        return new ClubesHolder(vista, recyclerViwerInterfaceClubes);

    }

    @Override
    public void onBindViewHolder(@NonNull ClubesHolder holder, int position) {
        holder.txt_ListaClube_id.setText(listaClubes.get(position).getId().toString());
        holder.txt_ListaClube_nome.setText(listaClubes.get(position).getNome().toString());
        holder.txt_ListaClube_descricao.setText(listaClubes.get(position).getDescricao().toString());
        holder.txt_ListaClube_whatsapp.setText(listaClubes.get(position).getWhatsapp().toString());


        if (listaClubes.get(position).getUrl_logo()!=null){
            carregarImagemWebService(listaClubes.get(position).getUrl_logo(),holder);
        } else {
            holder.img_ListaClube_logo.setImageResource(R.drawable.sem_foto);
        }
    }

    private void carregarImagemWebService(String urlImagem, ClubesHolder holder) {


        String ip = context.getString(R.string.ip);

        String caminhoImagem = ip + "/acao10/api/clubes/" + urlImagem;
        caminhoImagem = caminhoImagem.replace(" ", "%20");

        ImageRequest imageReq = new ImageRequest(caminhoImagem, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                holder.img_ListaClube_logo.setImageBitmap(response);

            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Erro ao carregar Imagem", Toast.LENGTH_SHORT).show();

            }
        });

        //request.add(imageReq);
        MySingleton.getInstance(context).addToRequestQueue(imageReq);

    }

    @Override
    public int getItemCount() {
        return listaClubes.size();
    }

    public class ClubesHolder extends RecyclerView.ViewHolder {
        TextView txt_ListaClube_id, txt_ListaClube_nome, txt_ListaClube_descricao, txt_ListaClube_whatsapp;
        ImageView img_ListaClube_logo;
        public ClubesHolder(@NonNull View itemView, RecyclerViwerInterface_Clubes recyclerViwerInterfaceClubes) {
            super(itemView);

            txt_ListaClube_id = (TextView) itemView.findViewById(R.id.txt_ListaClube_id);
            txt_ListaClube_nome = (TextView) itemView.findViewById(R.id.txt_ListaClube_nome);
            txt_ListaClube_descricao = (TextView) itemView.findViewById(R.id.txt_ListaClube_descricao);
            txt_ListaClube_whatsapp = (TextView) itemView.findViewById(R.id.txt_ListaClube_whatsapp);
            img_ListaClube_logo = itemView.findViewById(R.id.img_ListaClube_logo);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViwerInterfaceClubes != null){
                        String IdResult = txt_ListaClube_id.getText().toString(); //"XSiYFQvkPPPF5wLAKM0KWh8u5qa2";
                        recyclerViwerInterfaceClubes.onitemClick(IdResult);

                    }

                }
            });




        }
    }
}
