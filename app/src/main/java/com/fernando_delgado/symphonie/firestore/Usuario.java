package com.fernando_delgado.symphonie.firestore;

public class Usuario {
    private String correo;
    private String nombre;
    private String puesto;
    private String horario;
    private String rol;

    // Constructor vacío necesario para Firestore
    public Usuario() {}

    // Constructor con parámetros
    public Usuario(String correo, String nombre, String puesto, String horario, String rol) {
        this.correo = correo;
        this.nombre = nombre;
        this.puesto = puesto;
        this.horario = horario;
        this.rol = rol;
    }

    // Getters y setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
