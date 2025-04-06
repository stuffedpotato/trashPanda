package com.trashpanda.ShareList;

import com.trashpanda.DatabaseConfig;
import com.trashpanda.Item;
import com.trashpanda.ItemQuantityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShareListService {
    public List<ShareListEntry> getShareListEntries(String username) {
        List<ShareListEntry> entries = new ArrayList<>();
        String sql = "SELECT ingredient, quantity, units, expiration_date FROM sharelist WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String ingredient = rs.getString("ingredient");
                double quantity = rs.getDouble("quantity");
                String units = rs.getString("units");
                Date expirationDate = rs.getDate("expiration_date");

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
                ShareListEntry entry = new ShareListEntry(username, item, quantity, expirationDate);
                entries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public boolean insertShareListEntry(String username, Item item, double qty, Date expirationDate) {
        // Debug log
        System.out.println("Inserting share list entry:");
        System.out.println("Username: " + username);
        System.out.println("Item: " + (item != null ? item.getName() : "null"));
        System.out.println("Quantity: " + qty);
        System.out.println("Units: " + (item != null ? item.getQtyType() : "null"));
        System.out.println("Expiration date: " + expirationDate);

        String sql = "INSERT INTO sharelist (username, ingredient, quantity, units, expiration_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, item.getName());
            stmt.setDouble(3, qty);
            
            // Convert the enum to a string the database can handle
            String unitString = convertItemQuantityTypeToString(item.getQtyType());
            stmt.setString(4, unitString);

            // Make expiration_date optional
            if (expirationDate != null) {
                stmt.setDate(5, expirationDate);
            } else {
                stmt.setNull(5, Types.DATE);
            }

            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateShareListEntry(String username, Item item, double qty, Date expirationDate) {
        if (qty <= 0) {
            return removeShareListEntry(username, item);
        } else {
            String sql = "UPDATE sharelist SET quantity = ?, units = ?, expiration_date = ? " +
                    "WHERE username = ? AND ingredient = ?";

            try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setDouble(1, qty);
                
                // Convert the enum to a string the database can handle
                String unitString = convertItemQuantityTypeToString(item.getQtyType());
                stmt.setString(2, unitString);

                if (expirationDate != null) {
                    stmt.setDate(3, expirationDate);
                } else {
                    stmt.setNull(3, Types.DATE);
                }

                stmt.setString(4, username);
                stmt.setString(5, item.getName());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean removeShareListEntry(String username, Item item) {
        String sql = "DELETE FROM sharelist WHERE username = ? AND ingredient = ?";

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