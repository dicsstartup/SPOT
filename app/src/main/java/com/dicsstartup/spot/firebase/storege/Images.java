package com.dicsstartup.spot.firebase.storege;
import android.net.Uri;

import com.dicsstartup.spot.entities.ImagenSpot;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.dicsstartup.spot.interfaz.UploadCallback;
import com.dicsstartup.spot.interfaz.UploadCallbackImage;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Clase que contiene métodos para subir, eliminar y editar imágenes en Firebase Storage.
 */
public class Images {

    /**
     * Sube una imagen de un spot a Firebase Storage.
     *
     * @param storageReference Referencia a Firebase Storage.
     * @param uri              URI de la imagen a subir.
     * @param spot             Objeto Spot al que se asociará la imagen.
     * @param db               Instancia de FirebaseDatabase.
     * @param callback         Callback para manejar el resultado de la subida.
     * @param cantidad         Cantidad de imágenes que se están subiendo en total.
     */
    public void subirImagenSpot(FirebaseStorage storageReference, Uri uri, Spot spot, FirebaseDatabase db, UploadCallback callback, int cantidad) {
        DomainSPOT domainSPOT = new DomainSPOT();
        StorageReference archivoRef = storageReference.getReference().child("imagenes/" + spot.getNombre() + spot.getUsuarioCreador() + "/" + spot.getUsuarioCreador() + uri.getLastPathSegment());
        archivoRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    // Obtiene la URL de descarga de la imagen
                    archivoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Agrega la URL de la imagen a la lista
                        String urlImagen = downloadUri.toString();
                        spot.getImagenes().add(new ImagenSpot(urlImagen, "imagenes/" + spot.getNombre() + spot.getUsuarioCreador() + "/" + spot.getUsuarioCreador() + uri.getLastPathSegment()));
                        // Verifica si todas las imágenes se han subido y guardado las URLs
                        if (spot.getImagenes().size() == cantidad) {
                            domainSPOT.addSpot(db, spot, callback::onUploadComplete);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    StorageReference archivoRef1 = storageReference.getReference().child("imagenes/" + spot.getNombre());
                    archivoRef1.delete();
                });
    }

    /**
     * Sube una imagen de un spot para editar en Firebase Storage.
     *
     * @param storageReference Referencia a Firebase Storage.
     * @param uri              URI de la imagen a subir.
     * @param spot             Objeto Spot al que se asociará la imagen.
     * @param callback         Callback para manejar el resultado de la subida.
     */
    public void subirImagenSpotEditar(FirebaseStorage storageReference, Uri uri, Spot spot, UploadCallbackImage callback) {
        StorageReference archivoRef = storageReference.getReference().child("imagenes/" + spot.getNombre() + spot.getUsuarioCreador() + "/" + spot.getUsuarioCreador() + uri.getLastPathSegment());
        archivoRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    // Obtiene la URL de descarga de la imagen
                    archivoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Agrega la URL de la imagen a la lista
                        String urlImagen = downloadUri.toString();
                        callback.onUploadComplete(new ImagenSpot(urlImagen, "imagenes/" + spot.getNombre() + spot.getUsuarioCreador() + "/" + spot.getUsuarioCreador() + uri.getLastPathSegment()));
                    });
                })
                .addOnFailureListener(e -> callback.onUploadComplete(null));
    }

    /**
     * Sube una imagen de avatar de usuario a Firebase Storage.
     *
     * @param storageReference Referencia a Firebase Storage.
     * @param uri              URI de la imagen a subir.
     * @param user             Objeto Usuario al que se asociará la imagen.
     * @param db               Instancia de FirebaseDatabase.
     * @param callback         Callback para manejar el resultado de la subida.
     */
    public void subirAvatar(FirebaseStorage storageReference, Uri uri, Usuario user, FirebaseDatabase db, UploadCallback callback) {
        DomainUser domainUser = new DomainUser();
        StorageReference carpetRef = storageReference.getReference().child("avatar/" + user.getNick());
        carpetRef.delete();
        StorageReference archivoRef = storageReference.getReference().child("avatar/" + user.getNick() + "/" + uri.getLastPathSegment());
        archivoRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    // Obtiene la URL de descarga de la imagen
                    archivoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        // Agrega la URL de la imagen a la lista
                        String urlImagen = downloadUri.toString();
                        user.setAvatar(urlImagen);
                        // Verifica si todas las imágenes se han subido y guardado las URLs
                        domainUser.updateUser(db, user, result -> callback.onUploadComplete(result));
                    });
                })
                .addOnFailureListener(e -> {
                });
    }

    /**
     * Elimina una imagen de spot de Firebase Storage.
     *
     * @param storageReference Referencia a Firebase Storage.
     * @param path             Ruta de la imagen a eliminar.
     * @param uploadCallback   Callback para manejar el resultado de la eliminación.
     */
    public void eliminarImagenSpot(FirebaseStorage storageReference, ImagenSpot path, UploadCallback uploadCallback) {
        StorageReference archivoRef = storageReference.getReference().child(path.getPath());
        archivoRef.delete().addOnSuccessListener(aVoid -> {
                    // La imagen se eliminó exitosamente
                    System.out.println("Imagen eliminada correctamente.");
                    uploadCallback.onUploadComplete(true);
                })
                .addOnFailureListener(exception -> {
                    // Ocurrió un error al eliminar la imagen
                    uploadCallback.onUploadComplete(false);
                    System.out.println("Error al eliminar la imagen: " + exception.getMessage());
                });
    }
}

