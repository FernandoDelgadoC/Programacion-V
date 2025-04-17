package com.fernando_delgado.symphonie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fernando_delgado.symphonie.Funciones.Usuarios
import com.fernando_delgado.symphonie.Funciones.asistencias
import com.fernando_delgado.symphonie.Login.Login
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userDetails: TextView
    private lateinit var logoutButton: Button
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase y GoogleSignIn setup
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        if (user == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // GoogleSignInClient setup
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Views
        userDetails = findViewById(R.id.user_details)
        logoutButton = findViewById(R.id.logout)
        val btnRegistroUsuarios = findViewById<Button>(R.id.registros_usuarios)
        val btnRegistroAsistencia = findViewById<Button>(R.id.registro_asistencia)
        val btnReportes = findViewById<Button>(R.id.reportes)

        // Ocultar botones de admin por defecto
        btnRegistroUsuarios.visibility = View.GONE
        btnReportes.visibility = View.GONE

        // Firestore
        val db = FirebaseFirestore.getInstance()
        val emailUsuario = user?.email

        if (emailUsuario != null) {
            db.collection("usuarios")
                .whereEqualTo("correo", emailUsuario)
                .get()
                .addOnSuccessListener { documentos ->
                    if (!documentos.isEmpty) {
                        val doc = documentos.documents[0]
                        val nombre = doc.getString("nombre")
                        val rol = doc.getString("rol")

                        userDetails.text = "¡Bienvenido, $nombre!"

                        // Mostrar botones si es administrador
                        if (rol == "admin") {
                            btnRegistroUsuarios.visibility = View.VISIBLE
                            btnReportes.visibility = View.VISIBLE
                        }

                        // Acciones para botón registros de usuarios
                        btnRegistroUsuarios.setOnClickListener {
                            startActivity(Intent(this, Usuarios::class.java))
                        }

                        // TODO: Configura btnReportes cuando tengas la pantalla
                        btnReportes.setOnClickListener {
                            // startActivity(Intent(this, Reportes::class.java))
                        }

                    } else {
                        userDetails.text = "¡Bienvenido!"
                    }
                }
                .addOnFailureListener {
                    userDetails.text = "¡Bienvenido!"
                }
        }

        // Botón de asistencia
        btnRegistroAsistencia.setOnClickListener {
            startActivity(Intent(this, asistencias::class.java))
        }

        // Logout
        logoutButton.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                startActivity(Intent(this, Login::class.java))
                finish()
            }
        }
    }
}
