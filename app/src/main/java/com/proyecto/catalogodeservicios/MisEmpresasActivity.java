package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

public class MisEmpresasActivity extends AppCompatActivity {


    JSONObject objeto;
    RequestQueue web_Service;

    ListView misEmpresas;
    List<String> empresas = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    JSONArray a;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_empresas);
        getSupportActionBar().setTitle("Mis empresas");

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(MisEmpresasActivity.this);

        misEmpresas = (ListView) findViewById(R.id.listaEmpresas);

        final Button registroEmpresas = findViewById(R.id.btnRegistrarEmpresa);
        registroEmpresas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regEmpresas = new Intent(getApplicationContext(), RegistroEmpresaActivity.class);
                startActivity(regEmpresas);
            }
        });

        // Evento a los items de la lista de empresas
        misEmpresas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = a.getJSONObject(position);
                    SharedPreferences.Editor editor = getSharedPreferences("EmpresaSeleccionada", MODE_PRIVATE).edit();
                    editor.putString("idEmpresa", jsonObject.getString("_id").toString());
                    editor.commit();
                    Intent ESeleccionada = new Intent(getApplicationContext(), EmpresaSeleccionadaActivity.class);
                    startActivity(ESeleccionada);
                } catch (JSONException e) { e.printStackTrace(); }
            }
        });

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, empresas)
        { // En este metodo se soluciona el color de texto
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                LayoutInflater inflater = LayoutInflater.from(this.getContext());
                View view = inflater.inflate(R.layout.lista_empresas, null);
                TextView textView1 = (TextView)view.findViewById(R.id.name);
                TextView letra = (TextView)view.findViewById(R.id.letra);
                letra.setText((empresas.get(position).toString()).substring(0, 1));
                textView1.setText(empresas.get(position));
                textView1.setTextColor(Color.BLACK);
                //letra.setTextColor(Color.BLACK);
                return view;
            }
        };
        misEmpresas.setAdapter(adapter);

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
                        Toast.makeText(MisEmpresasActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
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
            a = mensaje.getJSONArray("datos");
            for (int i = 0; i < a.length(); i++) {
                JSONObject jsonObject = a.getJSONObject(i);
                empresas.add(jsonObject.getString("nombre").toString());
            }
            adapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        finish();
    }
}
