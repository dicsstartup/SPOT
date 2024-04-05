package com.dicsstartup.spot.app.home;

import static android.content.ContentValues.TAG;
import static com.dicsstartup.spot.firebase.database.DomainSPOT.markerSpot;
import static com.dicsstartup.spot.firebase.database.DomainUser.getUser;
import static com.dicsstartup.spot.utils.InfoGeneral.usuario;
import static java.util.Objects.requireNonNull;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.spot.SpotActivity;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.dicsstartup.spot.utils.Permisos;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

/**
 * Fragmento utilizado para mostrar un mapa y realizar interacciones relacionadas con él y los spots.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    /**
     * Constructor público de la clase MapsFragment.
     * Se requiere un constructor público vacío para que el sistema pueda instanciar el fragmento.
     */
    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    GoogleMap mMap;
    int contador = 0;
    Button myLocation;
    Button search, google;
    LinearLayout containerLayout;
    CardView buscador;
    ImageView imagen, like, avatar;
    TextView tipo, nombre, creador, ciudad;
    AutocompleteSupportFragment autocomplete;
    Marker TuUbicacion;
    DomainSPOT domainSPOT = new DomainSPOT();
    Double longitude, latitude;
    LatLng tu = new LatLng(0, 0);
    FirebaseDatabase db;
    PlacesClient placesClient;


    /**
     * Método llamado para crear y devolver la vista del fragmento.
     * verifica los permisos de ubicacion
     * obtiene la instancia de el API GOOGLE PLACES
     * obtiene el la localizacion por gps del dispositivo
     * Inicia el Mapa
     *
     * @param inflater           El objeto LayoutInflater que se utiliza para inflar la vista del fragmento.
     * @param container          El contenedor padre en el que se debe inflar la vista del fragmento.
     * @param savedInstanceState Un objeto Bundle que contiene el estado guardado del fragmento.
     * @return La vista inflada del fragmento.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps_principal, container, false);
        containerLayout = view.findViewById(R.id.containerSpot);
        View customElementView = inflater.inflate(R.layout.lists_spot, containerLayout, false);
        containerLayout.addView(customElementView);
        like = containerLayout.findViewById(R.id.item_like);
        tipo = containerLayout.findViewById(R.id.item_tipo_spot);
        nombre = containerLayout.findViewById(R.id.item_name_spot);
        creador = containerLayout.findViewById(R.id.item_spot_user);
        ciudad = containerLayout.findViewById(R.id.item_ubic_spot);
        imagen = containerLayout.findViewById(R.id.item_image_spot);
        avatar = containerLayout.findViewById(R.id.item_avatar);
        buscador = view.findViewById(R.id.cardSearch);
        buscador.setVisibility(View.INVISIBLE);
        google = view.findViewById(R.id.GoogleSearchButton);
        google.setOnClickListener(v -> toggleCardView(false));
        containerLayout.setVisibility(View.INVISIBLE);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.add_maps_Fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.add_maps_Fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        myLocation = view.findViewById(R.id.maps_button_mylocation);
        myLocation.setOnClickListener(v -> {
            if(!ubic){
                location();
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(tu));});
        search = view.findViewById(R.id.maps_busqueda);
        if (!Places.isInitialized()) {
            Places.initialize(requireNonNull(getContext()).getApplicationContext(), "AIzaSyCgDieze8s74KUgk6elYA1Pv64N_-ZEE8M");
        }
        placesClient = Places.createClient(requireNonNull(getContext()));
        autocomplete = (AutocompleteSupportFragment) this.getChildFragmentManager().findFragmentById(R.id.autocomplete_activity);
        assert autocomplete != null;
        autocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mMap.addMarker(new MarkerOptions().position(requireNonNull(place.getLatLng())).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }
        });
        search.setOnClickListener(v -> {

        });
        return view;
    }


    /**
     * Método que oculta o muestra el buscador de GOOGLE PLACES
     *
     * @param isVisible booleano para conocer que hacer.
     */
    public void toggleLinearLayout(boolean isVisible) {
        if (isVisible) {
            // Animación al hacer invisible
            containerLayout.animate()
                    .translationX(containerLayout.getWidth())
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            containerLayout.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            // Animación al hacer visible
            containerLayout.setAlpha(0.0f);
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.animate()
                    .translationX(0)
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(null);
        }
    }
    /**
     * Método que oculta o muestra el cardview inferior
     * sobre la informacion basica del spot seleccionado
     *
     * @param isVisible booleano para conocer que hacer.
     */
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
    }


    /**
     * Método que conoce los click en el mapa
     *
     * @param latLng coordenadas en el mapa donde se cliquio
     */
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        toggleLinearLayout(true);
        toggleCardView(true);
    }

    /**
     * Método que muestra cardView de la informacion basica del spot
     * muestra si el usuario le ha dado like o no
     * da funcionalidad a boton de like
     * si se hace clic en la imagen te envia activity de Spot
     *
     * @param spot spot a mostrar.
     */
    public void mostrar(Spot spot) {
        nombre.setText(spot.getNombre());
        tipo.setText(spot.getTipoSpot());
        ciudad.setText(spot.getNombreCiudad());
        like.setImageResource(R.drawable.icon_not_like);
        if (spot.getLikes() != null) {
            if (spot.getLikes().contains(usuario.getId())) {
                like.setImageResource(R.drawable.icon_like);
            } else {
                like.setImageResource(R.drawable.icon_not_like);
            }
        } else {
            like.setImageResource(R.drawable.icon_not_like);
        }
        like.setOnClickListener(v -> {
            if (spot.getLikes().contains(usuario.getId())) {
                domainSPOT.deleteLikeSpot(db, usuario.getId(), spot, result -> {
                    if (result) {
                        like.setImageResource(R.drawable.icon_not_like);

                    } else {
                        like.setImageResource(R.drawable.icon_like);
                    }
                });

            } else {
                domainSPOT.addLikeSpot(db, usuario.getId(), spot, result -> {
                    if (result) {

                        like.setImageResource(R.drawable.icon_like);
                    } else {
                        like.setImageResource(R.drawable.icon_not_like);
                    }
                });
            }
        });
        Picasso.get().load(spot.getImagenes().get(0).getUri()).into(imagen);
        imagen.setOnClickListener(v -> {

            Intent intent = new Intent(this.getContext(), SpotActivity.class);
            intent.putExtra("spot",spot);
            this.startActivity(intent);
            toggleLinearLayout(true);
        });
        toggleLinearLayout(false);

        getUser(db, spot.getUsuarioCreador(), new UploadCallbackSpot() {
            @Override
            public void onUploadCompleteUser(Usuario result) {
                if (result != null) {
                    creador.setText(result.getNick());
                    Picasso.get().load(result.getAvatar()).into(avatar);

                } else {
                    creador.setText("??");
                }
            }

            @Override
            public void onUploadCompleteSpot(Spot spot) {
                // Aquí obtienes el objeto Spot
                // Realiza las acciones necesarias con el objeto Spot
            }

        });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (!isGranted) {
                            Toast.makeText(this.getContext(), "Ubicacion actual boqueada", Toast.LENGTH_LONG).show();
                        }
                    });


    /**
     * Método que se llama cuando el mapa está listo para ser utilizado.
     *   crea el listener para obtener el marker que es seleccionado
     *      mueve la camara a ese lugar y de la informacion guardada en el marker
     *      muestra el card view de spot
     *   muestra el lugar de la ubicacion del dispositivo
     *
     *
     * @param googleMap El objeto GoogleMap que representa el mapa.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        db = FirebaseDatabase.getInstance();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            String spot = (String) marker.getTag();
            if (spot != null) {
                domainSPOT.SpotById(db.getReference().child("Spot"), spot, new UploadCallbackSpot() {
                    @Override
                    public void onUploadCompleteUser(Usuario result) {

                    }

                    @Override
                    public void onUploadCompleteSpot(Spot spot) {
                        if (spot != null) {
                            mostrar(spot);
                        }
                    }

                });
            }
            return true;
        });
        @SuppressLint("ResourceType") boolean success = mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.jsonMapa)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
        this.mMap.setOnMapClickListener(this);
        this.mMap.setOnMapLongClickListener(this);
        TuUbicacion = mMap.addMarker(new MarkerOptions().position(tu).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location)).title("Tu ubicacion"));
        Permisos p = new Permisos();
        if(p.confirmarPermisos(this.getContext(),requestPermissionLauncher)){
            location();
        }
        informacion();
    }
     boolean ubic= false;

    /**
     * Método que con el uso de Gps
     *
     *encuentra la ubicacion del dispositivo actualizandose cada 10000 milisegundos
     *asi actualizando el marker de la ubicacion en el mapa
     *
     */
    @SuppressLint("MissingPermission")
    public void location() {
        @SuppressLint("UseRequireInsteadOfGet") FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireNonNull(getContext()));
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                // Obtiene la última ubicación conocida del dispositivo
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    tu = new LatLng(latitude, longitude);
                    TuUbicacion.setPosition(tu);
                    if (contador < 1) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(tu));
                        contador++;
                    }
                    ubic= true;
                }
            }
        }, Looper.getMainLooper());
    }



    public void informacion(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Spot");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    Spot spot=x.getValue(Spot.class);
                    assert spot != null;
                    spot.setId(x.getKey());
                    LatLng tu = new LatLng(spot.getLatitud(),spot.getLongitud());
                   requireNonNull(mMap.addMarker(new MarkerOptions().position(tu).title(spot.getNombre()).icon(BitmapDescriptorFactory.fromResource(markerSpot(spot.getTipoSpot())))))
                           .setTag(spot.getId());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Se llama cuando se produce un error
            }
        });
    }

}