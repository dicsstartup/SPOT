package com.dicsstartup.spot.app.add;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class AddSpotMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    GoogleMap mMap;
    double longitude;
    double latitude;
    Button myLocation, google;
    CardView buscador;
    AutocompleteSupportFragment autocomplete;
    PlacesClient placesClient;
    Button finalizar;

    int contador = 0;
    String[] permisos = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };
    EditText longitud;
    EditText latitud;
    Marker TuUbicacion;
    Marker ubicacionSelecionada;
    Spot nuevo;
    boolean editar;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {

                        } else {
                            Toast.makeText(getApplicationContext(), "Ubicacion actual boqueada", Toast.LENGTH_LONG).show();
                        }
                    });

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"MissingPermission", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot_map);
        nuevo=getIntent().getSerializableExtra("spot",Spot.class);
        editar=getIntent().getBooleanExtra("editar",false);
        confirmarPermisos();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCgDieze8s74KUgk6elYA1Pv64N_-ZEE8M");
        }
        finalizar = findViewById(R.id.addSpotButton);
        buscador=findViewById(R.id.cardSearch);
        buscador.setVisibility(View.INVISIBLE);
        longitud = findViewById(R.id.longitud);
        latitud = findViewById(R.id.latitud);
        longitud.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        latitud.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        myLocation = findViewById(R.id.maps_button_mylocation);
        myLocation.setOnClickListener(v -> mMap.moveCamera(CameraUpdateFactory.newLatLng(TuUbicacion.getPosition())));
        google=findViewById(R.id.googleSearchButton);
        google.setOnClickListener(v -> toggleCardView(false));
        placesClient = Places.createClient(this);
        autocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_activity);
        autocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.add_maps_Fragment2);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        autocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }
        });
        finalizar.setOnClickListener(v -> {
            if (ubicacionSelecionada != null) {
                finalizar.setEnabled(false);
                nuevo.setLatitud(ubicacionSelecionada.getPosition().latitude);
                nuevo.setLongitud(ubicacionSelecionada.getPosition().longitude);
                Intent intent = new Intent(getApplicationContext(), AddSpotSubidaActivity.class);
                intent.putExtra("spot",nuevo);
                intent.putExtra("editar",editar);
                startActivity(intent);
                finish();
            }
        });

        longitud.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método se llama antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se llama cuando el texto está cambiando.
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    LatLng ubic = ubicacionSelecionada.getPosition();
                    LatLng ubicM = new LatLng(ubic.latitude, Double.parseDouble(s.toString()));
                    ubicacionSelecionada.setPosition(ubicM);
                }

            }
        });
        latitud.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método se llama antes de que el texto cambie.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se llama cuando el texto está cambiando.
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    LatLng ubic = ubicacionSelecionada.getPosition();
                    LatLng ubicM = new LatLng(Double.parseDouble(s.toString()), ubic.latitude);
                    ubicacionSelecionada.setPosition(ubicM);
                }

            }
        });

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest.Builder(1000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        ((Task<?>) task).addOnSuccessListener(this, locationSettingsResponse -> {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Por favor, activa la ubicación", Toast.LENGTH_SHORT).show();
                return;
            }
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();
            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // Obtiene la última ubicación conocida del dispositivo
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        // La ubicación está disponible, haz algo con ella
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        // Imprime la ubicación en la consola
                        Log.d("Mi ubicación", "Latitud: " + latitude + ", Longitud: " + longitude);

                        LatLng position = new LatLng(latitude, longitude);
                        TuUbicacion.setPosition(position);
                        if (contador < 1) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                            contador++;
                        }
                    }
                }
            }, Looper.getMainLooper());

        });
        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this, 12354);
                } catch (IntentSender.SendIntentException ignored) {
                }
            }
        });
    }

    public void toggleCardView(boolean isVisible) {
        if (isVisible) {
            // Animación al hacer invisible
            buscador.animate()
                    .translationX(-buscador.getWidth())
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            buscador.setVisibility(View.INVISIBLE);
                            google.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            // Animación al hacer visible
            buscador.setAlpha(0.0f);
            buscador.setVisibility(View.VISIBLE);
            buscador.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(null);
            google.setVisibility(View.INVISIBLE);
        }
        isVisible = !isVisible;
    }

    public void confirmarPermisos() {
        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(this, permiso)
                    != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no ha sido concedido, solicítalo
                requestPermissionLauncher.launch(permiso);
            }
        }
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        LatLng position = new LatLng(latLng.latitude, latLng.longitude);
        longitud.setText("" + position.longitude);
        latitud.setText("" + position.latitude);
        ubicacionSelecionada.setPosition(position);
        toggleCardView(true);
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        @SuppressLint("ResourceType") boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.jsonMapa)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);
        LatLng tu = new LatLng(latitude, longitude);
        TuUbicacion = mMap.addMarker(new MarkerOptions().position(tu).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location)).title("Tu ubicacion"));
        ubicacionSelecionada = mMap.addMarker(new MarkerOptions().position(tu).title("Spot").draggable(true).icon(BitmapDescriptorFactory.fromResource(DomainSPOT.markerSpot(nuevo.getTipoSpot()))));
        if(editar){
            ubicacionSelecionada.setPosition(new LatLng(nuevo.getLongitud(),nuevo.getLatitud()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(nuevo.getLongitud(),nuevo.getLatitud())));
            longitud.setText("" + nuevo.getLongitud());
            latitud.setText("" + nuevo.getLatitud());
        }
    }

}