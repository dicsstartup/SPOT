package com.dicsstartup.spot.app.reuniones;

import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Reunion;
import com.dicsstartup.spot.firebase.database.DomainReuniones;
import com.dicsstartup.spot.utils.AdapterReunion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Actividad que muestra la lista de reuniones para el usuario actual.
 */
public class MisReunionesActivity extends AppCompatActivity {

    public ArrayList<Reunion> reunions = new ArrayList<>();  // Lista de reuniones
    public AdapterReunion adapter;  // Adaptador para la lista de reuniones
    RecyclerView lista;  // RecyclerView que muestra la lista de reuniones
    ImageView close;  // Botón de cierre

    /**
     * Método que se ejecuta al crear la actividad.
     *  Cargar las reuniones del usuario y actualizar el adaptador
     * @param savedInstanceState Objeto Bundle que contiene los datos guardados de la actividad (si los hay)
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reuniones);

        lista = findViewById(R.id.listMisReuniones);
        close = findViewById(R.id.closeReuniones);

        close.setOnClickListener(v -> finish());

        LinearLayoutManager layoutManagerReunion = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        lista.setLayoutManager(layoutManagerReunion);

        adapter = new AdapterReunion(this, reunions, true);
        lista.setAdapter(adapter);

        DomainReuniones domainReuniones = new DomainReuniones();
        DatabaseReference refSpot = FirebaseDatabase.getInstance().getReference("Reunion");

        if (usuario != null) {
            // Cargar las reuniones del usuario y actualizar el adaptador
            domainReuniones.ReunionByUserAsistencia(refSpot, usuario.getId(), reunions, adapter);
        }
    }
}