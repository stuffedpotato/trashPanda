package com.trashpanda;

import com.trashpanda.ShareList.ShareListEntry;
import com.google.gson.*;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import io.github.cdimascio.dotenv.Dotenv;

public class RecipeGenerator {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent";

    // Load environment variables from .env file
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("GEMINI_API_KEY");

    public static String generateRecipesFromShareList(List<ShareListEntry> shareList) {
        System.out.println("Attempting to generate recipes with " + shareList.size() + " ingredients");
        
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.out.println("ERROR: GEMINI_API_KEY not found or empty");
            return "Error: GEMINI_API_KEY not set in environment variables.";
        }

        String ingredients = shareList.stream()
                .map(entry -> entry.getItem().getName())
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "Using the following ingredients: %s, generate 2-3 creative and simple recipes.\n\n" +
                        "For each recipe, provide:\n" +
                        "1. Recipe title (on a line by itself)\n" +
                        "2. A short 2-3 sentence summary\n" +
                        "3. A section called 'Ingredients You Have:' that lists items from the provided list\n" +
                        "4. A section called 'Additional Ingredients Needed:' that lists other ingredients\n\n" +
                        "Format each recipe with clear section headers and separate each recipe with a line of dashes (----).\n" +
                        "Avoid abbreviations (e.g., write 'tablespoons' not 'tbsp').",
                ingredients);

        try {
            // Build the payload
            JsonObject part = new JsonObject();
            part.addProperty("text", prompt);

            JsonArray partsArray = new JsonArray();
            partsArray.add(part);

            JsonObject content = new JsonObject();
            content.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(content);

            JsonObject payload = new JsonObject();
            payload.add("contents", contentsArray);

            // HTTP POST
            URI uri = new URI(GEMINI_API_URL + "?key=" + API_KEY);
            URL url = uri.toURL();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check if there was an error
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Error from Gemini API: Response code " + responseCode);
                
                // Read error message
                StringBuilder errorResponse = new StringBuilder();
                try (Scanner scanner = new Scanner(conn.getErrorStream())) {
                    while (scanner.hasNextLine()) {
                        errorResponse.append(scanner.nextLine());
                    }
                }
                System.out.println("Error details: " + errorResponse.toString());
                
                return "Failed to generate recipes. API response: " + responseCode;
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();

            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }

            return extractPlainTextRecipes(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to generate recipes: " + e.getMessage();
        }
    }

    // Extract plain text recipes from Gemini response
    private static String extractPlainTextRecipes(String response) {
        try {
            // Parse the response JSON
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                
                if (parts != null && parts.size() > 0) {
                    // Extract the text content directly without trying to parse it as JSON
                    String recipeText = parts.get(0).getAsJsonObject().get("text").getAsString();
                    return formatRecipeText(recipeText);
                }
            }
            
            return "No recipes could be generated.";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting recipes: " + e.getMessage();
        }
    }
    
    // Format recipe text for better display
    private static String formatRecipeText(String recipeText) {
        // Replace multiple newlines with a single newline to clean up spacing
        recipeText = recipeText.replaceAll("\\n{3,}", "\n\n");
        
        // Ensure recipe separators are formatted consistently
        recipeText = recipeText.replaceAll("[-]{4,}", "\n\n----\n\n");
        
        // Add HTML-friendly formatting (the frontend will handle this as HTML)
        recipeText = recipeText.replaceAll("\\n", "<br>");
        
        return recipeText;
    }
}