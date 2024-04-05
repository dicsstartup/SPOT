package com.dicsstartup.spot.utils;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dicsstartup.spot.R;
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

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.chatListViewHolder> {
    private List<Amistad> data;
    private Context context;



    public AdapterChatList(Context context, List<Amistad> data) {
        this.data = data;
        this.context = context;

    }

    @NonNull
    @Override
    public chatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reunion_list, parent, false);
        return new chatListViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull chatListViewHolder holder, int position) {
        // Obtener la reunión actual en la posición dada
        Amistad chat = data.get(position);
        // Configurar los datos de la reunión en el ViewHolder
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class chatListViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avatarChat;
        TextView hora;
        TextView ultimoMensaje;
        TextView nombreChat;


        public chatListViewHolder(@NonNull View itemView) {
            super(itemView);
                avatarChat=itemView.findViewById(R.id.chat_avatar);
                hora=itemView.findViewById(R.id.hora_chat);
                ultimoMensaje=itemView.findViewById(R.id.ultimeMensaje_chat);
                nombreChat=itemView.findViewById(R.id.chat_nombre);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(Amistad chat) {
            DomainUser domainUser= new DomainUser();
            if(chat.getEstadoAmistad()== EstadoAmistad.ACEPTADA){
                domainUser.userById(FirebaseDatabase.getInstance().getReference(), chat.getIdAmigo(), new UploadCallbackSpot() {
                    @Override
                    public void onUploadCompleteUser(Usuario result) {
                        Picasso.get().load(result.getAvatar()).into(avatarChat);
                        nombreChat.setText(result.getNick());
                    }
                    @Override
                    public void onUploadCompleteSpot(Spot spot) {

                    }
                });
            }


        }

    }

}