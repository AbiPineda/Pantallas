package com.example.abigail.pantallas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnEntrar;
    private EditText etusuario, etcontraseña;
    private TextView txolvido, txregistrarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etusuario = (EditText) findViewById(R.id.usuario);
        etcontraseña = (EditText) findViewById(R.id.contraseña);
        txregistrarme = (TextView) findViewById(R.id.registro);
        btnEntrar = (Button)findViewById(R.id.btnLogin);


        txregistrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
                startActivity(intent);
            }
        });
    }




}
