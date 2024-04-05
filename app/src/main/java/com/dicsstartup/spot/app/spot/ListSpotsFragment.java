package com.dicsstartup.spot.app.spot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.utils.AdapterSpotList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Fragmento que muestra una lista de spots.
 */
public class ListSpotsFragment extends Fragment {

    public ArrayList<Spot> spots = new ArrayList<>();  // Lista de spots
    public AdapterSpotList adapter;  // Adaptador para la lista de spots
    public Usuario user;  // Usuario actual
    ListView lista;  // ListView que muestra la lista de spots

    /**
     * Constructor vacío requerido para el fragmento.
     */
    public ListSpotsFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor que recibe el usuario actual.
     *
     * @param user Usuario actual
     */
    public ListSpotsFragment(Usuario user) {
        // Required empty public constructor
        this.user = user;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_spots, container, false);

        lista = view.findViewById(R.id.list_spots_list);
        adapter = new AdapterSpotList(getContext(), spots);
        lista.setAdapter(adapter);

        DomainSPOT domainSPOT = new DomainSPOT();
        DatabaseReference refSpot = FirebaseDatabase.getInstance().getReference("Spot");

        if (user != null) {
            // Cargar los spots del usuario y actualizar el adaptador
            domainSPOT.SpotByUser(refSpot, user.getId(), spots, adapter);
        }

        lista.setOnItemClickListener((parent, view1, position, id) -> {
            // Manejar el evento de clic en un spot de la lista
        });

        // Inflate the layout for this fragment
        return view;
    }
}
