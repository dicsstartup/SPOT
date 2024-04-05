package com.dicsstartup.spot.firebase.autenticacion;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dicsstartup.spot.app.home.HomeActivity;
import com.dicsstartup.spot.entities.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

/**
 * Clase de utilidad para registrar un nuevo usuario en la base de datos.
 */
public class RegistrarUsuario {

    /**
     * Verifica si un nombre de usuario (nick) está disponible.
     *
     * @param nick El nombre de usuario a verificar.
     * @param err  TextView donde mostrar el mensaje de error en caso de que el nick no esté disponible.
     */
    public static void nick(String nick, TextView err) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Usuario");

        // Consultar si existe algún registro con el mismo nick
        reference.orderByChild("nick").equalTo(nick).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    err.setText("*Nick no disponible");
                } else {
                    err.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Método cancelado
            }
        });
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * crea el usuario en Firebase Authentication
     * crea usuario en Firebase Database Realtime
     * y si sale correcto se envia a home
     *
     * @param user     El usuario a registrar.
     * @param err      TextView donde mostrar los mensajes de error.
     * @param c        Contexto de la aplicación.
     * @param et_mail  EditText del correo electrónico del usuario.
     * @param et_pass  EditText de la contraseña del usuario.
     * @param btAdd    Botón de registro.
     */
    public static void Registrar(Usuario user, TextView err, Context c, EditText et_mail, EditText et_pass, Button btAdd) {
        btAdd.setEnabled(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        // Ejecutar una transacción para registrar el usuario
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String ps = et_pass.getText().toString();
                FirebaseAuth auth = FirebaseAuth.getInstance();

                try {
                    auth.createUserWithEmailAndPassword(user.getEmail(), ps).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            auth.signInWithEmailAndPassword(user.getEmail(), ps);
                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference ref1 = db.getReference(Usuario.class.getSimpleName());
                            // Guardar el usuario en la base de datos
                            if (ref1.push().setValue(user).isSuccessful()) {
                                FirebaseUser user1 = auth.getCurrentUser();
                                user1.delete();
                            }
                        } else {
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            dameToastdeerror(errorCode, c, et_mail, et_pass, err);
                        }
                    });
                    return Transaction.success(currentData);
                } catch (Exception e) {
                    return Transaction.abort();
                }
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d("Transacción", "Transacción completada correctamente");
                Intent i = new Intent(c, HomeActivity.class);
                i.putExtra("cheak", true);
                i.putExtra("email", user.getEmail());
                i.putExtra("pass", user.getPassword());
                i.putExtra("registro",true);
                c.startActivity(i);
                btAdd.setEnabled(true);
            }
        });
    }

    /**
     * Muestra un Toast de error con el código de error especificado.
     * ademas de un alert el editext que presente el fallo
     * si se equivoca en la contraseña muestra el boton para cambiarla
     *
     * @param error      El código de error.
     * @param c          Contexto de la aplicación.
     * @param et_mail    EditText del correo electrónico.
     * @param et_pass    EditText de la contraseña.
     * @param olvido_pass TextView para restablecer la contraseña.
     */

    public static void dameToastdeerror(String error, Context c, EditText et_mail, EditText et_pass, TextView olvido_pass) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                Toast.makeText(c, "El formato del token personalizado es incorrecto. Por favor revise la documentación", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                Toast.makeText(c, "El token personalizado corresponde a una audiencia diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_CREDENTIAL":
                Toast.makeText(c, "La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_INVALID_EMAIL":
                Toast.makeText(c, "La dirección de correo electrónico está mal formateada.", Toast.LENGTH_LONG).show();
                et_mail.setError("La dirección de correo electrónico está mal formateada.");
                et_mail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                Toast.makeText(c, "La contraseña no es válida o el usuario no tiene contraseña.", Toast.LENGTH_LONG).show();
                et_pass.setError("la contraseña es incorrecta ");
                et_pass.requestFocus();
                olvido_pass.setVisibility(View.VISIBLE);
                et_pass.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                Toast.makeText(c, "Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                Toast.makeText(c, "Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                Toast.makeText(c, "Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                Toast.makeText(c, "La dirección de correo electrónico ya está siendo utilizada por otra cuenta..   ", Toast.LENGTH_LONG).show();
                et_mail.setError("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                et_mail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                Toast.makeText(c, "Esta credencial ya está asociada con una cuenta de usuario diferente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_DISABLED":
                Toast.makeText(c, "La cuenta de usuario ha sido inhabilitada por un administrador..", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_TOKEN_EXPIRED":

            case "ERROR_INVALID_USER_TOKEN":
                Toast.makeText(c, "La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_USER_NOT_FOUND":
                Toast.makeText(c, "No hay ningún registro de usuario que corresponda a este identificador. Es posible que se haya eliminado al usuario.", Toast.LENGTH_LONG).show();
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                Toast.makeText(c, "Esta operación no está permitida. Debes habilitar este servicio en la consola.", Toast.LENGTH_LONG).show();
                break;


        }

    }
}
