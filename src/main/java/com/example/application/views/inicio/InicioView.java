package com.example.application.views.inicio;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("")
public class InicioView extends VerticalLayout {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<ReservaUsuario> reservas = new ArrayList<>();
    private final VerticalLayout contenedor = new VerticalLayout();
    private Usuario usuarioActual = null;

    public InicioView() {
        setAlignItems(Alignment.CENTER);
        setSpacing(true);

        Button irRegistro = new Button("Registro", e -> mostrarFormularioRegistro());
        Button irLogin = new Button("Iniciar Sesión", e -> mostrarFormularioLogin());

        add(new H2("Bienvenido a la Biblioteca"), irRegistro, irLogin, contenedor);
        mostrarFormularioRegistro();
    }

    private void mostrarFormularioRegistro() {
        contenedor.removeAll();

        TextField nombre = new TextField("Nombre");
        EmailField correo = new EmailField("Correo");
        PasswordField contraseña = new PasswordField("Contraseña");

        Button registrar = new Button("Registrar", e -> {
            Usuario nuevo = new Usuario(nombre.getValue(), correo.getValue(), contraseña.getValue());

            if (!nuevo.camposCompletos()) {
                Notification.show("Todos los campos son obligatorios", 3000, Notification.Position.MIDDLE);
                return;
            }

            boolean correoExistente = usuarios.stream()
                    .anyMatch(u -> u.mismoCorreo(nuevo.getCorreo()));

            if (correoExistente) {
                Notification.show("Ese correo ya está registrado", 3000, Notification.Position.MIDDLE);
                return;
            }

            usuarios.add(nuevo);
            usuarioActual = nuevo;
            Notification.show("Registro exitoso. Bienvenido, " + nuevo.getNombre(), 3000,
                    Notification.Position.TOP_CENTER);
            mostrarFormularioReserva();
        });

        FormLayout formulario = new FormLayout(nombre, correo, contraseña, registrar);
        formulario.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        contenedor.add(new H2("Registro de Usuario"), formulario);
    }

    private void mostrarFormularioLogin() {
        contenedor.removeAll();

        EmailField correo = new EmailField("Correo");
        PasswordField contraseña = new PasswordField("Contraseña");

        Button ingresar = new Button("Ingresar", e -> {
            String correoIngresado = correo.getValue();
            String contraseñaIngresada = contraseña.getValue();

            if (correoIngresado.isBlank() || contraseñaIngresada.isBlank()) {
                Notification.show("Completa todos los campos", 3000, Notification.Position.MIDDLE);
                return;
            }

            for (Usuario u : usuarios) {
                if (u.credencialesValidas(correoIngresado, contraseñaIngresada)) {
                    usuarioActual = u;
                    Notification.show("Bienvenido de nuevo, " + u.getNombre(), 3000,
                            Notification.Position.TOP_CENTER);
                    mostrarFormularioReserva();
                    return;
                } else if (u.mismoCorreo(correoIngresado)) {
                    Notification.show("Contraseña incorrecta", 3000, Notification.Position.MIDDLE);
                    return;
                }
            }

            Notification.show("Correo no registrado", 3000, Notification.Position.MIDDLE);
        });

        FormLayout formulario = new FormLayout(correo, contraseña, ingresar);
        formulario.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        contenedor.add(new H2("Inicio de Sesión"), formulario);
    }

    private void mostrarFormularioReserva() {
        contenedor.removeAll();

        if (usuarioActual == null) {
            Notification.show("Debes iniciar sesión para reservar libros", 3000, Notification.Position.MIDDLE);
            return;
        }

        TextField tituloLibro = new TextField("Título del Libro");

        Button reservar = new Button("Reservar", e -> {
            if (tituloLibro.isEmpty()) {
                Notification.show("Ingresa el título del libro", 3000, Notification.Position.MIDDLE);
                return;
            }

            ReservaUsuario nuevaReserva = new ReservaUsuario(tituloLibro.getValue(), usuarioActual);
            reservas.add(nuevaReserva);

            Notification.show("Reserva creada: " + nuevaReserva.getTituloLibro(), 3000,
                    Notification.Position.TOP_CENTER);
            tituloLibro.clear();
            mostrarReservas();
        });

        FormLayout formulario = new FormLayout(tituloLibro, reservar);
        formulario.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        contenedor.add(new H2("Reservar Libro"), formulario);
    }

    private void mostrarReservas() {
        contenedor.add(new H2("Tus Reservas"));

        reservas.stream()
                .filter(r -> r.getUsuario().equals(usuarioActual))
                .forEach(r -> contenedor.add(new Paragraph(r.resumenReserva())));
    }
}