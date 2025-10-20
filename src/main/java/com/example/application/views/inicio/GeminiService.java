package com.example.application.views.inicio;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {

    private final String apiKey;
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta2/models/text-bison-001:generateText";

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String recomendarLibros(String genero) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String prompt = "Recomienda una lista de 5 libros populares del género '" + genero + "'. " +
                    "Devuelve solo la lista numerada del 1 al 5.";

            String requestBody = "{ \"prompt\": \"" + prompt + "\", \"temperature\": 0.7 }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body());
            String texto = jsonNode.at("/candidates/0/content").asText();

            return texto.isEmpty() ? "No se encontraron recomendaciones.2" : texto;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error al conectar con Gemini";
        }
    }
}
