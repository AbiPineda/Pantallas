package com.example.abigail.pantallas;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener{

    //declarar objetos
    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnRegistrar;
    private ProgressDialog progressDialog;
    //declarar un objeto firebase
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //inicializamos el objeto firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //referenciamos los views
        TextEmail = (EditText) findViewById(R.id.txtEmail);
        TextPassword = (EditText) findViewById(R.id.txtPassword);

        btnRegistrar = (Button) findViewById(R.id.botonRegistrar);

        progressDialog = new ProgressDialog(this);

        //botton de escucha
        btnRegistrar.setOnClickListener(this);
    }

    private void registrarUsuario(){
        //obtenemos el email y las contraseñas desde las cajas de texto
        String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Se Debe Ingresa un Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Se Debe Ingresa un Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Realizando registro en Linea.....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(RegistrarActivity.this, "Se ha Registrado el Usuario", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegistrarActivity.this, "No se Pudo Registrar el Usuario", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
    @Override
    public void onClick(View view) {

        registrarUsuario();
    }
}