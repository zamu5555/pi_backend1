package com.example.application.views.inicio;

public interface Prestable {
    void prestar(String usuario);
    void devolver();
    boolean estaPrestado();
}
