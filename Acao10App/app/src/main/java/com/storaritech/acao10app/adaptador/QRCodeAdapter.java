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
import com.storaritech.acao10app.entidades.QRCode;

import java.util.List;

public class QRCodeAdapter extends RecyclerView.Adapter<QRCodeAdapter.QRCodeHolder> {
    //private final RecyclerViwerInterface_Usuarios recyclerViwerInterfaceUsuarios;
    static List<QRCode> listaQRCodes;
    RequestQueue requestQRCode;
    Context context;

    public QRCodeAdapter(List<QRCode> listaQRCodes, Context context) {
        this.listaQRCodes = listaQRCodes;
        this.context = context;
        requestQRCode = Volley.newRequestQueue(context);
    }


    @NonNull
    @Override
    public QRCodeAdapter.QRCodeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_qrcodes, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        vista.setLayoutParams(layoutParams);


        return new QRCodeHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull QRCodeAdapter.QRCodeHolder holder, int position) {
        holder.var_QRCodeCodigo.setText(listaQRCodes.get(position).getCodigo().toString());
        holder.var_QRCodeClube.setText(listaQRCodes.get(position).getClube().toString());
        holder.var_QRCodeCaixa.setText(listaQRCodes.get(position).getCaixa().toString());
        holder.var_QRCodeDescricao.setText(listaQRCodes.get(position).getDescricao().toString());

        if (listaQRCodes.get(position).getUrl_logo()!=null){
            //carregarImagemWebService(listaQRCodes.get(position).getUrl_logo(),holder);
        } else {
            holder.var_QRCodeLogo.setImageResource(R.drawable.sem_foto);
        }
    }

    @Override
    public int getItemCount() {
        return listaQRCodes.size();
    }

    public class QRCodeHolder extends RecyclerView.ViewHolder {
        TextView var_QRCodeCodigo, var_QRCodeClube, var_QRCodeCaixa, var_QRCodeDescricao;
        ImageView var_QRCodeLogo;

        public QRCodeHolder(@NonNull View itemView) {
            super(itemView);

            var_QRCodeCodigo = (TextView) itemView.findViewById(R.id.txt_ListaQRCode_Codigo);
            var_QRCodeClube = (TextView) itemView.findViewById(R.id.txt_ListaQRCode_Clube);
            var_QRCodeCaixa = (TextView) itemView.findViewById(R.id.txt_ListaQRCode_Caixa);
            var_QRCodeDescricao = (TextView) itemView.findViewById(R.id.txt_ListaQRCode_Descricao);
            var_QRCodeLogo = (ImageView) itemView.findViewById(R.id.img_ListaQRCode_logo);

        }
    }
}
