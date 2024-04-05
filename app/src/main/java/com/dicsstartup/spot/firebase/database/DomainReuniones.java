package com.dicsstartup.spot.firebase.database;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dicsstartup.spot.entities.Reunion;
import com.dicsstartup.spot.interfaz.UploadCallback;
import com.dicsstartup.spot.utils.AdapterReunion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;


/**
 * Clase que contiene métodos para el dominio de las reuniones.
 */
public class DomainReuniones {

    /**
     * Agrega una reunión a la base de datos.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param reunion  La reunión a agregar.
     * @param callback Callback para manejar el resultado de la operación.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addReunion(FirebaseDatabase db, Reunion reunion, UploadCallback callback) {
        DatabaseReference ref = db.getReference(Reunion.class.getSimpleName());
        String hijoKey = ref.push().getKey();
        assert hijoKey != null;
        ref.child(hijoKey).setValue(reunion).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Obtiene las reuniones creadas por un usuario específico.
     *
     * @param databaseReference Referencia a la base de datos.
     * @param id               ID del usuario.
     * @param reuniones        Lista de reuniones donde se almacenarán los datos obtenidos.
     * @param adapterReunion   Adaptador de reuniones para actualizar la interfaz de usuario.
     * @param textView         TextView donde se mostrará el número de reuniones.
     */
    public void ReunionByUser(DatabaseReference databaseReference, String id, ArrayList<Reunion> reuniones, AdapterReunion adapterReunion, TextView textView) {
        // Realiza la consulta a la base de datos para obtener las reuniones del usuario
        Query query = databaseReference.orderByChild("idCreador").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verifica si el número de reuniones en la base de datos es menor al número de reuniones en la lista actual
                if (StreamSupport.stream(Spliterators.spliteratorUnknownSize(dataSnapshot.getChildren().iterator(), Spliterator.ORDERED), false).count() < reuniones.size()) {
                    // Limpia la lista de reuniones y notifica al adaptador para actualizar la interfaz
                    reuniones.clear();
                    adapterReunion.notifyDataSetChanged();
                    for (DataSnapshot x : dataSnapshot.getChildren()) {
                        Reunion reunion = x.getValue(Reunion.class);
                        Objects.requireNonNull(reunion).setId(x.getKey());
                        reuniones.add(reunion);
                        adapterReunion.notifyDataSetChanged();
                    }
                    textView.setText(reuniones.size() + "");
                } else {
                    // Agrega las nuevas reuniones a la lista y notifica al adaptador para actualizar la interfaz
                    for (DataSnapshot x : dataSnapshot.getChildren()) {
                        Reunion reunion = x.getValue(Reunion.class);
                        Objects.requireNonNull(reunion).setId(x.getKey());
                        if (!reunion.existeEnLista(reuniones)) {
                            reuniones.add(reunion);
                            adapterReunion.notifyDataSetChanged();
                        }
                    }
                    textView.setText(reuniones.size() + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "error al iniciar", error.toException());
            }
        });
    }

    /**
     * Obtiene las reuniones asociadas a un spot específico.
     *
     * @param databaseReference Referencia a la base de datos.
     * @param id               ID del spot.
     * @param reuniones        Lista de reuniones donde se almacenarán los datos obtenidos.
     * @param adapterReunion   Adaptador de reuniones para actualizar la interfaz de usuario.
     */
    public void ReunionBySpot(DatabaseReference databaseReference, String id, ArrayList<Reunion> reuniones, AdapterReunion adapterReunion) {
        // Realiza la consulta a la base de datos para obtener las reuniones asociadas al spot
        Query query = databaseReference.orderByChild("idSpot").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verifica si el número de reuniones en la base de datos es menor al número de reuniones en la lista actual
                if (StreamSupport.stream(Spliterators.spliteratorUnknownSize(dataSnapshot.getChildren().iterator(), Spliterator.ORDERED), false).count() < reuniones.size()) {
                    // Limpia la lista de reuniones y notifica al adaptador para actualizar la interfaz
                    reuniones.clear();
                    adapterReunion.notifyDataSetChanged();
                    for (DataSnapshot x : dataSnapshot.getChildren()) {
                        Reunion reunion = x.getValue(Reunion.class);
                        assert reunion != null;
                        reunion.setId(x.getKey());
                        reuniones.add(reunion);
                        adapterReunion.notifyDataSetChanged();
                    }
                } else {
                    // Agrega las nuevas reuniones a la lista y notifica al adaptador para actualizar la interfaz
                    for (DataSnapshot x : dataSnapshot.getChildren()) {
                        Reunion reunion = x.getValue(Reunion.class);
                        assert reunion != null;
                        reunion.setId(x.getKey());
                        if (!reunion.existeEnLista(reuniones)) {
                            reuniones.add(reunion);
                            adapterReunion.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "error al iniciar", error.toException());
            }
        });
    }

    /**
     * Agrega un usuario como asistente a una reunión.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param user     ID del usuario.
     * @param reunion  La reunión a la que se agregará el asistente.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void addAsistencia(FirebaseDatabase db, String user, Reunion reunion, UploadCallback callback) {
        DatabaseReference ref = db.getReference("Reunion").child(reunion.getId());
        if (reunion.getAsistencias() != null) {
            reunion.getAsistencias().add(user);
        } else {
            reunion.setAsistencias(new ArrayList<>());
            reunion.getAsistencias().add(user);
        }
        ref.setValue(reunion).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Elimina un usuario de la lista de asistentes de una reunión.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param user     ID del usuario a eliminar.
     * @param reunion  La reunión de la que se eliminará el asistente.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void deleteAsistencia(FirebaseDatabase db, String user, Reunion reunion, UploadCallback callback) {
        DatabaseReference ref = db.getReference("Reunion").child(reunion.getId());
        reunion.getAsistencias().remove(user);
        ref.setValue(reunion).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Elimina una reunión de la base de datos.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param reunion  La reunión a eliminar.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void deleteReunion(@NonNull FirebaseDatabase db, Reunion reunion, UploadCallback callback) {
        DatabaseReference ref = db.getReference("Reunion").child(reunion.getId());
        ref.removeValue().addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Obtiene las reuniones en las que un usuario es el creador o asistente.
     *
     * @param databaseReference Referencia a la base de datos.
     * @param id               ID del usuario.
     * @param reuniones        Lista de reuniones donde se almacenarán los datos obtenidos.
     * @param adapterReunion   Adaptador de reuniones para actualizar la interfaz de usuario.
     */
    public void ReunionByUserAsistencia(DatabaseReference databaseReference, String id, ArrayList<Reunion> reuniones, AdapterReunion adapterReunion) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    String creador = x.child("idCreador").getValue(String.class);
                    assert creador != null;
                    if (creador.equals(id)) {
                        Reunion reunion = x.getValue(Reunion.class);
                        assert reunion != null;
                        reunion.setId(x.getKey());
                        if (!reunion.existeEnLista(reuniones)) {
                            reuniones.add(reunion);
                            adapterReunion.notifyDataSetChanged();
                        }
                    } else {
                        DataSnapshot asistencias = x.child("asistencias");
                        for (DataSnapshot asiste : asistencias.getChildren()) {
                            String UsuarioId = asiste.getValue(String.class);

                            if (UsuarioId.equals(id)) {
                                Reunion reunion = x.getValue(Reunion.class);

                                reunion.setId(x.getKey());
                                if (!reunion.existeEnLista(reuniones)) {
                                    reuniones.add(reunion);
                                    adapterReunion.notifyDataSetChanged();
                                }
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "error al iniciar", error.toException());
            }
        });
    }
}

