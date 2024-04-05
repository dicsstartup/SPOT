package com.dicsstartup.spot.utils;
/** @hide */

import static com.dicsstartup.spot.R.layout.buttonsheet_add_evento;
import static com.dicsstartup.spot.firebase.database.DomainUser.getUser;
import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.spot.SpotActivity;
import com.dicsstartup.spot.app.usuario.PerfilActivity;
import com.dicsstartup.spot.entities.Reunion;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainReuniones;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class AdapterReunion extends RecyclerView.Adapter<AdapterReunion.ReunionViewHolder> {
    private List<Reunion> data;
    private Context context;
    Boolean isSpot;


    public AdapterReunion(Context context, List<Reunion> data,Boolean spot) {
        this.data = data;
        this.context = context;
        this.isSpot=spot;
    }

    @NonNull
    @Override
    public ReunionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reunion_list, parent, false);
        return new ReunionViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ReunionViewHolder holder, int position) {
        // Obtener la reunión actual en la posición dada
        Reunion reunion = data.get(position);
        // Configurar los datos de la reunión en el ViewHolder
        holder.bind(reunion);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ReunionViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avatarReunion;
        TextView diaReunion;
        TextView horaReunionList;
        TextView userNameReunion;
        TextView descripcionReunion;
        TextView asistenciasReunion;
        ImageView accionReunion;
        TextView mesReunion;
        DomainReuniones domainReuniones;
        Reunion reunionItem;
        TextView tipoSpot;
        TextView nombreSpot;

        public ReunionViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarReunion = itemView.findViewById(R.id.avatarReunion);
            diaReunion = itemView.findViewById(R.id.diaReunion);
            horaReunionList = itemView.findViewById(R.id.horaReunionList);
            userNameReunion = itemView.findViewById(R.id.userNameReunion);
            descripcionReunion = itemView.findViewById(R.id.descripcionReunion);
            asistenciasReunion = itemView.findViewById(R.id.asistenciasReunion);
            accionReunion = itemView.findViewById(R.id.accionReunion);
            mesReunion = itemView.findViewById(R.id.mesReunion);
            tipoSpot = itemView.findViewById(R.id.tipospot_reunion);
            nombreSpot = itemView.findViewById(R.id.nombrespot_reunion);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(Reunion reunion) {
            if(isSpot){
                DomainSPOT domainSPOT= new DomainSPOT();
                domainSPOT.SpotById(FirebaseDatabase.getInstance().getReference().child("Spot"), reunion.getIdSpot(), new UploadCallbackSpot() {
                    @Override
                    public void onUploadCompleteUser(Usuario result) {

                    }

                    @Override
                    public void onUploadCompleteSpot(Spot spot) {
                        if (spot != null) {
                            tipoSpot.setText(spot.getTipoSpot());
                            nombreSpot.setText(spot.getNombre());
                            tipoSpot.setOnClickListener(v->{
                                getSpot(spot);
                            });
                            nombreSpot.setOnClickListener(v->{
                                getSpot(spot);
                            });
                        }
                    }

                });
            }else{
                tipoSpot.setVisibility(View.INVISIBLE);
                nombreSpot.setVisibility(View.INVISIBLE);
            }
            LocalDateTime fecha = LocalDateTime.parse(reunion.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            reunionItem = reunion;
            diaReunion.setText("" + fecha.getDayOfMonth());
            horaReunionList.setText(fecha.getHour() + ":" + fecha.getMinute());
            mesReunion.setText(fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es")));
            descripcionReunion.setText(reunion.getDescripcion());
            asistenciasReunion.setText(reunion.getAsistencias().size()+"");
            domainReuniones = new DomainReuniones();

            if (reunion.getIdCreador().equals(usuario.getId())) {
                accionReunion.setImageResource(R.drawable.events_edit);
                accionReunion.setOnClickListener(v -> editar( this.itemView.getContext()));
            } else {
                if (reunion.getAsistencias().contains(usuario.getId())) {
                    accionReunion.setImageResource(R.drawable.events_cancel);
                    accionReunion.setOnClickListener(v -> requestDelete());
                } else {
                    accionReunion.setImageResource(R.drawable.events);
                    accionReunion.setOnClickListener(v -> add());
                }
            }

            getUser(FirebaseDatabase.getInstance(), reunion.getIdCreador(), new UploadCallbackSpot() {
                @Override
                public void onUploadCompleteUser(Usuario result) {
                    if (result != null) {
                        userNameReunion.setText(result.getNick());
                        Picasso.get().load(result.getAvatar()).into(avatarReunion);

                        userNameReunion.setOnClickListener(v -> {
                            getPerfil(result);
                        });
                        avatarReunion.setOnClickListener(v -> getPerfil(result));

                    }
                }

                @Override
                public void onUploadCompleteSpot(Spot spot) {
                    // Aquí obtienes el objeto Spot
                    // Realiza las acciones necesarias con el objeto Spot
                }

            });
        }

        void add() {
            domainReuniones.addAsistencia(FirebaseDatabase.getInstance(), usuario.getId(), reunionItem, result -> {
                if (result) {
                    accionReunion.setImageResource(R.drawable.events_cancel);
                    accionReunion.setOnClickListener(v -> requestDelete());
                    asistenciasReunion.setText(reunionItem.getAsistencias().size()+"");
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        void editar(Context c) {
            LocalDateTime fechaLocalDate = LocalDateTime.parse(reunionItem.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            BottomSheetDialog nuevo= new BottomSheetDialog(c,R.style.BottomSheetStyle);
            View vista = LayoutInflater.from(c.getApplicationContext()).inflate(buttonsheet_add_evento,null);
            EditText addReunionComentarios = vista.findViewById(R.id.add_reunion_comentarios);
            TextView fechaReunion = vista.findViewById(R.id.fechaReunion);
            TextView error= vista.findViewById(R.id.tituloHora);
            TextView titulo=vista.findViewById(R.id.titulo);;
            ImageButton eliminar=vista.findViewById(R.id.imageButton);;
            TextView horaReunion = vista.findViewById(R.id.horaReunion);
            addReunionComentarios.setText(reunionItem.getDescripcion());
            eliminar.setImageResource(R.drawable.baseline_delete_24);
            eliminar.setOnClickListener(v->{
                domainReuniones.deleteReunion(FirebaseDatabase.getInstance(),reunionItem, result -> {
                    if (result) {
                       nuevo.dismiss();
                    }
                });
            });
            titulo.setText("Edita Reunion");
            Button buttonAñadirReunion = vista.findViewById(R.id.buttonAñadirReunion);
            DomainReuniones domainReuniones= new DomainReuniones();
            fechaReunion.setText(fechaLocalDate.getYear() + "/" + fechaLocalDate.getMonthValue() + "/" + fechaLocalDate.getDayOfMonth());
            fechaReunion.setOnClickListener(r-> {
                fechaReunion.setError(null);
                error.setError(null);
                int yearnow = fechaLocalDate.getYear();
                int monthNow = fechaLocalDate.getMonthValue();
                int dayNow = fechaLocalDate.getDayOfMonth();
                DatePickerDialog fecha = new DatePickerDialog(c, (view,year, month, dayOfMonth) -> {
                    String d=""+dayOfMonth,m=""+(month+1);
                    if(month<=9){
                        m="0" + (month+1);
                    }
                    if(dayOfMonth<=9){
                        d="0" + month;
                    }
                    fechaReunion.setText(year + "/" + m + "/" + d);


                }, yearnow, monthNow, dayNow);
                fecha.show();
            });
            horaReunion.setText( fechaLocalDate.getHour()+":"+fechaLocalDate.getMinute());
            horaReunion.setOnClickListener(r-> {
                horaReunion.setError(null);
                error.setError(null);
                int horaNow = fechaLocalDate.getHour();
                int minuto = fechaLocalDate.getMinute();

                TimePickerDialog hora = new TimePickerDialog(c, (view, hourOfDay, minute) -> {
                    String h=""+hourOfDay,m=""+minute;
                    if(hourOfDay<=9){
                        h="0"+hourOfDay;
                    }
                    if(minute<=9){
                        m="0"+minute;
                    }
                    horaReunion.setText(h+":"+m);

                },horaNow,minuto,false);
                hora.show();
            });
            buttonAñadirReunion.setText("Guardar");
            buttonAñadirReunion.setOnClickListener(r->{
                horaReunion.setError(null);
                fechaReunion.setError(null);
                error.setError(null);
                if(addReunionComentarios.length()>0){
                    if( !fechaReunion.getText().equals("Seleccione fecha")){
                        if(!horaReunion.getText().equals("Seleccione hora")){
                            LocalDateTime fechaYHoraFinal = LocalDateTime.parse(fechaReunion.getText()+" "+horaReunion.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                            LocalDateTime fechaYHoraActual = LocalDateTime.now();
                            if(fechaYHoraFinal.isAfter(fechaYHoraActual) || fechaYHoraFinal.toLocalDate().isEqual(fechaYHoraActual.toLocalDate()) && fechaYHoraFinal.toLocalTime().isAfter(fechaYHoraActual.toLocalTime())){
                                reunionItem.setDescripcion(addReunionComentarios.getText().toString());
                                reunionItem.setFecha(fechaYHoraFinal.toString());
                                domainReuniones.addReunion(FirebaseDatabase.getInstance(), reunionItem, result -> {
                                    if(result){
                                        nuevo.dismiss();
                                    }
                                });
                            }else{
                                error.setError("La fecha tiene que ser despues");
                                error.append("\ndebe ser despues de la actual");
                            }
                        }else{
                            horaReunion.setError("Seleccione");
                        }
                    }else{
                        fechaReunion.setError("Seleccione");
                    }
                }else{
                    addReunionComentarios.setError("Esta vacio");
                }
            });

            nuevo.setCancelable(true);
            nuevo.setContentView(vista);
            nuevo.show();
        }
        void requestDelete() {
            domainReuniones.deleteAsistencia(FirebaseDatabase.getInstance(), usuario.getId(), reunionItem, result -> {
                if (result) {
                    accionReunion.setOnClickListener(v -> add());
                    accionReunion.setImageResource(R.drawable.events);
                    asistenciasReunion.setText(reunionItem.getAsistencias().size()+"");
                }
            });
        }
        public void getPerfil(Usuario user){
            Intent i = new Intent(this.itemView.getContext(), PerfilActivity.class);
            i.putExtra("user",user);
            this.itemView.getContext().startActivity(i);
        }
        public void getSpot(Spot spot){
            Intent intent = new Intent(this.itemView.getContext(), SpotActivity.class);
            intent.putExtra("spot",spot);
            this.itemView.getContext().startActivity(intent);
        }
    }



}