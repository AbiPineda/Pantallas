package com.example.abigail.pantallas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;

public class RegistrarActivity extends AppCompatActivity {

    private Button btnGuardar;
    private EditText etdui, etnombre, etdireccion, ettelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        etdui = (EditText) findViewById(R.id.dui);
        etnombre = (EditText) findViewById(R.id.nombre);
        etdireccion = (EditText) findViewById(R.id.direccion);
        ettelefono = (EditText) findViewById(R.id.telefono);

        btnGuardar = (Button)findViewById(R.id.btnGuardar);


    }
}
