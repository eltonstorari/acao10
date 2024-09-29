package com.storaritech.acao10app.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.entidades.Parceiro;

import java.util.List;

public class ParceirosAdapter extends RecyclerView.Adapter<ParceirosAdapter.ParceirosHolder> {
    List<Parceiro> listaParceiros;
    RequestQueue request;
    Context context;

    public ParceirosAdapter(List<Parceiro> listaParceiros, Context context) {
        this.listaParceiros = listaParceiros;

        this.context = context;
        request = Volley.newRequestQueue(context);
    }


    @NonNull
    @Override
    public ParceirosAdapter.ParceirosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_parceiros, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        vista.setLayoutParams(layoutParams);

        return new ParceirosAdapter.ParceirosHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ParceirosAdapter.ParceirosHolder holder, int position) {
        holder.Recycler_txt_ListaParceiro_CNPJ.setText(listaParceiros.get(position).getCnpj().toString());
        holder.Recycler_txt_ListaParceiro_nome.setText(listaParceiros.get(position).getNome().toString());
        holder.Recycler_txt_ListaParceiro_email.setText(listaParceiros.get(position).getEmail().toString());
        holder.Recycler_txt_ListaParceiro_whatsapp.setText(listaParceiros.get(position).getWhatsapp().toString());

    }

    @Override
    public int getItemCount() {

        return listaParceiros.size();
    }

    public class ParceirosHolder extends RecyclerView.ViewHolder {
        TextView Recycler_txt_ListaParceiro_CNPJ, Recycler_txt_ListaParceiro_nome,Recycler_txt_ListaParceiro_email, Recycler_txt_ListaParceiro_whatsapp;
        ImageView Recycler_img_ListaParceiro_logo;



        public ParceirosHolder(@NonNull View itemView) {
            super(itemView);


            Recycler_txt_ListaParceiro_CNPJ = (TextView) itemView.findViewById(R.id.txt_ListaParceiro_CNPJ);
            Recycler_txt_ListaParceiro_nome = (TextView) itemView.findViewById(R.id.txt_ListaParceiro_nome);
            Recycler_txt_ListaParceiro_email = (TextView) itemView.findViewById(R.id.txt_ListaParceiro_email);
            Recycler_txt_ListaParceiro_whatsapp = (TextView) itemView.findViewById(R.id.txt_ListaParceiro_whatsapp);
            Recycler_img_ListaParceiro_logo = (ImageView) itemView.findViewById(R.id.img_ListaParceiro_logo);

        }
    }
}
