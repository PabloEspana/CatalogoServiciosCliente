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

public class RegistroEmpresaActivity extends AppCompatActivity {

    RequestQueue web_Service;
    EditText nombre, correo, telefono;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_empresa);
        getSupportActionBar().setTitle("Registro de Empresa");

        nombre = (EditText)findViewById(R.id.txtNombreEmpresa);
        correo = (EditText)findViewById(R.id.txtEmailEmpresa);
        telefono = (EditText)findViewById(R.id.txtTelefonoEmpresa);

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(RegistroEmpresaActivity.this);

        final Button registarEmpresa = findViewById(R.id.btnRegistrar);
        registarEmpresa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog.setMessage("Espere");
                progressDialog.show();
                StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                        Request.Method.POST,
                        "https://catalogoservicios.herokuapp.com/users/registroEmpresa",
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
                                Toast.makeText(RegistroEmpresaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
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
                            parametros.put("nombre", nombre.getText().toString());
                            parametros.put("correo", correo.getText().toString());
                            parametros.put("idUser", objeto.get("_id").toString());
                            parametros.put("telefono", telefono.getText().toString());
                            parametros.put("tipoUsuario", "Empresarial");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
            Toast.makeText(this, "Empresa Creada correctamente", Toast.LENGTH_SHORT).show();
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
