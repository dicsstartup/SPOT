package com.dicsstartup.spot.firebase.database;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.interfaz.UploadCallback;
import com.dicsstartup.spot.interfaz.UploadCallbackSPOTs;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.dicsstartup.spot.utils.AdapterSpotList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class DomainSPOT {

    /**
     * Agrega un spot a la base de datos.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param spot     El spot a agregar.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void addSpot(FirebaseDatabase db, Spot spot, UploadCallback callback) {
        DatabaseReference ref = db.getReference(Spot.class.getSimpleName());
        String hijoKey = ref.push().getKey();
        // Agrega el hijo a la base de datos y obtén la tarea correspondiente
        assert hijoKey != null;
        ref.child(hijoKey).setValue(spot).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Actualiza un spot en la base de datos.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param spot     El spot a actualizar.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void actualizarSpot(FirebaseDatabase db, Spot spot, UploadCallback callback) {
        DatabaseReference ref = db.getReference(Spot.class.getSimpleName());
        ref.child(spot.getId()).setValue(spot).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Obtiene el ID del recurso de imagen correspondiente al tipo de spot.
     *
     * @param tipoSpot El tipo de spot.
     * @return El ID del recurso de imagen.
     */
    public static int markerSpot(String tipoSpot) {
        switch (tipoSpot) {
            case "SPOT":
                return R.mipmap.ic_spot;
            case "Skate Park":
                return R.mipmap.ic_skatepark;
            case "Bowls Park":
                return R.mipmap.ic_bowls;
            case "Shop":
                return R.mipmap.ic_shop;
            case "DIY Park":
                return R.mipmap.ic_diy;
            case "Indoor":
                return R.mipmap.ic_indoor;
            default:
                return 0;
        }
    }

    /**
     * Agrega un like de un usuario a un spot.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param user     ID del usuario que dio el like.
     * @param spot     El spot al que se agregará el like.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void addLikeSpot(FirebaseDatabase db, String user, Spot spot, UploadCallback callback) {
        DatabaseReference ref = db.getReference("Spot").child(spot.getId());
        spot.getLikes().add(user);
        ref.setValue(spot).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Elimina el like de un usuario de un spot.
     *
     * @param db       Instancia de FirebaseDatabase.
     * @param user     ID del usuario que dio el like.
     * @param spot     El spot del que se eliminará el like.
     * @param callback Callback para manejar el resultado de la operación.
     */
    public void deleteLikeSpot(FirebaseDatabase db, String user, Spot spot, UploadCallback callback) {
        DatabaseReference ref = db.getReference("Spot").child(spot.getId());
        spot.getLikes().remove(user);
        ref.setValue(spot).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Obtiene los spots creados por un usuario.
     *
     * @param databaseReference     Referencia a la base de datos.
     * @param id                    ID del usuario.
     * @param uploadCallbackSPOTs   Callback para manejar los spots obtenidos.
     */
    public void SpotByUser(DatabaseReference databaseReference, String id, UploadCallbackSPOTs uploadCallbackSPOTs) {
        Query query = databaseReference.orderByChild("usuarioCreador").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Spot> spots = new ArrayList<>();
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    Spot spot = x.getValue(Spot.class);
                    Objects.requireNonNull(spot).setId(x.getKey());
                    // Verificar si el nuevo Spot ya existe en la lista antes de agregarlo
                    if (!spot.existeEnLista(spots)) {
                        spots.add(spot);
                    }
                }
                uploadCallbackSPOTs.onUploadComplete(spots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "error al iniciar", error.toException());
            }
        });
    }

    /**
     * Obtiene los spots creados por un usuario y actualiza la interfaz de usuario a medida que se agregan nuevos spots.
     *
     * @param databaseReference Referencia a la base de datos.
     * @param id                ID del usuario.
     * @param spots             Lista de spots donde se almacenarán los datos obtenidos.
     * @param adapterSpotList   Adaptador de spots para actualizar la interfaz de usuario.
     */
    public void SpotByUser(DatabaseReference databaseReference, String id, ArrayList<Spot> spots, AdapterSpotList adapterSpotList) {
        Query query = databaseReference.orderByChild("usuarioCreador").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    Spot spot = x.getValue(Spot.class);
                    Objects.requireNonNull(spot).setId(x.getKey());
                    if (!spot.existeEnLista(spots)) {
                        spots.add(spot);
                        adapterSpotList.notifyDataSetChanged();
                    } else {
                        modificarSpot(spots, spot);
                        adapterSpotList.notifyDataSetChanged();
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
     * Obtiene los spots que recibieron likes de un usuario y actualiza la interfaz de usuario a medida que se agregan nuevos spots.
     *
     * @param databaseReference Referencia a la base de datos.
     * @param id                ID del usuario.
     * @param spots             Lista de spots donde se almacenarán los datos obtenidos.
     * @param adapterSpotList   Adaptador de spots para actualizar la interfaz de usuario.
     */
    public void SpotByUserLikes(DatabaseReference databaseReference, String id, ArrayList<Spot> spots, AdapterSpotList adapterSpotList) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot x : dataSnapshot.getChildren()) {
                    DataSnapshot likesSnapshot = x.child("likes");
                    for (DataSnapshot likeSnapshot : likesSnapshot.getChildren()) {
                        String likeUsuarioId = likeSnapshot.getValue(String.class);
                        assert likeUsuarioId != null;
                        if (likeUsuarioId.equals(id)) {
                            Spot spot = x.getValue(Spot.class);
                            assert spot != null;
                            spot.setId(x.getKey());
                            if (!spot.existeEnLista(spots)) {
                                spots.add(spot);
                                adapterSpotList.notifyDataSetChanged();
                            }
                            break;
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
     * Obtiene un spot por su ID.
     *
     * @param databaseReference  Referencia a la base de datos.
     * @param id                 ID del spot.
     * @param uploadCallbackSpot Callback para manejar el spot obtenido.
     */
    public void SpotById(DatabaseReference databaseReference, String id, UploadCallbackSpot uploadCallbackSpot) {
        databaseReference.child(id).get().addOnSuccessListener(dataSnapshot -> {
            Spot s = dataSnapshot.getValue(Spot.class);
            assert s != null;
            s.setId(dataSnapshot.getKey());
            uploadCallbackSpot.onUploadCompleteSpot(s);
        });
    }

    /**
     * Modifica un spot existente en la lista.
     *
     * @param lista         Lista de spots.
     * @param spotModificado Spot modificado.
     */
    public void modificarSpot(ArrayList<Spot> lista, Spot spotModificado) {
        lista.stream()
                .filter(spot -> spot.equals(spotModificado))
                .findFirst()
                .ifPresent(spotExistente -> {
                    spotExistente.setLatitud(spotModificado.getLatitud());
                    spotExistente.setLongitud(spotModificado.getLongitud());
                    spotExistente.setTags(spotModificado.getTags());
                    spotExistente.setImagenes(spotModificado.getImagenes());
                    spotExistente.setLikes(spotModificado.getLikes());
                    spotExistente.setComentarios(spotModificado.getComentarios());
                    spotExistente.setNombre(spotModificado.getNombre());
                    spotExistente.setNombreCiudad(spotModificado.getNombreCiudad());
                    spotExistente.setTipoSpot(spotModificado.getTipoSpot());
                    spotExistente.setDescripcion(spotModificado.getDescripcion());
                    spotExistente.setUsuarioCreador(spotModificado.getUsuarioCreador());
                });
    }
}



