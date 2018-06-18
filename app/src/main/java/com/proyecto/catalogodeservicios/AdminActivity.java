package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.YuvImage;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {
    JSONObject objeto;
    TextView nombreUsuario, telefono, correo, id, nombre;
    RequestQueue web_Service;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getSupportActionBar().setTitle("Admnistración de cuenta");
        nombreUsuario = (TextView)findViewById(R.id.lblNombreUsuario);
        telefono = (TextView)findViewById(R.id.lblTeleefono);
        correo = (TextView)findViewById(R.id.lblCorreo);
        nombre = (TextView)findViewById(R.id.lblNombre);

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(AdminActivity.this);

        final Button registroEmpresa = findViewById(R.id.btnIrARegEmpresa);
        registroEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegistroEmpresa = new Intent(getApplicationContext(), RegistroEmpresaActivity.class);
                startActivity(RegistroEmpresa);
            }
        });

        //Obtengo datos de sesion
        SharedPreferences sesion = getSharedPreferences("Sesion", MODE_PRIVATE);
        String datosSesion = sesion.getString("estado", String.valueOf(false));
        if (datosSesion != null) {
            String datos = sesion.getString("datos", null);
            //Toast.makeText(this, datos, Toast.LENGTH_SHORT).show();
            try {
                objeto = new JSONObject(datos);
                nombreUsuario.setText(objeto.get("nombre").toString());
                telefono.setText("Teléfono: " + objeto.get("telefono").toString());
                correo.setText("Correo Electrónico: " + objeto.get("correo").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Consulta de empresas del usuario
        progressDialog.setMessage("Espere");
        progressDialog.show();
        StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                Request.Method.POST,
                "https://catalogoservicios.herokuapp.com/users/misEmpresas",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try { montrarMensaje(response); }
                        catch (JSONException e) { e.printStackTrace(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AdminActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                SharedPreferences sesion = getSharedPreferences("Sesion", MODE_PRIVATE);
                String datos = sesion.getString("datos", null);
                try {
                    JSONObject objeto = new JSONObject(datos);
                    parametros.put("idUser", objeto.get("_id").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return parametros;
            }
        };
        web_Service.add(registroRequest);

    }

    public void montrarMensaje(String msg) throws JSONException {
        final JSONObject mensaje = new JSONObject(msg);
        if(mensaje.get("tipoMensaje").equals("correcto")){
            Toast.makeText(this, mensaje.get("datos").toString(), Toast.LENGTH_SHORT).show();
            nombre.setText("Nombre: "+mensaje.get("datos").toString());
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(mensaje.get("tipoMensaje").toString()).setMessage(mensaje.get("mensaje").toString())
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        alert.show();
    }
}
