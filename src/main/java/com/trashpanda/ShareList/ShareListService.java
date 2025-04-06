package com.trashpanda.ShareList;

import com.trashpanda.DatabaseConfig;
import com.trashpanda.Item;

import java.sql.*;

public class ShareListService {

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
}
