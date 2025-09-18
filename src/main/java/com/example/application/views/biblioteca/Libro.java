package com.example.application.views.biblioteca;

public class Libro implements Prestable, Renovable {
    private String titulo;
    private String autor;
    private boolean prestado;
    private String usuarioPrestamo;
    private int diasPrestamo;

    public Libro(String titulo, String autor) {
        this.titulo = titulo;
        this.autor = autor;
        this.prestado = false;
        this.usuarioPrestamo = null;
        this.diasPrestamo = 0;
    }

    // Métodos de Prestable
    @Override
    public void prestar(String usuario) {
        if (!prestado) {
            this.prestado = true;
            this.usuarioPrestamo = usuario;
            this.diasPrestamo = 14; // préstamo inicial de 14 días
            System.out.println("📚 Libro '" + titulo + "' prestado a " + usuario);
        } else {
            System.out.println("⚠️ El libro '" + titulo + "' ya está prestado.");
        }
    }

    @Override
    public void devolver() {
        if (prestado) {
            System.out.println("✅ El libro '" + titulo + "' ha sido devuelto por " + usuarioPrestamo);
            this.prestado = false;
            this.usuarioPrestamo = null;
            this.diasPrestamo = 0;
        } else {
            System.out.println("⚠️ El libro '" + titulo + "' no estaba prestado.");
        }
    }

    @Override
    public boolean estaPrestado() {
        return prestado;
    }

    // Métodos de Renovable
    @Override
    public void renovar() {
        if (puedeRenovarse()) {
            diasPrestamo += 7; // le damos 7 días más
            System.out.println("🔄 El préstamo del libro '" + titulo + "' ha sido renovado. Días restantes: " + diasPrestamo);
        } else {
            System.out.println("⚠️ No es posible renovar el libro '" + titulo + "'.");
        }
    }

    @Override
    public boolean puedeRenovarse() {
        return prestado && diasPrestamo > 0;
    }

    @Override
    public int diasRestantes() {
        return diasPrestamo;
    }

    // Getters básicos
    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }
}
