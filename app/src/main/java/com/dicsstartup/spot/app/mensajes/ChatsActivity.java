package com.dicsstartup.spot.app.mensajes;

import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Amistad;
import com.dicsstartup.spot.utils.AdapterChatList;

import java.util.ArrayList;

public class ChatsActivity extends AppCompatActivity {

    RecyclerView chatsList;
    ArrayList<Amistad> chats =new ArrayList<>();
    ImageButton regress;
    ImageButton options;
    AdapterChatList adapterChatList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        chatsList=findViewById(R.id.listChats);
        regress=findViewById(R.id.regresChat);
        regress.setOnClickListener(v->finish());
        options=findViewById(R.id.opcionesChats);
        options.setOnClickListener(v->showMenu());
        adapterChatList= new AdapterChatList(this,chats);
        chats.addAll(usuario.amigos);
        adapterChatList.notifyDataSetChanged();

    }

    PopupMenu popupMenu;
    private void showMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupMenuStyle);
        popupMenu = new PopupMenu( wrapper, options); // Crea una instancia de PopupMenu
        popupMenu.getMenuInflater().inflate(R.menu.menu_chats, popupMenu.getMenu()); // Infla el menú desde un archivo XML

        // Configura un listener para manejar la selección de las opciones del menú
        popupMenu.setOnMenuItemClickListener(item -> {
            // Aquí se ejecutará el código correspondiente a cada opción del menú seleccionada
            switch (item.getItemId()) {
                case R.id.chat1:
                    Intent i = new Intent(getApplicationContext(), SolicitudesActivity.class);
                    startActivity(i);
                    return true;
                case R.id.chat2:
                    // Código para la opción 2
                    return true;
                case R.id.chat3:

                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show(); // Muestra el menú emergente
    }

}