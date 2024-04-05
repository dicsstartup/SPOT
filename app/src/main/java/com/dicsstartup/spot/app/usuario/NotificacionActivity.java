package com.dicsstartup.spot.app.usuario;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Notificacion;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.dicsstartup.spot.utils.AdapterNotificaciones;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
/**
 * La clase NotificacionActivity es una actividad de Android que muestra una lista de notificaciones.
 * Se utiliza un RecyclerView y un AdapterNotificaciones para mostrar las notificaciones en la interfaz de usuario.
 */
public class NotificacionActivity extends AppCompatActivity {

    /**
     * Lista de notificaciones.
     */
    public ArrayList<Notificacion> notificaciones = new ArrayList<>();

    /**
     * Adaptador para enlazar los datos de las notificaciones con la vista de la lista.
     */
    public AdapterNotificaciones adapter;

    /**
     * RecyclerView que muestra la lista de notificaciones.
     */
    RecyclerView lista;

    /**
     * ImageView para cerrar la actividad.
     */
    ImageView close;

    /**
     * Método onCreate llamado al crear la actividad.
     *
     * @param savedInstanceState Objeto Bundle que contiene los datos guardados de la instancia anterior de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion);

        // Obtener referencias a los elementos de la interfaz
        lista = findViewById(R.id.notificaciones_list);
        close = findViewById(R.id.closeNotificacion);
        close.setOnClickListener(v -> finish());

        // Configurar el LinearLayoutManager para el RecyclerView
        LinearLayoutManager layoutManagerReunion = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        lista.setLayoutManager(layoutManagerReunion);

        // Crear el adaptador y asignarlo al RecyclerView
        adapter = new AdapterNotificaciones(this, notificaciones);
        lista.setAdapter(adapter);

        // Obtener las notificaciones de amistad desde la base de datos y actualizar el adaptador
        DomainUser domainUser = new DomainUser();
        domainUser.NotificacionAmistad(FirebaseDatabase.getInstance(), notificaciones, adapter);
    }
}