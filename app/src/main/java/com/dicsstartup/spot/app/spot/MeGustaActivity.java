package com.dicsstartup.spot.app.spot;

import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.utils.AdapterSpotList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Actividad que muestra los spots que han sido marcados como "Me gusta".
 */
public class MeGustaActivity extends AppCompatActivity {

    public ArrayList<Spot> spots = new ArrayList<>();  // Lista de spots marcados como "Me gusta"
    public AdapterSpotList adapter;  // Adaptador para la lista de spots
    ListView lista;  // ListView que muestra la lista de spots
    ImageView close;  // Botón de cierre de la actividad

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_gusta);

        lista = findViewById(R.id.meGusta_list);
        close = findViewById(R.id.closeMeGusta);

        close.setOnClickListener(v -> finish());

        adapter = new AdapterSpotList(this, spots);
        lista.setAdapter(adapter);

        DomainSPOT domainSPOT = new DomainSPOT();
        DatabaseReference refSpot = FirebaseDatabase.getInstance().getReference("Spot");

        if (usuario != null) {
            // Cargar los spots marcados como "Me gusta" por el usuario y actualizar el adaptador
            domainSPOT.SpotByUserLikes(refSpot, usuario.getId(), spots, adapter);
        }
    }
}
