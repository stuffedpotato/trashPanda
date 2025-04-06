package com.trashpanda;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static spark.Spark.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class LocationController {
    
    private static final Gson gson = new Gson();
    
    public static void initializeRoutes() {
        // Endpoint to update a user's location
        post("/update-location", (req, res) -> {
            res.type("application/json");
            
            try {
                // Parse request body
                String body = req.body();
                System.out.println("Received location update request: " + body);
                
                JsonObject requestJson = JsonParser.parseString(body).getAsJsonObject();
                String username = requestJson.get("username").getAsString();
                double latitude = requestJson.get("latitude").getAsDouble();
                double longitude = requestJson.get("longitude").getAsDouble();
                
                // Update location in database
                boolean success = updateUserLocation(username, latitude, longitude);
                
                if (success) {
                    JsonObject response = new JsonObject();
                    response.addProperty("success", true);
                    response.addProperty("message", "Location updated successfully");
                    return response.toString();
                } else {
                    res.status(500);
                    JsonObject response = new JsonObject();
                    response.addProperty("success", false);
                    response.addProperty("message", "Failed to update location");
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                JsonObject response = new JsonObject();
                response.addProperty("success", false);
                response.addProperty("message", "Error processing request: " + e.getMessage());
                return response.toString();
            }
        });
    }
    
    private static boolean updateUserLocation(String username, double latitude, double longitude) {
        String sql = "UPDATE profiles SET latitude = ?, longitude = ? WHERE username = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, latitude);
            stmt.setDouble(2, longitude);
            stmt.setString(3, username);
            
            int rowsAffected = stmt.executeUpdate();
            
            System.out.println("Updated location for user " + username + 
                          " to lat: " + latitude + ", lng: " + longitude + 
                          ". Rows affected: " + rowsAffected);
            
            return rowsAffected > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}