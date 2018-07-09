package com.example.abigail.pantallas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;

public class RegistrarActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarActivity";
    private static final String REQUIRED = "Requerido";
    private ProgressDialog mProgressDialog;

    // declaracion de database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //declaracion de los campos
    private EditText nombre;
    private EditText direccion;
    private EditText telefono;
    private Button btnRegistro;
    private EditText dui;

    private String FIREBASE_URL = "https://ferreteriafinal-b87ce.firebaseio.com/";
    private String FIREBASE_CHILD = "test";
    Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        dui = (EditText) findViewById(R.id.dui);
        nombre = (EditText) findViewById(R.id.nombre);
        direccion = (EditText) findViewById(R.id.direccion);
        telefono = (EditText) findViewById(R.id.telefono);
        btnRegistro = (Button) findViewById(R.id.btnGuardar);

        //click listener

        Firebase.setAndroidContext(this);



    }

    public void guardar(View view){

        firebase = new Firebase(FIREBASE_URL).child(getUid());
            firebase.setValue(nombre.getText().toString());
            nombre.setText("");
            dui.setText("");
        }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }
}