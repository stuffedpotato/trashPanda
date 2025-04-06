package com.trashpanda;

import com.google.gson.Gson;
import com.trashpanda.ShareList.ShareListController;
import com.trashpanda.ShareList.ShareListService;
import com.trashpanda.ShareList.ShareListEntry;
import com.trashpanda.WantList.WantListController;
import spark.Spark;

import java.util.List;

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        // Initialize database
        try {
            Database.initializeDatabase();
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Warning: Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }

        // Configure Spark
        Spark.port(4567);
        
        // Enable CORS
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        // Initialize controllers
        ShareListController.initializeRoutes();
        WantListController.initializeRoutes();

        // Recipe Generation Endpoint
        Spark.get("/recipes/:username", (req, res) -> {
            res.type("application/json");
            try {
                String username = req.params(":username");
                System.out.println("Generating recipes for: " + username);

                ShareListService service = new ShareListService();
                List<ShareListEntry> shareList = service.getShareListEntries(username);

                if (shareList.isEmpty()) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("No ingredients found for " + username));
                }

                String recipes = RecipeGenerator.generateRecipesFromShareList(shareList);
                return recipes;
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new ErrorResponse("Error generating recipes: " + e.getMessage()));
            }
        });

        // Matchmaking Endpoint
        Spark.get("/matches/:username", (req, res) -> {
            res.type("application/json");
            try {
                String username = req.params(":username");
                System.out.println("Finding matches for: " + username);
                
                // This is a placeholder - implement actual matchmaking logic
                // For testing purposes, return example match data
                String exampleJson = "[{\"user\":{\"userName\":\"avi\",\"firstName\":\"Avi\"},\"distance\":2.5},{\"user\":{\"userName\":\"piyusha\",\"firstName\":\"Piyusha\"},\"distance\":4.8}]";
                return exampleJson;
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new ErrorResponse("Error finding matches: " + e.getMessage()));
            }
        });

        // Handle OPTIONS requests for CORS preflight
        Spark.options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });
        
        // Print server start message
        System.out.println("Server started on port " + Spark.port());
        System.out.println("API endpoints available at http://localhost:" + Spark.port());
    }

    // Simple error response class for consistent messaging
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}