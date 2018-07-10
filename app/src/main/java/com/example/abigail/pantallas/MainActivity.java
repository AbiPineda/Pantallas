package com.example.abigail.pantallas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String usuario="nombre";
    private Button btnEntrar;
    private EditText etusuario, etcontraseña;
    private TextView txolvido, txregistrarme;
    private ProgressDialog progressDialog;
    private static final String REQUIRED = "Requerido";
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "Bienvenido: "+ user.getEmail(), Toast.LENGTH_SHORT).show();
            if (user.getEmail().equals("admin1@gmail.com")){
                showProgressDialog();
                Intent intent = new Intent(MainActivity.this, ProductosActivity.class);
                startActivity(intent);
            }else{
                showProgressDialog();
                Intent intent = new Intent(MainActivity.this, Categorias.class);
                startActivity(intent);
            }

        }else {
            firebaseAuth = FirebaseAuth.getInstance();
            etusuario = (EditText) findViewById(R.id.usuario);
            etcontraseña = (EditText) findViewById(R.id.contraseña);
            txregistrarme = (TextView) findViewById(R.id.registro);
            btnEntrar = (Button) findViewById(R.id.botonlogin);

            txregistrarme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent intent = new Intent(MainActivity.this, RegistrarActivity.class);
                    startActivity(intent);
                }
            });


            btnEntrar.setOnClickListener(this);
        }

    }
    private void entrar()
    {

        //obtenemos el email y las contraseñas desde las cajas de texto
        final String email = etusuario.getText().toString().trim();
        String password = etcontraseña.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            etusuario.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(password)){
            etcontraseña.setError(REQUIRED);
            return;
        }

        showProgressDialog();

        //loguearse usuario
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()){
                            if (email.equals("admin1@gmail.com")){
                                finish();
                                Toast.makeText(MainActivity.this, "Bienvenido: " + etusuario.getText(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplication(), ProductosActivity.class);
                                startActivity(intent);
                            }else {
                                finish();
                                Toast.makeText(MainActivity.this, "Bienvenido: " + etusuario.getText(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplication(), Categorias.class);
                                intent.putExtra(Categorias.usuario, email);
                                startActivity(intent);
                            }

                        }else if (task.getException() instanceof FirebaseAuthInvalidUserException){
                            etusuario.setError("El Correo no Existe en Nuestros Registros.");
                        }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            etusuario.setError("Correo o Contraseña Invalido");
                        }else {

                                Toast.makeText(MainActivity.this, "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        hideProgressDialog();
                    }
                });
    }


    @Override
    public void onClick(View view) {
        entrar();
    }
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verificando en Linea...");
            progressDialog.setCancelable(false);

        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
