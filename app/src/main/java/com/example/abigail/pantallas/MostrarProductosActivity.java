package com.example.abigail.pantallas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MostrarProductosActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ListView lista;
    private TextView texto;
    private ArrayList<String> arrayList = new ArrayList<>();
    private static final String TAG = "PostDetailActivity";
    private String mProKey;
    private DatabaseReference mProReference;
    private ValueEventListener mProListener;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    public static final String EXTRA_POST_KEY = "Pro_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_productos2);

        mProKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mProKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        mProReference = FirebaseDatabase.getInstance().getReference()
                .child("productos").child(mProKey);
        texto = (TextView)findViewById(R.id.texto);
        //lista = (ListView) findViewById(R.id.listaProducto);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice,
                listItems);
        lista.setAdapter(adapter);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


    }
    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                productos post = dataSnapshot.getValue(productos.class);
                // [START_EXCLUDE]
                texto.setText(post.getnProducto());
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(MostrarProductosActivity.this, "Fallo.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mProReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mProListener = postListener;

      /*  // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);*/
    }
    }
/*
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Instancia a la base de datos
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        //apuntamos al nodo que queremos leer
        DatabaseReference myRef = fdb.getReference();

        //Agregamos un ValueEventListener para que los cambios que se hagan en la base de datos
        //se reflejen en la aplicacion

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){

                //leeremos un objeto de tipo Estudiante
                GenericTypeIndicator<productos> t = new GenericTypeIndicator<productos>() {};
                productos estudiante = dataSnapshot.getValue(t);

                //formamos el resultado en un string
                String resultado = "Como objeto java:\n\n";
                resultado += estudiante + "\n";
                resultado += "Propiedad Estudiante:\nNombre completo: " +estudiante.toMap().toString();

                //Tambien podemos leer los datos como un string
                resultado += "\n\n-----------------------------\n\n";
                resultado += "Como JSON:\n\n";
                resultado += dataSnapshot.getValue().toString();

                resultado += "\n\nHijo de ConstruccionStanleyMartillo -> nProducto\n";

                resultado += dataSnapshot.child("nProducto").toString()+  "\n";

                //leemos un nodo hijo del nodo estudiante
                resultado += "\n Key: " + dataSnapshot.child("nProducto").getKey()+"\n";
                resultado += "\n Valor: " + dataSnapshot.child("nProducto").getValue(String.class);

                //mostramos en el textview
                lista.setAdapter(resultado);
            }

            @Override
            public void onCancelled(DatabaseError error){
                Log.e("ERROR FIREBASE",error.getMessage());
            }

        });
}//onCreate:end


    @Override
    public void onClick(View view) {

    }
}*/



