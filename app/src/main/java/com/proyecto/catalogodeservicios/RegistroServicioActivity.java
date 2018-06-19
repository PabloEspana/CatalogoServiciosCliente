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
import android.widget.EditText;
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

public class RegistroServicioActivity extends AppCompatActivity {

    RequestQueue web_Service;
    EditText titulo, descripcion, costo, idEmpresa;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_servicio);
        getSupportActionBar().setTitle("Registro de Servicio");
        titulo = (EditText)findViewById(R.id.txtTituloServicio);
        descripcion = (EditText)findViewById(R.id.txtDescripcionServicio);
        costo = (EditText)findViewById(R.id.txtCostoServicio);

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(RegistroServicioActivity.this);

        final Button registarServicio = findViewById(R.id.btnRegistrarServicio);
        registarServicio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog.setMessage("Espere");
                progressDialog.show();
                StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                        Request.Method.POST,
                        "https://catalogoservicios.herokuapp.com/users/registroServicio",
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
                                Toast.makeText(RegistroServicioActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        SharedPreferences empresaSeleccionada = getSharedPreferences("EmpresaSeleccionada", MODE_PRIVATE);
                        String id = empresaSeleccionada.getString("idEmpresa", null);
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("titulo", titulo.getText().toString());
                        parametros.put("descripcion", descripcion.getText().toString());
                        parametros.put("costo", costo.getText().toString());
                        parametros.put("idEmpresa", id);
                        return parametros;
                    }
                };
                web_Service.add(registroRequest);
            }
        });

    }
    public void montrarMensaje(String msg) throws JSONException {
        final JSONObject mensaje = new JSONObject(msg);
        if(mensaje.get("tipoMensaje").equals("correcto")){
            Intent Admin = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(Admin);
            Toast.makeText(this, "Servicio Publicado Correctamente", Toast.LENGTH_SHORT).show();
        }
        else if(mensaje.get("tipoMensaje").equals("error")){
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
}
