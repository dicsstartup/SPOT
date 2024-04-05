package com.dicsstartup.spot.app.add;


import static android.content.ContentValues.TAG;
import static com.dicsstartup.spot.app.add.InfoADD.image;
import static com.dicsstartup.spot.app.add.InfoADD.imageEnServidor;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.ImagenSpot;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.firebase.storege.Images;
import com.dicsstartup.spot.interfaz.HiloCallback;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class AddSpotSubidaActivity extends AppCompatActivity implements HiloCallback {

    TextView comentarios;
    ProgressBar barra;
    FirebaseDatabase db;
    Spot spot ;
    boolean editar;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subida_spot);
        spot = getIntent().getSerializableExtra("spot", Spot.class);
        editar = getIntent().getBooleanExtra("editar", false);
        comentarios = findViewById(R.id.comentarios);
        db = FirebaseDatabase.getInstance();
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        Images subir = new Images();
        barra = findViewById(R.id.progressBar);
        if (editar) {
            MiHilo miHilo = new MiHilo(this,spot,storageRef);
            miHilo.start();
        }else{
            spot.setImagenes(new ArrayList<>());
            for (Uri imagenUri : image) {
                subir.subirImagenSpot(storageRef, imagenUri,spot,db, result -> {
                    if (result) {
                        comentarios.setText("se creo el Spot");
                        finish();
                    } else {
                        comentarios.setText("error");
                        finish();
                    }
                },image.size());
        }
        }
    }

    @Override
    public void onHiloFinalizado() {
        finish();
    }


    // Clase del hilo
    class MiHilo extends Thread {
        private HiloCallback callback;
        private Spot spot;
        private FirebaseStorage ref;

        public MiHilo(HiloCallback callback,Spot spot,FirebaseStorage ref) {
            this.callback = callback;
            this.spot=spot;
            this.ref=ref;
        }

        @Override
        public void run() {
            // Lógica del hilo
            List<Uri> enServidor=imageEnServidor();
            List<ImagenSpot> eliminados = new ArrayList<>();
            List<ImagenSpot> ultimaLista = new ArrayList<>();
            DomainSPOT domainSPOT = new DomainSPOT();
            for (ImagenSpot objeto : spot.getImagenes()) {
                if (!enServidor.contains(Uri.parse(objeto.getUri()))) {
                    eliminados.add(objeto);
                    Log.d(TAG, "Este los objetos que filtro"+objeto.getPath());
                }else{
                    ultimaLista.add(objeto);
                }
            }
             Images domainStore= new Images();

            for(ImagenSpot delete:eliminados){
                domainStore.eliminarImagenSpot(ref,delete, result -> {
                    comentarios.setText("Imagen eliminada");
                });
            }

            spot.setImagenes(ultimaLista);
            if (image.isEmpty()) {
                domainSPOT.actualizarSpot(db, spot, result -> {
                    if (result) {
                        comentarios.setText("se actualizo el Spot");
                        finish();
                    } else {
                        comentarios.setText("error");
                        finish();
                    }
                });

            }else{
                AtomicInteger contador= new AtomicInteger(image.size());
                for (Uri imagenUri : image) {
                    domainStore.subirImagenSpotEditar( ref, imagenUri, spot,  result -> {
                        if(result!=null){
                            spot.getImagenes().add(result);
                           contador.getAndDecrement();
                           if(contador.get()==0){
                              domainSPOT.actualizarSpot(db, spot, resultado -> {
                                   if (resultado) {
                                       comentarios.setText("se actualizo el Spot");
                                   } else {
                                       comentarios.setText("error");
                                   }
                               });

                           }
                        }
                    });
                }

            }

            // Al finalizar el hilo, avisar a la Activity
            if (callback != null) {
                callback.onHiloFinalizado();
            }
        }
    }
}





