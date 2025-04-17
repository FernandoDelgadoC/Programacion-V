package com.fernando_delgado.symphonie.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.fernando_delgado.symphonie.MainActivity;
import com.fernando_delgado.symphonie.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextNombre, editTextPuesto, editTextTelefono, editTextHorario, editTextCodigoAdmin;
    Button buttonReg;
    ProgressBar progressBar;
    TextView textView;
    CheckBox checkBoxAdmin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextNombre = findViewById(R.id.nombre);
        editTextPuesto = findViewById(R.id.puesto);
        editTextTelefono = findViewById(R.id.telefono);
        editTextHorario = findViewById(R.id.horario);
        editTextCodigoAdmin = findViewById(R.id.codigo_admin);
        checkBoxAdmin = findViewById(R.id.checkbox_admin);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        TextInputLayout layoutCodigoAdmin = findViewById(R.id.layout_codigo_admin);

        // Mostrar/ocultar campo código admin
        checkBoxAdmin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layoutCodigoAdmin.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Ir al login
        textView.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        // Registro
        buttonReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String nombre = editTextNombre.getText().toString().trim();
            String puesto = editTextPuesto.getText().toString().trim();
            String telefono = editTextTelefono.getText().toString().trim();
            String horario = editTextHorario.getText().toString().trim();
            boolean esAdmin = checkBoxAdmin.isChecked();
            String codigoAdmin = editTextCodigoAdmin.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(nombre) || TextUtils.isEmpty(puesto)) {
                Toast.makeText(Registro.this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(Registro.this, "La clave debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (esAdmin && !codigoAdmin.equals("4875")) {
                Toast.makeText(Registro.this, "Código de administrador inválido", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Guardar en Firestore
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("correo", email);
                    usuario.put("nombre", nombre);
                    usuario.put("puesto", puesto);
                    usuario.put("telefono", telefono);
                    usuario.put("horario", horario);
                    usuario.put("rol", esAdmin ? "admin" : "usuario");

                    db.collection("usuarios").document(user.getUid())
                            .set(usuario)
                            .addOnSuccessListener(unused -> Log.d("Firestore", "Usuario registrado"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al registrar", e));

                    Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Exception exception = task.getException();
                    String error = (exception != null) ? exception.getMessage() : "Error desconocido";
                    Log.e("RegistroError", "Fallo en Firebase Auth", exception);
                    Toast.makeText(Registro.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
