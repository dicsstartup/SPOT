package com.dicsstartup.spot.utils;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Notificacion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Adaptador para mostrar una lista de notificaciones en un RecyclerView.
 */
public class AdapterNotificaciones extends RecyclerView.Adapter<AdapterNotificaciones.notificacionesListViewHolder> {
    private List<Notificacion> data;
    private final Context context;

    /**
     * Constructor de la clase AdapterNotificaciones.
     *
     * @param context Contexto de la aplicación.
     * @param data    Lista de notificaciones a mostrar.
     */
    public AdapterNotificaciones(Context context, List<Notificacion> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public notificacionesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de la vista de cada elemento de la lista
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new notificacionesListViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull notificacionesListViewHolder holder, int position) {
        // Obtener la notificación actual en la posición dada
        Notificacion notificacion = data.get(position);
        // Configurar los datos de la notificación en el ViewHolder
        holder.bind(notificacion);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * ViewHolder para representar cada elemento de la lista de notificaciones.
     */
    public class notificacionesListViewHolder extends RecyclerView.ViewHolder {
        TextView hora;
        TextView mensaje;
        ImageView check;
        ConstraintLayout layout;
        ImageView image;

        public notificacionesListViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar los componentes de la vista del elemento
            mensaje = itemView.findViewById(R.id.mensajeNotificacion);
            hora = itemView.findViewById(R.id.horaNotificacion);
            check = itemView.findViewById(R.id.checkNotificacion);
            image = itemView.findViewById(R.id.imagenNotificacion);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(Notificacion notificacion) {
            // Configurar los datos de la notificación en los componentes de la vista
            mensaje.setText(notificacion.getMensaje());
            if (notificacion.getEnterado()) {
                check.setImageResource(R.drawable.check_circle_true);
            } else {
                check.setImageResource(R.drawable.check_circle_false);
            }
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime fecha = LocalDateTime.parse(notificacion.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Duration duration = Duration.between(fecha, ahora);
            if (duration.toMinutes() < 60) {
                hora.setText("hace " + duration.toMinutes() + " minutos");
            } else if (duration.toHours() < 24) {
                hora.setText("hace " + duration.toHours() + " horas");
            } else if (duration.toDays() < 30) {
                hora.setText("hace " + duration.toDays() + " días");
            } else {
                Period period = Period.between(fecha.toLocalDate(), ahora.toLocalDate());
                hora.setText("hace " + period.toTotalMonths() + " meses");
            }
            if (notificacion.getMensaje().contains("Nueva solicitud de amistad")) {
                image.setImageResource(R.drawable.friend_wait);
            }
        }
    }
}
