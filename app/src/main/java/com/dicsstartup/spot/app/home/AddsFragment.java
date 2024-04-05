package com.dicsstartup.spot.app.home;

import static com.dicsstartup.spot.firebase.database.DomainAdds.listAdds;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Adds;
import com.dicsstartup.spot.enumerados.TipoAdd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Fragmento utilizado para mostrar anuncios y contenido web.
 */
public class AddsFragment extends Fragment {
    ImageView adds1;
    ImageView adds2;
    WebView web;
    ArrayList<Adds> findAllAdds = new ArrayList<>();

    /**
     * Constructor de la clase AddsFragment.
     */
    public AddsFragment() {
    }

    /**
     * Método llamado para crear y devolver la vista del fragmento.
     * obteniendo todas las referencias a los objetos de la vista.
     * llamado a Firebase Databse y obtiene todos los links y los muestra en la pagina web o lo imageView
     *
     * @param inflater           el inflater utilizado para inflar el diseño del fragmento
     * @param container          el contenedor que contiene el fragmento
     * @param savedInstanceState el estado previamente guardado del fragmento, o nulo si no hay ninguno
     * @return la vista creada del fragmento
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adds, container, false);
        adds1 = view.findViewById(R.id.adds_image1);
        adds2 = view.findViewById(R.id.adds_image2);
        web = view.findViewById(R.id.adds_wed);
        ArrayList<ImageView> addsImage = new ArrayList<>();
        addsImage.add(adds1);
        addsImage.add(adds2);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Adds.class.getSimpleName());
        listAdds(ref, findAllAdds, result -> {
            if (result) {
                for (Adds add : findAllAdds) {
                    if (add.getTipoadd().equals(TipoAdd.IMAGE)) {
                        if (addsImage.size() > 0) {
                            image(add.getUrlmedia(), addsImage.get(addsImage.size() - 1));
                            addsImage.remove(addsImage.size() - 1);
                        }
                    } else {
                        web(add.getUrlmedia(), web);
                    }
                }
            }
        });
        return view;
    }

    /**
     * Método utilizado para cargar una imagen desde una URL en un ImageView utilizando Picasso.
     *
     * @param url   la URL de la imagen
     * @param image el ImageView en el que se cargará la imagen
     */
    public void image(String url, ImageView image) {
        Picasso.get().load(url).into(image);
    }

    /**
     * Método utilizado para cargar una página web en un WebView.
     *
     * @param url la URL de la página web
     * @param web el WebView en el que se cargará la página web
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void web(String url, WebView web) {
        web.getSettings().setJavaScriptEnabled(true);
        // Habilita el zoom y la escala de la página
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        // Configura un cliente web para manejar la navegación
        web.setWebViewClient(new WebViewClient());
        // Carga una URL
        web.loadUrl(url);
    }
}