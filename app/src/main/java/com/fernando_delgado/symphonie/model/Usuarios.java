package com.fernando_delgado.symphonie.model;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fernando_delgado.symphonie.firestore.Usuario;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fernando_delgado.symphonie.R;

public class Usuarios extends AppCompatActivity {

    private EditText editTextCorreo, editTextNombre, editTextPuesto, editTextHorario;
    private RadioGroup radioGroupRol;
    private Spinner spinnerHorario;
    private Button btnGuardarUsuario;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Inicializar los campos y el botón
        editTextCorreo = findViewById(R.id.editTextCorreo);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextPuesto = findViewById(R.id.editTextPuesto);
        editTextHorario = findViewById(R.id.editTextHorario);
        radioGroupRol = findViewById(R.id.radioGroupRol);
        spinnerHorario = findViewById(R.id.spinnerHorario);
        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);

        // Configurar el Spinner para horario (Nocturno o Diurno)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.horarios_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHorario.setAdapter(adapter);

        // Configurar el botón de guardar
        btnGuardarUsuario.setOnClickListener(v -> {
            String correo = editTextCorreo.getText().toString();
            String nombre = editTextNombre.getText().toString();
            String puesto = editTextPuesto.getText().toString();
            String horario = editTextHorario.getText().toString();

            if (correo.isEmpty() || nombre.isEmpty() || puesto.isEmpty() || horario.isEmpty()) {
                Toast.makeText(Usuarios.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            } else {
                // Obtener el rol seleccionado
                int selectedRolId = radioGroupRol.getCheckedRadioButtonId();
                RadioButton selectedRol = findViewById(selectedRolId);
                String rol = selectedRol.getText().toString();

                // Obtener el horario seleccionado (diurno o nocturno)
                String horarioSeleccionado = spinnerHorario.getSelectedItem().toString();

                // Crear un objeto Usuario
                Usuario usuario = new Usuario(correo, nombre, puesto, horario, rol);

                // Guardar el usuario en Firestore
                db.collection("usuarios")
                        .add(usuario)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(Usuarios.this, "Usuario guardado con éxito", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Usuarios.this, "Error al guardar el usuario", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
