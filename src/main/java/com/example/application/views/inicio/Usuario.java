package com.example.application.views.inicio;

public class Usuario {
    private String nombre;
    private String correo;
    private String contraseña;

    public Usuario(String nombre, String correo, String contraseña) {
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña = contraseña;
    }

    public boolean camposCompletos() {
        return nombre != null && !nombre.isBlank()
            && correo != null && !correo.isBlank()
            && contraseña != null && !contraseña.isBlank();
    }

    public boolean credencialesValidas(String correoIngresado, String contraseñaIngresada) {
        return this.correo.equalsIgnoreCase(correoIngresado)
            && this.contraseña.equals(contraseñaIngresada);
    }

    public boolean mismoCorreo(String correoIngresado) {
        return this.correo.equalsIgnoreCase(correoIngresado);
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContraseña() { return contraseña; }
}