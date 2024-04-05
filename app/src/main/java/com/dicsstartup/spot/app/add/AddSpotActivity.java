package com.dicsstartup.spot.app.add;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.dicsstartup.spot.app.add.InfoADD.image;
import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.ImagenSpot;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.utils.AdapterImagenes;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddSpotActivity extends AppCompatActivity {
    ArrayList<Uri> rutasImagenes;
    ImageView addImage;
    AdapterImagenes adapter;
    RecyclerView listFotos;
    Spinner spinner;
    AwesomeValidation validation;
    Button button , close;
    boolean permisosImages = false;
    EditText nombre, comentarios, tags;
    TextView  ciudad;
    AutocompleteSupportFragment autocomplete;
    PlacesClient placesClient;
    LinearLayout information;
    ConstraintLayout buscador;
    Spot spot;
    TextView titulo;
     boolean editar= false;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @SuppressLint("NotifyDataSetChanged")
    private final ActivityResultLauncher<Intent> myLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Uri imageUri = result.getData().getData();
                    rutasImagenes.add(imageUri);
                    adapter.notifyDataSetChanged();
                }
            }
    );
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"IntentReset", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot_inicial);
        spot= getIntent().getSerializableExtra("spot", Spot.class);
        buscador=findViewById(R.id.searchAPI);
        buscador.setVisibility(View.INVISIBLE);
        close=findViewById(R.id.closeButton);
        titulo=findViewById(R.id.tituloadd);
        spinner = findViewById(R.id.add_fragment_spinner);
        addImage = findViewById(R.id.add_image_button);
        listFotos = findViewById(R.id.add_recyclerview);
        button = findViewById(R.id.add_next_fragment);
        image = new ArrayList<>();
        rutasImagenes = new ArrayList<>();
        information=findViewById(R.id.linearLayout3);
        comentarios = findViewById(R.id.add_fragment_comentarios);
        tags = findViewById(R.id.add_fragment_tags);
        placesClient = Places.createClient(this);
        nombre = findViewById(R.id.add_fragment_nombre);
        ciudad = findViewById(R.id.add_fragment_ciudad);
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                permisosImages = true;
            } else {
                permisosImages = false;
                Toast.makeText(getApplicationContext(), "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        });
        autocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_activity);
        assert autocomplete != null;
        autocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        close.setOnClickListener(v -> {
            buscador.animate()
                    .alpha(0.0f)
                    .setDuration(500)
                    .withEndAction(() -> buscador.setVisibility(View.INVISIBLE));
            information.setVisibility(View.VISIBLE);
            information.setAlpha(0.0f);
            information.animate()
                    .alpha(1.0f)
                    .setDuration(500);
        });
        autocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
            }
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                ciudad.setText(place.getName());
                buscador.animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .withEndAction(() -> buscador.setVisibility(View.INVISIBLE));
                information.setVisibility(View.VISIBLE);
                information.setAlpha(0.0f);
                information.animate()
                        .alpha(1.0f)
                        .setDuration(500);
            }
        });
        List<String> elementos = new ArrayList<>();
        elementos.add("Elije el tipo");
        elementos.add("Bowls Park");
        elementos.add("DIY Park");
        elementos.add("Indoor");
        elementos.add("SPOT");
        elementos.add("Skate Park");
        elementos.add("Shop");
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_list, elementos);
        spinner.setAdapter(spinner_adapter);
        button.setOnClickListener(this::next);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        listFotos.setLayoutManager(layoutManager);
        adapter = new AdapterImagenes(rutasImagenes, getApplicationContext());
        listFotos.setAdapter(adapter);
        addImage.setOnClickListener(v -> {
            if (permisosImages) {
                if (rutasImagenes.size() < 5) {
                    @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/");
                    myLauncher.launch(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "ya no puedes añadir mas imagenes", Toast.LENGTH_LONG).show();
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        });

        ciudad.setOnClickListener(v -> {
            buscador.setVisibility(View.VISIBLE);
            buscador.setAlpha(0.0f);
            buscador.animate()
                    .alpha(1.0f)
                    .setDuration(500);
            information.animate()
                    .alpha(0.0f)
                    .setDuration(500)
                    .withEndAction(() -> information.setVisibility(View.INVISIBLE));
        });
        if(spot!=null){
            titulo.setText("Editar spot");
            nombre.setText(spot.getNombre());
            spinner.setSelection(spinner_adapter.getPosition(spot.getTipoSpot()));
            ciudad.setText(spot.getNombreCiudad());
            comentarios.setText(spot.getDescripcion());
            tags.setText(String.join(" ", spot.getTags().toArray(new String[0])));
            rutasImagenes.addAll(spot.getImagenes().stream()
                    .map(ImagenSpot::getUri)
                    .map(Uri::parse)
                    .collect(Collectors.toList()));
            editar=true;
        }else{
            spot= new Spot();
        }
        validation = new AwesomeValidation(BASIC);
        validation.addValidation(this, R.id.add_fragment_nombre, ".{3,}", R.string.invalid);
        validation.addValidation(this, R.id.add_fragment_ciudad, ".{3,}", R.string.invalid);
        validation.addValidation(this, R.id.add_fragment_comentarios, ".{3,}", R.string.invalid);
        validation.addValidation(this, R.id.add_fragment_tags, "(#\\w+\\s*)+", R.string.tags);
    }
    public void next(View view) {
        if (!spinner.getSelectedItem().toString().equals("Elije el tipo")) {
            if (rutasImagenes.size() > 0) {
                if (validation.validate()) {
                    spot.setNombre(nombre.getText().toString());
                    spot.setTipoSpot(spinner.getSelectedItem().toString());
                    spot.setNombreCiudad(ciudad.getText().toString());
                    spot.setDescripcion(comentarios.getText().toString());
                    spot.setTags(new ArrayList<>(Arrays.asList(tags.getText().toString().split("\\s+"))));
                    spot.setUsuarioCreador(usuario.getId());
                    image = rutasImagenes;
                    Intent intent = new Intent(this.getApplicationContext(), AddSpotMapActivity.class);
                    intent.putExtra("spot",spot);
                    intent.putExtra("editar",editar);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "añada una foto", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "seleccione un Tipo de Spot", Toast.LENGTH_LONG).show();
        }
    }
}