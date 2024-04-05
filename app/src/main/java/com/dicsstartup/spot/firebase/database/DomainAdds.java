package com.dicsstartup.spot.firebase.database;

import androidx.annotation.NonNull;

import com.dicsstartup.spot.entities.Adds;
import com.dicsstartup.spot.interfaz.UploadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Clase que contiene métodos para el dominio de los Adds (anuncios).
 */
public class DomainAdds {

    /**
     * Obtiene una lista de Adds desde una referencia de la base de datos.
     *
     * @param ref            Referencia a la base de datos.
     * @param adds           Lista de Adds donde se almacenarán los datos obtenidos.
     * @param uploadCallback Callback para manejar el resultado de la operación.
     */
    public static void listAdds(DatabaseReference ref, ArrayList<Adds> adds, UploadCallback uploadCallback) {
        DatabaseReference dataref = ref;
        dataref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot x : snapshot.getChildren()) {
                    Adds add = x.getValue(Adds.class);
                    adds.add(add);
                }
                uploadCallback.onUploadComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                uploadCallback.onUploadComplete(false);
            }
        });
    }
}

