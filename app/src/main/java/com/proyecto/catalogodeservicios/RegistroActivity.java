package com.proyecto.catalogodeservicios;

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

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    RequestQueue web_Service;
    EditText nombre, correo, telefono, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        nombre = (EditText)findViewById(R.id.txtNombre);
        correo = (EditText)findViewById(R.id.txtEmail);
        telefono = (EditText)findViewById(R.id.txtTelefono);
        contrasena = (EditText)findViewById(R.id.txtContra);

        web_Service = Volley.newRequestQueue(RegistroActivity.this);

        final Button button = findViewById(R.id.btnRegistrar);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringRequest registroRequest = new StringRequest(
                        Request.Method.POST,
                        "https://catalogoservicios.herokuapp.com/users/registro",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(RegistroActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(RegistroActivity.this, "Error al conectarse", Toast.LENGTH_SHORT).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("nombre", nombre.getText().toString());
                        parametros.put("correo", correo.getText().toString());
                        parametros.put("telefono", telefono.getText().toString());
                        parametros.put("contrasena", contrasena.getText().toString());
                        return parametros;
                    }
                };
                web_Service.add(registroRequest);
            }
        });
    }
}
