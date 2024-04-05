package com.dicsstartup.spot.firebase.database;


import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dicsstartup.spot.entities.Amistad;
import com.dicsstartup.spot.entities.Chat;
import com.dicsstartup.spot.entities.Notificacion;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.enumerados.EstadoAmistad;
import com.dicsstartup.spot.interfaz.UploadCallback;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.dicsstartup.spot.utils.AdapterNotificaciones;
import com.dicsstartup.spot.utils.AdapterSolicitudes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;


public class DomainUser {
    /**
     * Obtiene un usuario por su ID.
     *
     * @param databaseReference   Referencia a la base de datos.
     * @param id                  ID del usuario.
     * @param uploadCallbackSpot  Callback para manejar el usuario obtenido.
     */
    public void userById(DatabaseReference databaseReference, String id, UploadCallbackSpot uploadCallbackSpot) {
        databaseReference.child(id).get().addOnSuccessListener(dataSnapshot -> {
            Usuario s = dataSnapshot.getValue(Usuario.class);
            assert s != null;
            s.setId(dataSnapshot.getKey());
            uploadCallbackSpot.onUploadCompleteUser(s);
        });
    }
    /**
     * Obtiene un usuario por su email.
     *
     * @param databaseReference   Referencia a la base de datos.
     * @param email               Email del usuario.
     * @param uploadCallbackSpot  Callback para manejar el usuario obtenido.
     */
    public void userByEmail(DatabaseReference databaseReference, String email, UploadCallbackSpot uploadCallbackSpot) {
        Query query = databaseReference.orderByChild("email").equalTo(email).limitToFirst(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot x : snapshot.getChildren()) {
                    Usuario s = x.getValue(Usuario.class);
                    assert s != null;
                    s.setId(x.getKey());
                    uploadCallbackSpot.onUploadCompleteUser(s);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Obtiene un usuario por su ID utilizando un DatabaseReference.
     *
     * @param db                  FirebaseDatabase.
     * @param user                ID del usuario.
     * @param uploadCallbackSpot  Callback para manejar el usuario obtenido.
     */
    public static void getUser(FirebaseDatabase db, String user, UploadCallbackSpot uploadCallbackSpot) {
        DatabaseReference ref = db.getReference(Usuario.class.getSimpleName()).child(user);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Aquí obtienes el objeto Usuario
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                assert usuario != null;
                usuario.setId(dataSnapshot.getKey());
                uploadCallbackSpot.onUploadCompleteUser(usuario);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                uploadCallbackSpot.onUploadCompleteUser(null);
            }
        });
    }

    /**
     * Actualiza un usuario en la base de datos.
     *
     * @param db       FirebaseDatabase.
     * @param user     Usuario a actualizar.
     * @param callback Callback para manejar la finalización de la actualización.
     */
    public void updateUser(FirebaseDatabase db, Usuario user, UploadCallback callback) {
        DatabaseReference ref = db.getReference("Usuario").child(user.getId());
        ref.setValue(user).addOnCompleteListener(task -> callback.onUploadComplete(task.isSuccessful()));
    }

    /**
     * Envía una solicitud de amistad entre dos usuarios.
     *
     * @param userAdd          ID del usuario al que se envía la solicitud de amistad.
     * @param db               FirebaseDatabase.
     * @param uploadCallback   Callback para manejar la finalización del envío de la solicitud.
     */
    public void solicitudAmistad(String userAdd, FirebaseDatabase db, UploadCallback uploadCallback) {
        Usuario user = usuario;
        userById(db.getReference("Usuario"), userAdd, new UploadCallbackSpot() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onUploadCompleteUser(Usuario result) {
                if (result != null) {
                    Amistad a = new Amistad();
                    a.setEstadoAmistad(EstadoAmistad.PENDIENTE);
                    a.setFecha(LocalDateTime.now().toString());
                    a.setIdAmigo(user.getId());
                    a.setEnvia(false);
                    Amistad a2 = new Amistad();
                    a2.setEstadoAmistad(EstadoAmistad.PENDIENTE);
                    a2.setFecha(LocalDateTime.now().toString());
                    a2.setIdAmigo(result.getId());
                    a2.setEnvia(true);
                    user.getAmigos().add(a2);
                    result.getNotificaciones().add(new Notificacion(false, "Nueva solicitud de amistad", LocalDateTime.now().toString()));
                    result.getAmigos().add(a);
                    db.getReference("Usuario").child(user.getId()).setValue(user);
                    db.getReference("Usuario").child(result.getId()).setValue(result);
                    uploadCallback.onUploadComplete(true);
                }
            }

            @Override
            public void onUploadCompleteSpot(Spot spot) {

            }
        });
    }

    /**
     * Elimina una amistad entre dos usuarios.
     *
     * @param userAdd          ID del usuario con el que se elimina la amistad.
     * @param db               FirebaseDatabase.
     * @param uploadCallback   Callback para manejar la finalización de la eliminación de la amistad.
     */
    public void EliminarAmistad(String userAdd, FirebaseDatabase db, UploadCallback uploadCallback) {
        Usuario user = usuario;
        userById(db.getReference("Usuario"), userAdd, new UploadCallbackSpot() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onUploadCompleteUser(Usuario result) {
                if (result != null) {
                    user.getAmigos().remove(user.obtenerIndiceAmistadConId(result.getId()));
                    result.getAmigos().remove(result.obtenerIndiceAmistadConId(user.getId()));
                    db.getReference("Usuario").child(user.getId()).setValue(user);
                    db.getReference("Usuario").child(result.getId()).setValue(result);
                    uploadCallback.onUploadComplete(true);
                }
            }

            @Override
            public void onUploadCompleteSpot(Spot spot) {

            }
        });
    }

    /**
     * Administra el estado de una amistad entre dos usuarios.
     *
     * @param userAdd          ID del usuario con el que se administra la amistad.
     * @param db               FirebaseDatabase.
     * @param estado           Estado de la amistad.
     * @param uploadCallback   Callback para manejar la finalización de la administración de la amistad.
     */
    public void AdministrarAmistad(String userAdd, FirebaseDatabase db, EstadoAmistad estado, UploadCallback uploadCallback) {
        Usuario user = usuario;
        userById(db.getReference("Usuario"), userAdd, new UploadCallbackSpot() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onUploadCompleteUser(Usuario result) {
                if (result != null) {
                    user.getAmigos().get(user.obtenerIndiceAmistadConId(result.getId())).setEstadoAmistad(estado);
                    result.getAmigos().get(result.obtenerIndiceAmistadConId(user.getId())).setEstadoAmistad(estado);
                    if (estado == EstadoAmistad.ACEPTADA) {
                        String id = db.getReference("Chat").push().getKey();
                        user.getAmigos().get(user.obtenerIndiceAmistadConId(result.getId())).setIdChat(id);
                        result.getAmigos().get(result.obtenerIndiceAmistadConId(user.getId())).setIdChat(id);
                        ArrayList<String> miembros = new ArrayList<>();
                        miembros.add(user.getId());
                        miembros.add(result.getId());
                        Chat chat = new Chat(id, miembros);
                        assert id != null;
                        db.getReference("Chat").child(id).setValue(chat);
                    }
                    db.getReference("Usuario").child(user.getId()).setValue(user);
                    db.getReference("Usuario").child(result.getId()).setValue(result);
                    uploadCallback.onUploadComplete(true);
                }
            }

            @Override
            public void onUploadCompleteSpot(Spot spot) {

            }
        });
    }

    /**
     * Obtiene las notificaciones de amistad para un usuario.
     *
     * @param db               FirebaseDatabase.
     * @param notificacions    Lista de notificaciones existentes.
     * @param adapter          Adaptador para actualizar la vista de las notificaciones.
     */
    public void NotificacionAmistad(FirebaseDatabase db, ArrayList<Notificacion> notificacions, AdapterNotificaciones adapter) {
        Usuario user = usuario;
        DatabaseReference ref = db.getReference(Usuario.class.getSimpleName()).child(user.getId());
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                assert usuario != null;
                usuario.setId(dataSnapshot.getKey());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                if (usuario.getNotificaciones().size() < notificacions.size()) {
                    notificacions.clear();
                    adapter.notifyDataSetChanged();
                    notificacions.addAll(usuario.getNotificaciones());
                    notificacions.sort(Comparator.comparing(solicitud -> LocalDateTime.parse(solicitud.getFecha(), formatter), Comparator.reverseOrder()));
                    adapter.notifyDataSetChanged();
                } else {
                    for (Notificacion notificacion : usuario.getNotificaciones()) {
                        if (!notificacion.existeEnLista(notificacions)) {
                            notificacions.add(notificacion);
                            notificacions.sort(Comparator.comparing(solicitud -> LocalDateTime.parse(solicitud.getFecha(), formatter), Comparator.reverseOrder()));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Obtiene las amistades de un usuario.
     *
     * @param db           FirebaseDatabase.
     * @param user         ID del usuario.
     * @param amistads     Lista de amistades existentes.
     * @param adapter      Adaptador para actualizar la vista de las amistades.
     */
    public void getAmistades(FirebaseDatabase db, String user, ArrayList<Amistad> amistads, AdapterSolicitudes adapter) {
        DatabaseReference ref = db.getReference(Usuario.class.getSimpleName()).child(user);
        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                assert usuario != null;
                usuario.setId(dataSnapshot.getKey());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                if (usuario.getAmigos().size() < amistads.size()) {
                    amistads.clear();
                    adapter.notifyDataSetChanged();
                    amistads.addAll(usuario.amigos);
                    amistads.sort(Comparator.comparing(solicitud -> LocalDateTime.parse(solicitud.getFecha(), formatter), Comparator.reverseOrder()));
                    adapter.notifyDataSetChanged();
                } else {
                    for (Amistad amistad : usuario.getAmigos()) {
                        if (!amistad.existeEnLista(amistads)) {
                            amistads.add(amistad);
                            amistads.sort(Comparator.comparing(solicitud -> LocalDateTime.parse(solicitud.getFecha(), formatter), Comparator.reverseOrder()));
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    }


