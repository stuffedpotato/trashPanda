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
        if (API_KEY == null || API_KEY.isEmpty()) {
            return "Error: GEMINI_API_KEY not set in environment variables.";
        }

        String ingredients = shareList.stream()
                .map(entry -> entry.getItem().getName())
                .collect(Collectors.joining(", "));

        String prompt = String.format(
                "Using the following ingredients: %s, generate 2-3 creative and simple recipes.\n\n" +
                        "For each recipe, provide:\n" +
                        "- Recipe title\n" +
                        "- A short 2-3 sentence summary\n" +
                        "- A list of ingredients from the provided list\n" +
                        "- A list of additional ingredients\n\n" +
                        "Respond in JSON array format, like this:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"title\": \"Fried Rice with Spinach\",\n" +
                        "    \"summary\": \"Quick and easy fried rice using pantry staples.\",\n" +
                        "    \"usedIngredients\": [\"rice\", \"spinach\", \"olive oil\"],\n" +
                        "    \"neededIngredients\": [\"soy sauce\", \"onion\", \"garlic\"]\n" +
                        "  },\n" +
                        "  ...more recipes\n" +
                        "]\n" +
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

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();

            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }

            return parseGeminiResponse(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to generate recipes.";
        }
    }

    private static String parseGeminiResponse(String rawJson) {
        JsonObject root = JsonParser.parseString(rawJson).getAsJsonObject();
        JsonArray candidates = root.getAsJsonArray("candidates");
    
        if (candidates != null && candidates.size() > 0) {
            JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
    
            if (parts != null && parts.size() > 0) {
                // 👇 This is the actual JSON-formatted recipe array (as a string)
                String jsonText = parts.get(0).getAsJsonObject().get("text").getAsString();
    
                try {
                    JsonArray recipeArray = JsonParser.parseString(jsonText).getAsJsonArray();
                    return new GsonBuilder().setPrettyPrinting().create().toJson(recipeArray);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    return "Gemini returned malformed JSON.";
                }
            }
        }
    
        return "Could not parse Gemini response.";
    }    
}
