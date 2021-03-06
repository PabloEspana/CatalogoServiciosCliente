package com.proyecto.catalogodeservicios;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServiciosActivity extends AppCompatActivity {


    JSONObject objeto;
    RequestQueue web_Service;
    ListView listViewServicios;
    List<String> listaServicios = new ArrayList<String >();
    ArrayAdapter<String> adapter;
    JSONArray a;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicios);
        getSupportActionBar().setTitle("Servicios");

        progressDialog = new ProgressDialog(this);
        web_Service = Volley.newRequestQueue(ServiciosActivity.this);

        listViewServicios = (ListView) findViewById(R.id.listaServicios);

        // Hay que validar si tiene empresa registrada (esto aun no debera estar)
        final Button nuevoServicio = findViewById(R.id.btnNuevoServicio);
        nuevoServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuevo = new Intent(getApplicationContext(),  MisEmpresasActivity.class);
                startActivity(nuevo);

            }
        });

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listaServicios)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                LayoutInflater inflater = LayoutInflater.from(this.getContext());
                View view = inflater.inflate(R.layout.lista_servicios, null);
                int[] androidColors = getResources().getIntArray(R.array.androidcolors);
                TextView titulo = (TextView)view.findViewById(R.id.nombreServicio);
                int randomAndroidColor = androidColors[new Random().nextInt(androidColors.length)];
                titulo.setBackgroundColor(randomAndroidColor);
                titulo.setTextColor(Color.WHITE);
                titulo.setText(listaServicios.get(position));
                titulo.setPadding(3,3,3,3);
                return view;
            }
        };
        listViewServicios.setAdapter(adapter);

        //Consulta de todos los servicios
        progressDialog.setMessage("Espere");
        progressDialog.show();

        StringRequest registroRequest = new StringRequest(      // Desde aqui inicia la peticion al servidor
                Request.Method.POST,
                "https://catalogoservicios.herokuapp.com/users/servicios",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try { montrarMensaje(response); }
                        catch (JSONException e) { e.printStackTrace(); } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ServiciosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        web_Service.add(registroRequest);
    }

    public void montrarMensaje(String msg) throws JSONException, ParseException {
        final JSONObject mensaje = new JSONObject(msg);
        if(mensaje.get("tipoMensaje").equals("correcto")){
            a = mensaje.getJSONArray("datos");
            for (int i = 0; i < a.length(); i++) {
                JSONObject jsonObject = a.getJSONObject(i);
                String empresa = jsonObject.getString("idEmpresa").toString();
                JSONObject object_empresa = new JSONObject(empresa);
                String nombres = "Titulo: " + jsonObject.getString("titulo").toString()+"\n" +
                        "Descripción: "+jsonObject.getString("descripcion").toString()+"\n"+
                        "Costo: $"+jsonObject.getString("costo").toString()+"\n"+
                        "Nombre de la empresa: "+ object_empresa.get("nombre").toString()+"\n"+
                        "Teléfono de la empresa: "+ object_empresa.get("telefono").toString()+"\n"+
                        "Correo de la empresa: "+ object_empresa.get("correo").toString()+"\n";
                        listaServicios.add(nombres);
            }
        }
        adapter.notifyDataSetChanged();
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
