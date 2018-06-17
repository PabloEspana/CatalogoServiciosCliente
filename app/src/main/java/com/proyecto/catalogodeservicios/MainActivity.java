package com.proyecto.catalogodeservicios;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button registro = findViewById(R.id.btnIrARegistrar);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Registro = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(Registro);
            }
        });
        final Button logon = findViewById(R.id.btnIrALogin);
        logon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Login = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(Login);
            }
        });
    }

}
