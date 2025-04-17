package com.fernando_delgado.symphonie.Funciones;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fernando_delgado.symphonie.MainActivity;
import com.fernando_delgado.symphonie.R;
import com.fernando_delgado.symphonie.adaptadores.AsistenciaAdapter;
import com.fernando_delgado.symphonie.model.Asistencia;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class asistencias extends AppCompatActivity {

    private Button btnRegistrarAsistencia;
    private RecyclerView rvAsistencias;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private boolean isEntrada = true;  // Usaremos esta variable para alternar entre entrada y salida

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_asistencias);

        btnRegistrarAsistencia = findViewById(R.id.btnRegistrarAsistencia);
        rvAsistencias = findViewById(R.id.rvAsistencias);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        rvAsistencias.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegistrarAsistencia.setOnClickListener(v -> registrarAsistencia());

        cargarAsistencias();

        Button btnVolverMenu = findViewById(R.id.btnVolverMenu);
        btnVolverMenu.setOnClickListener(v -> {
            // Crear un Intent para ir a la pantalla principal (MainActivity)
            Intent intent = new Intent(asistencias.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registrarAsistencia() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String correoUsuario = user.getEmail();
        String correoSanitizado = correoUsuario != null ? correoUsuario.replace(".", "_") : "usuario";

        // Obtener fecha y hora actual
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Generar un ID único para el documento
        String estado = isEntrada ? "Entrada" : "Salida";
        String docId = correoSanitizado + "_" + fecha + "_" + estado + "_" + System.currentTimeMillis();

        // Crear mapa con los datos
        Map<String, Object> asistencia = new HashMap<>();
        asistencia.put("usuario", correoUsuario);
        asistencia.put("fecha", fecha);
        asistencia.put("hora", hora);
        asistencia.put("estado", estado);  // Guardamos el estado en la base de datos

        // Guardar en Firestore con ID personalizado
        firestore.collection("asistencia")
                .document(docId)
                .set(asistencia)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Asistencia registrada como " + estado, Toast.LENGTH_SHORT).show();
                    cargarAsistencias(); // recargar lista
                    toggleBotonEstado();  // Cambiar el texto del botón
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar asistencia", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarAsistencias() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String correoUsuario = user.getEmail();

        firestore.collection("asistencia")
                .whereEqualTo("usuario", correoUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Asistencia> lista = new ArrayList<>();
                    for (var doc : queryDocumentSnapshots) {
                        String fecha = doc.getString("fecha");
                        String hora = doc.getString("hora");
                        String estado = doc.getString("estado");  // Obtener el estado
                        lista.add(new Asistencia(fecha, hora,estado));  // Pasar el estado al modelo
                    }

                    AsistenciaAdapter adapter = new AsistenciaAdapter(lista);
                    rvAsistencias.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar asistencias", Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleBotonEstado() {
        // Alternar entre "Registrar Entrada" y "Registrar Salida"
        isEntrada = !isEntrada;
        if (isEntrada) {
            btnRegistrarAsistencia.setText("Registrar Entrada");
        } else {
            btnRegistrarAsistencia.setText("Registrar Salida");
        }
    }
}
