package com.example.abigail.pantallas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Categorias extends AppCompatActivity {
    public static final String usuario="nombre";
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    GridView gridView;

    String[] Names = {"Construcción","Pintura", "Herramientas", "Hogar", "Maquinaria", "Varios"};
    int[] Images = {R.mipmap.pala, R.mipmap.pintura, R.mipmap.her, R.mipmap.hog, R.mipmap.maquinaria, R.mipmap.var};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);
        //finding listview
        gridView = findViewById(R.id.grid);
        firebaseAuth = FirebaseAuth.getInstance();

        CustomAdapter customAdapter = new CustomAdapter();
        gridView.setAdapter(customAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),fruitNames[i],Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),GridItemActivity.class);
                intent.putExtra("name",Names[i]);
                intent.putExtra("image",Images[i]);
                startActivity(intent);

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.perfil:
                return true;
            case R.id.salir:
                salir();
                return true;

            case R.id.pedidos:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void salir() {
        firebaseAuth.signOut();
        showProgressDialog();
        Intent intent = new Intent(Categorias.this, MainActivity.class);
        startActivity(intent);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Verificando en Linea...");
        }

        progressDialog.show();
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Images.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.columnas,null);
            //getting view in row_data
            TextView name = view1.findViewById(R.id.fruits);
            ImageView image = view1.findViewById(R.id.images);

            name.setText(Names[i]);
            image.setImageResource(Images[i]);
            return view1;



        }
    }
}
