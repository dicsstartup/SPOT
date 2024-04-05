package com.dicsstartup.spot.app.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.fragment.app.Fragment;

import com.dicsstartup.spot.R;


/**
 * Fragmento utilizado para mostrar la página de la tienda.
 */
public class ShopFragment extends Fragment {
    WebView web;

    /**
     * Constructor de la clase ShopFragment.
     */
    public ShopFragment() {
    }

    /**
     * Método llamado para crear y devolver la vista del fragmento. obteniedo el WebView de la vista
     * y cargandole con la tienda prestablecida. se habilita funciones como:
     * javaScript
     * Zoom
     * @param inflater           el inflater utilizado para inflar el diseño del fragmento
     * @param container          el contenedor que contiene el fragmento
     * @param savedInstanceState el estado previamente guardado del fragmento, o nulo si no hay ninguno
     * @return la vista creada del fragmento
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        web = view.findViewById(R.id.shop_web);
        web.getSettings().setJavaScriptEnabled(true);
        // Habilita el zoom y la escala de la página
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        // Configura un cliente web para manejar la navegación
        web.setWebViewClient(new WebViewClient());
        // Carga una URL
        web.loadUrl("https://monarksupply.com/es/");
        return view;
    }
}