package com.trashpanda;

import com.google.gson.Gson;
import com.trashpanda.ShareList.ShareListController;
import com.trashpanda.ShareList.ShareListService;
import com.trashpanda.ShareList.ShareListEntry;
import com.trashpanda.WantList.WantListController;
import com.trashpanda.WantList.WantListEntry;
import spark.Spark;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        LocationController.initializeRoutes();  // Add the new Location Controller

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
                
                // Get the requesting user
                User requester = getUserFromDatabase(username);
                if (requester == null) {
                    res.status(404);
                    return gson.toJson(new ErrorResponse("User not found: " + username));
                }
                
                // Get all users for matching
                List<User> allUsers = getAllUsersFromDatabase();
                
                // Find matches using the Matchmaker class
                List<MatchResult> matches = Matchmaker.findSortedMatches(requester, allUsers);
                
                // Convert to JSON and return
                return gson.toJson(matches);
                
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
                loadShareList(user, conn);
                
                // Load want list
                loadWantList(user, conn);
                
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

    // Helper method to load a user's share list
    private static void loadShareList(User user, Connection conn) throws SQLException {
        String sql = "SELECT ingredient, quantity, units, expiration_date FROM sharelist WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, user.getUserName());
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String ingredient = rs.getString("ingredient");
            double quantity = rs.getDouble("quantity");
            String units = rs.getString("units");
            Date expirationDate = rs.getDate("expiration_date");
            
            // Convert units string to enum
            ItemQuantityType qtyType;
            try {
                qtyType = ItemQuantityType.valueOf(units.toUpperCase());
            } catch (IllegalArgumentException e) {
                qtyType = ItemQuantityType.UNIT; // Default if not found
            }
            
            Item item = new Item(ingredient, null, qtyType);
            ShareListEntry entry = new ShareListEntry(user.getUserName(), item, quantity, expirationDate);
            user.insertShareListEntry(entry);
        }
    }

    // Helper method to load a user's want list
    private static void loadWantList(User user, Connection conn) throws SQLException {
        String sql = "SELECT ingredient, quantity, units FROM wantlist WHERE username = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, user.getUserName());
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String ingredient = rs.getString("ingredient");
            double quantity = rs.getDouble("quantity");
            String units = rs.getString("units");
            
            // Convert units string to enum
            ItemQuantityType qtyType;
            try {
                qtyType = ItemQuantityType.valueOf(units.toUpperCase());
            } catch (IllegalArgumentException e) {
                qtyType = ItemQuantityType.UNIT; // Default if not found
            }
            
            Item item = new Item(ingredient, null, qtyType);
            WantListEntry entry = new WantListEntry(user.getUserName(), item, quantity);
            user.insertWantListEntry(entry);
        }
    }

    // Simple error response class for consistent messaging
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}