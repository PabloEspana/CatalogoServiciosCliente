package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.YuvImage;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
}
