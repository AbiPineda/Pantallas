package com.example.abigail.pantallas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProductosActivity extends AppCompatActivity implements View.OnClickListener{

    //llamando la database
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private final String REQUERID="Requerido";
    private StorageReference mStorage;
    private FirebaseStorage storage;
    private final String CARPETA_RAIZ="misImagenesFerreteria/";
    private final String RUTA_IMAGEN=CARPETA_RAIZ+"misProductos";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private EditText nProducto;
    private Button btnGuardar;
    private EditText mProducto;
    private EditText pProducto;
    private EditText cProducto;
    private  Spinner tProducto;

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    Button botonCargar;
    ImageView imagen;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        //llamar los tipos
        nProducto = (EditText)findViewById(R.id.nombreProducto);
        mProducto = (EditText)findViewById(R.id.marcaProducto);
        pProducto = (EditText)findViewById(R.id.precioProducto);
        cProducto = (EditText)findViewById(R.id.cantidadProducto);
        btnGuardar = (Button)findViewById(R.id.botonguardar);
        tProducto = (Spinner) findViewById(R.id.spinner);
        String[] letra = {"Construccion","Herramientas","Hogar","Pintura","Maquinaria","Varios"};
        tProducto.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, letra));
        final String TAG = "ProductosActivity";
        //Referencia imagenes

        mStorage = FirebaseStorage.getInstance().getReference();




        imagen= (ImageView) findViewById(R.id.imagemId);
        botonCargar= (Button) findViewById(R.id.btnCargarImg);
        firebaseAuth = FirebaseAuth.getInstance();

        if(validaPermisos()){
            botonCargar.setEnabled(true);
        }else{
            botonCargar.setEnabled(false);
        }

        btnGuardar.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.salir:
                salir();
                return true;
            case R.id.pedidos:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void salir() {
        showProgressDialog();
        firebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(ProductosActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verificando en Linea...");
            progressDialog.setCancelable(false);

        }

        progressDialog.show();
    }

    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED){
                botonCargar.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }

    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(ProductosActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(ProductosActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialogo.show();
    }

    public void onclick(View view) {
        cargarImagen();
    }

    private void cargarImagen() {

        final CharSequence[] opciones={"Tomar Foto","Cargar Imagen","Cancelar"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(ProductosActivity.this);
        alertOpciones.setTitle("Seleccione una Opción");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("Tomar Foto")){
                    tomarFotografia();
                }else{
                    if (opciones[i].equals("Cargar Imagen")){
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
                    }else{
                        dialogInterface.dismiss();
                    }
                }
            }
        });
        alertOpciones.show();

    }

    private void tomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);

        ////
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){


            switch (requestCode){
                case COD_SELECCIONA:

                    final Uri miPath=data.getData();
                    StorageReference fileput = mStorage.child("productos").child(miPath.getLastPathSegment());


                    imagen.setImageURI(miPath);
                   /* fileput.putFile(miPath).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(ProductosActivity.this, "Error de subida"+exception, Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            Toast.makeText(ProductosActivity.this, "Subida Exitosa", Toast.LENGTH_SHORT).show();
                        }
                    });*/

                    break;

                case COD_FOTO:

                    MediaScannerConnection.scanFile(this, new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path);

                                }
                            });

                    Bitmap bitmap= BitmapFactory.decodeFile(path);
                    imagen.setImageBitmap(bitmap);
                    /*StorageReference mountainsRef = mStorage.child("productos").child("mountains.jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data1 = baos.toByteArray();
                    UploadTask uploadTask = mountainsRef.putBytes(data1);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(ProductosActivity.this, "Error de subida"+exception, Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            Toast.makeText(ProductosActivity.this, "Subida Exitosa", Toast.LENGTH_SHORT).show();
                        }
                    });*/


                    break;
            }


        }
    }
 

    @Override
    public void onClick(View view) {

        String nombre = nProducto.getText().toString().trim();
        String marca = mProducto.getText().toString().trim();
        String precio = pProducto.getText().toString().trim();
        String cantidad = cProducto.getText().toString().trim();


        if (TextUtils.isEmpty(nombre)){
            nProducto.setError(REQUERID);
            return;
        }
        if (TextUtils.isEmpty(marca)){
            mProducto.setError(REQUERID);
            return;
        }
        if (TextUtils.isEmpty(precio)){
            pProducto.setError(REQUERID);
            return;
        }
        if (TextUtils.isEmpty(cantidad)){
            cProducto.setError(REQUERID);
            return;
        }

        guardarProducto();
    }

    private void guardarProducto(){


        String pId = getUid();
        String Pro_Key = mDatabaseReference.child("producto").push().getKey();

        String text = tProducto.getSelectedItem().toString();

        String key = mDatabaseReference.child("producto").push().getKey();
        productos prod = new productos(Pro_Key,nProducto.getText().toString(),mProducto.getText().toString(),text,pProducto.getText().toString(),cProducto.getText().toString());
        Map<String, Object> productosValues = prod.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/producto/" + Pro_Key, productosValues);

        mDatabaseReference.updateChildren(childUpdates);
        //Agrega a un usuario especifico
        //childUpdates.put("/user-posts/" + pId + "/" + key, postValues);

        Toast.makeText(this, "Producto "+nProducto.getText().toString()+" Se Guardo Exitosamente", Toast.LENGTH_SHORT).show();

        imagen.setDrawingCacheEnabled(true);
        imagen.buildDrawingCache();
        Bitmap bitmap= ((BitmapDrawable) imagen.getDrawable()).getBitmap();
        StorageReference mountainsRef = mStorage.child("productos/".concat(Pro_Key));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(ProductosActivity.this, "Error de subida"+exception, Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(ProductosActivity.this, "Subida Exitosa", Toast.LENGTH_SHORT).show();
            }
        });
        nProducto.setText("");
        mProducto.setText("");
        cProducto.setText("");
        pProducto.setText("");

    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();

    }
}
