package com.example.mapa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Marcadores;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private List<Marcadores> listmarc = new ArrayList<Marcadores>();
    ArrayAdapter<Marcadores> arrayAdaptermarca;

    EditText nom, emp,dept,mun,lat,lon;
    ListView list_estaciones;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Marcadores estacionSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nom= findViewById(R.id.txt_nombre);
        emp = findViewById(R.id.txt_empresa);
        dept = findViewById(R.id.txt_departamento);
        mun = findViewById(R.id.txt_municipio);
        lat = findViewById(R.id.txt_latitud);
        lon = findViewById(R.id.txt_longitud);

        list_estaciones = findViewById(R.id.estaciones);


        inicializarFirebase();
        listarDatos();


        list_estaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                estacionSelected = (Marcadores) parent.getItemAtPosition(position);
                nom.setText(estacionSelected.getNombre());
                emp.setText(estacionSelected.getEmpresa());
                dept.setText(estacionSelected.getDepartamento());
                mun.setText(estacionSelected.getMunicipio());
                lat.setText(estacionSelected.getLatitud());
                lon.setText(estacionSelected.getLongitud());

            }

    });

    }

    private void listarDatos(){
        databaseReference.child("Marcadores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listmarc.clear();
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Marcadores m = objSnaptshot.getValue(Marcadores.class);
                    listmarc.add(m);

                    arrayAdaptermarca = new ArrayAdapter<Marcadores>(MainActivity.this, android.R.layout.simple_list_item_1, listmarc);

                    list_estaciones.setAdapter(arrayAdaptermarca);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

    private void inicializarFirebase() {

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();
    }

    private void limpiarCajas() {
        nom.setText("");
        emp.setText("");
        dept.setText("");
        mun.setText("");
        lat.setText("");
        lon.setText("");

    }

    private void validacion() {
        String nnomb = nom.getText().toString();
        String nemp = emp.getText().toString();
        String ndept = dept.getText().toString();
        String nmun = mun.getText().toString();
        String nlat = lat.getText().toString();
        String nlon = lon.getText().toString();
        if (nnomb.equals("")){
            nom.setError("Requerido");
        }
        else if (nemp.equals("")){
            emp.setError("Requerido");
        }
        else if (ndept.equals("")){
            dept.setError("Requerido");
        }
        else if (nmun.equals("")){
            mun.setError("Requerido");
        } else if (nlat.equals("")){
            lat.setError("Requerido");
        } else if (nlon.equals("")){
            lon.setError("Requerido");
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nnomb = nom.getText().toString();
        String nemp = emp.getText().toString();
        String ndept = dept.getText().toString();
        String nmun = mun.getText().toString();
        String nlat = lat.getText().toString();
        String nlon = lon.getText().toString();


        switch (item.getItemId()){
            case R.id.icon_add:{
                if (nnomb.equals("")||nemp.equals("")||ndept.equals("")||nmun.equals("") ||nlat.equals("") ||nlon.equals("")){
                    validacion();
                }
                else {
                    Marcadores m = new Marcadores();
                    m.setUid(UUID.randomUUID().toString());
                    m.setNombre(nnomb);
                    m.setEmpresa(nemp);
                    m.setDepartamento(ndept);
                    m.setMunicipio(nmun);
                    m.setLatitud(nlat);
                    m.setLongitud(nlon);
                    databaseReference.child("Marcadores").child(m.getUid()).setValue(m);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                break;
            }
            case R.id.icon_save:{
                Marcadores m = new Marcadores();
                m.setUid(estacionSelected.getUid());
                m.setNombre(nom.getText().toString().trim());
                m.setEmpresa(emp.getText().toString().trim());
                m.setDepartamento(dept.getText().toString().trim());
                m.setMunicipio(mun.getText().toString().trim());
                m.setLatitud(lat.getText().toString().trim());
                m.setLongitud(lon.getText().toString().trim());
                databaseReference.child("Marcadores").child(m.getUid()).setValue(m);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            case R.id.icon_delete:{
                Marcadores m = new Marcadores();
                m.setUid(estacionSelected.getUid());
                databaseReference.child("Marcadores").child(m.getUid()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    public void mapa(View view){
        Intent intent2 = new Intent(this,MapsActivity.class);
        startActivity(intent2);
        finish();
    }
}



