package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    RequestQueue web_Service;
    EditText correo, contrasena;
    private ProgressDialog progressDialog;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Inicio de Sesión");

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

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        correo = (EditText)findViewById(R.id.txtEmailLogin);
        contrasena = (EditText)findViewById(R.id.txtContraLogin);

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(LoginActivity.this);

        final Button inicioSesion = findViewById(R.id.btnLogin);
        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(correo.getText().toString().trim().isEmpty() || contrasena.getText().toString().trim().isEmpty()){
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Complete todos los campos", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.rgb(255, 255, 255));
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.rgb(198, 40, 40));
                    TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.rgb(255, 255, 255));
                    snackbar.show();
                }
                else{
                    progressDialog.setMessage("Espere");
                    progressDialog.show();
                    StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                            Request.Method.POST,
                            "https://catalogoservicios.herokuapp.com/users/login",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try { montrarMensaje(response); /*finish();*/ }
                                    catch (JSONException e) { e.printStackTrace(); }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                                }
                            }
                    ){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parametros = new HashMap<String, String>();
                            parametros.put("correo", correo.getText().toString());
                            parametros.put("contrasena", contrasena.getText().toString());
                            return parametros;
                        }
                    };
                    web_Service.add(registroRequest);
                }
            }
        });
    }

    public void montrarMensaje(String msg) throws JSONException {
        final JSONObject mensaje = new JSONObject(msg);
        if(mensaje.get("tipoMensaje").equals("correcto")){
            SharedPreferences.Editor editor = getSharedPreferences("Sesion", MODE_PRIVATE).edit();
            editor.putString("estado", "iniciada");
            editor.putString("datos", mensaje.get("datos").toString());
            editor.commit();
            Intent Admin = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(Admin);
            finish();
        }
        /*AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(mensaje.get("tipoMensaje").toString()).setMessage(mensaje.get("mensaje").toString())
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        alert.show();*/

        Snackbar snackbar = Snackbar.make(coordinatorLayout, mensaje.get("mensaje").toString(), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.rgb(255, 255, 255));
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.rgb(198, 40, 40));
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.rgb(255, 255, 255));
        snackbar.show();

    }

    @Override
    public void onBackPressed() {
        Intent inicio = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(inicio);
        finish();
    }
}
