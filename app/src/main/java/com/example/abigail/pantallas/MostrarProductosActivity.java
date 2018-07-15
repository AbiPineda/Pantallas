package com.example.abigail.pantallas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MostrarProductosActivity extends AppCompatActivity {
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mDatabase;
    private ListView lista;
    private TextView texto;
    private ArrayList<String> arrayList = new ArrayList<>();
    private static final String TAG = "MostrarActivity";
    private String mProKey;
    private DatabaseReference mProReference;
    private ValueEventListener mProListener;
    String listItems = new String();
    ArrayList<String> listKeys = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private Boolean itemSelected = false;
    private int selectedPosition = 0;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("producto");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_productos2);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("producto");
        texto = (TextView) findViewById(R.id.texto);
        lista = (ListView) findViewById(R.id.lista);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        lista.setAdapter(adapter);
        lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        selectedPosition = position;
                        itemSelected = true;
                        //remover(selectedPosition);
                    }
                });


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot ciclo: dataSnapshot.getChildren()){
                    productos ver = ciclo.getValue(productos.class);
                    adapter.add(ver.toString());
                    listKeys.add(ver.getUid());
                }

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(postListener);

    }

    public void remover(int selectedPosition) {

        dbRef.child(listKeys.get(selectedPosition)).removeValue();

    }

}



