package com.dicsstartup.spot.app.reuniones;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Reunion;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainReuniones;
import com.dicsstartup.spot.utils.AdapterReunion;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Fragmento que muestra una lista de reuniones.
 */
public class ListReunionesFragment extends Fragment {

    public ArrayList<Reunion> reunions = new ArrayList<>();
    public AdapterReunion adapter;
    Usuario user;
    RecyclerView lista;
    TextView textView;

    /**
     * Constructor público vacío requerido por la instancia del fragmento.
     */
    public ListReunionesFragment() {
        // Required empty public constructor
    }

    /**
     * Constructor que acepta un objeto Usuario y un TextView como parámetros.
     * este se utiliza en el perfil
     *
     * @param user    El objeto Usuario.
     * @param numero  El TextView para mostrar el número de reuniones.
     */
    public ListReunionesFragment(Usuario user, TextView numero) {
        this.user = user;
        this.textView = numero;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     *obtiene la lista usando la clase DomainReuniones y la añade a el adaptador
     *
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_reuniones, container, false);
        lista = view.findViewById(R.id.list_reuniones_list);
        LinearLayoutManager layoutManagerReunion = new LinearLayoutManager(getContext().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        lista.setLayoutManager(layoutManagerReunion);
        adapter = new AdapterReunion(getContext(), reunions, true);
        lista.setAdapter(adapter);
        DomainReuniones domainReuniones = new DomainReuniones();
        DatabaseReference refSpot = FirebaseDatabase.getInstance().getReference("Reunion");
        if (user != null) {
            domainReuniones.ReunionByUser(refSpot, user.getId(), reunions, adapter, textView);
        }

        return view;
    }
}
