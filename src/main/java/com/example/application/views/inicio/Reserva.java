package com.example.application.views.inicio;

import java.time.LocalDate;

public class Reserva {
    protected String tituloLibro;
    protected LocalDate fechaReserva;
    protected boolean activa;

    public Reserva(String tituloLibro) {
        this.tituloLibro = tituloLibro;
        this.fechaReserva = LocalDate.now();
        this.activa = true;
    }

    public void cancelar() {
        this.activa = false;
    }

    public boolean estaActiva() {
        return activa;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }
}
