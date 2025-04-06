package com.trashpanda.WantList;

import com.trashpanda.DatabaseConfig;
import com.trashpanda.Item;
import com.trashpanda.ItemQuantityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WantListService {
    public List<WantListEntry> getWantListEntries(String username) {
        List<WantListEntry> entries = new ArrayList<>();
        String sql = "SELECT ingredient, quantity, units FROM wantlist WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String ingredient = rs.getString("ingredient");
                double quantity = rs.getDouble("quantity");
                String units = rs.getString("units");

                // Convert units string to enum with error handling
                ItemQuantityType qtyType;
                try {
                    qtyType = convertStringToItemQuantityType(units);
                    System.out.println("Successfully converted units: " + units + " to enum: " + qtyType);
                } catch (Exception e) {
                    System.out.println("Failed to convert units: " + units + " - Error: " + e.getMessage());
                    qtyType = ItemQuantityType.UNIT; // Default to UNIT if conversion fails
                }

                Item item = new Item(ingredient, null, qtyType);
                WantListEntry entry = new WantListEntry(username, item, quantity);
                entries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public boolean insertWantListEntry(String username, Item item, double qty) {
        String sql = "INSERT INTO wantlist (username, ingredient, quantity, units) " +
                "VALUES (?, ?, ?, ?)";
        
        // Convert the enum to a string the database can handle
        String unitString = convertItemQuantityTypeToString(item.getQtyType());
        
        System.out.println("Executing SQL: " + sql);
        System.out.println("With parameters: username=" + username + ", ingredient=" + item.getName() + 
                           ", quantity=" + qty + ", units=" + unitString);
    
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.setString(2, item.getName());
            stmt.setDouble(3, qty);
            stmt.setString(4, unitString);
    
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateWantListEntry(String username, Item item, double qty) {
        if (qty <= 0) {
            return removeWantListEntry(username, item);
        } else {
            String sql = "UPDATE wantlist SET quantity = ?, units = ? " +
                    "WHERE username = ? AND ingredient = ?";

            try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setDouble(1, qty);
                
                // Convert the enum to a string the database can handle
                String unitString = convertItemQuantityTypeToString(item.getQtyType());
                stmt.setString(2, unitString);
                
                stmt.setString(3, username);
                stmt.setString(4, item.getName());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean removeWantListEntry(String username, Item item) {
        String sql = "DELETE FROM wantlist WHERE username = ? AND ingredient = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, item.getName());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method to convert unit strings from the frontend to enum values
    private ItemQuantityType convertStringToItemQuantityType(String unitString) {
        if (unitString == null || unitString.isEmpty()) {
            return ItemQuantityType.UNIT;
        }
        
        switch (unitString.toUpperCase()) {
            case "G":
                return ItemQuantityType.GRAM;
            case "KG":
                return ItemQuantityType.KILOGRAM;
            case "ML":
                return ItemQuantityType.MILLILITER;
            case "L":
                return ItemQuantityType.LITER;
            case "TSP":
                return ItemQuantityType.TSP;
            case "TBSP":
                return ItemQuantityType.TBSP;
            case "CUP":
                return ItemQuantityType.CUP;
            case "OZ":
                return ItemQuantityType.OZ;
            case "LB":
                return ItemQuantityType.LB;
            case "UNIT":
                return ItemQuantityType.UNIT;
            case "PIECE":
                return ItemQuantityType.PIECE;
            case "PACK":
                return ItemQuantityType.PACK;
            case "BOTTLE":
                return ItemQuantityType.BOTTLE;
            case "CAN":
                return ItemQuantityType.CAN;
            case "BOX":
                return ItemQuantityType.BOX;
            case "BAG":
                return ItemQuantityType.BAG;
            case "JAR":
                return ItemQuantityType.JAR;
            case "SLICES":
                return ItemQuantityType.PIECE;
            default:
                System.out.println("Unknown unit type: " + unitString + ", defaulting to UNIT");
                return ItemQuantityType.UNIT;
        }
    }
    
    // Helper method to convert enum values to strings for the database
    private String convertItemQuantityTypeToString(ItemQuantityType qtyType) {
        if (qtyType == null) {
            return "UNIT";
        }
        
        switch (qtyType) {
            case GRAM:
                return "G";
            case KILOGRAM:
                return "KG";
            case MILLILITER:
                return "ML";
            case LITER:
                return "L";
            // For the rest, just use the enum name
            default:
                return qtyType.name();
        }
    }
}