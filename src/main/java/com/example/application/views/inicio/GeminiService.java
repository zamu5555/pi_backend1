package com.example.application.views.inicio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {

    private final String apiKey;
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String recomendarLibros(String genero) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            String prompt = "Recomienda una lista de 5 libros populares del género '" + genero + "'. " +
                    "Devuelve solo la lista numerada del 1 al 5 con título y autor.";

            // Crear el JSON correcto según la API de Gemini
            ObjectNode requestJson = mapper.createObjectNode();
            ArrayNode contentsArray = mapper.createArrayNode();
            ObjectNode contentObject = mapper.createObjectNode();
            ArrayNode partsArray = mapper.createArrayNode();
            ObjectNode partObject = mapper.createObjectNode();
            
            partObject.put("text", prompt);
            partsArray.add(partObject);
            contentObject.set("parts", partsArray);
            contentsArray.add(contentObject);
            requestJson.set("contents", contentsArray);

            String requestBody = mapper.writeValueAsString(requestJson);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar si la respuesta fue exitosa
            if (response.statusCode() != 200) {
                System.err.println("Error HTTP: " + response.statusCode());
                System.err.println("Respuesta: " + response.body());
                return "Error al conectar con Gemini (código " + response.statusCode() + ")";
            }

            JsonNode jsonNode = mapper.readTree(response.body());
            
            // Ruta correcta para obtener el texto de la respuesta
            JsonNode textNode = jsonNode.at("/candidates/0/content/parts/0/text");
            
            if (textNode.isMissingNode() || textNode.asText().isEmpty()) {
                System.err.println("Respuesta vacía o sin el formato esperado");
                System.err.println("Respuesta completa: " + response.body());
                return "No se encontraron recomendaciones.";
            }

            return textNode.asText();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error al conectar con Gemini: " + e.getMessage();
        }
    }
}
