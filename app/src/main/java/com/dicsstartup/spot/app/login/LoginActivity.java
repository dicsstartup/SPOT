package com.dicsstartup.spot.app.login;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.dicsstartup.spot.firebase.autenticacion.RegistrarUsuario.dameToastdeerror;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dicsstartup.spot.R;
import com.dicsstartup.spot.app.home.HomeActivity;
import com.dicsstartup.spot.app.usuario.RegistrarActivity;
import com.dicsstartup.spot.utils.NetworkUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.Objects;

/**
 * Actividad de inicio de sesión que permite a los usuarios iniciar sesión en la aplicación.
 *
 */
public class LoginActivity extends AppCompatActivity {
    EditText email; // Campo de texto para introducir el correo electrónico
    EditText pass; // Campo de texto para introducir la contraseña
    Button login; // Botón de inicio de sesión
    String mail; // Correo electrónico introducido
    String password; // Contraseña introducida
    TextView registrar, olvido_pass; // Enlaces para registrar una nueva cuenta y restablecer la contraseña
    Context context = this; // Contexto de la actividad
    AwesomeValidation validation; // Validador para los campos de entrada
    ConstraintLayout layout; // Diseño de la actividad
    CheckBox checkBox; // Casilla de verificación para recordar sesión

    /**
     * Método llamado cuando se crea la actividad.
     * dandole funcionalidad a la vista
     * comprueba un archivo props y si existe y no esta vacio pasa directamente al home
     *
     * @param savedInstanceState El estado anteriormente guardado de la actividad.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        issave();
        // Configurar el modo nocturno
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        // Configurar la orientación de la pantalla
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        validation = new AwesomeValidation(BASIC);
        // Obtener referencias a las vistas del diseño de la actividad
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);
        login = findViewById(R.id.login_button);
        registrar = findViewById(R.id.login_registrar);
        olvido_pass = findViewById(R.id.login_olvido);
        checkBox = findViewById(R.id.login_cheak);

        // Validar los campos de entrada
        validation.addValidation(this, R.id.login_email, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        validation.addValidation(this, R.id.login_password, "^[^\\s]+$", R.string.espacio);

        // Ocultar el enlace para restablecer la contraseña
        olvido_pass.setVisibility(View.INVISIBLE);
        olvido_pass.setOnClickListener(v -> resetPassword(email.getText().toString()));

        // Acción al hacer clic en el enlace para registrar una nueva cuenta
        registrar.setOnClickListener(view -> {
            Intent intent = new Intent(context, RegistrarActivity.class);
            startActivity(intent);
        });

        // Acción al hacer clic en el botón de inicio de sesión
        login.setOnClickListener(view -> {
            mail = email.getText().toString();
            password = pass.getText().toString();
            Login(mail, password);
        });
    }

    /**
     * Método para verificar si ya hay una seccion activa
     */
    public void issave() {
        layout = findViewById(R.id.loginContraint);
        layout.setVisibility(View.INVISIBLE);
        // Obtener las preferencias compartidas
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.SPOT_pref), MODE_PRIVATE);
        // Obtener el correo electrónico y la contraseña guardados
        String email = prefs.getString("email", null);
        String pass = prefs.getString("pass", null);
        Boolean cheak= prefs.getBoolean("cheak",false);
        if (email != null & pass != null) {
            // Verificar la conexión a Internet
            boolean isConnected = NetworkUtils.isNetworkAvailable(context);
            if (isConnected) {
                // Hay conexión a Internet
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.putExtra("cheak",cheak);
                i.putExtra("email",email);
                i.putExtra("pass",pass);
                startActivity(i);
            } else {
                Toast.makeText(context, "No hay Coneccion a Internet ", Toast.LENGTH_SHORT).show();
            }
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Método para realizar el inicio de sesión del usuario.
     * usando el api de Firebase Authentication
     * @param mail Correo electrónico del usuario.
     * @param ps   Contraseña del usuario.
     */
    public void Login(String mail, String ps) {
        // Verificar la conexión a Internet
        boolean isConnected = NetworkUtils.isNetworkAvailable(context);
        if (isConnected) {
            if (validation.validate()) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(mail, ps).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        i.putExtra("cheak", checkBox.isChecked());
                        i.putExtra("email", mail);
                        i.putExtra("pass", ps);
                        startActivity(i);
                    } else {
                        login.setEnabled(true);
                        layout.setVisibility(View.VISIBLE);
                        String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                        dameToastdeerror(errorCode, context, email, pass, olvido_pass);
                    }
                });
            }
        } else {
            Toast.makeText(context, "No hay Coneccion a Internet ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método para restablecer la contraseña del usuario.
     * envia un correo al gmail para cambiarla de manera facil
     *
     * @param email Correo electrónico del usuario.
     */
    private void resetPassword(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se enviara un correo a tu email para que cambies la contraseña");
        // Configurar botón "Aceptar"
        builder.setPositiveButton("aceptar", (dialog, which) -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(email);
            dialog.dismiss(); // Cerrar la ventana emergente
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
