package com.storaritech.acao10app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.storaritech.acao10app.Login;
import com.storaritech.acao10app.R;
import com.storaritech.acao10app.databinding.ActivityMenuAdminBinding;
import com.storaritech.acao10app.entidades.MySingleton;
import com.storaritech.acao10app.entidades.Usuario;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Menu_Admin extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    ImageView img_PerfilNav;
    TextView txt_IdNav , txt_NomeNav, txt_EmailNav;
    RequestQueue request;
    JsonObjectRequest jsonObjectReq;
    StringRequest stringRequest;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuAdminBinding binding;


    @Override
    protected void onStart(){
        super.onStart();
        IniciarComponentes();

        txt_IdNav.setText(FirebaseAuth.getInstance().getUid());
        carregarWEBService();
    }

    private void carregarWEBService() {
        String ip = getString(R.string.ip);
        String url = ip + "/acao10/api/login/consultarUrl.php?id=" + txt_IdNav.getText().toString();
        url = url.replace(" ", "%20");


        jsonObjectReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Usuario tabUsuarios = new Usuario();

                JSONArray json = response.optJSONArray("usuarios");
                JSONObject jsonObject  = null;

                try {
                    assert json != null;
                    jsonObject = json.getJSONObject(0);
                    tabUsuarios.setNome(jsonObject.optString("nome"));
                    tabUsuarios.setEmail(jsonObject.optString("email"));
                    tabUsuarios.setSenha(jsonObject.optString("senha"));
                    tabUsuarios.setNivel(jsonObject.optString("nivel"));
                    tabUsuarios.setUrl_imagem(jsonObject.optString("url_imagem"));
                }catch (JSONException e){
                    e.printStackTrace();
                }

                txt_NomeNav.setText(tabUsuarios.getNome());
                txt_EmailNav.setText(tabUsuarios.getEmail());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Não foi possivel efetuar a consulta " + error.toString(), Toast.LENGTH_LONG).show();
                Log.i("ERRO", error.toString());
            }
        });

        //resquest.add(jsonObjectReq);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq);


    }

    private void IniciarComponentes(){

        //Instanciando a tela de SideBar
        NavigationView navigationView = binding.navViewAdmin;
        View header = navigationView.getHeaderView(0);
        img_PerfilNav = header.findViewById(R.id.img_Admin_PerfilNav);
        txt_IdNav = header.findViewById(R.id.txt_Admin_IdNav);
        txt_NomeNav = header.findViewById(R.id.txt_Admin_NomeNav);
        txt_EmailNav = header.findViewById(R.id.txt_Admin_EmailNav);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarFormMenuAdmin.toolbar);
        /**binding.appBarFormMenuAdmin.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });**/
        DrawerLayout drawer = binding.drawerLayoutAdmin;
        NavigationView navigationView = binding.navViewAdmin;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_CadUsuario, R.id.nav_Clube, R.id.nav_QRCode, R.id.nav_Parceiro, R.id.nav_Propaganda)

                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_menu_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_menu_admin, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_menu_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.action_settings_admin);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@androidx.annotation.NonNull MenuItem item) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Menu_Admin.this, Login.class);
                startActivity(intent);
                finish();
                return false;
            }
        });



        return false;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(JSONObject jsonObject) {

    }
}