package com.proyecto.catalogodeservicios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;


public class AdminActivity extends AppCompatActivity {

    JSONObject objeto;
    TextView nombreUsuario, telefono, correo, id, nombre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admnistración de cuenta");
        nombreUsuario = (TextView)findViewById(R.id.lblNombreUsuario);
        telefono = (TextView)findViewById(R.id.lblTeleefono);
        correo = (TextView)findViewById(R.id.lblCorreo);

        final Button misEmpresas = findViewById(R.id.btnVerEmpresa);
        misEmpresas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent verEmpresas = new Intent(getApplicationContext(), MisEmpresasActivity.class);
                startActivity(verEmpresas);
            }
        });

        final Button verServicios = findViewById(R.id.btnVerTodosServicios);
        verServicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent todosServicios = new Intent(getApplicationContext(), ServiciosActivity.class);
                startActivity(todosServicios);
            }
        });

        final Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences("Sesion", MODE_PRIVATE);
                settings.edit().clear().commit();
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
                finish();
            }
        });

        //Obtengo datos de sesion
        SharedPreferences sesion = getSharedPreferences("Sesion", MODE_PRIVATE);
        String datosSesion = sesion.getString("estado", String.valueOf(false));
        if (datosSesion != null) {
            String datos = sesion.getString("datos", null);
            try {
                objeto = new JSONObject(datos);
                nombreUsuario.setText(objeto.get("nombre").toString());
                telefono.setText("Teléfono: " + objeto.get("telefono").toString());
                correo.setText("Correo Electrónico: " + objeto.get("correo").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
