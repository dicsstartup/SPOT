package com.dicsstartup.spot.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

import java.util.Locale;

/**
 * Clase para confirmar y solicitar permisos en Android.
 */
public class Permisos {

    // Lista de permisos a confirmar
    private String[] permisos = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

    };

    /**
     * Confirma los permisos requeridos y solicita los permisos no concedidos.
     *
     * @param context                  Contexto de la aplicación.
     * @param requestPermissionLauncher ActivityResultLauncher para solicitar permisos.
     * @return {@code true} si todos los permisos están confirmados, {@code false} en caso contrario.
     */
    public boolean confirmarPermisos(Context context, ActivityResultLauncher<String> requestPermissionLauncher) {
        boolean res = false;

        for (String permiso : permisos) {
            if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                // Si el permiso no ha sido concedido, solicítalo
                requestPermissionLauncher.launch(permiso);
                res = false; // Indica que los permisos no están confirmados todavía
            } else {
                res = true; // Indica que los permisos están confirmados
            }
        }

        return res;
    }
}