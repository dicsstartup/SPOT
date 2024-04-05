package com.dicsstartup.spot.utils;


import static com.dicsstartup.spot.firebase.database.DomainUser.getUser;
import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.spot.SpotActivity;
import com.dicsstartup.spot.entities.Spot;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.database.DomainSPOT;
import com.dicsstartup.spot.interfaz.UploadCallbackSpot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSpotList extends BaseAdapter {

    DomainSPOT domainSPOT ;
    private List<Spot> data;
    private Context context;
        public AdapterSpotList(Context context, List<Spot> data) {
            this.data = data;
            this.context = context;
            domainSPOT = new DomainSPOT();
        }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            // Obtener el objeto actual
           Spot spot = (Spot) getItem(position);
            // Crear o reutilizar la vista
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lists_spot, parent, false);
            }
            // Asignar el texto y el icono
            TextView tipo = convertView.findViewById(R.id.item_tipo_spot);
            TextView nombre = convertView.findViewById(R.id.item_name_spot);
            TextView ciudad = convertView.findViewById(R.id.item_ubic_spot);
            TextView userCreator = convertView.findViewById(R.id.item_spot_user);
            ImageView imagenP = convertView.findViewById(R.id.item_image_spot);
            ImageView like = convertView.findViewById(R.id.item_like);
            ImageView avatar=convertView.findViewById(R.id.item_avatar);
            if(spot!=null){
                nombre.setText(spot.getNombre());
                tipo.setText(spot.getTipoSpot());
                ciudad.setText(spot.getNombreCiudad());
                like.setImageResource(R.drawable.icon_not_like);
                if(spot.getLikes()!=null){
                    if(spot.getLikes().contains(usuario.getId())){
                        like.setImageResource(R.drawable.icon_like);
                    }else{
                        like.setImageResource(R.drawable.icon_not_like);
                    }
                }else{
                    like.setImageResource(R.drawable.icon_not_like);
                }
                like.setOnClickListener(v -> {
                    if(spot.getLikes().contains(usuario.getId())){
                        domainSPOT.deleteLikeSpot(FirebaseDatabase.getInstance(),usuario.getId(),spot, result -> {
                            if (result) {
                                like.setImageResource(R.drawable.icon_not_like);

                            } else {
                                like.setImageResource(R.drawable.icon_like);
                            }
                        });

                    }else{
                        domainSPOT.addLikeSpot(FirebaseDatabase.getInstance(),usuario.getId(),spot,result -> {
                            if (result) {
                                like.setImageResource(R.drawable.icon_like);
                            } else {
                                like.setImageResource(R.drawable.icon_not_like);
                            }
                        });
                    }
                });
                Picasso.get().load(spot.getImagenes().get(0).getUri()).into(imagenP);
                imagenP.setOnClickListener(v -> {
                    Intent intent = new Intent(context, SpotActivity.class);
                    intent.putExtra("spot",spot);
                    context.startActivity(intent);

                });
                getUser(FirebaseDatabase.getInstance(),spot.getUsuarioCreador(), new UploadCallbackSpot() {
                    @Override
                    public void onUploadCompleteUser(Usuario result) {
                        if (result!=null) {
                            userCreator.setText(result.getNick());
                            Picasso.get().load(result.getAvatar()).into(avatar);

                        } else {
                            userCreator.setText("??");
                        }
                    }
                    @Override
                    public void onUploadCompleteSpot(Spot spot) {
                    }

                });

            }

            return convertView;
        }

}
