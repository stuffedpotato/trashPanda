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
//                String units = rs.getString("units");
                Date expirationDate = rs.getDate("expiration_date");

                Item item = new Item(ingredient, null, ItemQuantityType.TSP); // placeholder enum
                ShareListEntry entry = new ShareListEntry(username, item, quantity, expirationDate);
                entries.add(entry);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public boolean insertShareListEntry(String username, Item item, double qty, Date expirationDate) {
        String sql = "INSERT INTO sharelist (username, ingredient, quantity, units, expiration_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, item.getName());
            stmt.setDouble(3, qty);
            stmt.setString(4, item.getQtyType().name());

            if (expirationDate != null) {
                stmt.setDate(5, expirationDate);
            } else {
                stmt.setNull(5, Types.DATE);
            }

            int rowsAffected = stmt.executeUpdate();
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
                stmt.setString(2, item.getQtyType().name());

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
}
