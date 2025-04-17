package com.fernando_delgado.symphonie.firestore;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreHelper {

    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void guardarUsuario(Usuario usuario) {
        // Obtiene el correo del usuario
        String correo = usuario.getCorreo();

        // Usa el correo como ID del documento para asegurarse de que no se dupliquen
        db.collection("usuarios")
                .document(correo)  // Usamos el correo como ID del documento
                .set(usuario)  // Guarda los datos del usuario
                .addOnSuccessListener(aVoid -> {
                    // Puedes manejar la respuesta de éxito aquí
                    Log.d("Firestore", "Usuario guardado exitosamente.");
                })
                .addOnFailureListener(e -> {
                    // Puedes manejar el error aquí
                    Log.e("Firestore", "Error al guardar el usuario: ", e);
                });
    }
}

