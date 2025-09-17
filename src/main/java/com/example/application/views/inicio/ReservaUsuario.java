package com.example.application.views.inicio;

public class ReservaUsuario extends Reserva {
    private Usuario usuario;

    public ReservaUsuario(String tituloLibro, Usuario usuario) {
        super(tituloLibro);
        this.usuario = usuario;
    }

    public String resumenReserva() {
        return tituloLibro + " reservado por " + usuario.getNombre() +
               " el " + fechaReserva + ". Estado: " + (activa ? "Activa" : "Cancelada");
    }

    public Usuario getUsuario() {
        return usuario;
    }
}