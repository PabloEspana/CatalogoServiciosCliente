package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class EmpresaSeleccionadaActivity extends AppCompatActivity {

    JSONObject objeto;
    private ProgressDialog progressDialog;
    RequestQueue web_Service;
    JSONArray a;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa_seleccionada);
        getSupportActionBar().setTitle("Mi empresa");

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(EmpresaSeleccionadaActivity.this);

        final Button verSetvicios = findViewById(R.id.btnVerServicios);
        verSetvicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regServicio = new Intent(getApplicationContext(), RegistroServicioActivity.class);
                startActivity(regServicio);
            }
        });

        //Consulta de empresas del usuario
        progressDialog.setMessage("Espere");
        progressDialog.show();
        StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                Request.Method.POST,
                "https://catalogoservicios.herokuapp.com/users/busqEmpresa",
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
                        Toast.makeText(EmpresaSeleccionadaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }
        ){


            //    SharedPreferences empresaSeleccionada = getSharedPreferences("EmpresaSeleccionada", MODE_PRIVATE);
             //   String id = empresaSeleccionada.getString("datos", null);

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences empresaSeleccionada = getSharedPreferences("EmpresaSeleccionada", MODE_PRIVATE);
                String id = empresaSeleccionada.getString("idEmpresa", null);
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("idEmpresa", id);
                return parametros;
            }
        };
        web_Service.add(registroRequest);
    }

    public void montrarMensaje(String msg) throws JSONException {
        final JSONObject mensaje = new JSONObject(msg);
        if(mensaje.get("tipoMensaje").equals("correcto")){
            a = mensaje.getJSONArray("datos");
            Toast.makeText(this, a.toString(), Toast.LENGTH_SHORT).show();
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
