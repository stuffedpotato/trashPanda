package com.trashpanda;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeFetcher {

    private static String API_KEY;

    static {
        loadEnv(".env");
        API_KEY = System.getProperty("SPOONACULAR_API_KEY");
        
        if (API_KEY == null) {
            throw new RuntimeException("SPOONACULAR_API_KEY not found in .env file");
        }
    }

    private static void loadEnv(String path) {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            lines.filter(line -> line.contains("=") && !line.startsWith("#"))
                 .forEach(line -> {
                     String[] parts = line.split("=", 2);
                     if (parts.length == 2) {
                         System.setProperty(parts[0].trim(), parts[1].trim());
                     }
                 });
        } catch (IOException e) {
            System.err.println("Could not load .env: " + e.getMessage());
        }
    }

    public static String getRecipesFromShareList(List<ShareListEntry> shareList) throws Exception {
        if (shareList == null || shareList.isEmpty()) return "[]";

        String ingredients = shareList.stream()
                .map(entry -> entry.getItem().getName())
                .distinct()
                .collect(Collectors.joining(","));

        String endpoint = "https://api.spoonacular.com/recipes/findByIngredients"
                + "?ingredients=" + ingredients
                + "&number=5"
                + "&apiKey=" + API_KEY;

        URL url = URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("API call failed. Code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return content.toString();
    }
}