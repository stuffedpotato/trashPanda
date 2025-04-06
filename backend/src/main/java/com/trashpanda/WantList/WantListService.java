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

                // Determine the appropriate unit type from the string
                ItemQuantityType qtyType;
                try {
                    qtyType = ItemQuantityType.valueOf(units.toUpperCase());
                } catch (IllegalArgumentException e) {
                    qtyType = ItemQuantityType.UNIT; // Default to UNIT if not found
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
        
        System.out.println("Executing SQL: " + sql);
        System.out.println("With parameters: username=" + username + ", ingredient=" + item.getName() + 
                           ", quantity=" + qty + ", units=" + item.getQtyType().name());
    
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, username);
            stmt.setString(2, item.getName());
            stmt.setDouble(3, qty);
            stmt.setString(4, item.getQtyType().name());
    
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
                stmt.setString(2, item.getQtyType().name());
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
}