package com.dicsstartup.spot.app.usuario;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.dicsstartup.spot.firebase.autenticacion.RegistrarUsuario.nick;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dicsstartup.spot.R;
import com.dicsstartup.spot.entities.Usuario;
import com.dicsstartup.spot.firebase.autenticacion.RegistrarUsuario;

/**
 * Clase que representa la actividad de registro de usuario.
 */
public class RegistrarActivity extends AppCompatActivity {
    EditText nick, Email, pass, passC;
    TextView err;
    AwesomeValidation validation;
    Button registrar_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        validation = new AwesomeValidation(BASIC);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        // Inicialización de variables y referencias a los elementos de la interfaz
        nick = findViewById(R.id.register_nick);
        registrar_btn = findViewById(R.id.register_button);
        Email = findViewById(R.id.register_email);
        pass = findViewById(R.id.register_password);
        passC = findViewById(R.id.register_password_confirm);
        err = findViewById(R.id.register_err);

        // Agregar TextWatcher al EditText 'nick' para manejar el evento de que el usuario dejó de escribir
        nick.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se utiliza en este ejemplo
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se utiliza en este ejemplo
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Cancela la tarea programada anteriormente para evitar múltiples tareas en cola
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }

                // Programa una tarea para manejar el evento de que el usuario dejó de escribir
                runnable = () -> {
                    String text = nick.getText().toString();

                    if (TextUtils.isEmpty(text)) {
                        err.setText("");
                    } else {
                        // El EditText tiene contenido
                        nick("@" + text, err);
                    }
                    // Aquí se coloca la lógica para manejar el evento de que el usuario dejó de escribir
                };
                handler.postDelayed(runnable, 500); // Espera 500ms antes de ejecutar la tarea
            }
        });

        // Agregar reglas de validación utilizando AwesomeValidation
        validation.addValidation(this, R.id.register_nick, "^[^\\s]+$", R.string.espacio);
        validation.addValidation(this, R.id.register_email, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        validation.addValidation(this, R.id.register_password, "^[^\\s]{6,}$", R.string.invalid_pass);
        validation.addValidation(this, R.id.register_password_confirm, R.id.register_password, R.string.invalid_passC);
        validation.addValidation(this, R.id.register_nick, ".{4,}", R.string.invalid);
    }

    /**
     * Método invocado cuando se hace clic en la imagen para registrar.
     *
     * @param view La vista que disparó el evento.
     */
    @SuppressLint("SetTextI18n")
    public void onImageRegistrar(View view) {
        String n = nick.getText().toString();
        String em = Email.getText().toString();

        // Validar los campos de entrada utilizando AwesomeValidation
        if (validation.validate()) {
            if (!err.getText().equals("*Nick no disponible")) {
                registrar_btn.setEnabled(false);
                Usuario u = new Usuario("@" + n, em);
                u.setPassword(pass.getText().toString());
                RegistrarUsuario.Registrar(u, err, this, Email, pass, registrar_btn);
                registrar_btn.setEnabled(true);
            } else {
                err.setText("*Nick no disponible");
            }
        }
    }
}
