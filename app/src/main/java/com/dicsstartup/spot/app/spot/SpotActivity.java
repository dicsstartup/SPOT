package com.dicsstartup.spot.app.spot;
/** @hide */

import static com.dicsstartup.spot.R.layout.buttonsheet_add_evento;
import static com.dicsstartup.spot.firebase.database.DomainUser.getUser;
import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.add.AddSpotActivity;
import com.dicsstartup.spot.app.usuario.PerfilActivity;
import com.dicsstartup.spot.entities.ImagenSpot;
import com.dicsstartup.spot.entities.Reunion;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainReuniones;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.dicsstartup.spot.utils.AdapterImagenesSpot;
import com.dicsstartup.spot.utils.AdapterReunion;
import com.dicsstartup.spot.utils.AdapterTag;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
/**
 * Actividad que muestra la información y funcionalidades relacionadas con un spot específico.
 */
public class SpotActivity extends AppCompatActivity {
    ShapeableImageView avatar;
    private ImageView ButtonLike_spot;
    private ImageView cambio_spot;
    private TextView creador_spot;
    private RecyclerView tags_spot;
    private ConstraintLayout botonera;
    boolean rot=false;

    public  Spot spot;

    public  Usuario user;
    DomainSPOT domainSPOT;
    DomainReuniones domainReuniones;

    ArrayList<ImagenSpot> imagenes= new ArrayList<>();
    ArrayList<String> tagsLista= new ArrayList<>();

    public ArrayList<Reunion> reuniones= new ArrayList<>();
    public AdapterReunion adapterReunion;


    /**
     * Método de creación de la actividad.
     * añade funcionalidad a todos los datos relacionados con el spot
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"MissingInflatedId", "ResourceAsColor", "UseCompatLoadingForDrawables", "SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        spot= getIntent().getSerializableExtra("spot",Spot.class);
        domainSPOT= new DomainSPOT();
        domainReuniones= new DomainReuniones();
        botonera= findViewById(R.id.botonera_spot);
        cambio_spot=findViewById(R.id.botonCambio_spot);
        avatar=findViewById(R.id.spot_avatar);
        TextView spot_nombre = findViewById(R.id.spot_nombre);
        TextView ciudad_spot = findViewById(R.id.ciudad_spot);
        TextView like_spot = findViewById(R.id.like_spot);
        ButtonLike_spot = findViewById(R.id.ButtonLike_spot);
        RecyclerView images_spot = findViewById(R.id.images_spot);
        creador_spot = findViewById(R.id.creador_spot);
        TextView des_spot = findViewById(R.id.des_spot);
        tags_spot = findViewById(R.id.tags_spot);
        TextView tipo_spot = findViewById(R.id.tipo_spot);
        RecyclerView reuniones_spot = findViewById(R.id.eventos_spot);
        cambio_spot.setRotation(-180f);
        ImageView spotComentar = findViewById(R.id.spot_comentar);
        ImageView spotGoogleMaps = findViewById(R.id.spot_googlemaps);
        ImageView spotCalendario = findViewById(R.id.spot_calendario);
        ImageView spotReportar = findViewById(R.id.spot_reportar);
        TextView textReportar = findViewById(R.id.textViewreport);

        if(usuario.getId().equals(spot.getUsuarioCreador())){
            spotReportar.setImageResource(R.drawable.edit);
            textReportar.setText("Editar");
            textReportar.setTextColor(getResources().getColor(R.color.amarillo,null));
            spotReportar.setOnClickListener(v -> {
                Intent i = new Intent(getApplicationContext(), AddSpotActivity.class);
                i.putExtra("spot",spot);
                startActivity(i);
            });
        }
        spotCalendario.setOnClickListener(v->{
            BottomSheetDialog nuevo= new BottomSheetDialog(this,R.style.BottomSheetStyle);
            @SuppressLint("InflateParams") View vista = LayoutInflater.from(getApplicationContext()).inflate(buttonsheet_add_evento,null);
            EditText addReunionComentarios = vista.findViewById(R.id.add_reunion_comentarios);
            TextView fechaReunion = vista.findViewById(R.id.fechaReunion);
            TextView error= vista.findViewById(R.id.tituloHora);
            DomainReuniones domainReuniones= new DomainReuniones();
            fechaReunion.setOnClickListener(r-> {
                fechaReunion.setError(null);
                error.setError(null);
                        Calendar cal = Calendar.getInstance();
                        int yearnow = cal.get(Calendar.YEAR);
                        int monthNow = cal.get(Calendar.MONTH);
                        int dayNow = cal.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog fecha = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
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
            TextView horaReunion = vista.findViewById(R.id.horaReunion);
            horaReunion.setOnClickListener(r-> {
                horaReunion.setError(null);
                error.setError(null);
                Calendar cal = Calendar.getInstance();
                int horaNow = cal.get(Calendar.HOUR_OF_DAY);
                int minuto = cal.get(Calendar.MINUTE);
                TimePickerDialog hora = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
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
            Button buttonAnadirReunion = vista.findViewById(R.id.buttonAñadirReunion);
            buttonAnadirReunion.setOnClickListener(r->{
                horaReunion.setError(null);
                fechaReunion.setError(null);
                error.setError(null);
                if(addReunionComentarios.length()>0){
                    if( !fechaReunion.getText().equals("Seleccione fecha")){
                        if(!horaReunion.getText().equals("Seleccione hora")){
                            LocalDateTime fechaYHoraFinal = LocalDateTime.parse(fechaReunion.getText()+" "+horaReunion.getText(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                            LocalDateTime fechaYHoraActual = LocalDateTime.now();
                            if(fechaYHoraFinal.isAfter(fechaYHoraActual) || fechaYHoraFinal.toLocalDate().isEqual(fechaYHoraActual.toLocalDate()) && fechaYHoraFinal.toLocalTime().isAfter(fechaYHoraActual.toLocalTime())){
                                Reunion reunion= new Reunion();
                                reunion.setIdSpot(spot.getId());
                                reunion.setIdCreador(usuario.getId());
                                reunion.setDescripcion(addReunionComentarios.getText().toString());
                                reunion.setFecha(fechaYHoraFinal.toString());
                                domainReuniones.addReunion(FirebaseDatabase.getInstance(), reunion, result -> {
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
        });
        spotReportar = findViewById(R.id.spot_reportar);
        ImageView regres = findViewById(R.id.regresSpot);
        regres.setOnClickListener(v->finish());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        images_spot.setLayoutManager(layoutManager);
        AdapterImagenesSpot adapter = new AdapterImagenesSpot(imagenes, getApplicationContext());
        images_spot.setAdapter(adapter);
        LinearLayoutManager layoutManagerTag = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        tags_spot.setLayoutManager(layoutManagerTag);
        AdapterTag adapterTags = new AdapterTag(tagsLista, getApplicationContext());
        tags_spot.setAdapter(adapterTags);
        botonera.setVisibility(View.GONE);
        if(spot!=null){
            spot_nombre.setText(spot.getNombre());
            ciudad_spot.setText(spot.getNombreCiudad());
            tipo_spot.setText(spot.getTipoSpot());
            des_spot.setText(spot.getDescripcion());
            LinearLayoutManager layoutManagerReunion = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            reuniones_spot.setLayoutManager(layoutManagerReunion);
            adapterReunion = new AdapterReunion(this.getApplicationContext(), reuniones,false);
            reuniones_spot.setAdapter(adapterReunion);
            DatabaseReference refSpot = FirebaseDatabase.getInstance().getReference("Reunion");
            domainReuniones.ReunionBySpot(refSpot,spot.getId(), reuniones, adapterReunion);
            spotGoogleMaps.setOnClickListener(v->{
                String uri = "geo:" + spot.getLatitud() + "," + spot.getLongitud()  + "?q=" + spot.getLatitud() + "," + spot.getLongitud()  + "(" + spot.getNombre() + ")"; // URI con la latitud, longitud y zoom
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps"); // Especifica que deseas abrir Google Maps
                // Comprueba si la aplicación de Google Maps está instalada en el dispositivo
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            });
            cambio_spot.setOnClickListener(v -> {
                if(rot){
                    cambio_spot.setRotation(-180f);
                    tags_spot.setVisibility(View.VISIBLE);
                    botonera.setVisibility(View.GONE);
                    rot=false;
                }else{
                    cambio_spot.setRotation(0f);
                    tags_spot.setVisibility(View.GONE);
                    botonera.setVisibility(View.VISIBLE);
                    rot=true;
                }

            });
            if(spot.getImagenes()!=null){
                imagenes.addAll(spot.getImagenes());
                adapter.notifyDataSetChanged();
            }
            if(spot.getTags()!=null){
                tagsLista.addAll(spot.getTags());
                adapterTags.notifyDataSetChanged();
            }

                if(spot.getLikes()!=null){
                    like_spot.setText(""+spot.getLikes().size());
                    if(spot.getLikes().contains(usuario.getId())){
                        ButtonLike_spot.setImageResource(R.drawable.icon_like);
                    }else{
                        ButtonLike_spot.setImageResource(R.drawable.icon_not_like);
                    }
                }else{
                    like_spot.setText("0");
                    ButtonLike_spot.setImageResource(R.drawable.icon_not_like);
                }
            ButtonLike_spot.setOnClickListener(v -> {
                    if(spot.getLikes().contains(usuario.getId())){
                        domainSPOT.deleteLikeSpot(FirebaseDatabase.getInstance(),usuario.getId(),spot, result -> {
                            if (result) {
                                ButtonLike_spot.setImageResource(R.drawable.icon_not_like);

                            } else {
                                ButtonLike_spot.setImageResource(R.drawable.icon_like);
                            }
                        });
                    }else{
                        domainSPOT.addLikeSpot(FirebaseDatabase.getInstance(),usuario.getId(),spot,result -> {
                            if (result) {
                                ButtonLike_spot.setImageResource(R.drawable.icon_like);
                            } else {
                                ButtonLike_spot.setImageResource(R.drawable.icon_not_like);
                            }
                        });
                    }
                });
        }
        getUser(FirebaseDatabase.getInstance(),spot.getUsuarioCreador(), new UploadCallbackSpot() {
            @Override
            public void onUploadCompleteUser(Usuario result) {
                if (result!=null) {
                    creador_spot.setText(result.getNick());
                    Picasso.get().load(result.getAvatar()).into(avatar);
                    user=result;
                    creador_spot.setOnClickListener(v -> getPerfil());
                    avatar.setOnClickListener(v -> getPerfil());

                } else {
                    creador_spot.setText("??");
                    user= new Usuario();
                }
            }
            @Override
            public void onUploadCompleteSpot(Spot spot) {
                // Aquí obtienes el objeto Spot
                // Realiza las acciones necesarias con el objeto Spot
            }

        });
    }
    /**
     * Método para abrir la actividad de perfil del usuario creador del spot.
     */
    public void getPerfil(){
            Intent i = new Intent(getApplicationContext(), PerfilActivity.class);
            i.putExtra("user",user);
            startActivity(i);
    }
}