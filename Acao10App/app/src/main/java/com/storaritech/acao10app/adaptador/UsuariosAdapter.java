package com.storaritech.acao10app.adaptador;

import static android.provider.Settings.Global.getString;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
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
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.RecyclerViwerInterface;
import com.storaritech.acao10app.entidades.MySingleton;
import com.storaritech.acao10app.entidades.Usuario;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuariosHolder> {
    private final RecyclerViwerInterface recyclerViwerInterface;
    List<Usuario>listaUsuarios;
    RequestQueue request;
    Context context;


    public UsuariosAdapter(List<Usuario> listaUsuarios, RecyclerViwerInterface recyclerViwerInterface, Context context){
        this.listaUsuarios = listaUsuarios;
        this.recyclerViwerInterface = recyclerViwerInterface;
        this.context = context;
        request = Volley.newRequestQueue(context);
    }

    @NonNull
    @Override
    public UsuariosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_usuarios, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
                );

        vista.setLayoutParams(layoutParams);



        return new UsuariosHolder(vista, recyclerViwerInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosHolder holder, int position) {
        holder.txt_idConsulta.setText(listaUsuarios.get(position).getId().toString());
        holder.txt_nomeConsulta.setText(listaUsuarios.get(position).getNome().toString());
        holder.txt_emailConsulta.setText(listaUsuarios.get(position).getEmail().toString());
        //holder.txt_senhaConsulta.setText(listaUsuarios.get(position).getSenha().toString());
        holder.txt_nivelConsulta.setText(listaUsuarios.get(position).getNivel().toString());


        if (listaUsuarios.get(position).getUrl_imagem()!=null){
            carregarImagemWebService(listaUsuarios.get(position).getUrl_imagem(),holder);
        } else {
            holder.idImagem.setImageResource(R.drawable.sem_foto);
        }


    }

    private void carregarImagemWebService(String urlImagem, UsuariosHolder holder) {


            String ip = context.getString(R.string.ip);

            String caminhoImagem = ip + "/acao10/api/usuarios/" + urlImagem;
            caminhoImagem = caminhoImagem.replace(" ", "%20");

            ImageRequest imageReq = new ImageRequest(caminhoImagem, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {

                    holder.idImagem.setImageBitmap(response);

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
        return listaUsuarios.size();
    }

    public class UsuariosHolder extends RecyclerView.ViewHolder {
        TextView txt_idConsulta, txt_nomeConsulta, txt_emailConsulta, txt_senhaConsulta, txt_nivelConsulta;
        ImageView idImagem;
        public UsuariosHolder(@NonNull View itemView, RecyclerViwerInterface recyclerViwerInterface) {
            super(itemView);

            txt_idConsulta = (TextView) itemView.findViewById(R.id.nomeId);
            txt_nomeConsulta = (TextView) itemView.findViewById(R.id.nomeUsuario);
            txt_emailConsulta = (TextView) itemView.findViewById(R.id.nomeEmail);
            //txt_senhaConsulta = (TextView) itemView.findViewById(R.id.txt_senhaConsulta);
            txt_nivelConsulta = (TextView) itemView.findViewById(R.id.nomeNivel);
            idImagem = itemView.findViewById(R.id.idImagem);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViwerInterface != null){
                        String IdResult = txt_idConsulta.getText().toString(); //"XSiYFQvkPPPF5wLAKM0KWh8u5qa2";
                        recyclerViwerInterface.onitemClick(IdResult);

                    }

                }
            });




        }
    }

}
