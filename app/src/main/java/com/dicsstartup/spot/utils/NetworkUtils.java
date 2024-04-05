package com.dicsstartup.spot.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Clase de utilidades para verificar la disponibilidad de la red en un dispositivo Android.
 */
public class NetworkUtils {

    /**
     * Verifica si hay una conexión de red disponible.
     *
     * @param context Contexto de la aplicación.
     * @return {@code true} si hay una conexión de red disponible, {@code false} en caso contrario.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}