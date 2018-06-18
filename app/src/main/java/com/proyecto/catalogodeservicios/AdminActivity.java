package com.proyecto.catalogodeservicios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity {
    JSONObject objeto;
    TextView nombreUsuario, telefono, correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admnistración de cuenta");
        nombreUsuario = (TextView)findViewById(R.id.lblNombreUsuario);
        telefono = (TextView)findViewById(R.id.lblTeleefono);
        correo = (TextView)findViewById(R.id.lblCorreo);

        final Button registroEmpresa = findViewById(R.id.btnIrARegEmpresa);
        registroEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegistroEmpresa = new Intent(getApplicationContext(), RegistroEmpresaActivity.class);
                startActivity(RegistroEmpresa);
            }
        });

        SharedPreferences sesion = getSharedPreferences("Sesion", MODE_PRIVATE);
        String datosSesion = sesion.getString("estado", String.valueOf(false));
        if (datosSesion != null) {
            String datos = sesion.getString("datos", null);
            Toast.makeText(this, datos, Toast.LENGTH_SHORT).show();
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
}
