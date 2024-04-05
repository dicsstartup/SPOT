package com.dicsstartup.spot.interfaz;


import com.dicsstartup.spot.entities.Spot;

import java.util.ArrayList;

public interface UploadCallbackSPOTs {
    void onUploadComplete(ArrayList<Spot> result);
}