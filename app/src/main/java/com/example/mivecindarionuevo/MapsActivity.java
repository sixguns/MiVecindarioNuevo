package com.example.mivecindarionuevo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.mivecindarionuevo.modelos.Hogar;
import com.example.mivecindarionuevo.modelos.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    String nmUsuario, apUsuario;


    Usuario usuarioActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cargarPreferencias();
        inicializarFirebase();


    }



    private void inicializarFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void cargarPreferencias() {
        SharedPreferences preferencias = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        nmUsuario = preferencias.getString("nombreUsuario","NoSesion");
        apUsuario = preferencias.getString("apellidoUsuario","NoSesion");
    }

    public void ingresarEvento (View v){
        Intent intent = new Intent(this,ingresarEvento.class);
        startActivity(intent);

    }

    public void misDatos (View v){
        Intent intent = new Intent(this,datosUsuario.class);
        startActivity(intent);

    }

    public void miHogar (View v){
        Intent intent = new Intent(this,editarHogar.class);
        startActivity(intent);

    }

    public void cerrarSesion (View v){
        SharedPreferences preferecias = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferecias.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this,iniciarSesion.class);
        startActivity(intent);
        finish();
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMinZoomPreference(17.0f);
        agregarMarcadores(googleMap);
        // Add a marker in Sydney and move the camera
    }


    public void agregarMarcadores(GoogleMap googleMap){

        mMap = googleMap;

        databaseReference.child("Usuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Usuario u = objSnapshot.getValue(Usuario.class);
                    if (u.getNombre().equals(nmUsuario) && u.getApellido().equals(apUsuario)){
                            usuarioActual = u;
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        databaseReference.child("Hogar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot : dataSnapshot.getChildren()){
                    Hogar h = objSnapshot.getValue(Hogar.class);

                    if (usuarioActual.getHogar().getVecindario().getNombre().equals(h.getVecindario().getNombre())){

                        double latitud = Double.parseDouble(h.getLatitud());
                        double longitud = Double.parseDouble(h.getLongitud());
                        LatLng padreHurtado = new LatLng(latitud,longitud);
                        mMap.addMarker(new MarkerOptions().position(padreHurtado).title(h.getComentario()+" "+h.getDireccion()+" "+h.getNombre()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_casa_round)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(padreHurtado));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
