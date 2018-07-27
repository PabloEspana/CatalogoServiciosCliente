package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.design.widget.Snackbar;
import android.support.design.widget.CoordinatorLayout;
import android.graphics.Color;
import android.widget.TextView;
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

public class RegistroActivity extends AppCompatActivity {

    RequestQueue web_Service;
    EditText nombre, correo, telefono, contrasena;
    private ProgressDialog progressDialog;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setTitle("Registro");

        nombre = (EditText)findViewById(R.id.txtNombre);
        correo = (EditText)findViewById(R.id.txtEmail);
        telefono = (EditText)findViewById(R.id.txtTelefono);
        contrasena = (EditText)findViewById(R.id.txtContra);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout2);


        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(RegistroActivity.this);

        final Button registarse = findViewById(R.id.btnRegistrar);
        registarse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(nombre.getText().toString().trim().isEmpty() || correo.getText().toString().trim().isEmpty()
                || telefono.getText().toString().trim().isEmpty() || contrasena.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Complete todos los campos", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.rgb(255, 255, 255));
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.rgb(198, 40, 40));
                    TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.rgb(255, 255, 255));
                    snackbar.show();
                }else{
                    progressDialog.setMessage("Espere");
                    progressDialog.show();
                    StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                            Request.Method.POST,
                            "https://catalogoservicios.herokuapp.com/users/registro",
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
                                    Toast.makeText(RegistroActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
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
            }
        });
    }

    public AlertDialog montrarMensaje(String msg) throws JSONException {
        final JSONObject mensaje = new JSONObject(msg);
        String msgPositiveButton = "OK";
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (mensaje.get("tipoMensaje").toString().equals("correcto")){
            msgPositiveButton = "Iniciar Sesión";
        }
        alert.setTitle(mensaje.get("tipoMensaje").toString()).setMessage(mensaje.get("mensaje").toString())
                .setPositiveButton(msgPositiveButton,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if(mensaje.get("tipoMensaje").equals("correcto")){
                                Intent Login = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(Login);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return alert.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
