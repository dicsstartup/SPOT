package com.dicsstartup.spot.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.usuario.PerfilActivity;
import com.dicsstartup.spot.entities.Amistad;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.enumerados.EstadoAmistad;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSolicitudes extends RecyclerView.Adapter<AdapterSolicitudes.SolicitudesListViewHolder>{
    private List<Amistad> data;
    private Context context;



    public AdapterSolicitudes(Context context, List<Amistad> data) {
        this.data = data;
        this.context = context;

    }

    @NonNull
    @Override
    public SolicitudesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.solicitud_amistad, parent, false);
        return new SolicitudesListViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull SolicitudesListViewHolder holder, int position) {
        // Obtener la reunión actual en la posición dada
        Amistad amistad = data.get(position);
        // Configurar los datos de la reunión en el ViewHolder
        holder.bind(amistad);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SolicitudesListViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avatar;
        TextView nombre;
        Button aceptar;
        Button rechazar;
        TextView estado;


        public SolicitudesListViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar =itemView.findViewById(R.id.avatarSolicitud);
            nombre=itemView.findViewById(R.id.nombreSolicitud);
            aceptar=itemView.findViewById(R.id.aceptarSolicitud);
            rechazar=itemView.findViewById(R.id.rechazarSolicitud);
            estado=itemView.findViewById(R.id.EstadoSolicitud);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(Amistad amistad) {
            DomainUser domainUser= new DomainUser();

            DomainUser.getUser(FirebaseDatabase.getInstance(), amistad.getIdAmigo(), new UploadCallbackSpot() {
                @Override
                public void onUploadCompleteUser(Usuario result) {
                    Picasso.get().load(result.getAvatar()).into(avatar);
                    nombre.setText(result.getNick());
                    avatar.setOnClickListener(v->getPerfil(result));
                    nombre.setOnClickListener(v->getPerfil(result));
                    if(amistad.getEstadoAmistad()== EstadoAmistad.PENDIENTE&&amistad.isEnvia()){
                        aceptar.setVisibility(View.INVISIBLE);
                        rechazar.setText("Cancelar");
                        rechazar.setOnClickListener(v-> cancelar(domainUser,amistad));
                    }else if(amistad.getEstadoAmistad()== EstadoAmistad.PENDIENTE&&!amistad.isEnvia()){
                        aceptar.setOnClickListener(v-> aceptar(domainUser,amistad));
                        rechazar.setOnClickListener(v-> rechazar(domainUser,amistad));
                    }else if(amistad.getEstadoAmistad()==EstadoAmistad.RECHAZADA&& amistad.isEnvia()){
                        estado.setText("Te la rechazo");
                        aceptar.setVisibility(View.INVISIBLE);
                        rechazar.setVisibility(View.INVISIBLE);
                    }else if(amistad.getEstadoAmistad()==EstadoAmistad.RECHAZADA&& !amistad.isEnvia()){
                        estado.setText("La haz rechazado");
                        aceptar.setVisibility(View.INVISIBLE);
                        rechazar.setText("cancelar");
                        rechazar.setOnClickListener(v->cancelar(domainUser,amistad));
                    }else{
                        estado.setText("Solicitud aceptada");
                        aceptar.setVisibility(View.INVISIBLE);
                        rechazar.setText("cancelar");
                        rechazar.setOnClickListener(v-> cancelar(domainUser,amistad));
                    }
                }

                @Override
                public void onUploadCompleteSpot(Spot spot) {

                }
            });


        }
        public void getPerfil(Usuario user){
            Intent i = new Intent(this.itemView.getContext(), PerfilActivity.class);
            i.putExtra("user",user);
            this.itemView.getContext().startActivity(i);
        }


        public void aceptar(DomainUser domainUser,Amistad amistad){
            domainUser.AdministrarAmistad(amistad.getIdAmigo(), FirebaseDatabase.getInstance(), EstadoAmistad.ACEPTADA, result14 -> {
                if(result14){
                    aceptar.setVisibility(View.INVISIBLE);
                    estado.setText("Solicitud aceptada");
                }
            });
        }
        public void cancelar(DomainUser domainUser,Amistad amistad){
            domainUser.EliminarAmistad(amistad.getIdAmigo(), FirebaseDatabase.getInstance(), result1 -> {
            });
        }
        public void rechazar(DomainUser domainUser,Amistad amistad){
            domainUser.AdministrarAmistad(amistad.getIdAmigo(), FirebaseDatabase.getInstance(), EstadoAmistad.RECHAZADA, result13 -> {
                if(result13){
                    aceptar.setVisibility(View.INVISIBLE);
                    estado.setText("Solicitud Rechazada");
                    rechazar.setText("Cancelar");
                    rechazar.setOnClickListener(v->cancelar(domainUser,amistad));
                }
            });
        }

    }

}

