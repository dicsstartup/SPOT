package com.dicsstartup.spot.app.usuario;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;
import static com.dicsstartup.spot.firebase.autenticacion.RegistrarUsuario.nick;
import static com.dicsstartup.spot.utils.InfoGeneral.usuario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.dicsstartup.spot.R;
import com.dicsstartup.spot.firebase.database.DomainUser;
import com.dicsstartup.spot.firebase.storege.Images;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
/**
 * Activity que permite editar los datos de un usuario.
 */
public class EditUsuarioActivity extends AppCompatActivity {
    private ShapeableImageView editAvatar;
    private TextView editErr;
    private EditText editNick;
    private Button editButton;
    boolean permisosImages = false;
    AwesomeValidation validation;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    public Uri imageEdit;
    DomainUser domainUser = new DomainUser();
    Images images = new Images();

    /**
     * Lanzador de actividad de resultado para seleccionar una imagen de la galería.
     */
    private final ActivityResultLauncher<Intent> myLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    imageEdit = result.getData().getData();
                    Picasso.get().load(imageEdit).into(editAvatar);
                }
            }
    );

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_usuario);
        validation = new AwesomeValidation(BASIC);
        ImageView closeEdit = findViewById(R.id.closeEdit);
        ImageView addImage = findViewById(R.id.imageView2);
        editAvatar = findViewById(R.id.edit_avatar);
        editErr = findViewById(R.id.edit_err);
        editNick = findViewById(R.id.edit_nick);
        TextView editCambiarAvatar = findViewById(R.id.edit_cambiarAvatar);
        editButton = findViewById(R.id.edit_button);
        Button editCambiarC = findViewById(R.id.edit_cambiarC);

        // Código para cerrar la actividad al hacer clic en el botón de cerrar
        closeEdit.setOnClickListener(v -> finish());

        // Código para abrir la galería al hacer clic en el botón de cambiar avatar
        editCambiarAvatar.setOnClickListener(v -> Image());
        addImage.setOnClickListener(v -> Image());

        // Cargar el avatar actual del usuario usando la biblioteca Picasso
        Picasso.get().load(usuario.getAvatar()).into(editAvatar);

        // Establecer el texto del EditText con el nick actual del usuario
        editNick.setText(usuario.getNick());

        // Agregar un TextWatcher al EditText para detectar cambios en el texto
        editNick.addTextChangedListener(new TextWatcher() {
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
                    String text = editNick.getText().toString();

                    if (TextUtils.isEmpty(text)) {
                        editErr.setText("");
                    } else {
                        if (!text.equals(usuario.getNick())) {
                            nick(text, editErr);
                        }
                        // El EditText tiene contenido
                    }
                };
                handler.postDelayed(runnable, 500); // Espera 500ms antes de ejecutar la tarea
            }
        });

        // Mostrar un diálogo de confirmación al hacer clic en el botón de cambiar contraseña
        editCambiarC.setOnClickListener(v -> showPopup());

        // Registro del lanzador de permisos de lectura de imágenes
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                permisosImages = true;
            } else {
                permisosImages = false;
                Toast.makeText(getApplicationContext(), "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        });

        // Manejar el clic en el botón de editar
        editButton.setOnClickListener(v -> {
            String n = editNick.getText().toString();
            if (validation.validate()) {
                if (!editErr.getText().equals("*Nick no disponible")) {
                    showProgressDialog();
                    editButton.setEnabled(false);
                    usuario.setNick(n);

                    if (imageEdit != null) {
                        images.subirAvatar(FirebaseStorage.getInstance(), imageEdit, usuario, FirebaseDatabase.getInstance(), result -> {
                            hideProgressDialog();
                            PerfilActivity.user = usuario;
                        });
                    } else {
                        domainUser.updateUser(FirebaseDatabase.getInstance(), usuario, result -> {
                            hideProgressDialog();
                            PerfilActivity.user = usuario;
                        });
                    }

                    editButton.setEnabled(true);
                } else {
                    editErr.setText("*Nick no disponible");
                }
            }
        });

        // Agregar las reglas de validación para el campo de nick
        validation.addValidation(this, R.id.register_nick, ".{4,}", R.id.edit_nick);
        validation.addValidation(this, R.id.register_nick, "^[^\\s]+$", R.id.edit_nick);
    }

    /**
     * Método para seleccionar una imagen de la galería.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("IntentReset")
    public void Image() {
        if (permisosImages) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/");
            myLauncher.launch(intent);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    }

    /**
     * Muestra un diálogo de confirmación para cambiar la contraseña.
     */
    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Se enviará un correo a tu email para que cambies la contraseña");

        // Configurar botón "Aceptar"
        builder.setPositiveButton("aceptar", (dialog, which) -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.sendPasswordResetEmail(usuario.getEmail());
            dialog.dismiss(); // Cerrar la ventana emergente
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Muestra un diálogo de progreso.
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    ProgressDialog progressDialog;

    /**
     * Oculta el diálogo de progreso.
     */
    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
