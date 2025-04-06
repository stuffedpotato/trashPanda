package com.trashpanda;

import com.google.gson.Gson;
import com.trashpanda.ShareList.ShareListController;
import com.trashpanda.ShareList.ShareListService;
import com.trashpanda.ShareList.ShareListEntry;
import com.trashpanda.WantList.WantListEntry;
import com.trashpanda.WantList.WantListController;
import com.trashpanda.WantList.WantListService;
import io.github.cdimascio.dotenv.Dotenv;
import spark.Spark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        // Initialize database
        Database.initializeDatabase();

        // Load environment variables
        Dotenv dotenv = Dotenv.load();

        // Configure Spark
        Spark.port(Integer.parseInt(dotenv.get("PORT", "4567")));
        
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

                ShareListService service = new ShareListService();
                List<ShareListEntry> shareList = service.getShareListEntries(username);

                if (shareList.isEmpty()) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("No ingredients found for " + username));
                }

                String recipes = RecipeGenerator.generateRecipesFromShareList(shareList);
                return recipes;
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(new ErrorResponse("Error generating recipes: " + e.getMessage()));
            }
        });

        // Matchmaking Endpoint
        Spark.get("/matches/:username", (req, res) -> {
            res.type("application/json");
            try {
                String username = req.params(":username");
                
                // Get the current user
                User requester = getUserFromDatabase(username);
                if (requester == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("User not found: " + username));
                }
                
                // Get all users for matching
                List<User> allUsers = getAllUsersFromDatabase();
                
                // Find matches using the Matchmaker
                List<MatchResult> matches = Matchmaker.findSortedMatches(requester, allUsers);
                
                return gson.toJson(matches);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new ErrorResponse("Error finding matches: " + e.getMessage()));
            }
        });

        // Error handling for OPTIONS requests (CORS preflight)
        Spark.options("/*", (req, res) -> {
            res.status(204);
            return "";
        });
    }

    // Helper method to get a user from the database
    private static User getUserFromDatabase(String username) {
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            // Get user profile information
            String sql = "SELECT firstname, lastname, longitude, latitude, password, radius, contact FROM profiles WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                double longitude = rs.getDouble("longitude");
                double latitude = rs.getDouble("latitude");
                String password = rs.getString("password");
                double radius = rs.getDouble("radius");
                String contact = rs.getString("contact");
                
                Location location = new Location(latitude, longitude);
                User user = new User(firstName, lastName, contact, username, password, location, radius);
                
                // Load share list
                ShareListService shareService = new ShareListService();
                List<ShareListEntry> shareList = shareService.getShareListEntries(username);
                for (ShareListEntry entry : shareList) {
                    user.insertShareListEntry(entry);
                }
                
                // Load want list
                WantListService wantService = new WantListService();
                List<com.trashpanda.WantList.WantListEntry> wantListEntries = wantService.getWantListEntries(username);
                for (com.trashpanda.WantList.WantListEntry entry : wantListEntries) {
                    WantListEntry wantEntry = new WantListEntry(
                        entry.getUsername(), 
                        entry.getItem(), 
                        entry.getQty()
                    );
                    user.insertWantListEntry(wantEntry);
                }
                
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Helper method to get all users from the database
    private static List<User> getAllUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            // Get all usernames
            String sql = "SELECT username FROM profiles";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String username = rs.getString("username");
                User user = getUserFromDatabase(username);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    // Simple error response class for consistent error messaging
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}