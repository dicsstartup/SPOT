package com.dicsstartup.spot.app.home;

import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.add.AddSpotActivity;
import com.dicsstartup.spot.app.mensajes.ChatsActivity;
import com.dicsstartup.spot.app.reuniones.MisReunionesActivity;
import com.dicsstartup.spot.app.spot.MeGustaActivity;
import com.dicsstartup.spot.app.usuario.NotificacionActivity;
import com.dicsstartup.spot.app.usuario.PerfilActivity;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.dicsstartup.spot.utils.Permisos;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Actividad principal de la aplicación que muestra la interfaz de inicio y las funcionalidades principales.
 * dando acceso al resto de apartados
 */
public class HomeActivity extends AppCompatActivity {
    ShapeableImageView home_avatar;
    BottomNavigationView bottomNavigationView;
    static ViewPager2 viewPager;
    DomainUser domainUser;
    PopupMenu popupMenu;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (!isGranted) {
                            Toast.makeText(getApplicationContext(), "Ubicacion actual bloqueada", Toast.LENGTH_LONG).show();
                        }
                    });

    /**
     * Método llamado al crear la actividad.
     * resive como extras el email el password y el booleano para saber si se guarda o no
     * al iniciar si esta configurado guarga una propiedad con el usuario y la contraseña para no tener que loguer al iniciar
     * solicita permisos de lo calizacion y pide wque se encienda en gps
     * crea los fregments de adds mapa princiapal y shop agregandolos al viewpager central
     * da funcionalidad a barra de navegacion inferior
     * crear el menu de la parte superior
     *
     *
     * @param savedInstanceState el estado previamente guardado de la actividad, o nulo si no hay ninguno
     */
    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String email;
        String pass;
        domainUser = new DomainUser();
        email=getIntent().getStringExtra("email");
        pass=getIntent().getStringExtra("pass");
        boolean cheak= getIntent().getBooleanExtra("cheak",false);
        boolean registrado= getIntent().getBooleanExtra("registro",false);
        home_avatar = findViewById(R.id.home_avatar);
        bottomNavigationView = findViewById(R.id.home_navegationMenu);
        home_avatar.setOnClickListener(v -> showMenu());
        usuario = new Usuario();
        if(registrado){
            guardarProps(email, pass,  cheak);
            domainUser.userByEmail(FirebaseDatabase.getInstance().getReference("Usuario"), email, new UploadCallbackSpot() {
                @Override
                public void onUploadCompleteUser(Usuario result) {
                    usuario = result;
                    Picasso.get().load(result.getAvatar()).into(home_avatar);
                }

                @Override
                public void onUploadCompleteSpot(Spot spot) {

                }
            });
        }
        else if (cheak) {
            guardarProps(email, pass,  cheak);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Inicio correcto", Toast.LENGTH_SHORT);
                    toast.show();
                    domainUser.userByEmail(FirebaseDatabase.getInstance().getReference("Usuario"), email, new UploadCallbackSpot() {
                        @Override
                        public void onUploadCompleteUser(Usuario result) {
                            usuario = result;
                            Picasso.get().load(result.getAvatar()).into(home_avatar);
                        }

                        @Override
                        public void onUploadCompleteSpot(Spot spot) {

                        }
                    });
                } else {
                    SharedPreferences pref = getSharedPreferences(getResources().getString(R.string.SPOT_pref), MODE_PRIVATE);
                    SharedPreferences.Editor editorr = pref.edit();
                    editorr.clear();
                    editorr.apply();
                    finish();
                }
            });
        }

        Permisos p = new Permisos();
        p.confirmarPermisos(this, requestPermissionLauncher);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(new LocationRequest.Builder(9000).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        ((Task<?>) task).addOnSuccessListener(this, locationSettingsResponse -> {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Por favor, activa la ubicación", Toast.LENGTH_SHORT).show();
            }
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

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MapsFragment());
        fragments.add(new AddsFragment());
        fragments.add(new ShopFragment());

        viewPager = findViewById(R.id.home_viewpager);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.spots:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.chats:
                    Intent chat = new Intent(getApplicationContext(), ChatsActivity.class);
                    startActivity(chat);
                    return true;
                case R.id.adds:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.store:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.newSpot:
                    Intent i = new Intent(getApplicationContext(), AddSpotActivity.class);
                    startActivity(i);
                    return true;
            }
            return false;
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.spots);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.adds);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.store);
                        break;
                }
            }
        });
    }

    /**
     * Método utilizado para mostrar el menú emergente al hacer clic en el avatar de inicio.
     * a cada item se la da una accion
     * los 4 primeros crean nuevas actividades
     * cerrar seccion borra si existe el archivo de props y finaliza el activity
     */
    @SuppressLint("NonConstantResourceId")
    private void showMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuStyle);
        popupMenu = new PopupMenu(wrapper, home_avatar); // Crea una instancia de PopupMenu
        popupMenu.getMenuInflater().inflate(R.menu.menu_options, popupMenu.getMenu()); // Infla el menú desde un archivo XML

        // Configura un listener para manejar la selección de las opciones del menú
        popupMenu.setOnMenuItemClickListener(item -> {
            // Aquí se ejecutará el código correspondiente a cada opción del menú seleccionada
            switch (item.getItemId()) {
                case R.id.option1:
                    Intent i = new Intent(getApplicationContext(), PerfilActivity.class);
                    i.putExtra("user", usuario);
                    startActivity(i);
                    return true;
                case R.id.option2:
                    Intent notificaciones = new Intent(getApplicationContext(), NotificacionActivity.class);
                    startActivity(notificaciones);
                    return true;
                case R.id.option3:
                    Intent reuniones = new Intent(getApplicationContext(), MisReunionesActivity.class);
                    startActivity(reuniones);
                    return true;
                case R.id.option4:
                    Intent likes = new Intent(getApplicationContext(), MeGustaActivity.class);
                    startActivity(likes);
                    return true;
                case R.id.option5:
                    SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.SPOT_pref), MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("email");
                    editor.remove("pass");
                    editor.apply();
                    finish();
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show(); // Muestra el menú emergente
    }

    public void guardarProps(String email, String pass, boolean cheak){
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.SPOT_pref), MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("pass", pass);
        editor.putBoolean("cheak",cheak);
        editor.apply();
    }
    @Override
    public void onBackPressed() {
        // No hacer nada al presionar el botón de retroceso
        super.onBackPressed();
    }

    /**
     * Adaptador personalizado para el ViewPager2 que muestra los fragmentos.
     * teniedo una lista de fragmets
     * mostrandolos de manera ordenada y proporcionando de metodos para su
     * visualizacion
     */
    public class MyAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments;

        public MyAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager, getLifecycle());
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}