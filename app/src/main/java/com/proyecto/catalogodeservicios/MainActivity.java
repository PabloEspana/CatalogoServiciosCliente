package com.proyecto.catalogodeservicios;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Catálogo de Servicios");

        //Obtengo datos de sesion
        SharedPreferences sesion = getSharedPreferences("Sesion", MODE_PRIVATE);
        String datosSesion = sesion.getString("estado", null);
        if (datosSesion != null) {
            if (datosSesion.equals("iniciada")){
                Intent admin = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(admin);
                finish();
            }
        }

        final Button registro = findViewById(R.id.btnIrARegistrar);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Registro = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(Registro);
            }
        });
        final Button logon = findViewById(R.id.btnIrALogin);
        logon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(Login);
                finish();
            }
        });
    }

}
