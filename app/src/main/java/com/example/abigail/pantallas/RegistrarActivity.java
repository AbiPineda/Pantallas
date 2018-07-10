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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistrarActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String REQUIRED = "Requerido";
    private static final String TAG = "RegistarActivity" ;
    //declarar objetos
    private EditText TextEmail;
    private EditText TextPassword;
    private Button btnRegistrar,btnRegresar;
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
        btnRegresar = (Button) findViewById(R.id.botonRegresar);

        progressDialog = new ProgressDialog(this);

        //botton de escucha
        btnRegistrar.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);
    }

    private void registrarUsuario(){
        //obtenemos el email y las contraseñas desde las cajas de texto
        final String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            TextEmail.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(password)){
            TextPassword.setError(REQUIRED);
            return;
        }

        showProgressDialog();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        if(task.isSuccessful()){
                            Toast.makeText(RegistrarActivity.this, "Se ha Registrado el Usuario con el Email: "+TextEmail.getText(), Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplication(), Categorias.class);
                            intent.putExtra(MainActivity.usuario, email);
                            startActivity(intent);
                        }else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(RegistrarActivity.this, "Ese Usuario ya esta en Uso", Toast.LENGTH_SHORT).show();
                        }else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            TextEmail.setError("Correo Invalido");
                            TextPassword.setError("Contraseña Invalida");
                        }else if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                            TextPassword.setError("Contraseña Debe Contener Por lo Menos 6 Caracteres");
                        }else{
                            Toast.makeText(RegistrarActivity.this, "Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });

    }

    @Override
    public void onClick(View view) {

        int i = view.getId();

        if (i == R.id.botonRegistrar) {
            registrarUsuario();
        }
        if (i == R.id.botonRegresar){
            finish();
            Intent intent = new Intent(RegistrarActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Verificando en Linea...");
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}