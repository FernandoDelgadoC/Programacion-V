package com.fernando_delgado.symphonie.Funciones;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fernando_delgado.symphonie.MainActivity;
import com.fernando_delgado.symphonie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class horario extends AppCompatActivity {

    private TextView tvPuesto, tvHorario;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);

        // Inicialización de las vistas
        tvPuesto = findViewById(R.id.tvPuesto);
        tvHorario = findViewById(R.id.tvHorario);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        cargarHorarioDelUsuario();

        // Botón para volver al menú principal
        Button btnVolverMenu = findViewById(R.id.btnVolverMenuPrincipal);
        btnVolverMenu.setOnClickListener(v -> {
            Intent intent = new Intent(horario.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    private void cargarHorarioDelUsuario() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String correo = user.getEmail();
        if (correo == null) {
            Toast.makeText(this, "Correo no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        // Consulta en Firestore para encontrar el usuario por correo
        firestore.collection("usuarios")
                .whereEqualTo("correo", correo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String tipoHorario = doc.getString("horario");
                        String puesto = doc.getString("puesto");  // Recuperar el puesto del usuario

                        // Mostrar el puesto
                        tvPuesto.setText("Puesto: " + puesto); // Aquí asignamos el puesto al TextView

                        // Mostrar el horario dependiendo del tipo
                        String displayText = "";
                        if ("Diurno".equalsIgnoreCase(tipoHorario)) {
                            displayText =
                                    "Horario: Diurno\n" +
                                            "Lunes: 9 a.m. a 6 p.m.\n" +
                                            "Martes: 9 a.m. a 6 p.m.\n" +
                                            "Miércoles: 9 a.m. a 6 p.m.\n" +
                                            "Jueves: 9 a.m. a 6 p.m.\n" +
                                            "Viernes: 9 a.m. a 6 p.m.\n" +
                                            "Sábado: Libre\n" +
                                            "Domingo: Libre";
                        } else if ("Nocturno".equalsIgnoreCase(tipoHorario)) {
                            displayText =
                                    "Horario: Nocturno\n" +
                                            "Lunes: 6 p.m. a 2 a.m.\n" +
                                            "Martes: 6 p.m. a 2 a.m.\n" +
                                            "Miércoles: 6 p.m. a 2 a.m.\n" +
                                            "Jueves: 6 p.m. a 2 a.m.\n" +
                                            "Viernes: 6 p.m. a 2 a.m.\n" +
                                            "Sábado: Libre\n" +
                                            "Domingo: Libre";
                        } else {
                            displayText = "Horario no especificado.";
                        }

                        // Mostrar la información completa del horario
                        tvHorario.setText(displayText);

                        // Agregar borde alrededor del horario
                        GradientDrawable horarioBorder = new GradientDrawable();
                        horarioBorder.setShape(GradientDrawable.RECTANGLE);
                        horarioBorder.setStroke(2, Color.BLACK); // Borde de 2px de grosor
                        horarioBorder.setCornerRadius(8); // Esquinas redondeadas de 8px
                        tvHorario.setBackground(horarioBorder); // Aplica el borde al TextView de horario

                    } else {
                        Toast.makeText(this, "Usuario no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                        tvHorario.setText("Usuario no encontrado.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al consultar Firestore", Toast.LENGTH_SHORT).show();
                    tvHorario.setText("Error al cargar el horario.");
                    e.printStackTrace();
                });
    }
}
