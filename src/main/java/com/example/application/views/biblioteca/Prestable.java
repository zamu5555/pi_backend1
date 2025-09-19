package com.example.application.views.biblioteca;

public interface Prestable {
    void prestar(String usuario);
    void devolver();
    boolean estaPrestado();
}
