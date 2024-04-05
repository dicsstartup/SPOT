package com.dicsstartup.spot.app.usuario;


import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.reuniones.ListReunionesFragment;
import com.dicsstartup.spot.app.spot.ListSpotsFragment;
import com.dicsstartup.spot.entities.Amistad;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.enumerados.EstadoAmistad;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
/**
 * Clase que representa la actividad de perfil de usuario.
 */
public class PerfilActivity extends AppCompatActivity {
    private TextView spotsPerfil;
    private TextView reunionesPerfil;
    private ShapeableImageView avatarPerfil;
    private ImageView buttonAdd;
    private TextView NickPerfil;
    private ImageView closePerfil;
    private TabLayout menuPerfil;
    private ViewPager2 pagerPerfil;
    public  static Usuario user;
    DomainUser domainUser= new DomainUser();
    DomainSPOT domainSPOT= new DomainSPOT();
    /**
     * Método onCreate llamado al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene los datos guardados de la instancia anterior de la actividad.
     */
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        user= getIntent().getSerializableExtra("user",Usuario.class);
        TextView amigosPerfil = findViewById(R.id.amigosPerfil);
        spotsPerfil = findViewById(R.id.spotsPerfil);
        reunionesPerfil = findViewById(R.id.reunionesPerfil);
        avatarPerfil = findViewById(R.id.avatarPerfil);
        buttonAdd = findViewById(R.id.imageView7);
        NickPerfil = findViewById(R.id.NickPerfil);
        closePerfil = findViewById(R.id.closePerfil);
        closePerfil.setOnClickListener(v -> finish());
        menuPerfil = findViewById(R.id.menuPerfil);
        pagerPerfil = findViewById(R.id.pagerPerfil);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ListSpotsFragment(user));
        fragments.add(new ListReunionesFragment(user,reunionesPerfil));
        DatabaseReference refSpot = FirebaseDatabase.getInstance().getReference("Spot");
        if(user!=null){
            domainSPOT.SpotByUser(refSpot,user.getId(),result -> {
                if(result!=null){
                    spotsPerfil.setText(""+result.size());
                }
            });
            NickPerfil.setText(user.getNick());
            Picasso.get().load(user.getAvatar()).into(avatarPerfil);
            if(user.amigos!=null){
                amigosPerfil.setText(""+(user.amigos.stream()
                        .filter(amistad -> amistad.getEstadoAmistad()==EstadoAmistad.ACEPTADA)
                        .count()));
            }
            if(user.getId().equals(usuario.getId())){
                buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.edit_perfil,null));
                buttonAdd.setOnClickListener(
                        v -> {
                            Intent i = new Intent(getApplicationContext(), EditUsuarioActivity.class);
                            startActivity(i);
                            finish();
                        }
                );
            }else{
                Amistad amistadEncontrada = usuario.getAmigos().stream()
                        .filter(amistad -> amistad.getIdAmigo().equals(user.getId()))
                        .findFirst()
                        .orElse(null);
                if(amistadEncontrada!=null){
                    if(amistadEncontrada.getEstadoAmistad()== EstadoAmistad.ACEPTADA){
                        buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.delete_friend,null));
                        buttonAdd.setOnClickListener(v -> delete());
                    }else if(amistadEncontrada.getEstadoAmistad()== EstadoAmistad.PENDIENTE){
                        buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.friend_wait,null));
                        buttonAdd.setOnClickListener(v -> requestDelete());
                    }else if (amistadEncontrada.getEstadoAmistad()== EstadoAmistad.RECHAZADA){
                        buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.decline_friend,null));
                    }
                }else {
                    buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.add_friend,null));
                    buttonAdd.setOnClickListener(v -> add());
                }

            }

           MyAdapter adapter = new MyAdapter(getSupportFragmentManager(), fragments);
           pagerPerfil.setAdapter(adapter);
            new TabLayoutMediator(menuPerfil, pagerPerfil,
                    (tab, position) -> {
                        // Aquí puedes configurar los textos o iconos de las pestañas
                        if (position == 0) {
                            tab.setText("Spots");
                        } else {
                            tab.setText("Reuniones");
                        }
                    }
            ).attach();
        }
    }
    /**
     * Clase interna que actúa como adaptador para los fragmentos de las pestañas del perfil.
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

    /**
     * Método para agregar un amigo.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    void add(){
        domainUser.solicitudAmistad(user.getId(), FirebaseDatabase.getInstance(), result -> {
            if(result){
                buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.friend_wait,null));
                buttonAdd.setOnClickListener(v->requestDelete());
            }
        });
    }

    /**
     * Método para eliminar a un amigo.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    void delete(){
        domainUser.EliminarAmistad(user.getId(), FirebaseDatabase.getInstance(), result -> {
            if(result){
                buttonAdd.setOnClickListener(v->add());
                buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.add_friend,null));
            }
        });
    }

    /**
     * Método para cancelar una solicitud de amistad.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    void requestDelete(){
        domainUser.EliminarAmistad(user.getId(), FirebaseDatabase.getInstance(), result -> {
            if(result){
                buttonAdd.setOnClickListener(v->add());
                buttonAdd.setImageDrawable(getResources().getDrawable(R.drawable.add_friend,null));
            }
        });
    }
}