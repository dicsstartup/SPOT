package com.dicsstartup.spot.app.add;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class InfoADD {

   public static ArrayList<Uri> image = new ArrayList<>();


   public static List<Uri> imageEnServidor(){
       ArrayList<Uri> enServidor= new ArrayList<>();
        for(Uri u:image){
            String scheme = u.getScheme();
            if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
             enServidor.add(u);
            }
        }
       image.removeAll(enServidor);
       return enServidor;
   }
}
