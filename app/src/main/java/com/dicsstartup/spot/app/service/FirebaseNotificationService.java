package com.dicsstartup.spot.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseNotificationService {

    public class NotificacionesService extends Service {

        private DatabaseReference databaseReference;
        private ValueEventListener valueEventListener;

        @Override
        public void onCreate() {
            super.onCreate();
            // Inicializa la referencia de Firebase que deseas monitorear
            databaseReference = FirebaseDatabase.getInstance().getReference().child("ruta_de_referencia");
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Configura el listener para escuchar los cambios en Firebase
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Maneja los cambios y genera una notificación
                    generarNotificacion();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Maneja los errores si es necesario
                }
            };

            // Agrega el listener a la referencia de Firebase
            databaseReference.addValueEventListener(valueEventListener);

            // Indica que el servicio debe seguir ejecutándose en segundo plano
            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            // Remueve el listener de la referencia de Firebase al destruir el servicio
            if (databaseReference != null && valueEventListener != null) {
                databaseReference.removeEventListener(valueEventListener);
            }
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void generarNotificacion() {
            // Crea y muestra una notificación para informar al usuario sobre los cambios en Firebase
            // Utiliza las APIs de notificaciones de Android, tal como se mencionó en la respuesta anterior
            // Personaliza la apariencia y el contenido de la notificación según tus necesidades
        }
    }
}
