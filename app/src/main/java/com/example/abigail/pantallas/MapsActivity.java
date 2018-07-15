package com.example.abigail.pantallas;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.style.BulletSpan;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    //instancias de localizacion con gps
    private Marker currentLocationMarket;
    private LatLng currentLocationLatLng;
    //llamado instancia para base de datos
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Inicia el llamado del gps y ubicarlo
        startGettingLocations();
        //Referecia a la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //llamado a los markets de ubicacion antiguos
        getMarkers();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng recibir = new LatLng(13.694373, -89.222934);
        mMap.addMarker(new MarkerOptions().position(recibir).title("Marcador en San salvador"));

        CameraPosition cameraPosition= new CameraPosition.Builder().zoom(15).target(recibir).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLocationChanged(Location location) {

        if (currentLocationMarket != null){
            currentLocationMarket.remove();
        }
        // agregar market

        currentLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(currentLocationLatLng);
        markerOptions.title("Posicion Actual");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarket = mMap.addMarker(markerOptions);

        CameraPosition cameraPosition= new CameraPosition.Builder().zoom(15).target(currentLocationLatLng).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Meter a base la locacion actual
        LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude());
        mDatabase.child("Location").child(String.valueOf(new Date().getTime())).setValue(locationData);

        Toast.makeText(this, "Localizacion Actualizada", Toast.LENGTH_SHORT).show();
        //llamado de markets antiguos y actualizarlos en el mapa
        getMarkers();
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted){
        ArrayList result= new ArrayList();

        for (String perm: wanted){
            if (!hasPermission(perm)){
                result.add(perm);
            }
        }

        return result;

    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Desactivado");
        alertDialog.setMessage("Activar el GPS? ");
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.show();
    }

    public void startGettingLocations(){
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATE = 100; //distancia en metros
        long MIN_TIME_BW_UPDATES= 1000*10; // tiempo en milisegundos

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //verifica si el gps o el network estan apagados asi encenderlos
        if (!isGPS && !isNetwork){
            showSettingAlert();
        }else{

            //verifica los permisos de todas las versionas
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (permissionsToRequest.size() > 0 ){
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }

        //verifica si el FINE LOCATION y COARSE hayan sido aceptados
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permiso Negado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (canGetLocation){
            if (isGPS){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATE,this);
            } else if(isNetwork){
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATE,this);
            }
        }else{
            Toast.makeText(this, "No es Posible Obtener a Localizacion", Toast.LENGTH_SHORT).show();
        }

    }

    private void getMarkers(){
        mDatabase.child("Location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //capturando el mapeo del usuario en datasnapshot
                if (dataSnapshot.getValue() != null){
                    getAllLocations((Map<String, Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllLocations(Map<String, Object> locations){

        for (Map.Entry<String, Object> entry: locations.entrySet()){

            Date newDate = new Date(Long.valueOf(entry.getKey()));
            Map singleLocation = (Map) entry.getValue();
            LatLng latlng = new LatLng((Double) singleLocation.get("latitud"), (Double) singleLocation.get("longitud"));
            addGreenMarker(newDate,latlng);
        }

    }

    private void addGreenMarker(Date newDate, LatLng latlng) {
        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        MarkerOptions markerOptions= new MarkerOptions();
        markerOptions.position(latlng);
        markerOptions.title(dt.format(newDate));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(markerOptions);
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
