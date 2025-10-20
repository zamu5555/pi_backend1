package com.example.application.views.inicio;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;


import java.util.ArrayList;
import java.util.List;

@Route("")
public class InicioView extends VerticalLayout {

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<ReservaUsuario> reservas = new ArrayList<>();
    private final List<Libro> libros = new ArrayList<>(List.of(
            new Libro("Cien años de soledad", "Gabriel García Márquez"),
            new Libro("1984", "George Orwell"),
            new Libro("El Principito", "Antoine de Saint-Exupéry")));

    private Usuario usuarioActual = null;

    private final VerticalLayout seccionAuth = new VerticalLayout();
    private final VerticalLayout contenedorAcceso = new VerticalLayout();
    private final VerticalLayout formularioAcceso = new VerticalLayout();
    private final VerticalLayout seccionReserva = new VerticalLayout();
    private final VerticalLayout seccionPrestamo = new VerticalLayout();
    private final VerticalLayout contenedorReservas = new VerticalLayout();
    private final VerticalLayout seccionRecomendaciones = new VerticalLayout();

    private final GeminiService gemini = new GeminiService("AIzaSyCUOnNR4IIatK-vHotwvJZF25R9Dq_jgCg");

    public InicioView() {
        setAlignItems(Alignment.CENTER);
        setSpacing(true);

        add(new H2("Bienvenido a la Biblioteca"));
        add(seccionAuth);

        mostrarSeccionAuth();
    }

    private void mostrarSeccionAuth() {
        seccionAuth.removeAll();
        contenedorAcceso.removeAll();
        formularioAcceso.removeAll();

        Button btnRegistro = new Button("Registro", e -> mostrarFormularioRegistro());
        Button btnLogin = new Button("Iniciar Sesión", e -> mostrarFormularioLogin());

        HorizontalLayout botones = new HorizontalLayout(btnRegistro, btnLogin);
        botones.setSpacing(true);

        contenedorAcceso.add(new H2("Acceso de Usuario"), botones, formularioAcceso);
        seccionAuth.add(contenedorAcceso);

        mostrarFormularioLogin();
    }

    private void mostrarFormularioRegistro() {
        formularioAcceso.removeAll();

        TextField nombre = new TextField("Nombre");
        EmailField correo = new EmailField("Correo");
        PasswordField contraseña = new PasswordField("Contraseña");

        Button registrar = new Button("Registrar", e -> {
            Usuario nuevo = new Usuario(nombre.getValue(), correo.getValue(), contraseña.getValue());

            if (!nuevo.camposCompletos()) {
                Notification.show("Todos los campos son obligatorios");
                return;
            }

            boolean correoExistente = usuarios.stream()
                    .anyMatch(u -> u.mismoCorreo(nuevo.getCorreo()));

            if (correoExistente) {
                Notification.show("Ese correo ya está registrado");
                return;
            }

            usuarios.add(nuevo);
            usuarioActual = nuevo;
            Notification.show("Registro exitoso. Bienvenido, " + nuevo.getNombre());

            add(seccionReserva, seccionPrestamo, seccionRecomendaciones);
            mostrarFormularioReserva();
            mostrarLibrosDisponibles();
            mostrarSeccionRecomendaciones();
        });

        FormLayout formulario = new FormLayout(nombre, correo, contraseña, registrar);
        formulario.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formularioAcceso.add(new H2("Registro de Usuario"), formulario);
    }

    private void mostrarFormularioLogin() {
        formularioAcceso.removeAll();

        EmailField correo = new EmailField("Correo");
        PasswordField contraseña = new PasswordField("Contraseña");

        Button ingresar = new Button("Ingresar", e -> {
            String correoIngresado = correo.getValue();
            String contraseñaIngresada = contraseña.getValue();

            if (correoIngresado.isBlank() || contraseñaIngresada.isBlank()) {
                Notification.show("Completa todos los campos");
                return;
            }

            for (Usuario u : usuarios) {
                if (u.credencialesValidas(correoIngresado, contraseñaIngresada)) {
                    usuarioActual = u;
                    Notification.show("Bienvenido de nuevo, " + u.getNombre());

                    add(seccionReserva, seccionPrestamo, seccionRecomendaciones);
                    mostrarFormularioReserva();
                    mostrarLibrosDisponibles();
                    mostrarSeccionRecomendaciones();
                    return;
                } else if (u.mismoCorreo(correoIngresado)) {
                    Notification.show("Contraseña incorrecta");
                    return;
                }
            }

            Notification.show("Correo no registrado");
        });

        FormLayout formulario = new FormLayout(correo, contraseña, ingresar);
        formulario.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        formularioAcceso.add(new H2("Inicio de Sesión"), formulario);
    }

    private void mostrarFormularioReserva() {
        seccionReserva.removeAll();

        if (usuarioActual == null) {
            seccionReserva.add(new Paragraph("Debes iniciar sesión para reservar libros"));
            return;
        }

        TextField tituloLibro = new TextField("Título del Libro");

        Button reservar = new Button("Reservar", e -> {
            if (tituloLibro.isEmpty()) {
                Notification.show("Ingresa el título del libro");
                return;
            }

            boolean yaReservado = reservas.stream()
                    .anyMatch(r -> r.getUsuario().equals(usuarioActual) &&
                            r.getTituloLibro().equalsIgnoreCase(tituloLibro.getValue()));

            if (yaReservado) {
                Notification.show("Ya has reservado ese libro");
                return;
            }

            ReservaUsuario nuevaReserva = new ReservaUsuario(tituloLibro.getValue(), usuarioActual);
            reservas.add(nuevaReserva);

            Notification.show("Reserva creada: " + nuevaReserva.getTituloLibro());
            tituloLibro.clear();
            mostrarReservas();
        });

        FormLayout formulario = new FormLayout(tituloLibro, reservar);
        formulario.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        seccionReserva.add(new H2("Reservar Libro"), formulario, contenedorReservas);

        mostrarReservas();
    }

    private void mostrarReservas() {
        contenedorReservas.removeAll();

        contenedorReservas.add(new H2("Tus Reservas"));

        reservas.stream()
                .filter(r -> r.getUsuario().equals(usuarioActual))
                .forEach(r -> contenedorReservas.add(new Paragraph(r.resumenReserva())));
    }

    private void mostrarLibrosDisponibles() {
        seccionPrestamo.removeAll();

        seccionPrestamo.add(new H2("Gestión de Préstamos"));

        for (Libro libro : libros) {
            HorizontalLayout fila = new HorizontalLayout();
            fila.setAlignItems(Alignment.CENTER);

            Label info = new Label(libro.getTitulo() + " - " + libro.getAutor());

            Button prestarBtn = new Button("Prestar", e -> {
                libro.prestar(usuarioActual.getNombre());
                Notification.show("Prestado: " + libro.getTitulo());
                mostrarLibrosDisponibles();
            });

            Button devolverBtn = new Button("Devolver", e -> {
                libro.devolver();
                Notification.show("Devuelto: " + libro.getTitulo());
                mostrarLibrosDisponibles();
            });

            Button renovarBtn = new Button("Renovar", e -> {
                libro.renovar();
                Notification.show("Renovado: " + libro.getTitulo() + " (" + libro.diasRestantes() + " días)");
                mostrarLibrosDisponibles();
            });

            prestarBtn.setEnabled(!libro.estaPrestado());
            devolverBtn.setEnabled(libro.estaPrestado());
            renovarBtn.setEnabled(libro.puedeRenovarse());

            fila.add(info, prestarBtn, devolverBtn, renovarBtn);
            seccionPrestamo.add(fila);
        }
    }

    private void mostrarSeccionRecomendaciones() {
        seccionRecomendaciones.removeAll();

        TextField generoInput = new TextField("Género para recomendaciones");
        Button btnRecomendar = new Button("Obtener recomendaciones");
        TextArea resultadoArea = new TextArea("Libros recomendados");
        resultadoArea.setWidthFull();
        resultadoArea.setHeight("200px");

        btnRecomendar.addClickListener(event -> {
            String genero = generoInput.getValue().trim();
            if (genero.isEmpty()) {
                Notification.show("Ingresa un género");
                return;
            }

            try {
                
                String recomendaciones = gemini.recomendarLibros(genero);
                resultadoArea.setValue(recomendaciones.isEmpty() ? "No se encontraron recomendaciones.1" : recomendaciones);
            } catch (Exception e) { 
                e.printStackTrace();
                resultadoArea.setValue("Error al conectar con Gemini");
            }
        });

        seccionRecomendaciones.add(new H2("Recomendaciones de Libros con Gemini"),
                generoInput, btnRecomendar, resultadoArea);
    }
}
