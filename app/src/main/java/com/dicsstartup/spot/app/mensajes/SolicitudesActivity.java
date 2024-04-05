package com.dicsstartup.spot.app.mensajes;

import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Amistad;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.dicsstartup.spot.utils.AdapterSolicitudes;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SolicitudesActivity extends AppCompatActivity {

    public ArrayList<Amistad> solicitudes= new ArrayList<>();
    public AdapterSolicitudes adapter;
    RecyclerView lista;
    ImageView close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes);
        close=findViewById(R.id.closeSolicitud);
        lista=findViewById(R.id.notificaciones_list);
        close.setOnClickListener(v->finish());
        LinearLayoutManager layoutManagerReunion = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        lista.setLayoutManager(layoutManagerReunion);
        adapter = new AdapterSolicitudes(this, solicitudes);
        lista.setAdapter(adapter);
        DomainUser domainUser= new DomainUser();
        domainUser.getAmistades(FirebaseDatabase.getInstance(),usuario.getId(),solicitudes,adapter);


    }
}