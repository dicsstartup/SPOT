package com.dicsstartup.spot.interfaz;

import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;

public interface UploadCallbackSpot {
    void onUploadCompleteUser(Usuario result);
    void onUploadCompleteSpot(Spot spot);
}
