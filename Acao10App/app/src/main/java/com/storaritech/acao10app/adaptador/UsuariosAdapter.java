package com.storaritech.acao10app.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.storaritech.acao10app.R;
import com.storaritech.acao10app.RecyclerViwerInterface;
import com.storaritech.acao10app.entidades.Usuario;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuariosHolder> {
    private final RecyclerViwerInterface recyclerViwerInterface;
    List<Usuario>listaUsuarios;


    public UsuariosAdapter(List<Usuario> listaUsuarios, RecyclerViwerInterface recyclerViwerInterface){
        this.listaUsuarios = listaUsuarios;
        this.recyclerViwerInterface = recyclerViwerInterface;
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
        holder.txt_senhaConsulta.setText(listaUsuarios.get(position).getSenha().toString());
        holder.txt_nivelConsulta.setText(listaUsuarios.get(position).getNivel().toString());
        holder.btn_Editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }





    public class UsuariosHolder extends RecyclerView.ViewHolder {
        TextView txt_idConsulta, txt_nomeConsulta, txt_emailConsulta, txt_senhaConsulta, txt_nivelConsulta;
        Button btn_Editar;
        public UsuariosHolder(@NonNull View itemView, RecyclerViwerInterface recyclerViwerInterface) {
            super(itemView);

            txt_idConsulta = (TextView) itemView.findViewById(R.id.txt_idConsulta);
            txt_nomeConsulta = (TextView) itemView.findViewById(R.id.txt_nomeConsulta);
            txt_emailConsulta = (TextView) itemView.findViewById(R.id.txt_emailConsulta);
            txt_senhaConsulta = (TextView) itemView.findViewById(R.id.txt_senhaConsulta);
            txt_nivelConsulta = (TextView) itemView.findViewById(R.id.txt_nivelConsulta);
            btn_Editar = (Button) itemView.findViewById(R.id.btn_ListaUsuario_Editar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViwerInterface != null){
                        String IdResult = txt_idConsulta.getText().toString();
                        recyclerViwerInterface.onitemClick(IdResult);

                    }

                }
            });


        }
    }

}
